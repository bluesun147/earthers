package com.beside.earthers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EarthersApplication

fun main(args: Array<String>) {
	runApplication<EarthersApplication>(*args)
}
