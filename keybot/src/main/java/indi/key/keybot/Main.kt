package indi.key.keybot

import com.google.gson.Gson
import indi.key.keybot.learn.ChatRecorder
import indi.key.keybot.learn.ResponseFromLearnCommand
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.join
import net.mamoe.mirai.message.MessageEvent
import java.io.File
import java.util.*

val locks = Hashtable<Long, Mutex>()

fun getLock(subject: Contact): Mutex {
    if (subject.id !in locks) {
        locks[subject.id] = Mutex()
    }
    return locks[subject.id]!!
}

fun getFile(subject: Contact): File {
    val parent = File("data")
    if (!parent.isDirectory) {
        parent.mkdirs()
    }
    val file = File(parent, "${subject.id}.json")
    return file
}

@Suppress("UNCHECKED_CAST")
suspend fun process(command: BaseCommand, messageEvent: MessageEvent, master: Group, arguments: String) {
    if (command.willUseEnvironment) {
        getLock(messageEvent.subject).withLock {
            realProcess(command, messageEvent, master, arguments)
        }
    } else {
        realProcess(command, messageEvent, master, arguments)
    }
}

suspend fun realProcess(command: BaseCommand, messageEvent: MessageEvent, master: Group, arguments: String) {
    val file = getFile(messageEvent.subject)
    // load data
    val environment = if (!file.exists() || !command.willUseEnvironment) {
        Environment()
    } else {
        Gson().fromJson(file.readText(), Environment::class.java) as Environment
    }

    command.process(messageEvent, master, environment, arguments.trim())

    // store data if command will change environment
    if (command.willUseEnvironment) {
        file.writeText(Gson().toJson(environment))
    }
}

suspend fun main() {
    initEnvironment()

    val userInfo = Environment.userInfo

    val bot = Bot(
        userInfo.qq,
        userInfo.password
    ) {
        fileBasedDeviceInfo("device.json")
    }.alsoLogin()

    val master = bot.groups.first { it.id == userInfo.masterGroup }
    println("[master] $master")

    COMMANDS.forEach { command ->
        val regex = Regex("(!|！)\\s*${command.command}\\s*(.*)\\s*")
        bot.subscribeFriendMessages {
            regex matching { content ->
                val arguments = regex.matchEntire(content)!!.groupValues[2]
                process(command, this, master, arguments)
            }
        }
        bot.subscribeGroupMessages {
            regex matching { content ->
                val arguments = regex.matchEntire(content)!!.groupValues[2]
                process(command, this, master, arguments)
            }
        }
    }

    bot.subscribeGroupMessages {
        atBot {
            process(HelpCommand, this, master, "")
        }
    }

    bot.subscribeMessages {
        always {
            println("[subject] ${subject}")
            println("[sender] ${sender}")
            println("[message] (${message.javaClass.name}) $message")
            println("[messageContent] (${message.javaClass.name}) ${message.contentToString()}")
        }

        always {
            if (!this.message.contentToString().startsWith("!") &&
                !this.message.contentToString().startsWith("！") &&
                this.sender.id != userInfo.qq
            ) {
                process(ResponseFromLearnCommand, this, master, this.message.contentToString())
            }
        }

        always {
            ChatRecorder.save(message, subject, sender.id)
        }
    }

    bot.join()
}

private fun initEnvironment() {
    Environment.userInfo = Gson().fromJson(File("user.json").readText(), UserInfo::class.java)
    val raw = Gson().fromJson(
        File("repository", "answer.json").readText(), Array<AnswerEntity>::class.java
    ) as Array<AnswerEntity>
    Environment.answer = hashMapOf<String, AnswerEntity>().apply {
        raw.forEach {
            this[it.file] = it
        }
    }
}