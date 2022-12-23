package se.smasseman.fridge

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class Fridge {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun on() {
        log.info("Fridge on")
    }

    fun off() {
        log.info("Fridge off")
    }

}
