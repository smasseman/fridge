package se.smasseman.fridge

import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TempReader {
    fun read() : BigDecimal {
        return BigDecimal(9)
    }

}