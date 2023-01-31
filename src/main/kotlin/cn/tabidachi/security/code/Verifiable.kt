package cn.tabidachi.security.code

interface Verifiable {
    fun generate(key: String): String
    fun verify(key: String, value: String): VerifyResult
}