package cn.tabidachi.model.response

import org.junit.Test

class ForbiddenResponseTest {
    @Test
    fun test() {
        val forbiddenResponse = ForbiddenResponse()
        forbiddenResponse.message.let(::println)
    }
}