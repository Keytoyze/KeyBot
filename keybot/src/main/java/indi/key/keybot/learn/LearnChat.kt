package indi.key.keybot.learn

import indi.key.keybot.BaseCommand
import indi.key.keybot.Environment
import indi.key.keybot.util.sendMessageSafely
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent

object LearnChat : BaseCommand() {
    override val command = "学习聊天"
    override val help = "让机器人看到前面第a条消息的时候，回复前面第b条消息。支持表情和图片哦！"
    override val postFixHelp = "[a b]"
    override val showHelp = false

    private suspend fun onFailed(contact: Contact, environment: Environment) {
        contact.sendMessageSafely(environment, "格式错误！用法：${LearnChat}。" +
                "让机器人看到前面第a条消息的时候，回复前面第b条消息。支持表情和图片哦！")
    }

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        if (!arguments.contains(" ")) {
            onFailed(messageEvent.subject, environment)
            return
        }
        val elements = arguments.split(" ", limit = 2)
        val a = elements[0].toIntOrNull()
        val b = elements[1].toIntOrNull()
        if (a == null || b == null) {
            onFailed(messageEvent.subject, environment)
            return
        }
        val key = ChatRecorder.queryByOffset(messageEvent.subject, a)
        val value = ChatRecorder.queryByOffset(messageEvent.subject, b)
        // TODO: download
        if (key == null || value == null) {
            messageEvent.subject.sendMessageSafely(environment, "消息太久远啦！我记不清惹～")
            return
        }
        if (environment.learnMap == null) {
            environment.learnMap = hashMapOf()
        }
        environment.learnMap!!["##"]
    }
}