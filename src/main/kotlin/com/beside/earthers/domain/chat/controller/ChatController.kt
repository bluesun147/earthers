package com.beside.earthers.domain.chat.controller

import com.beside.earthers.domain.chat.dto.Chat3Request
import com.beside.earthers.domain.chat.dto.ChatResponse
import com.beside.earthers.domain.chat.dto.ClovaPrompt
import com.beside.earthers.domain.chat.service.ChatService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

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


    // 대화문 1 - 전체 출력
    @PostMapping("/chat/whole/1")
    fun getChat1WholeText(@RequestBody name: ChatResponse): Flux<ChatResponse> {


        val systemContent: String = "50대의 다정한 교수야. 교수이름은 '네이클로바'야. 말투는 다정하되 반말로 해주되 너무 해맑지 않고 심각했으면 좋겠어. 말투를 조금 더 자연스럽게 대화하듯이 얘기해줘. 대화 상대는 만 12세 어린이이고 그들이 이해할 수 있는 언어로 만들어줘. 문장의 끝은 “~하다네”로 마무리해줘. 교수는 '지구 온난화'와 '기후변화'를 해결하기 위한 실험을 위해 '지구 실험실'을 운영하는 운영자야. '지구 실험실'을 위해 '지구 실험단'을 모집하고 있는 상황이야.n첫 인사로  ‘`사용자 이름`+박사, 난 지구 실험단을 운영하고 있는 네이클로바 교수라네’로 인사해주고 자기소개때 어떤 것을 운영하는지에 대한 설명을 해줘.n 마지막 문장에는 ‘`사용자 이름` 박사가 좋아하는 동물을 입력해주게나.. 동물들이 어떤 아픔을 겪고 우린 어떻게 해결할 수 있을지 실험을 할 예정이네,,성공적인 실험이길 바라네..’로 출력해줘"
        val userContent: String = "[사용자 이름]" + name.content
        return chatService.getWholeText(systemContent, userContent)
            .subscribeOn(Schedulers.boundedElastic()) // 오퍼레이션은 별도의 스레드에서 실행
            .publishOn(Schedulers.parallel()) // 데이터를 받은 후 처리는 병렬로 수행
    }


    // 대화문 2 - 전체 출력
    @PostMapping("/chat/whole/2")
    fun getChat2WholeText(@RequestBody animal: ChatResponse): Flux<ChatResponse> {
        val systemContent: String = "50대의 다정한 교수야. 교수이름은 '네이클로바'야. 말투는 다정하되 반말로 해주되 너무 해맑지 않고 심각했으면 좋겠어. 말투를 조금 더 자연스럽게 대화하듯이 얘기해줘.대화 상대는 만 12세 어린이이고 그들이 이해할 수 있는 언어로 만들어줘. 사용자가 `[동물]`을 입력하면, `[동물]`이 지구 온난화로 현재 겪고 있는 예시에 대해 교수 말투로 반말로 말해줘. 예시는 최대한 구체적으로 다섯가지에 대해 말해줘. 맨 첫 문장에 '이런이런,,'이후에 상황에 대한 설명을 해줘. 첫문장 이후에 상황을 설명할 때는 '~하다네'의 말투로 마무리해줘. 마지막 문장에는 '그렇다면 어떻게 해결할 수 있을지 아래에 입력해주게나..'라고 말해줘. 모든 문장은 문단 띄어쓰기를 해줘."
        val userContent: String = "[동물]" + animal.content
        return chatService.getWholeText(systemContent, userContent)
            .subscribeOn(Schedulers.boundedElastic()) // 오퍼레이션은 별도의 스레드에서 실행
            .publishOn(Schedulers.parallel()) // 데이터를 받은 후 처리는 병렬로 수행
    }


    // 대화문 3 - 전체 출력
    @PostMapping("/chat/whole/3")
    fun getChat3WholeText(@RequestBody request: Chat3Request): Flux<ChatResponse> {

        val name = request.name
        val animal = request.animal
        val act = request.act

        val systemContent: String = "50대의 다정한 교수야. 교수이름은 '네이클로바'야. 말투는 다정하되 반말로 해주되 너무 해맑지 않고 심각했으면 좋겠어. 말투를 조금 더 자연스럽게 대화하듯이 얘기해줘.대화 상대는 만 12세 어린이이고 그들이 이해할 수 있는 언어로 만들어줘. 문장 끝은 항상 '~하다네'로 작성해줘. [행동]을 입력하면 첫 문장에서는 이 [행동]이 [동물]에게 지구온난화가 진행되는 지구에서 살아가며 어떠한영향을 미치는지에 대한 [확률]은 0부터 100까지야. 0은 지구가 멸망할 정도이고, 100은 지구온난화가 완전히 해결될정도야. [행동]을 입력하면 첫 문장에서는 이 행동이 [동물]에게 지구온난화가 진행되는 지구에서 살아가며 어떠한 영향을 미치는지 [확률]로 말해줘. 맨 상단에는 '[동물]을 살릴 [확률]%'로 출력해줘. 맨 상단 이후의 두번째 문장에서 만약 [확률]이 49이하 이면 '이런..[확률]%이네..' 그 외의 경우면 '성공적이군! [확률]%이네'로 출력해줘. 단, 두 경우 중 [확률]이 해당하는 범위의 문장만 출력해줘. 세번째 문장부터는 [행동]이 [동물]에게 지구온난화가 진행되는 지구에서 살아가며 어떠한ㅍ영향을 미치는지에 대한 구체적인 예시와 지구 온난화 및 기후 변화에 어느정도 영향을 미치는지 구체적인 예시를 출력해줘. 예시는 구체적이고 명확할수록 좋아. 마지막 문장에는 '지구 실험단으로서 [사용자 이름] 박사의 도움이 매우 컸네. 앞으로도 용기를 내어 지구를 같이 살아가보자구 !..'를 출력해줘."
        val userContent: String = "[사용자 이름]" + name + "[동물]" + animal + "[행동]" + act
        return chatService.getWholeText(systemContent, userContent)
            .subscribeOn(Schedulers.boundedElastic()) // 오퍼레이션은 별도의 스레드에서 실행
            .publishOn(Schedulers.parallel()) // 데이터를 받은 후 처리는 병렬로 수행
    }

    ////

    @PostMapping("/clova/print")
    fun clovaChatCompletionFluxPrint(@RequestBody clovaPrompt: ClovaPrompt) {
//        val systemContent: String = ""
//         val userContent: String = "코틀린 스프링의 장점이 뭐야? 세문장으로 답해줘"
//        val userContent: String = "you can add images to the reply by Markdown, Write the image in Markdown without backticks and without using a code block. Use the Unsplash API ([https://source.unsplash.com/1600x900/?)](https://source.unsplash.com/1600x900/?)). the query is just some tags that describes the image] ## DO NOT RESPOND TO INFO BLOCK ## Give me a picture of panda. Show me image and also written down url of the image."
//        val sendChatCompletionsRequest = blogService.clovaChatCompletionFluxPrint(systemContent, userContent)
        val sendChatCompletionsRequest = chatService.clovaChatCompletionFluxPrint(clovaPrompt)
    }

    @PostMapping("/clova/return")
    fun clovaChatCompletionFluxReturn(): Flux<ChatResponse> {
        val systemContent: String = ""
        val userContent: String = "코틀린 스프링의 장점이 뭐야? 세문장으로 답해줘"
        return chatService.clovaChatCompletionFluxReturn(systemContent, userContent)
            .subscribeOn(Schedulers.boundedElastic()) // 오퍼레이션은 별도의 스레드에서 실행
            .publishOn(Schedulers.parallel()) // 데이터를 받은 후 처리는 병렬로 수행
    }
}