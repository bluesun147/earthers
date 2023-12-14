package com.beside.earthers.domain.chat.dto

// 대화문 3 입력
data class Chat3Request(
    val name: String,
    val animal: String,
    val act: String
)