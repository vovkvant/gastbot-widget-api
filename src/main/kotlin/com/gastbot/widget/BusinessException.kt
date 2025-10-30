package com.gastbot.widget

class BusinessException(override val message: String,
                        val messageLocaleCode: String? = null): RuntimeException(message) {
}