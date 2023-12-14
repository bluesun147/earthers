package com.beside.earthers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

// db 정보 없을 경우에 실행
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class EarthersApplication

fun main(args: Array<String>) {
	runApplication<EarthersApplication>(*args)
}