package com.beside.earthers.domain.chat.controller

import com.beside.earthers.domain.chat.dto.ChatResponse
import com.beside.earthers.domain.chat.service.ChatService
import org.springframework.web.bind.annotation.*

@RequestMapping("/")
@RestController
class ChatController (
    // 서비스 주입
    val chatService: ChatService
) {

    // 이름 입력
    @PostMapping("/name")
    fun getName(@RequestBody name: String): String {
        return chatService.getName(name)
    }

    // 동물 입력
    @PostMapping("/animal")
    fun getAnimal(@RequestBody animal: String): String {
        println(animal)
        return chatService.getAnimal(animal)
    }

    // 동물 이미지 url
    @PostMapping("/image")
    fun getAnimalImage(@RequestBody animal: ChatResponse): ChatResponse {
        val animalName = animal.content
        return chatService.getAnimalImage(animalName)
    }
}