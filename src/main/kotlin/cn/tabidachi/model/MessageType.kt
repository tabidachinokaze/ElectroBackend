package cn.tabidachi.model

import kotlinx.serialization.Serializable

@Serializable
class MessageType(
    val type: String, val subtype: String
) {
    override fun equals(other: Any?): Boolean {
        return other is MessageType &&
                type.equals(other.type, ignoreCase = true) &&
                subtype.equals(other.subtype, ignoreCase = true)
    }

    override fun hashCode(): Int = type.lowercase().hashCode() + subtype.lowercase().hashCode()

    override fun toString(): String = "$type/$subtype"

    fun match(pattern: MessageType): Boolean {
        return equals(pattern)
    }

    companion object {
        fun parse(value: String): MessageType {
            val slash = value.indexOf('/')
            if (slash == -1) {
                return Unknown
            }
            val type = value.substring(0, slash).trim()
            if (type.isEmpty()) {
                return Unknown
            }
            val subtype = value.substring(slash + 1).trim()
            if (type.contains(' ') || subtype.contains(' ')) {
                return Unknown
            }
            if (subtype.isEmpty() || subtype.contains('/')) {
                return Unknown
            }
            return MessageType(type, subtype)
        }
        val Unknown = MessageType("*", "*")
    }

    object Dialog {
        val New = MessageType("dialog", "new")
    }
    object Message {
        val New = MessageType("message", "new")
        val Delete = MessageType("message", "delete")
        val Update = MessageType("message", "update")
    }

    object WebRTC {
        val Any = MessageType("webrtc", "*")
        val Request = MessageType("webrtc", "request")
        val Response = MessageType("webrtc", "response")
        val Command = MessageType("webrtc", "command")
        val Offer = MessageType("webrtc", "offer")
        val Answer = MessageType("webrtc", "answer")
        val End = MessageType("webrtc", "end")
        val Ice = MessageType("webrtc", "ice")
    }

    object OnlineStatus {
        val Status = MessageType("online_status", "status")
        val Listen = MessageType("online_status", "listen")
        val Unlisten = MessageType("online_status", "unlisten")
    }
}