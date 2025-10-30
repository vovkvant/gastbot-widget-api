package com.gastbot.widget

import com.gastbot.widget.model.*
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ChatbotWidgetService(
    private val integrationDao: IntegrationDao,
    private val messageDao: MessageDao,
    private val openAiService: OpenAiService
) {

    // TODO  add caching for integration
    fun processMessage(
        apiKey: String,
        widgetChatMessage: ChatMessageRequest
    ): ChatMessageResponse {
        // TODO limit number of messages per randomId
        // TODO rate limiter here is required!!!!!
        ChatMessageRequest.Companion.validateChatMessageRequest(widgetChatMessage)
        if (widgetChatMessage.chatId == null) {
            throw BusinessException("chatId should be provided")
        }
        val integrationAndPromptList = integrationDao.getItAndPromptByApiKey(apiKey)

        if (integrationAndPromptList.isNotEmpty()) {
            val integrationAndPrompt = integrationAndPromptList.first()
            val period = LocalDate.now().withDayOfMonth(1)
            val countOfMessages = messageDao.getCountOfMessages(integrationAndPrompt.userId, period)
            if (countOfMessages > MessageConst.DEFAULT_AVAILABLE_MESSAGES) {
                throw BusinessException("You have reached the message limit")
            }
            val openAiResponse = openAiService.sendMessage(
                integrationAndPrompt.prompt,
                widgetChatMessage,
                MessageSource.WIDGET
            )

            val messageId = messageDao.createMessage(
                openAiResponse = openAiResponse,
                userId = integrationAndPrompt.userId,
                integrationId = integrationAndPrompt.id,
                chatId = widgetChatMessage.chatId
            )

            messageDao.incrementCounter(integrationAndPrompt.id, integrationAndPrompt.userId)
            return ChatMessageResponse(
                messageId = messageId,
                dialog = openAiResponse.payload
            )
        } else {
            throw BusinessException("No such integration")
        }
    }

    fun getFirstMessage(apiKey: String): FirstMessageResponse {
        val integrationAndPromptList = integrationDao.getItAndPromptByApiKey(apiKey)
        if (integrationAndPromptList.isNotEmpty()) {
            return FirstMessageResponse(firstMessage = integrationAndPromptList.first().firstMessage)
        } else {
            throw BusinessException("No such integration")
        }

    }

    fun processLike(apiKey: String, likeRequest: LikeRequest) {
        val integrationAndPromptList = integrationDao.getItAndPromptByApiKey(apiKey)
        if (integrationAndPromptList.isNotEmpty()) {
            messageDao.likeMessage(likeRequest.messageId)
        } else {
            throw BusinessException("No such integration")
        }
    }
}