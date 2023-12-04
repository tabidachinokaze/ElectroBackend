package moe.tabidachi.security.access

import kotlinx.coroutines.*
import java.time.Duration

/**
 * @param duration 期间
 * @param max 在 [duration] 期间内的最大请求次数
 */
class AccessControl(
    private val duration: Duration,
    private val max: Int
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val map = HashMap<String, AccessState>()

    /**
     * 是否允许请求
     * @param key 请求注册的邮箱/手机号码或请求登录的用户id
     */
    fun isAllow(key: String): Boolean {
        val accessState = map[key] ?: AccessState {
            map.remove(key)
        }.also {
            map[key] = it
        }
        return accessState.isAllow()
    }

    inner class AccessState(
        val action: () -> Unit
    ) {
        private var count: Int = 0

        init {
            scope.launch {
                delay(duration.toMillis())
                count = 0
                action()
            }
        }

        fun isAllow(): Boolean {
            count++
            return count <= max
        }
    }
}