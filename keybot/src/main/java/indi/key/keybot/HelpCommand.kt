package indi.key.keybot

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.PlainText

object HelpCommand : BaseCommand() {
    override val command: String = "帮助"
    override val help: String = "查看帮助"

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        messageEvent.subject.sendMessage(
            PlainText("帮助：\n") +
                    COMMANDS.map {
                        it.toStringNoQuotes()
                    }
                        .joinToString("\n") + "\n直接输入消息就能召唤我了哦！"
        )
    }
}