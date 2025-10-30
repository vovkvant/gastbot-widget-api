package com.gastbot.widget

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/widget-internal-api")
class InternalApiWidgetController(
    private val messageDao: MessageDao
) {

    val log = org.slf4j.LoggerFactory.getLogger(InternalApiWidgetController::class.java)

    // TODO
    @GetMapping("/messages/count")
    fun messagesCount(
        @RequestHeader(name = "Internal-Api-Key") internalApiKey: String
    ): ResponseEntity<Void> {
        log.info("Get messages count")
        if(internalApiKey != "somekey") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        //val response = chatbotWidgetService.getFirstMessage(apiKey)
        return ResponseEntity.ok().build()
    }

    fun messagesHistory() {

    }
}