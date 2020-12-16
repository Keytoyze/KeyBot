package indi.key.keybot

import com.google.gson.Gson
import indi.key.keybot.math.CalculateCommand
import net.mamoe.mirai.Bot
import net.mamoe.mirai.LowLevelAPI
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.data.MemberInfo
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.Voice
import net.mamoe.mirai.utils.ExternalImage
import java.io.File
import java.io.InputStream
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

suspend fun main() {
    Environment.userInfo = Gson().fromJson(File("KeyBot-repository/user.json").readText(), UserInfo::class.java)
    CalculateCommand.process(DummyMessageEvent(), DummyGroup(), Environment(), "sin(x)^cos(x)")
}

class DummyContact : Contact() {
    override val bot: Bot
        get() = TODO("Not yet implemented")
    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")
    override val id: Long
        get() = TODO("Not yet implemented")

    override suspend fun sendMessage(message: Message): MessageReceipt<Contact> {
        println("send message: \n${message}")
        println("send success!")
        exitProcess(0)
    }

    override fun toString(): String {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImage(image: ExternalImage): Image {
        TODO("Not yet implemented")
    }
}

class DummyMessageEvent : MessageEvent() {
    override val bot: Bot
        get() = TODO("Not yet implemented")
    override val message: MessageChain
        get() = TODO("Not yet implemented")
    override val sender: User
        get() = TODO("Not yet implemented")
    override val senderName: String
        get() = TODO("Not yet implemented")
    override val subject: Contact
        get() = DummyContact()
    override val time: Int
        get() = TODO("Not yet implemented")
}

class DummyGroup : Group() {
    override val bot: Bot
        get() = TODO("Not yet implemented")
    override val botAsMember: Member
        get() = TODO("Not yet implemented")
    override val botMuteRemaining: Int
        get() = TODO("Not yet implemented")
    override val botPermission: MemberPermission
        get() = TODO("Not yet implemented")
    override val id: Long
        get() = TODO("Not yet implemented")
    override val members: ContactList<Member>
        get() = TODO("Not yet implemented")
    override var name: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override val owner: Member
        get() = TODO("Not yet implemented")
    override val settings: GroupSettings
        get() = TODO("Not yet implemented")

    override fun contains(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(id: Long): Member {
        TODO("Not yet implemented")
    }

    override fun getOrNull(id: Long): Member? {
        TODO("Not yet implemented")
    }

    @LowLevelAPI
    override fun newMember(memberInfo: MemberInfo): Member {
        TODO("Not yet implemented")
    }

    override suspend fun quit(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(message: Message): MessageReceipt<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImage(image: ExternalImage): Image {
        TODO("Not yet implemented")
    }

    override suspend fun uploadVoice(input: InputStream): Voice {
        TODO("Not yet implemented")
    }

    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")

    override fun toString(): String {
        TODO("Not yet implemented")
    }
}