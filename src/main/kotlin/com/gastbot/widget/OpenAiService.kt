package com.gastbot.widget

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.gastbot.widget.model.ChatDialog
import com.gastbot.widget.model.ChatDialogHeader
import com.gastbot.widget.model.ChatMessageRequest
import com.gastbot.widget.model.Message
import com.gastbot.widget.model.MessageSource
import com.gastbot.widget.model.OpenAiResponse
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch
import java.time.ZoneId
import java.time.ZonedDateTime

@Service
class OpenAiService(
    @Value("\${gastbot.openai.api-secret}") val openAiApiKey: String
) {

    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    val openAi = OpenAI(openAiApiKey)

    companion object {
        const val CHAT_MODEL = "gpt-4.1-mini"
    }

    fun sendMessage(prompt: String, chatMessageRequest: ChatMessageRequest, source: MessageSource): OpenAiResponse {
        val currentTime = ZonedDateTime.now(ZoneId.of("Europe/Berlin"))
        val messages = chatMessageRequest.history.map {
            when (it.role) {
                Message.Role.BOT -> ChatMessage(ChatRole.Assistant, it.content)
                Message.Role.USER -> ChatMessage(ChatRole.User, it.content)
                else -> throw BusinessException("Unknown role: ${it.role}")
            }
        }

        val systemMessages = listOf(
            ChatMessage(ChatRole.System, prompt),
            // TODO think about this place
            ChatMessage(
                ChatRole.System, "" +
                        """
                            You are a helpful chatbot that always replies in Markdown format.
                            Always format links as [text](url).
                            Current time: $currentTime
                        """.trimIndent()
            ),
        )

        val fullMessages = systemMessages + messages + ChatMessage(ChatRole.User, chatMessageRequest.userMessage)

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(CHAT_MODEL),
            messages = fullMessages
        )

        val watch = StopWatch()
        watch.start()
        val response = runBlocking {
            openAi.chatCompletion(chatCompletionRequest)
        }
        watch.stop()

        val reply = response.choices.first().message.content
        if (reply != null) {
            val header = ChatDialogHeader(
                source = source.toString(),
                promptTokens = response.usage?.promptTokens ?: -1,
                completionTokens = response.usage?.completionTokens ?: -1,
                totalTokens = response.usage?.totalTokens ?: -1,
                timeTook = watch.totalTimeMillis
            )
            return OpenAiResponse(
                header = header,
                payload = ChatDialog(
                    userMessage = chatMessageRequest.userMessage,
                    replyMessage = reply
                )
            )
        } else {
            throw BusinessException("No reply from AI")
        }
    }
}