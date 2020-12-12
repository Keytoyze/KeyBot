package indi.key.keybot.question

import indi.key.keybot.BaseCommand
import indi.key.keybot.Environment
import indi.key.keybot.uploadImageSafely
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.PlainText

object RequestCommand : BaseCommand() {

    override val command = "做题"
    override val help = "来做一道紧张刺激的高考题目"

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        if (environment.currentQuestion != null) {
            messageEvent.subject.sendMessage(
                environment.getCurrentQuestionFile().uploadImageSafely(master, messageEvent) +
                        PlainText("上一题还没回答哦！题目见上。\n若想放弃此题直接查看答案，请发送指令：$SkipCommand")
            )
            return
        }
        if (environment.visitedQuestion == null) {
            environment.visitedQuestion = arrayListOf()
        }
        val totalQuestions = Environment.answer.keys.toMutableSet()
        if (totalQuestions.size <= environment.visitedQuestion!!.size) {
            messageEvent.subject.sendMessage("题库已经做光啦！稍等片刻，船新题目马上奉上！")
            return
        }

        totalQuestions.removeAll(environment.visitedQuestion!!)

        val currentQuestion = totalQuestions.random()
        environment.currentQuestion = currentQuestion

        messageEvent.subject.sendMessage(
            environment.getCurrentQuestionFile().uploadImageSafely(master, messageEvent) +
                    PlainText("做出题目后，请发送指令：${AnswerCommand}。第一个回答正确者可以得一分！")
        )
    }
}
