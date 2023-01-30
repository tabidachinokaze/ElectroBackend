package cn.tabidachi.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.util.*

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/rtc") { // websocketSession
            val sessionId = UUID.randomUUID()
            try {
                SessionManager.onSessionStarted(sessionId, this)

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            SessionManager.onMessage(sessionId, frame.readText())
                        }
                        else -> Unit
                    }
                }
                println("Exiting incoming loop, closing session: $sessionId")
                SessionManager.onSessionClose(sessionId)
            } catch (e: ClosedReceiveChannelException) {
                println("onClose $sessionId")
                SessionManager.onSessionClose(sessionId)
            } catch (e: Throwable) {
                println("onClose $sessionId $e")
                SessionManager.onSessionClose(sessionId)
            }
        }
    }
}

object SessionManager {
    private val sessionManagerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutex = Mutex()
    private val clients = mutableMapOf<UUID, DefaultWebSocketServerSession>()
    private var sessionState: WebRTCSessionState = WebRTCSessionState.Impossible

    fun onSessionStarted(sessionId: UUID, session: DefaultWebSocketServerSession) {
        sessionManagerScope.launch {
            mutex.withLock {
                if (clients.size > 1) {
                    sessionManagerScope.launch(NonCancellable) {
                        session.send(Frame.Close())
                    }
                    return@launch
                }
                clients[sessionId] = session
                session.send("Added as a client: $sessionId")
                if (clients.size > 1) {
                    sessionState = WebRTCSessionState.Ready
                }
                notifyAboutStateUpdate()
            }
        }
    }

    fun onMessage(sessionId: UUID, message: String) {
        when {
            message.startsWith(MessageType.STATE.toString(), true) -> handleState(sessionId)
            message.startsWith(MessageType.OFFER.toString(), true) -> handleOffer(sessionId, message)
            message.startsWith(MessageType.ANSWER.toString(), true) -> handleAnswer(sessionId, message)
            message.startsWith(MessageType.ICE.toString(), true) -> handleIce(sessionId, message)
        }
    }

    private fun handleState(sessionId: UUID) {
        sessionManagerScope.launch {
            clients[sessionId]?.send("${MessageType.STATE} $sessionState")
        }
    }

    private fun handleOffer(sessionId: UUID, message: String) {
        if (sessionState != WebRTCSessionState.Ready) {
            error("Session should be in Ready state to handle offer")
        }
        sessionState = WebRTCSessionState.Creating
        println("handling offer from $sessionId")
        notifyAboutStateUpdate()
        val clientToSendOffer = clients.filterKeys { it != sessionId }.values.first()
        clientToSendOffer.send(message)
    }

    private fun handleAnswer(sessionId: UUID, message: String) {
        if (sessionState != WebRTCSessionState.Creating) {
            error("Session should be in Creating state to handle answer")
        }
        println("handling answer from $sessionId")
        val clientToSendAnswer = clients.filterKeys { it != sessionId }.values.first()
        clientToSendAnswer.send(message)
        sessionState = WebRTCSessionState.Active
        notifyAboutStateUpdate()
    }

    private fun handleIce(sessionId: UUID, message: String) {
        println("handling ice from $sessionId")
        val clientToSendIce = clients.filterKeys { it != sessionId }.values.first()
        clientToSendIce.send(message)
    }

    fun onSessionClose(sessionId: UUID) {
        sessionManagerScope.launch {
            mutex.withLock {
                clients.remove(sessionId)
                sessionState = WebRTCSessionState.Impossible
                notifyAboutStateUpdate()
            }
        }
    }

    private fun notifyAboutStateUpdate() {
        clients.forEach { (_, client) ->
            client.send("${MessageType.STATE} $sessionState")
        }
    }

    private fun DefaultWebSocketServerSession.send(message: String) {
        sessionManagerScope.launch {
            this@send.send(Frame.Text(message))
        }
    }

    enum class WebRTCSessionState {
        Active,
        Creating,
        Ready,
        Impossible
    }

    enum class MessageType {
        STATE, OFFER, ANSWER, ICE
    }
}