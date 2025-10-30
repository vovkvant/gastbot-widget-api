package com.gastbot.widget

import com.gastbot.widget.model.IntegratedPrompt
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

interface IntegrationDao {
    fun getItAndPromptByApiKey(apiKeyHash: String): List<IntegratedPrompt>
}

@Service
class IntegrationDaoImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : IntegrationDao {

    override fun getItAndPromptByApiKey(apiKeyHash: String): List<IntegratedPrompt> {
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
}