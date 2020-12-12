package indi.key.keybot.question

import indi.key.keybot.BaseCommand
import indi.key.keybot.Environment
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.PlainText

object SkipCommand : BaseCommand() {

    override val command = "查看答案"
    override val help = "查看最近一题的答案。查看完就不能再答题了呢。"

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        environment.checkQuestion(messageEvent)?.let { answer ->
            environment.currentQuestion = null
            messageEvent.subject.sendMessage(
                PlainText(
                    "题目答案：${answer.text}。很遗憾，没有人回答出来。再接再厉哦！\n" +
                            "发送指令：${RequestCommand}，即可再做一道题目～\n\n当前排行榜：\n${environment.toRankingList()}"
                )
            )
        }
    }
}