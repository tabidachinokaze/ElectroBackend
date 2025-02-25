package moe.tabidachi.system

import jakarta.mail.Message
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import java.util.*

class ElectroEmail(
    private val host: String,
    private val port: String,
    private val username: String,
    private val password: String,
) {
    private val session = Session.getInstance(
        Properties().apply {
            put("mail.smtp.host", host)
            put("mail.smtp.port", port.toInt())
            put("mail.smtp.ssl.enable", true)
        }
    )

    fun sendSecurityCode(
        recipient: String,
        code: String,
        block: MimeMessage.() -> Unit = {}
    ): Result<Unit> {
        return kotlin.runCatching {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(username, "Electro"))
                setRecipients(Message.RecipientType.TO, recipient)
                subject = String.format(TEMPLATE_TITLE, code)
                setText(String.format(TEMPLATE_CONTENT_LOGIN, code, 15))
                this.block()
            }
            Transport.send(message, username, password)
        }
    }

    companion object {
        const val TEMPLATE_TITLE = "%s 是你的 Electro 验证码"
        const val TEMPLATE_CONTENT_LOGIN = "%s（验证码用于注册）%s分钟内有效，如非本人操作请忽略。"
    }
}