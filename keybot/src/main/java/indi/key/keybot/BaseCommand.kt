package indi.key.keybot

import indi.key.keybot.learn.ForgetCommand
import indi.key.keybot.learn.LearnCommand
import indi.key.keybot.math.CalculateCommand
import indi.key.keybot.question.AnswerCommand
import indi.key.keybot.question.RequestCommand
import indi.key.keybot.question.SkipCommand
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent

abstract class BaseCommand {
    abstract val command: String
    abstract val help: String
    open val postFixHelp: String = ""
    open val showHelp: Boolean = true
    open val willUseEnvironment: Boolean = true
    abstract suspend fun process(messageEvent: MessageEvent, master: Group, environment: Environment, arguments: String)

    override fun toString() = "【${toStringNoQuotes()}】"

    fun toStringNoQuotes() = "！$command" + (if (postFixHelp.isEmpty()) {
        ""
    } else {
        " $postFixHelp"
    })
}

val COMMANDS = listOf(
    RequestCommand,
    AnswerCommand,
    SkipCommand,
    CalculateCommand,
    LearnCommand,
    ForgetCommand,
    PingCommand,
    HelpCommand
)

