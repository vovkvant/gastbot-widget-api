package com.gastbot.widget.model

import com.gastbot.widget.BusinessException
import java.util.*

object MessageConst {
    const val DEFAULT_AVAILABLE_MESSAGES = 100
}

data class ChatMessageRequest(
    val chatId: Long?,
    val promptId: UUID?,
    val history: List<Message> = listOf(),
    val userMessage: String
) {
    companion object {
        val MAX_USER_MESSAGE_LENGTH = 200

        // TODO replace with validation exception, annotations???
        fun validateChatMessageRequest(chatMessageRequest: ChatMessageRequest) {
            if (chatMessageRequest.userMessage.isEmpty()) {
                throw BusinessException("Messages cannot be empty")
            }

            if(chatMessageRequest.chatId == null && chatMessageRequest.promptId == null) {
                throw BusinessException("Identifier of dialog should be provided")
            }

            chatMessageRequest.history.forEach {
                if (it.role != "user" && it.role != "bot") {
                    throw BusinessException("Invalid message role: ${it.role}")
                }
                if (it.content.isNullOrBlank()) {
                    throw BusinessException("Message content cannot be blank")
                }
                if (it.role == Message.USER && it.content.length > MAX_USER_MESSAGE_LENGTH) {
                    throw BusinessException("Message content is too long")
                }
            }
        }
    }
}

data class Message(
    val role: String?,
    val content: String?
) {
    companion object Role {
        const val BOT = "bot"
        const val USER = "user"
    }
}

enum class MessageSource {
    ADMIN,
    WIDGET
}

data class ChatDialogHeader(
    val source: String,
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val timeTook: Long
)

data class ChatDialog(
    val userMessage: String,
    val replyMessage: String
)

data class OpenAiResponse(
    val header: ChatDialogHeader,
    val payload: ChatDialog
)

data class ChatMessageResponse(
    val messageId: Long,
    val dialog: ChatDialog
)

data class LikeRequest(
    val messageId: Long
)

data class FirstMessageResponse(
    val firstMessage: String
)

data class InitialSettings(
    val isOn: Boolean,
    val firstMessage: String,
    val backgroundColor: String,
    val textColor: String,
    val bubbleColor: String,
    val bubbleTextColor: String
)