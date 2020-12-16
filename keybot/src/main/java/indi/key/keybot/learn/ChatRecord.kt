package indi.key.keybot.learn

import com.google.gson.Gson
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageContent
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.concurrent.thread

data class MessageContentData(
    val type: String,
    val contentJson: String
)

object ChatRecorder {

    private val databaseConnection: Connection
    private const val CREATE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS %s (" +
            "TIME INTEGER PRIMARY KEY," +
            "SENDER_ID INTEGER," +
            "MESSAGE_JSON TEXT" +
            ")"
    private const val INSERT_STATEMENT = "INSERT INTO %s (TIME, SENDER_ID, MESSAGE_JSON)" +
            "VALUES (?, ?, ?)"

    init {
        println("Initialize DB start")
        val path = "chat_record.db"
        Class.forName("org.sqlite.JDBC")
        databaseConnection = DriverManager.getConnection("jdbc:sqlite:$path")
        Runtime.getRuntime().addShutdownHook(
            thread(start = false) {
                println("Close DB start")
                databaseConnection.close()
                println("Close DB success")
            }
        )
        println("Initialize DB end")
    }

    private fun getTableName(subject: Contact): String {
        val prefix = when (subject) {
            is Group -> "Group"
            is Friend -> "Friend"
            is Member -> "Member"
            else -> "Other"
        }
        return "${prefix}_%d".format(subject.id)
    }

    private fun executeSQL(statement: String, placeHolder: Array<Any> = emptyArray()) {
        println("Execute: $statement, placeHolder: ${Arrays.toString(placeHolder)}")
        val preparedStatement = databaseConnection.prepareStatement(statement)
        placeHolder.forEachIndexed { index, param ->
            when (param) {
                is Long -> preparedStatement.setLong(index + 1, param)
                is String -> preparedStatement.setString(index + 1, param)
                else -> error("Unknown type: ${param.javaClass.name}")
            }
        }
        val result = preparedStatement.executeUpdate()
        println("Result: $result")
    }

    private fun createTableIfNeed(subject: Contact) {
        executeSQL(CREATE_TABLE_STATEMENT.format(getTableName(subject)))
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
            executeSQL(
                INSERT_STATEMENT.format(getTableName(subject)), arrayOf(
                    System.currentTimeMillis(),
                    senderId,
                    messageJson
                )
            )
        }
    }
}