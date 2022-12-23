package se.smasseman.fridge

import java.math.BigDecimal

data class StateDTO(val temperature: BigDecimal, val wanted: BigDecimal, val time: Long)
