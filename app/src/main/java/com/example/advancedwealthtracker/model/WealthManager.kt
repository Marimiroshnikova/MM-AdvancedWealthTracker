package com.example.advancedwealthtracker.model

import kotlin.math.roundToInt

class WealthManager(
    private val mmVaNameLetters: Int,
    private val mmVaSurnameLetters: Int,
    private val mmVaBirthDay: Int
) {

    init {
        require(mmVaBirthDay > 0) { "birthDay must be greater than 0" }
    }

    companion object {
        fun fromCounts(nameLetters: Int, surnameLetters: Int, birthDay: Int): WealthManager {
            return WealthManager(nameLetters, surnameLetters, birthDay)
        }
    }

    private val coefficientK: Double
        get() = (mmVaNameLetters + mmVaSurnameLetters).toDouble() / mmVaBirthDay.toDouble()

    fun calculateSavings(income: Double, expenses: Double): Double {
        return (income - expenses) * coefficientK
    }

    fun calculateSavedPercent(income: Double, savings: Double): Double {
        if (income <= 0.0) return 0.0
        return ((savings / income) * 100.0).coerceIn(0.0, 100.0)
    }

    fun percentToProgress(percent: Double): Int {
        return percent.roundToInt().coerceIn(0, 100)
    }

    fun getK(): Double = coefficientK
}