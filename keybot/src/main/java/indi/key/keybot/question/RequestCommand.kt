package indi.key.keybot.question

import indi.key.keybot.BaseCommand
import indi.key.keybot.Environment
import indi.key.keybot.uploadImageSafely
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.sendTo

object RequestCommand : BaseCommand() {

    override val command = "做题"
    override val help = "随机出一道选择题或填空题"

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        if (environment.currentQuestion != null) {
            environment.getCurrentQuestionFile().uploadImageSafely(master, messageEvent).sendTo(messageEvent.subject)
            (PlainText("上一题还没回答哦！题目见上。\n若想放弃此题直接查看答案，请发送指令：$SkipCommand")).sendTo(messageEvent.subject)
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

        val file = environment.getCurrentQuestionFile()
        val image = file.uploadImageSafely(master, messageEvent)
        image.sendTo(messageEvent.subject)
        PlainText("做出题目后，请发送指令：${AnswerCommand}。回答正确可以得一分！").sendTo(messageEvent.subject)
    }
}