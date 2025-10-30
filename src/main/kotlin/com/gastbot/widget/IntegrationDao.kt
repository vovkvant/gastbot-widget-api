package com.gastbot.widget

import com.gastbot.widget.model.IntegratedPrompt
import com.gastbot.widget.model.IntegrationCreateRequest
import com.gastbot.widget.model.IntegrationResponse
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class IntegrationDao(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    fun getItAndPromptByApiKey(apiKeyHash: String): List<IntegratedPrompt> {
        val sql = """
            SELECT ci.id, ci.user_id, ci.domain, ci.prompt_id, up.prompt, up.first_message
            FROM chatbot_integration ci 
            JOIN user_prompts up ON ci.prompt_id = up.id AND ci.user_id = up.user_id
            WHERE ci.api_key_hash = :apiKeyHash AND revoked = false
        """.trimIndent()
        val params = mapOf("apiKeyHash" to apiKeyHash)

        return jdbcTemplate.query(sql, params) { rs, rowNum ->
            IntegratedPrompt.Companion.mapRow(rs, rowNum)
        }
    }

    fun getByUserId(userId: UUID): List<IntegrationResponse> {
        val sql = """
            SELECT ci.id, ci.api_key_hash, ci.domain, ci.prompt_id, up.name 
            FROM chatbot_integration ci 
            JOIN user_prompts up ON ci.prompt_id = up.id AND ci.user_id = up.user_id
            WHERE ci.user_id = :userId AND revoked = false
        """.trimIndent()
        val params = mapOf("userId" to userId)

        return jdbcTemplate.query(sql, params) { rs, rowNum ->
            IntegrationResponse.Companion.mapRow(rs, rowNum)
        }
    }

    fun create(userId: UUID, createRequest: IntegrationCreateRequest, apiKeyHash: String) {
        val integrationId = UUID.randomUUID()
        val sql = """
            INSERT INTO chatbot_integration(id, user_id, api_key_hash, domain, prompt_id)
            VALUES(:integrationId, :userId, :apiKeyHash, :domain, :promptId)
        """.trimIndent()
        val params = mapOf(
            "integrationId" to integrationId,
            "userId" to userId,
            "apiKeyHash" to apiKeyHash,
            "domain" to createRequest.domain,
            "promptId" to createRequest.promptId
        )

        jdbcTemplate.update(sql, params)
    }

    fun delete(userId: UUID, integrationId: UUID) {
        val sql = """
            UPDATE chatbot_integration SET revoked = true WHERE user_id = :userId
            AND id = :integrationId
        """.trimIndent()
        val params = mapOf(
            "userId" to userId,
            "integrationId" to integrationId
        )
        jdbcTemplate.update(sql, params)
    }
}