package moe.tabidachi.security.code

import kotlinx.coroutines.*
import java.time.Duration
import kotlin.random.Random

class SecurityCode(
    private val duration: Duration,
) : Verifiable {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val map = HashMap<String, CodeState>()

    override fun generate(key: String): String {
        return String.format("%06d", Random.nextInt(CODE_RANGE)).also {
            val codeState = map[key]
            codeState?.cancel()
            map[key] = CodeState(it) {
                map.remove(key)
            }
        }
    }

    override fun verify(key: String, value: String): VerifyResult {
        return when (val codeState = map[key]) {
            null -> VerifyResult.EXPIRED
            else -> when (codeState.code) {
                value -> {
                    map.remove(key)
                    VerifyResult.CORRECT
                }

                else -> VerifyResult.ERROR
            }
        }
    }

    inner class CodeState(val code: String, onExpired: () -> Unit) {
        private val job = scope.launch {
            delay(duration.toMillis())
            onExpired()
        }

        fun cancel() = job.cancel()
    }

    companion object {
        const val CODE_RANGE = 1_000_000
    }
}