package cn.tabidachi.security

import cn.tabidachi.security.access.AccessControl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeoutOrNull
import org.junit.Test
import java.time.Duration


class TokenTest {

    @Test
    fun allow() {
        val accessControl = AccessControl(Duration.ofSeconds(10), 5)
        runBlocking {
            launch {
                delay(5000)
                withTimeoutOrNull(Duration.ofMinutes(1)) {
                    while (true) {
                        println("a "+accessControl.isAllow("1"))
                        delay(1000)
                    }
                }
            }
            launch {
                withTimeoutOrNull(Duration.ofMinutes(1)) {
                    while (true) {
                        println("b "+accessControl.isAllow("2"))
                        delay(1000)
                    }
                }
            }
        }
    }
}