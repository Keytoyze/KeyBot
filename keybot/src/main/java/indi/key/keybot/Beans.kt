package indi.key.keybot

import com.google.gson.annotations.SerializedName
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageContent
import net.mamoe.mirai.message.data.PlainText

@Suppress("UNCHECKED_CAST")
data class Environment(
    var currentQuestion: String? = null,
    var rankingList: HashMap<Long, Pair<String, Double>> = hashMapOf(),
    var visitedQuestion: MutableList<String>? = arrayListOf(),
    var currentErrorCount: Int? = 0,

    var learnMap: HashMap<String, String>? = hashMapOf(),

    var faultCountMap: HashMap<Long, Int>? = hashMapOf(),

    var lastSendTime: Long? = null,
    var abandonInterval: Long? = null
) {

    companion object {
        lateinit var answer: Map<String, AnswerEntity>

        lateinit var userInfo: UserInfo

        fun constructAt(user: User): MessageContent {
            return if (user is Member) {
                At(user)
            } else {
                PlainText("@${user.nick} ")
            }
        }
    }
}

data class UserInfo(
    @SerializedName("qq")
    val qq: Long,
    @SerializedName("password")
    val password: String,
    @SerializedName("master_group")
    val masterGroup: Long,
    @SerializedName("wolframalpha_appid")
    val wolframAlphaAppId: String
)

data class AnswerEntity(
    @SerializedName("file")
    val file: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("double")
    val double: Double?,
    @SerializedName("list")
    val list: List<String>?,
)