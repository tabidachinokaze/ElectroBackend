package moe.tabidachi

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.minio.*
import io.minio.http.Method
import java.io.FileInputStream
import java.io.InputStream

class MinIO(
    url: String = "http://localhost:9000",
    accessKey: String = ACCESS_KEY,
    secretKey: String = SECRET_KEY
) {
    val client = MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build()
    fun checkOrCreateBucket(name: String): Boolean {
        return when (client.bucketExists(BucketExistsArgs.builder().bucket(name).build())) {
            true -> true
            false -> {
                return kotlin.runCatching {
                    client.makeBucket(MakeBucketArgs.builder().bucket(name).build())
                    true
                }.getOrElse {
                    false
                }
            }
        }
    }

    fun upload(inputStream: InputStream, filename: String): String {
        if (!checkOrCreateBucket(ELECTRO_BUCKET)) {
            throw Exception("未创建bucket")
        }
        return client.putObject(
            PutObjectArgs.builder()
                .bucket(ELECTRO_BUCKET)
                .stream(inputStream, -1, 1_073_741_824)
                .`object`(filename)
                .build()
        ).`object`()
    }

    fun download(filename: String): GetObjectResponse? {
        return client.getObject(
            GetObjectArgs.builder()
                .bucket(ELECTRO_BUCKET)
                .`object`(filename)
                .build()
        )
    }

    fun statObject(filename: String): StatObjectResponse? {
        return client.statObject(
            StatObjectArgs.builder()
                .bucket(ELECTRO_BUCKET)
                .`object`(filename)
                .build()
        )
    }

    companion object {
        const val ACCESS_KEY = "353ABDYlJcsRVMKG"
        const val SECRET_KEY = "ElAxUZXuXJ8Vrxyc4UkQWTPA9DzHX714"
        private val minio = MinIO()
        const val ELECTRO_BUCKET = "electro"
        fun getInstance(): MinIO = minio
    }
}

suspend fun main() {
    val minio = MinIO.getInstance()
    minio.checkOrCreateBucket("test")
    val url = minio.client.getPresignedObjectUrl(
        GetPresignedObjectUrlArgs.builder()
            .bucket("test")
            .`object`("balala.jpeg")
            .method(Method.PUT)
            .build()
    )
    val ktor = HttpClient(CIO) {

    }
    FileInputStream("C:/Users/kaze/Desktop/3b3eb89b51b0995f8e065547a56aedc81626046392801.jpeg").use { fis ->
        runCatching {
            ktor.put {
                url(url)
                setBody(
                    fis.readBytes()
                )
            }
        }.onSuccess {
            println(it.status)
        }
    }
}
