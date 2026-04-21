package com.example.advancedwealthtracker.interfaces

interface DataListener {
    fun onDataReceived(income: Double, expenses: Double, savings: Double)
}