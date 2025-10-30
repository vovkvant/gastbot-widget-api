package com.gastbot.widget

import com.gastbot.widget.model.OpenAiResponse
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class MessageDao(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    fun createMessage(
        openAiResponse: OpenAiResponse,
        userId: UUID,
        integrationId: UUID? = null,
        chatId: Long? = null
    ): Long {
        val sql = """
            INSERT INTO message_history(
            integration_id, user_id, chat_id, 
            user_message, reply_message, source, 
            prompt_tokens, completion_tokens, total_tokens, 
            timeTook) VALUES(
                :integrationId, :userId, :chatId, 
                :userMessage, :replyMessage, :source, 
                :promptTokens, :completionTokens, :totalTokens,
                :timeTook
            )
        """.trimIndent()

        val params = MapSqlParameterSource(
            mapOf(
                "userId" to userId,
                "integrationId" to integrationId,
                "chatId" to chatId,
                "userMessage" to openAiResponse.payload.userMessage,
                "replyMessage" to openAiResponse.payload.replyMessage,
                "source" to openAiResponse.header.source,
                "promptTokens" to openAiResponse.header.promptTokens,
                "completionTokens" to openAiResponse.header.completionTokens,
                "totalTokens" to openAiResponse.header.totalTokens,
                "timeTook" to openAiResponse.header.timeTook
            )
        )

        val keyHolder: KeyHolder = GeneratedKeyHolder()

        jdbcTemplate.update(
            sql, params, keyHolder, arrayOf("id")
        )

        return keyHolder.key?.toLong() ?: throw IllegalStateException("Failed to retrieve generated ID")
    }

    fun incrementCounter(integrationId: UUID, userId: UUID) {
        val sql = """MERGE INTO widget_message_counter AS dest USING (
                            SELECT 
                                :integrationId AS integration_id,
                                :userId        AS user_id,
                                date_trunc('month', current_date)::date AS period
                            ) AS src
                     ON dest.integration_id = src.integration_id
                        AND dest.user_id = src.user_id
                        AND dest.period = src.period
                     WHEN MATCHED THEN
                        UPDATE SET counter = dest.counter + 1
                     WHEN NOT MATCHED THEN
                        INSERT (integration_id, user_id, period, counter)
                        VALUES (src.integration_id, src.user_id, src.period, 1)"""

        val params = mapOf(
            "integrationId" to integrationId,
            "userId" to userId
        )
        jdbcTemplate.update(sql, params)
    }

    fun getCountOfMessages(userId: UUID, period: LocalDate): Int {
        val sql = """
            SELECT COALESCE(sum(counter), 0) as messageCounter FROM widget_message_counter 
            WHERE user_id = :userId AND period = :period
        """.trimIndent()

        val params = mapOf(
            "userId" to userId,
            "period" to period
        )

        return jdbcTemplate.queryForObject(sql, params, Int::class.java) ?: 0
    }

    fun likeMessage(messageId: Long) {
        val sql = """
            UPDATE message_history SET like_counter = 1 WHERE id = :messageId and like_counter = 0
        """.trimIndent()
        val params = mapOf(
            "messageId" to messageId,
        )
        jdbcTemplate.update(sql, params)
    }
}