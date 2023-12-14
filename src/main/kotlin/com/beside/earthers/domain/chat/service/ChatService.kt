package com.beside.earthers.domain.chat.service

import com.beside.earthers.domain.chat.dto.ChatResponse
import org.springframework.stereotype.Service
@Service
class ChatService() {

    // 이름 입력
    fun getName(name: String): String {
        return name
    }

    // 동물 입력
    fun getAnimal(animal: String): String {
        return animal
    }

    // 동물 이미지 url
    fun getAnimalImage(animal: String): ChatResponse {
        println(animal)
        val url = "https://source.unsplash.com/900x900/?" + animal
        return ChatResponse(content = url)
    }

}