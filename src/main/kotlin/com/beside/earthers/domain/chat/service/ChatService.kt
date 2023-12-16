package com.beside.earthers.domain.chat.service

import com.beside.earthers.domain.chat.dto.ChatResponse
import com.beside.earthers.domain.chat.dto.ClovaPrompt
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChatService() {

    @Value("\${clovastudio.api-key}")
    private lateinit var clovaStudioApiKey: String

    @Value("\${clovastudio.apigw-api-key}")
    private lateinit var apigwApiKey: String

    @Value("\${clovastudio.request-id}")
    private lateinit var requestId: String

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

    // 동물 이미지 url2
    fun getAnimalImage2(animal: String): ChatResponse {
        println(animal)
        val url = "https://source.unsplash.com/900x900/?" + animal
        return ChatResponse(content = url)
    }

    // 대화문 전체 한번에 리턴
    fun getWholeText(systemContent: String, userContent: String): Flux<ChatResponse> {

        val webClient = WebClient
            .builder()
            .baseUrl("https://clovastudio.stream.ntruss.com/testapp")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

        val requestPayload = """
        {
            "messages": [
                {"role": "system", "content": "$systemContent"},
                {"role": "user", "content": "$userContent"}
            ],
            "topP": 0.8,
            "topK": 0,
            "maxTokens": 256,
            "temperature": 0.5,
            "repeatPenalty": 5.0,
            "stopBefore": [],
            "includeAiFilters": true
        }
        """.trimIndent()

        val objectMapper = ObjectMapper()

        return webClient
            .post()
            .uri("/v1/chat-completions/HCX-002")
            .header("X-NCP-CLOVASTUDIO-API-KEY", clovaStudioApiKey)
            .header("X-NCP-APIGW-API-KEY", apigwApiKey)
            .header("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
            .header(HttpHeaders.ACCEPT, "text/event-stream")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestPayload))
            .retrieve()
            .bodyToFlux(String::class.java)
            .flatMap { data ->
                try {
                    val jsonNode: JsonNode = objectMapper.readTree(data)
                    val stopReasonNode = jsonNode.get("stopReason")
                    val content = jsonNode["message"]["content"].asText()
                    if (stopReasonNode != null && stopReasonNode.asText() == "stop_before" && content != "") {
//                        val content = jsonNode["message"]["content"].asText()


                        Flux.just(ChatResponse(content))

                    } else if (content == "") {
                        Flux.empty()

                    } else {
                        Flux.empty()
                    }
                } catch (e: Exception) {
                    // JSON 파싱 중 에러가 발생한 경우 빈 Flux 반환
                    Flux.empty()
                }
            }
    }


    fun getWholeText2(systemContent: String, userContent: String): Flux<Map<String, String>> {

        val webClient = WebClient
            .builder()
            .baseUrl("https://clovastudio.stream.ntruss.com/testapp")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

        val requestPayload = """
        {
            "messages": [
                {"role": "system", "content": "$systemContent"},
                {"role": "user", "content": "$userContent"}
            ],
            "topP": 0.8,
            "topK": 0,
            "maxTokens": 256,
            "temperature": 0.5,
            "repeatPenalty": 5.0,
            "stopBefore": [],
            "includeAiFilters": true
        }
    """.trimIndent()

        val objectMapper = ObjectMapper()

        return webClient
            .post()
            .uri("/v1/chat-completions/HCX-002")
            .header("X-NCP-CLOVASTUDIO-API-KEY", clovaStudioApiKey)
            .header("X-NCP-APIGW-API-KEY", apigwApiKey)
            .header("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
            .header(HttpHeaders.ACCEPT, "text/event-stream")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestPayload))
            .retrieve()
            .bodyToFlux(String::class.java)
            .flatMap { data ->
                try {
                    val jsonNode: JsonNode = objectMapper.readTree(data)
                    val stopReasonNode = jsonNode.get("stopReason")
                    val content = jsonNode["message"]["content"].asText()
                    if (stopReasonNode != null && stopReasonNode.asText() == "stop_before" && content != "") {
                        Flux.just(mapOf("content" to content))
                    } else {
                        Flux.empty()
                    }
                } catch (e: Exception) {
                    // JSON 파싱 중 에러가 발생한 경우 빈 Flux 반환
                    Flux.empty()
                }
            }
    }


    // 대화문 스트림 리턴
    fun processChatCompletion(systemContent: String, userContent: String): Flux<String> {

        val objectMapper = ObjectMapper()


        val webClient = WebClient
            .builder()
            .baseUrl("https://clovastudio.stream.ntruss.com/testapp")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

        val requestPayload = """
            {
                "messages": [
                    {"role": "system", "content": "$systemContent"},
                    {"role": "user", "content": "$userContent"}
                ],
                "topP": 0.8,
                "topK": 0,
                "maxTokens": 600,
                "temperature": 1.0,
                "repeatPenalty": 5.0,
                "stopBefore": [],
                "includeAiFilters": true
            }
        """.trimIndent()

        return webClient
            .post()
            .uri("/v1/chat-completions/HCX-002")
            .header("X-NCP-CLOVASTUDIO-API-KEY", clovaStudioApiKey)
            .header("X-NCP-APIGW-API-KEY", apigwApiKey)
            .header("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
            .header(HttpHeaders.ACCEPT, "text/event-stream")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestPayload))
            .retrieve()
            .bodyToFlux(String::class.java)

            // takeUntil : 지정된 조건 충족될 때까지의 데이터만을 방출하고 이후 스트림 종료
            .takeUntil { data ->
                // println("data2 : " + data)
                val jsonNode: JsonNode = objectMapper.readTree(data)
                val stopReasonNode = jsonNode.get("stopReason")
                stopReasonNode != null && stopReasonNode.isTextual && stopReasonNode.asText() == "stop_before"
            }

            .map { data ->
                // println("data1 : " + data)
                val jsonNode: JsonNode = objectMapper.readTree(data)
                val content = jsonNode["message"]["content"].asText()
//                "$content" // 각 데이터를 새로운 라인으로 분리

                // Check if content is "\n" and replace it with "\n" in quotes
                val modifiedContent = if (content == "\n") "\"\\n\"\"\\n\"" else content

                modifiedContent // Return modified content
            }
            .doOnCancel { }
    }











    // 테스트

    // 출력은 한글자씩 되는데 리턴이 안되는 상황
    // 컨트롤러에서 리턴해서 두번 호출하게 되면 429 에러 (Too many requests)
//    fun clovaChatCompletionFluxPrint(systemContent: String, userContent: String): Flux<String> {
    fun clovaChatCompletionFluxPrint(clovaPrompt: ClovaPrompt): Flux<String> {

        val systemContent: String = clovaPrompt.systemContent
        val userContent: String = clovaPrompt.userContent

        val webClient = WebClient
            .builder()
            .baseUrl("https://clovastudio.stream.ntruss.com/testapp") // API 베이스 URL
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

        val requestPayload = """
            {
                "messages": [
                    {"role": "system", "content": "$systemContent"},
                    {"role": "user", "content": "$userContent"}
                ],
                "topP": 0.8,
                "topK": 0,
                "maxTokens": 256,
                "temperature": 0.5,
                "repeatPenalty": 5.0,
                "stopBefore": [],
                "includeAiFilters": true
            }
        """.trimIndent()

        val objectMapper = ObjectMapper()

        val flux = webClient
            .post()
            .uri("/v1/chat-completions/HCX-002")
            .header("X-NCP-CLOVASTUDIO-API-KEY", clovaStudioApiKey)
            .header("X-NCP-APIGW-API-KEY", apigwApiKey)
            .header("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
            // stream으로 명시 안하면 한번에 리턴
            .header(HttpHeaders.ACCEPT, "text/event-stream")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestPayload))
            .retrieve()
            // mono로 하면 데이터 다 받고 한번에 리턴
            // .bodyToMono(String::class.java)
            // flux는 데이터 여러번 방출 가능. 실시간으로 출력
            .bodyToFlux(String::class.java)

        var disposable: Disposable? = null

        disposable = flux.subscribe(
            // 데이터를 받을 때 호출될 콜백
            { data ->
                // 데이터를 JsonNode로 파싱
                val jsonNode: JsonNode = objectMapper.readTree(data)
                // content 필드의 값을 가져와 출력
                val content = jsonNode["message"]["content"].asText()
//                println("$data")
                print("$content")

                // stopReason이 "stop_before"일 때 해당 데이터까지만 출력하고 뒤이어 받은 데이터는 무시
                val stopReason = jsonNode["stopReason"]?.asText()

                if (stopReason == "stop_before") {
                    println()
                    println("the end!")
                    disposable?.dispose() // 구독 종료
                }
            },
            // 에러가 발생했을 때 호출될 콜백
            { error -> println("Error occurred: $error") },
            // 작업이 완료되었을 때 호출될 콜백
            { println("Operation completed") }
        )

        return flux
    }

    // postman으로 리턴
    // 왜 다 받고나서 한번에 리턴하지?
    // chatGpt Stream : https://firstws.tistory.com/66
    // 이거랑 똑같을텐데 왜 안되는거지
    fun clovaChatCompletionFluxReturn(systemContent: String, userContent: String): Flux<ChatResponse> {

        val webClient = WebClient
            .builder()
            .baseUrl("https://clovastudio.stream.ntruss.com/testapp")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()

        val requestPayload = """
        {
            "messages": [
                {"role": "system", "content": "$systemContent"},
                {"role": "user", "content": "$userContent"}
            ],
            "topP": 0.8,
            "topK": 0,
            "maxTokens": 256,
            "temperature": 0.5,
            "repeatPenalty": 5.0,
            "stopBefore": [],
            "includeAiFilters": true
        }
        """.trimIndent()

        val objectMapper = ObjectMapper()

        return webClient
            .post()
            .uri("/v1/chat-completions/HCX-002")
            .header("X-NCP-CLOVASTUDIO-API-KEY", clovaStudioApiKey)
            .header("X-NCP-APIGW-API-KEY", apigwApiKey)
            .header("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
            .header("Accept", "text/event-stream")
            .header(HttpHeaders.ACCEPT, "text/event-stream")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(requestPayload))
            .retrieve()
            .bodyToFlux(String::class.java)
//            .filter { data ->
//                val jsonNode: JsonNode = objectMapper.readTree(data)
//                val stopReasonNode = jsonNode.get("stopReason")
//                stopReasonNode != null && stopReasonNode.asText() != "stop_before"
//            }
//            .map { data ->
//                val jsonNode: JsonNode = objectMapper.readTree(data)
//                val content = jsonNode["message"]["content"].asText()
//                ChatResponse(content)
//            }
            .flatMap { data ->
                try {
                    val jsonNode: JsonNode = objectMapper.readTree(data)
                    val stopReasonNode = jsonNode.get("stopReason")

                    if (stopReasonNode != null && stopReasonNode.asText() == "stop_before") {
                        // 만약 stop_before이면 빈 Flux를 반환하여 종료
                        Flux.empty()
                    } else {
                        // stop_before가 아니면 ChatResponse를 반환
                        val content = jsonNode["message"]["content"].asText()
                        Flux.just(ChatResponse(content))
                    }
                } catch (e: Exception) {
                    // JSON 파싱 중 에러가 발생한 경우 빈 Flux 반환
                    Flux.empty()
                }
            }
    }
}