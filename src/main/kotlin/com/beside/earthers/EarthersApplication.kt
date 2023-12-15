package com.beside.earthers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


// db 정보 없을 경우에 실행
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class EarthersApplication

fun main(args: Array<String>) {
	runApplication<EarthersApplication>(*args)
}

@Bean
fun corsConfigurer(): WebMvcConfigurer {
	return object : WebMvcConfigurer {
		override fun addCorsMappings(registry: CorsRegistry) {
			registry.addMapping("/**").allowedOriginPatterns("*")
		}
	}
}