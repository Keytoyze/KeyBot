package indi.key.keybot.learn

import indi.key.keybot.BaseCommand
import indi.key.keybot.Environment
import indi.key.keybot.util.sendMessageSafely
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent

object RepeatCommand : BaseCommand() {
    override val command: String = "复读"
    override val help: String = "复读前面的第n条消息"
    override val postFixHelp = "[n]"
    override val showHelp: Boolean = true

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        val number = arguments.toIntOrNull()
        if (number == null) {
            messageEvent.subject.sendMessageSafely(environment, "格式错误！用法：$this")
            return
        }
        val messageWithTime = ChatRecorder.queryByOffset(messageEvent.subject, number)
        if (messageWithTime == null) {
            messageEvent.subject.sendMessageSafely(environment, "消息太久远啦！我忘记了，复读不到惹～")
        } else {
            messageEvent.subject.sendMessageSafely(environment, messageWithTime.first)
        }
    }
}