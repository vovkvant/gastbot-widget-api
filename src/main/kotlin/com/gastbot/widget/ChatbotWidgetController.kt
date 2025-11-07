package com.gastbot.widget

import com.gastbot.widget.model.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Rate limiter
 * Test how to start application on PROD
 * Add domain check
 * Get integration details from admin app
 * Provide internal api for admin panel with message count
 * Add liquibase
 * Create separate database
 * Unit and integration tests
 */
@RestController
@RequestMapping("/widget-api")
@CrossOrigin(origins = ["*"])
class ChatbotWidgetController(
    val chatbotWidgetService: ChatbotWidgetService
) {

    val log = org.slf4j.LoggerFactory.getLogger(ChatbotWidgetService::class.java)

    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("UP")
    }

    @GetMapping("/settings")
    fun initialSettings(
        @RequestHeader(name = "X-Api-Key") apiKey: String
    ): ResponseEntity<InitialSettings> {
        log.info("Request settings")
        val response = chatbotWidgetService.getInitialSettings(apiKey)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/message")
    fun sendMessage(
        @RequestHeader(name = "X-Api-Key") apiKey: String,
        @RequestBody widgetChatMessage: ChatMessageRequest
    ): ResponseEntity<ChatMessageResponse> {
        log.info("Widget request: $widgetChatMessage")
        val response = chatbotWidgetService.processMessage(apiKey, widgetChatMessage)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/message/like")
    fun likeMessage(
        @RequestHeader(name = "X-Api-Key") apiKey: String,
        @RequestBody likeRequest: LikeRequest
    ): ResponseEntity<Void> {
        chatbotWidgetService.processLike(apiKey, likeRequest, true)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/message/dislike")
    fun dislikeMessage(
        @RequestHeader(name = "X-Api-Key") apiKey: String,
        @RequestBody dislikeRequest: LikeRequest
    ): ResponseEntity<Void> {
        chatbotWidgetService.processLike(apiKey, dislikeRequest, false)
        return ResponseEntity.ok().build()
    }
}