package indi.key.keybot.question

import indi.key.keybot.BaseCommand
import indi.key.keybot.Environment
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.PlainText
import kotlin.math.abs

object AnswerCommand : BaseCommand() {

    override val command = "回答"
    override val postFixHelp = "答案"
    override val help = "回答机器人出的题目。注意不要随便乱答哦！也不要通过搜题来投机取巧~"

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        val answer = environment.checkQuestion(messageEvent) ?: return
        val correct = when (answer.type) {
            "fraction" -> {
                abs(arguments.toDoubleOrNull() ?: Double.NaN - answer.double!!) < 1e-7
                        || compareList(
                    arguments,
                    answer.list!!,
                    "/"
                )
            }
            "double" -> {
                abs(arguments.toDoubleOrNull() ?: Double.NaN - answer.double!!) < 1e-7
            }
            "or" -> {
                compareList(
                    arguments.split("或").sorted().joinToString(","), answer.list!!.sorted()
                )
            }
            "list" -> {
                compareList(arguments, answer.list!!)
            }
            else -> {
                arguments.equals(answer.text, ignoreCase = true)
            }
        }
        val id = messageEvent.sender.id
        val name = messageEvent.sender.nick
        if (correct) {
            environment.incrementQuestionScore(id, name, 1.0)
            messageEvent.subject.sendMessage(
                PlainText("回答正确，恭喜") + Environment.constructAt(messageEvent.sender)
                        + PlainText("得一分！\n发送指令：${RequestCommand}，即可再做一道题目～\n\n当前排行榜：\n${environment.toRankingList()}")
            )
            if (environment.visitedQuestion != null) {
                environment.visitedQuestion = arrayListOf()
            }
            environment.visitedQuestion!!.add(environment.currentQuestion!!)
            environment.currentQuestion = null
        } else {
            environment.incrementQuestionScore(id, name, -0.3)
            messageEvent.subject.sendMessage(
                Environment.constructAt(messageEvent.sender)
                        + "回答错误，扣0.3分 TAT 再想想呢～\n若想放弃此题直接查看答案，请发送指令：$SkipCommand"
            )
        }
    }

    private fun compareList(response: String, answers: List<String>, divider: String? = null): Boolean {
        return if (divider != null) {
            response == answers.joinToString(divider)
        } else {
            (compareList(response, answers, " ")
                    || compareList(response, answers, ",")
                    || compareList(response, answers, ", ")
                    || compareList(response, answers, ""))
        }
    }

}