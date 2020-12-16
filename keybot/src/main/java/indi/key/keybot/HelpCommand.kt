package indi.key.keybot

import indi.key.keybot.util.sendMessageSafely
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent

object HelpCommand : BaseCommand() {
    override val command: String = "帮助"
    override val help: String = "查看帮助"

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        messageEvent.subject.sendMessageSafely(environment,
            "指令大全：\n" +
                    COMMANDS.filter { it.showHelp }.map {
                        "【${it.toStringNoQuotes()}】${it.help}"
                    }
                        .joinToString("\n") + "\n直接输入感叹号+指令，就能召唤我了哦！"
        )
    }
}