package indi.key.keybot.learn

import com.google.gson.Gson
import indi.key.keybot.util.executeQuerySQL
import indi.key.keybot.util.executeUpdateSQL
import indi.key.keybot.util.initializeDB
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.MessageContent
import java.sql.Connection
import java.util.concurrent.atomic.AtomicLong

data class MessageContentData(
    val type: String,
    val contentJson: String
)

object ChatRecorder {

    private val databaseConnection: Connection = initializeDB("chat_record.db")
    private const val CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS %s (" +
            "TIME INTEGER PRIMARY KEY," +
            "SENDER_ID INTEGER," +
            "MESSAGE_JSON TEXT" +
            ")"
    private const val INSERT_STATEMENT = "INSERT INTO %s (TIME, SENDER_ID, MESSAGE_JSON)" +
            "VALUES (?, ?, ?)"
    private const val SELECT_BY_OFFSET_STATEMENT = "SELECT * FROM %s ORDER BY TIME DESC LIMIT 1 OFFSET %d"

    private fun getTableName(subject: Contact): String {
        val prefix = when (subject) {
            is Group -> "Group"
            is Friend -> "Friend"
            is Member -> "Member"
            else -> "Other"
        }
        return "${prefix}_%d".format(subject.id)
    }

    private fun createTableIfNeed(subject: Contact) {
        databaseConnection.executeUpdateSQL(CREATE_TABLE_STATEMENT.format(getTableName(subject)))
    }

    fun save(messageChain: MessageChain, subject: Contact, senderId: Long) {
        createTableIfNeed(subject)
        val gson = Gson()
        val messageContentDataList = mutableListOf<MessageContentData>()
        messageChain
            .filterIsInstance<MessageContent>()
            .map { MessageContentData(it.javaClass.name, gson.toJson(it)) }
            .let { messageContentDataList.addAll(it) }
        if (messageContentDataList.size != 0) {
            val messageJson = gson.toJson(messageContentDataList)
            databaseConnection.executeUpdateSQL(
                INSERT_STATEMENT.format(getTableName(subject)), arrayOf(
                    System.currentTimeMillis(),
                    senderId,
                    messageJson
                )
            )
        }
    }

    fun queryByOffset(subject: Contact, offset: Int): Pair<Message, Long>? {
        val gson = Gson()
        val time = AtomicLong()
        val result = databaseConnection.executeQuerySQL(
            SELECT_BY_OFFSET_STATEMENT.format(
                getTableName(subject),
                offset
            )
        ) {
            time.set(it.getLong("TIME"))
            gson.fromJson(it.getString("MESSAGE_JSON"), Array<MessageContentData>::class.java)
        }
            .firstOrNull()
            ?.map {
                val clazz = Class.forName(it.type)
                gson.fromJson(it.contentJson, clazz) as MessageContent
            } ?: return null
        val builder = MessageChainBuilder()
        builder.addAll(result)
        return builder.asMessageChain() to time.get()
    }
}