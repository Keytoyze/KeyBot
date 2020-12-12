package indi.key.keybot

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.utils.toExternalImage
import java.io.File

suspend fun File.uploadImageSafely(master: Contact, messageEvent: MessageEvent): Image {
    val externalImage = toExternalImage()
    master.sendMessage(master.uploadImage(externalImage))
    return messageEvent.subject.uploadImage(externalImage)
}