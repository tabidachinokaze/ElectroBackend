package cn.tabidachi.ext

import io.ktor.http.*

val URLProtocol.Companion.ELECTRO: URLProtocol
    get() = URLProtocol("electro", 23333)

val URLProtocol.Companion.MINIO: URLProtocol
    get() = URLProtocol("minio", 9000)