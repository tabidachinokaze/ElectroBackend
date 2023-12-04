package moe.tabidachi

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message

object ElectroFirebaseAdmin {
    init {
        kotlin.runCatching {
            val resource = this::class.java.getResource("/service-account.json")
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource!!.openStream()))
                .build()

            FirebaseApp.initializeApp(options)
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun buildMessage(
        token: String,
        src: Long,
        dst: Long,
        image: String,
        username: String
    ): Message {
        return Message.builder()
            .putData("type", "call")
            .putData("src", src.toString())
            .putData("dst", dst.toString())
            .putData("image", image)
            .putData("username", username)
            .setToken(token)
            .build()
    }

    fun sendMessage(message: Message): String {
        val response = FirebaseMessaging.getInstance().send(message)
        println("ElectroFirebaseAdmin: response $response")
        return response
    }
}