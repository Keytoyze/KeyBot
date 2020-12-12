package indi.key.keybot.question

import indi.key.keybot.AnswerEntity
import indi.key.keybot.Environment
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.sendTo
import java.io.File
import kotlin.math.roundToInt

internal fun Environment.toRankingList() = rankingList.entries
    .asSequence()
    .filter { it.value.second > 0 }
    .sortedByDescending { it.value.second }
    .take(10)
    .mapIndexed { index, mutableEntry ->
        "No.${index + 1} ${mutableEntry.value.first}: ${
            String.format(
                "%.1lf",
                mutableEntry.value.second
            )
        } 分"
    }
    .joinToString("\n")

internal fun Environment.getCurrentQuestionFile(): File {
    return File("repository", currentQuestion!!)
}

internal fun Environment.incrementQuestionScore(id: Long, name: String, increment: Double) {
    var oldScore = 0.0
    if (id in rankingList) {
        oldScore = rankingList[id]!!.second
    }
    rankingList[id] = name to (oldScore + increment)
}

internal suspend fun Environment.checkQuestion(messageEvent: MessageEvent): AnswerEntity? {
    val currentQuestion = currentQuestion
    if (currentQuestion == null) {
        PlainText("当前还没有问题呢。请发送指令：${RequestCommand}，即可获取一道题目～").sendTo(messageEvent.subject)
        return null
    }
    val answers = Environment.answer
    val answer = answers[currentQuestion]
    if (answer == null) {
        PlainText("发生错误！").sendTo(messageEvent.subject)
        return null
    }
    return answer
}