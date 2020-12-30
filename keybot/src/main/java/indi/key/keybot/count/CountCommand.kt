package indi.key.keybot.count

import indi.key.keybot.BaseCommand
import indi.key.keybot.Environment
import indi.key.keybot.util.sendMessageSafely
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.At
import kotlin.math.max

abstract class BaseFaultCommand : BaseCommand() {

    override val postFixHelp = "@某人/QQ号"
    override val showHelp = true

    abstract val requirePermission: Boolean
    abstract fun sendOnParseQQNumber(qqNumber: Long, countMap: HashMap<Long, Int>): String

    override suspend fun process(
        messageEvent: MessageEvent,
        master: Group,
        environment: Environment,
        arguments: String
    ) {
        val subject = messageEvent.subject
        val sender = messageEvent.sender
        if (subject !is Group || sender !is Member) {
            subject.sendMessageSafely(environment, "该功能只对群开放！")
            return
        }
        if (requirePermission && sender.permission == MemberPermission.MEMBER) {
            subject.sendMessageSafely(environment, "该功能只对群主或管理员开放！")
            return
        }
        if (environment.faultCountMap == null) {
            environment.faultCountMap = HashMap()
        }
        val message = messageEvent.message
        val qqNumber: Long?
        if (message.size == 2) {
            // QQ number
            qqNumber = arguments.toLongOrNull()
        } else {
            // At
            val at = message[2]
            if (at !is At) {
                subject.sendMessageSafely(environment, "格式错误！用法：${this}")
                return
            }
            qqNumber = at.target
        }
        if (qqNumber == null) {
            subject.sendMessageSafely(environment, "格式错误！用法：${this}")
            return
        }
        environment.faultCountMap?.let { map ->
            subject.sendMessageSafely(environment, sendOnParseQQNumber(qqNumber, map))
        }
    }
}

object IncreaseFaultCommand: BaseFaultCommand() {
    override val command = "记过"
    override val help = "记过一次（需要权限）"
    override val requirePermission = true

    override fun sendOnParseQQNumber(qqNumber: Long, countMap: HashMap<Long, Int>): String {
        countMap[qqNumber] = max(0, countMap.getOrDefault(qqNumber, 0) + 1)
        return "记过成功！该成员当前记过次数：${countMap[qqNumber]}"
    }
}

object DecreaseFaultCommand: BaseFaultCommand() {
    override val command = "取消记过"
    override val help = "取消一次记过（需要权限）"
    override val requirePermission = true

    override fun sendOnParseQQNumber(qqNumber: Long, countMap: HashMap<Long, Int>): String {
        countMap[qqNumber] = max(0, countMap.getOrDefault(qqNumber, 0) - 1)
        return "取消成功！该成员当前记过次数：${countMap[qqNumber]}"
    }
}

object ViewFaultCommand : BaseFaultCommand() {
    override val command = "查看记过"
    override val help = "查看该成员的过错次数（无需权限）"
    override val requirePermission = false

    override fun sendOnParseQQNumber(qqNumber: Long, countMap: HashMap<Long, Int>): String {
        return "该成员当前记过次数：${countMap[qqNumber] ?: 0}"
    }
}