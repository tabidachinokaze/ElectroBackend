package moe.tabidachi.service

import moe.tabidachi.model.ResponseData
import moe.tabidachi.model.response.AuthResponse

interface AuthService {
    fun auth(email: String, password: String, code: String?): ResponseData<String?>
    fun login(email: String, password: String): ResponseData<AuthResponse?>
    fun register(email: String, password: String, code: String): ResponseData<AuthResponse?>
}