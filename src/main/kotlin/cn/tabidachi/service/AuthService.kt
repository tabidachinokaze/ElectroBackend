package cn.tabidachi.service

import cn.tabidachi.model.ResponseData
import cn.tabidachi.model.response.AuthResponse

interface AuthService {
    fun auth(email: String, password: String, code: String?): ResponseData<String?>
    fun login(email: String, password: String): ResponseData<AuthResponse?>
    fun register(email: String, password: String, code: String): ResponseData<AuthResponse?>
}