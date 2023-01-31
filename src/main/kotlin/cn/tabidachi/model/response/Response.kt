package cn.tabidachi.model.response

interface Response {
    val code: Int
    val message: String?
}