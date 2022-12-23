package se.smasseman.fridge

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference


@RestController
class FridgeController(val tempReader: TempReader, val fridge: Fridge) {

    private val sseEmitters: ConcurrentHashMap<String, SseEmitter> = ConcurrentHashMap()
    private val wanted = AtomicReference(BigDecimal.TEN)
    private val timer = Executors.newSingleThreadScheduledExecutor()

    init {
        val action = java.lang.Runnable {
            val state = pushCurrentState()
            if (state.temperature > wanted.get()) {
                fridge.on()
            } else {
                fridge.off()
            }
        }
        timer.scheduleWithFixedDelay(action, 1, 1, TimeUnit.SECONDS)
    }

    @GetMapping("/api/ping")
    fun ping(): ResponseEntity<String> {
        return ResponseEntity.ok("pong")
    }

    @GetMapping("/api/state")
    fun eventEmitter(): SseEmitter {
        val sseEmitter = SseEmitter(Long.MAX_VALUE)
        val guid = UUID.randomUUID()
        sseEmitters[guid.toString()] = sseEmitter
        sseEmitter.onCompletion { sseEmitters.remove(guid.toString()) }
        sseEmitter.onTimeout { sseEmitters.remove(guid.toString()) }
        pushCurrentState()
        return sseEmitter
    }

    @PostMapping("/api/set")
    fun setWanted(@RequestBody wanted: String): ResponseEntity<Void> {
        this.wanted.set(BigDecimal(wanted))
        pushCurrentState()
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    private fun pushCurrentState(): StateDTO {
        val state = StateDTO(tempReader.read(), wanted.get(), System.currentTimeMillis())
        sseEmitters.values.forEach {
            it.send(SseEmitter.event().data(state))
        }
        return state
    }

}