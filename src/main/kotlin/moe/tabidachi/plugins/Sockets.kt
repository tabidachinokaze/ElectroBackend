package moe.tabidachi.plugins

import moe.tabidachi.WebSocketClient
import moe.tabidachi.model.WebSocketMessage
import moe.tabidachi.security.jwt.UserPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
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
    val client: WebSocketClient by inject()
    routing {
        authenticate {
            webSocketRaw("/") {
                val uid = when (val principal = call.principal<UserPrincipal>()) {
                    null -> {
                        this.close(
                            CloseReason(
                                CloseReason.Codes.CANNOT_ACCEPT, "未验证身份"
                            )
                        )
                        return@webSocketRaw
                    }

                    else -> {
                        principal.userId
                    }
                }
                client.online(uid, this)

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            kotlin.runCatching {
                                Json.decodeFromString<WebSocketMessage>(frame.readText())
                            }.onSuccess {
                                client.onMessage(uid, it, this)
                            }.onFailure {
                                println("unsupported message: ${frame.readText()}")
                                it.printStackTrace()
                            }
                        }

                        is Frame.Ping -> {
                            kotlin.runCatching {
                                send(Frame.Pong(frame.data))
                            }.onFailure {
                                it.printStackTrace()
                            }
                        }

                        else -> {

                        }
                    }
                }
                client.offline(uid, this)
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
            message.startsWith(SignalingCommand.STATE.toString(), true) -> handleState(sessionId)
            message.startsWith(SignalingCommand.OFFER.toString(), true) -> handleOffer(sessionId, message)
            message.startsWith(SignalingCommand.ANSWER.toString(), true) -> handleAnswer(sessionId, message)
            message.startsWith(SignalingCommand.ICE.toString(), true) -> handleIce(sessionId, message)
        }
    }

    private fun handleState(sessionId: UUID) {
        sessionManagerScope.launch {
            clients[sessionId]?.send("${SignalingCommand.STATE} $sessionState")
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
            client.send("${SignalingCommand.STATE} $sessionState")
        }
    }

    private fun DefaultWebSocketServerSession.send(message: String) {
        sessionManagerScope.launch {
            this@send.send(Frame.Text(message))
        }
    }
}

enum class WebRTCSessionState {
    Active, Creating, Ready, Impossible
}

enum class SignalingCommand {
    STATE, OFFER, ANSWER, ICE
}