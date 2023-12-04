package cn.tabidachi.database.dao

import cn.tabidachi.database.model.Session
import cn.tabidachi.database.table.SessionTable

interface ChatDao {
    fun saveChat(
        type: SessionTable.SessionType,
        title: String? = null,
        description: String? = null,
        image: String? = null,
        isPublic: Boolean = false,
        needRequest: Boolean = true
    ): Session
    fun removeChat(sid: Long): Boolean
    fun updateChat(
        sid: Long,
        title: String? = null,
        description: String? = null,
        image: String? = null,
        isPublic: Boolean? = null,
        needRequest: Boolean? = null
    ): Boolean
    fun findByChatId(sid: Long): Session?
    fun findByChatIdAndType(sid: Long, type: SessionTable.SessionType): Session?
}