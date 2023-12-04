package moe.tabidachi.security.code

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.Duration

class SecurityCodeTest {

    @Test
    fun generate() {
        val securityCode = SecurityCode(Duration.ofSeconds(5))
        val key = ""
        val code = securityCode.generate(key)
        runBlocking {
            println(securityCode.verify(key, code))
            delay(1000)
            println(securityCode.verify(key, "342456"))
            delay(6000)
            println(securityCode.verify(key, code))
        }
    }

    @Test
    fun verify() {
    }
}