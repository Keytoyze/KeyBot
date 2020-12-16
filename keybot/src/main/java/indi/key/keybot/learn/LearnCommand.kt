package indi.key.keybot.learn

import indi.key.keybot.BaseCommand
import indi.key.keybot.Environment
import indi.key.keybot.util.sendMessageSafely
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent

object LearnCommand : BaseCommand() {
    override val command: String = "学习"
    override val help: String = "让机器人学习一个关键词！"
    override val postFixHelp: String = "[关键词] [要我说的话]"

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        if (!arguments.contains(" ")) {
            messageEvent.subject.sendMessageSafely(environment, "格式错误：用法：【！学习 [关键词] [要我说的话]】。不要忘记加空格了哦！")
            return
        }
        val elements = arguments.split(" ", limit = 2)
        val keyword = elements[0]
        val value = elements[1]
        if (environment.learnMap == null) {
            environment.learnMap = hashMapOf()
        }
        environment.learnMap!![keyword] = value
        messageEvent.subject.sendMessageSafely(environment, "学习成功！对我说 $keyword 试试吧！")
    }
}

object ForgetCommand : BaseCommand() {
    override val command: String = "忘记"
    override val help: String = "让机器人忘记刚刚学到的关键词"
    override val postFixHelp: String = "[关键词]"

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        if (environment.learnMap == null) {
            environment.learnMap = hashMapOf()
        }
        if (arguments !in environment.learnMap!!) {
            messageEvent.subject.sendMessageSafely(environment, "咦？我好像还没学会这个关键词呢～快对我用指令${LearnCommand}试试吧！")
        } else {
            environment.learnMap!!.remove(arguments)
            messageEvent.subject.sendMessageSafely(environment, "我已经忘记啦！")
        }
    }
}

object ResponseFromLearnCommand : BaseCommand() {
    override val command: String
        get() = ""
    override val help: String
        get() = ""
    override val postFixHelp: String
        get() = ""
    override val showHelp: Boolean = false

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        val content = environment.learnMap
            ?.filter { it.key in arguments }
            ?.map { it.value }
        if (!content.isNullOrEmpty()) {
            content.random()
                .let { response ->
                    messageEvent.subject.sendMessageSafely(environment, response)
                }
        }
    }
}