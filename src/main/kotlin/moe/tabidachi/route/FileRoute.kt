package moe.tabidachi.route

import moe.tabidachi.MinIO
import moe.tabidachi.ext.ELECTRO
import moe.tabidachi.model.response.Response
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.file() {
    route("/files") {
        post {
            val data = call.receiveMultipart()
            var name = UUID.randomUUID().toString().filter { it != '-' }
            data.forEachPart { part ->
                if (part is PartData.FileItem) {
                    name += part.originalFileName?.substringAfterLast('.', "")?.let { if (it.isEmpty()) "" else ".$it" }
                        ?: ""
                    part.streamProvider().use { stream ->
                        MinIO.getInstance().upload(stream, name)
                    }
                }
                part.contentType.let(::println)
                part.dispose()
            }
            call.request.header(HttpHeaders.ContentType).let(::println)
            val url = Url(
                URLBuilder(
                    protocol = URLProtocol.ELECTRO,
                    pathSegments = listOf("files", name)
                )
            ).toString()
            call.respond(HttpStatusCode.OK, Response(HttpStatusCode.OK.value, "上传成功", url))
        }
        get("/{name}") {
            val filename = call.parameters["name"]!!
            call.respondOutputStream(
                contentLength = MinIO.getInstance().statObject(filename)?.size()
            ) {
                MinIO.getInstance().download(filename)?.use { stream ->
                    stream.copyTo(this)
                }
            }
        }
    }
}