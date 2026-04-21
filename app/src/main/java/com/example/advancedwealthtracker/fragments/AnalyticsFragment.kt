package com.example.advancedwealthtracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.advancedwealthtracker.R
import com.example.advancedwealthtracker.communication.FinanceResultContract
import com.example.advancedwealthtracker.model.WealthManager
import com.google.android.material.progressindicator.CircularProgressIndicator

class AnalyticsFragment : Fragment() {

    companion object {
        private const val KEY_INCOME = "key_income"
        private const val KEY_EXPENSES = "key_expenses"
        private const val KEY_SAVINGS = "key_savings"
        private const val KEY_HAS_DATA = "key_has_data"
    }

    private var mmVaTvIncome: TextView? = null
    private var mmVaTvExpenses: TextView? = null
    private var mmVaTvSavings: TextView? = null
    private var mmVaTvSavedPercent: TextView? = null
    private var mmVaDonutChart: CircularProgressIndicator? = null

    private var mmVaPendingIncome: Double = 0.0
    private var mmVaPendingExpenses: Double = 0.0
    private var mmVaPendingSavings: Double = 0.0
    private var mmVaHasData: Boolean = false
    private var mmVaWealthManager: WealthManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(FinanceResultContract.MM_VA_RESULT_KEY, this) { _, bundle ->
            val mmVaIncome = bundle.getDouble(FinanceResultContract.MM_VA_KEY_INCOME)
            val mmVaExpenses = bundle.getDouble(FinanceResultContract.MM_VA_KEY_EXPENSES)
            val mmVaSavings = bundle.getDouble(FinanceResultContract.MM_VA_KEY_SAVINGS)
            updateData(mmVaIncome, mmVaExpenses, mmVaSavings)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mmVaTvIncome = view.findViewById(R.id.mm_va_tv_income)
        mmVaTvExpenses = view.findViewById(R.id.mm_va_tv_expenses)
        mmVaTvSavings = view.findViewById(R.id.mm_va_tv_savings)
        mmVaTvSavedPercent = view.findViewById(R.id.mm_va_tv_saved_percent)
        mmVaDonutChart = view.findViewById(R.id.mm_va_donut_chart)
        refreshViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble(KEY_INCOME, mmVaPendingIncome)
        outState.putDouble(KEY_EXPENSES, mmVaPendingExpenses)
        outState.putDouble(KEY_SAVINGS, mmVaPendingSavings)
        outState.putBoolean(KEY_HAS_DATA, mmVaHasData)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            mmVaPendingIncome = it.getDouble(KEY_INCOME)
            mmVaPendingExpenses = it.getDouble(KEY_EXPENSES)
            mmVaPendingSavings = it.getDouble(KEY_SAVINGS)
            mmVaHasData = it.getBoolean(KEY_HAS_DATA)
            refreshViews()
        }
    }

    fun updateData(income: Double, expenses: Double, savings: Double) {
        mmVaPendingIncome = income
        mmVaPendingExpenses = expenses
        mmVaPendingSavings = savings
        mmVaHasData = true
        refreshViews()
    }

    private fun refreshViews() {
        if (!mmVaHasData) return
        mmVaTvIncome?.text = "%.2f".format(mmVaPendingIncome)
        mmVaTvExpenses?.text = "%.2f".format(mmVaPendingExpenses)
        mmVaTvSavings?.text = "%.2f".format(mmVaPendingSavings)

        if (mmVaWealthManager == null) {
            mmVaWealthManager = WealthManager.fromCounts(
                resources.getInteger(R.integer.student_name_letters),
                resources.getInteger(R.integer.student_surname_letters),
                resources.getInteger(R.integer.student_birth_day)
            )
        }

        val mmVaManager = mmVaWealthManager ?: return
        val mmVaSavedPercent = mmVaManager.calculateSavedPercent(mmVaPendingIncome, mmVaPendingSavings)

        mmVaTvSavedPercent?.text = "%.1f%%".format(mmVaSavedPercent)
        mmVaDonutChart?.setProgressCompat(mmVaManager.percentToProgress(mmVaSavedPercent), true)
    }
}