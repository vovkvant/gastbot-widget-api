package com.gastbot.widget.model

import java.sql.ResultSet
import java.util.UUID

data class IntegrationCreateRequest (
    val domain: String,
    val promptId: UUID,
)

data class IntegrationResponse(
    val id: UUID,
    val domain: String,
    val promptId: UUID,
    val promptName: String,
    val apiKey: String
) {
    companion object {
        fun mapRow(rs: ResultSet, rowNum: Int): IntegrationResponse {
            return IntegrationResponse(
                id = UUID.fromString(rs.getString("id")),
                domain = rs.getString("domain"),
                promptId = UUID.fromString(rs.getString("prompt_id")),
                promptName = rs.getString("name"),
                apiKey = rs.getString("api_key_hash")
            )
        }
    }
}

data class IntegratedPrompt(
    val id: UUID,
    val userId: UUID,
    val promptId: UUID,
    val domain: String,
    val prompt: String,
    val firstMessage: String,
    val isOn: Boolean,
    val bgColor: String,
    val textColor: String
) {
    companion object {
        fun mapRow(rs: ResultSet, rowNum: Int): IntegratedPrompt {
            return IntegratedPrompt(
                id = UUID.fromString(rs.getString("id")),
                userId = UUID.fromString(rs.getString("user_id")),
                promptId = UUID.fromString(rs.getString("prompt_id")),
                domain = rs.getString("domain"),
                prompt = rs.getString("prompt"),
                firstMessage = rs.getString("first_message"),
                isOn = rs.getBoolean("is_on"),
                bgColor = rs.getString("bg_color"),
                textColor = rs.getString("text_color")
            )
        }
    }
}