package indi.key.keybot

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent

object PingCommand : BaseCommand() {
    override val command: String = "ping"
    override val help: String = "pong！"

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        messageEvent.subject.sendMessageSafely(environment,
            Environment.constructAt(messageEvent.sender) +
            " pong!"
        )
    }
}