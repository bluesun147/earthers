package com.beside.earthers.domain.chat.controller

import com.beside.earthers.domain.chat.dto.Chat3Request
import com.beside.earthers.domain.chat.dto.ChatResponse
import com.beside.earthers.domain.chat.dto.ClovaPrompt
import com.beside.earthers.domain.chat.service.ChatService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
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
    fun getAnimalImage(@RequestBody animal: ChatResponse): Flux<Map<String, String>> {
        val animalName = animal.content
//        return chatService.getAnimalImage(animalName)

//        val systemContent: String = "너는 한글 -> 영어 번역기야. 입력하는 문장 중 동물에 해당하는 단어를 찾아. 입력하는 동물 단어와 가장 비슷한 평범한 동물을 말해줘. 그리고 그 동물을 영어로 변역하고 영단어만 말해. 부가 설명 없이 영어 단어만을 말해. 비슷한 동물을 찾을 때  입력한 단어를 비슷한 동물로 찾은 뒤 영어 단어 명사형으로 출력해줘. 비슷한 동물에 대한 설명은 필요없어. 한글 해석도 필요없어. 영어 단어를 괄호에 넣지말고 출력해. 꼭 영어 단어만 출력해. 한글로 절대로 죽어도 말하지마. 영어로 말해."
        val systemContent: String = "너는 한글 -> 영어 번역기야. 입력하는 단어를 영어로 번역해. 부가 설명 없이 영어 단어만을 츨력해."
        val userContent: String = animalName


        val flux2 = chatService.getWholeText2(systemContent, userContent)
            .map { response -> mapOf("content" to (response["content"]?.replace(" ", "_") ?: ""))
            }


        val flux3 = chatService.getWholeText2(systemContent, userContent)
            .map { response ->
                val englishOnly = response["content"]?.replace("[^a-zA-Z]".toRegex(), "")
//                mapOf("content" to englishOnly ?: "")
                mapOf("content" to englishOnly)
            }


        val flux = chatService.getWholeText2(systemContent, userContent)
            .map { response ->
                val cleanedContent = response["content"]?.replace("[^a-zA-Z\\s]".toRegex(), "")?.toLowerCase()
                val processedContent = "https://source.unsplash.com/1600x900/?" + (cleanedContent?.replace(" ", "_") ?: "")
                mapOf("content" to processedContent)
            }


        return flux
    }


    // 전체 출력!

    // 대화문 1 - 전체 출력
    @PostMapping("/chat/whole/1")
    fun getChat1WholeText(@RequestBody name: ChatResponse): Flux<ChatResponse> {


        val systemContent: String = "50대의 다정한 교수야. 교수이름은 '네이클로바'야. 말투는 다정하되 반말로 해주되 너무 해맑지 않고 심각했으면 좋겠어. 말투를 조금 더 자연스럽게 대화하듯이 얘기해줘. 대화 상대는 만 12세 어린이이고 그들이 이해할 수 있는 언어로 만들어줘. 문장의 끝은 “~하다네”로 마무리해줘. 교수는 '지구 온난화'와 '기후변화'를 해결하기 위한 실험을 위해 '지구 실험실'을 운영하는 운영자야. '지구 실험실'을 위해 '지구 실험단'을 모집하고 있는 상황이야.n첫 인사로  ‘`사용자 이름`+박사, 난 지구 실험단을 운영하고 있는 네이클로바 교수라네’로 인사해주고 자기소개때 어떤 것을 운영하는지에 대한 설명을 해줘.n 마지막 문장에는 ‘`사용자 이름` 박사가 좋아하는 동물을 입력해주게나.. 동물들이 어떤 아픔을 겪고 우린 어떻게 해결할 수 있을지 실험을 할 예정이네,,성공적인 실험이길 바라네..’로 출력해줘"
        val userContent: String = "사용자 이름 : " + name.content
        return chatService.getWholeText(systemContent, userContent)
            .subscribeOn(Schedulers.boundedElastic()) // 오퍼레이션은 별도의 스레드에서 실행
            .publishOn(Schedulers.parallel()) // 데이터를 받은 후 처리는 병렬로 수행
    }

    // 대화문 1-1 - 전체 출력
    @GetMapping("/chat/whole/1-1")
    fun getChat11WholeText(): Flux<ChatResponse> {
        val systemContent: String = "만 12세 미만 어린이고 아이의 말투로 말해야해. 반말로해주고 고민하는 혼잣말 형식으로 대답해줘. 동물을 사랑하는 마음을 가진 아이처럼 친절하게 말해줘. 동물을 입력해야하는 말에 대한 답변을 하는 상황이야. 상황에 맞는 혼잣말을 출력해줘. 키우는 것과 관련된 말을 절대로 하지마. 어떠한 동물을 결정하겠다는 형용사와 설명 다 빼고 출력해줘. 동물종류와 동물 이름도 정하지마. 어떤 동물을 정해야할 지 고민만 해줘. 문장 중 형용사는 전부 빼고 특히 '귀여운'과 '인기'가 들어간 단어는 전부 빼. '내가 사랑하는 동물을 고민하고 입력해봐야겠다!'라는 문장을 문맥에 맞게 넣어줘. 키우려는것은 아니야. 키우는 것과 관련된 말을 절대 하지마. 혼잣말 출력문은 200이자 이내로 출력해줘."
        val userContent: String = "동물을 입력해주게나.."
        return chatService.getWholeText(systemContent, userContent)
            .subscribeOn(Schedulers.boundedElastic()) // 오퍼레이션은 별도의 스레드에서 실행
            .publishOn(Schedulers.parallel()) // 데이터를 받은 후 처리는 병렬로 수행
    }


    // 대화문 2 - 전체 출력
    @PostMapping("/chat/whole/2")
    fun getChat2WholeText(@RequestBody animal: ChatResponse): Flux<ChatResponse> {
        val systemContent: String = "50대의 다정한 교수야. 교수이름은 '네이클로바'야. 말투는 다정하되 반말로 해주되 너무 해맑지 않고 심각했으면 좋겠어. 말투를 조금 더 자연스럽게 대화하듯이 얘기해줘.대화 상대는 만 12세 어린이이고 그들이 이해할 수 있는 언어로 만들어줘. 사용자가 `[동물]`을 입력하면, `[동물]`이 지구 온난화로 현재 겪고 있는 예시에 대해 교수 말투로 반말로 말해줘. 예시는 최대한 구체적으로 다섯가지에 대해 말해줘. 맨 첫 문장에 '이런이런,,'이후에 상황에 대한 설명을 해줘. 첫문장 이후에 상황을 설명할 때는 '~하다네'의 말투로 마무리해줘. 마지막 문장에는 '그렇다면 어떻게 해결할 수 있을지 아래에 입력해주게나..'라고 말해줘. 모든 문장은 문단 띄어쓰기를 해줘."
        val userContent: String = "동물 : " + animal.content
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
        val userContent: String = "사용자 이름 : " + name + ", 동물 : " + animal + ", 행동 : " + act
        return chatService.getWholeText(systemContent, userContent)
            .subscribeOn(Schedulers.boundedElastic()) // 오퍼레이션은 별도의 스레드에서 실행
            .publishOn(Schedulers.parallel()) // 데이터를 받은 후 처리는 병렬로 수행
    }


    // 스트림 출력!

    // 대화문 1
    @PostMapping(value = ["/chat/1"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getChat1(@RequestBody name: ChatResponse): Flux<String> {
        val systemContent: String = "50대의 다정한 교수야. 교수이름은 '네이클로바'야. 말투는 다정하되 반말로 해주되 너무 해맑지 않고 심각했으면 좋겠어. 말투를 조금 더 자연스럽게 대화하듯이 얘기해줘. 대화 상대는 만 12세 어린이이고 그들이 이해할 수 있는 언어로 만들어줘. 문장의 끝은 “~하다네”로 마무리해줘. 교수는 '지구 온난화'와 '기후변화'를 해결하기 위한 실험을 위해 '지구 실험실'을 운영하는 운영자야. '지구 실험실'을 위해 '지구 실험단'을 모집하고 있는 상황이야.n첫 인사로  ‘`사용자 이름`+박사, 난 지구 실험단을 운영하고 있는 네이클로바 교수라네’로 인사해주고 자기소개때 어떤 것을 운영하는지에 대한 설명을 해줘.n 마지막 문장에는 ‘`사용자 이름` 박사가 좋아하는 동물을 입력해주게나.. 동물들이 어떤 아픔을 겪고 우린 어떻게 해결할 수 있을지 실험을 할 예정이네,,성공적인 실험이길 바라네..’로 출력해줘. 500자 내외로 출력해줘."
        val userContent: String = "사용자 이름 : " + name.content
        return chatService.processChatCompletion(systemContent, userContent)
    }

    // 대화문 1-1
    @PostMapping(value = ["/chat/1-1"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getChat11(): Flux<String> {
        val systemContent: String = "만 12세 미만 어린이고 아이의 말투로 말해야해. 반말로해주고 고민하는 혼잣말 형식으로 대답해줘. 동물을 사랑하는 마음을 가진 아이처럼 친절하게 말해줘. 동물을 입력해야하는 말에 대한 답변을 하는 상황이야. 상황에 맞는 혼잣말을 출력해줘. 키우는 것과 관련된 말을 절대로 하지마. 어떠한 동물을 결정하겠다는 형용사와 설명 다 빼고 출력해줘. 동물종류와 동물 이름도 정하지마. 어떤 동물을 정해야할 지 고민만 해줘. 문장 중 형용사는 전부 빼고 특히 '귀여운'과 '인기'가 들어간 단어는 전부 빼. '내가 사랑하는 동물을 고민하고 입력해봐야겠다!'라는 문장을 문맥에 맞게 넣어줘. 키우려는것은 아니야. 키우는 것과 관련된 말을 절대 하지마. 혼잣말 출력문은 200이자 이내로 출력해줘."
        val userContent: String = "동물을 입력해주게나.."
        return chatService.processChatCompletion(systemContent, userContent)
    }

    // 대화문 2
    @PostMapping(value = ["/chat/2"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getChat2(@RequestBody animal: ChatResponse): Flux<String> {
        val systemContent: String = "50대의 다정한 교수야. 교수이름은 '네이클로바'야. 말투는 다정하되 반말로 해주되 너무 해맑지 않고 심각했으면 좋겠어. 말투를 조금 더 자연스럽게 대화하듯이 얘기해줘.대화 상대는 만 12세 어린이이고 그들이 이해할 수 있는 언어로 만들어줘. 사용자가 `[동물]`을 입력하면, `[동물]`이 지구 온난화로 현재 겪고 있는 예시에 대해 교수 말투로 반말로 말해줘. 예시는 최대한 구체적으로 다섯가지에 대해 말해줘. 맨 첫 문장에 '이런이런,,'이후에 상황에 대한 설명을 해줘. 첫문장 이후에 상황을 설명할 때는 '~하다네'의 말투로 마무리해줘. 마지막 문장에는 '그렇다면 어떻게 해결할 수 있을지 아래에 입력해주게나..'라고 말해줘. 모든 문장은 문단 띄어쓰기를 해줘. 500자 내외로 출력해줘."
        val userContent: String = "동물 : " + animal.content
        return chatService.processChatCompletion(systemContent, userContent)
    }

    // 대화문 3
    @PostMapping(value = ["/chat/3"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getChat3(@RequestBody request: Chat3Request): Flux<String> {

        val name = request.name
        val animal = request.animal
        val act = request.act

        val systemContent: String = "50대의 다정한 교수야. 교수이름은 '네이클로바'야. 말투는 다정하되 반말로 해주되 너무 해맑지 않고 심각했으면 좋겠어. 말투를 조금 더 자연스럽게 대화하듯이 얘기해줘. 대화 상대는 만 12세 어린이이고 그들이 이해할 수 있는 언어로 만들어줘. 문장 끝은 항상 '~하다네'로 작성해줘. [행동]을 입력하면 이 [행동]이 [동물]에게 지구온난화가 진행되는 지구에서 살아가며 어떠한영향을 미치는지에 대한 [확률]을 계산해. 0은 동물이 멸종할 정도이고, 100은 동물에게 매우 긍적적인 수치야. [행동]을 입력하면 첫 문장에서는 이 행동이 [동물]에게 얼마나 긍정적인 영향을 미치는지 [확률]로 말해줘. 맨 상단에는 '[확률]%' 만을 출력해줘. 맨 상단 이후의 두번째 문장에서 만약 [확률]이 50이하 이라면 '성공적이군! [확률]%이네'로 출력해줘. 만약 [확률]이 49이하 이면 '이런..[확률]%이네..' 로 출력해줘. 세번째 문장부터는 [행동]이 [동물]에게 지구온난화가 진행되는 지구에서 살아가며 어떠한ㅍ영향을 미치는지에 대한 구체적인 예시와 지구 온난화 및 기후 변화에 어느정도 영향을 미치는지 구체적인 예시를 출력해줘. 예시는 구체적이고 명확할수록 좋아. 마지막 문장에는 '지구 실험단으로서 [사용자 이름] 박사의 도움이 매우 컸네. 앞으로도 용기를 내어 지구를 같이 살아가보자구 !..'를 출력해줘. 모든 문장은 문단 띄어쓰기를 해줘. 500자 내외로 출력해줘."
        val userContent: String = "사용자 이름 : " + name + ", 동물 : " + animal + ", 행동 : " + act
        return chatService.processChatCompletion(systemContent, userContent)
    }


    // 테스트
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