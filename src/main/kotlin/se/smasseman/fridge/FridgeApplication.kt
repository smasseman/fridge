package se.smasseman.fridge

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FridgeApplication

fun main(args: Array<String>) {
	runApplication<FridgeApplication>(*args)
}
