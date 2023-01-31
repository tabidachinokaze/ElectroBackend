package cn.tabidachi.service

import cn.tabidachi.model.ResponseData

interface AuthService {
    fun auth(email: String, password: String, code: String): ResponseData<String?>
}