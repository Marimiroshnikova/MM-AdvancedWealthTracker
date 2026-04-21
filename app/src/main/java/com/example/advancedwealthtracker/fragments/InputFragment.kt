package com.example.advancedwealthtracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.viewpager2.widget.ViewPager2
import com.example.advancedwealthtracker.R
import com.example.advancedwealthtracker.communication.FinanceResultContract
import com.example.advancedwealthtracker.model.WealthManager
import java.util.Locale

class InputFragment : Fragment() {

    private var mmVaWealthManager: WealthManager? = null
    private var mmVaLastTouchY: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mmVaWealthManager == null) {
            mmVaWealthManager = WealthManager.fromCounts(
                resources.getInteger(R.integer.student_name_letters),
                resources.getInteger(R.integer.student_surname_letters),
                resources.getInteger(R.integer.student_birth_day)
            )
        }

        val mmVaEtIncome = view.findViewById<EditText>(R.id.mm_va_et_income)
        val mmVaEtExpenses = view.findViewById<EditText>(R.id.mm_va_et_expenses)
        val mmVaTvKChip = view.findViewById<TextView>(R.id.mm_va_tv_k_chip)
        val mmVaBtnSave = view.findViewById<Button>(R.id.mm_va_btn_save)
        val mmVaInputScroll = view.findViewById<NestedScrollView>(R.id.mm_va_sv_input)

        mmVaInputScroll.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    mmVaLastTouchY = event.y
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_MOVE -> {
                    val mmVaDy = event.y - mmVaLastTouchY
                    mmVaLastTouchY = event.y

                    val mmVaAtTopAndPullingDown = mmVaDy > 0 && !mmVaInputScroll.canScrollVertically(-1)
                    val mmVaAtBottomAndPushingUp = mmVaDy < 0 && !mmVaInputScroll.canScrollVertically(1)

                    if (mmVaAtTopAndPullingDown || mmVaAtBottomAndPushingUp) {
                        v.parent?.requestDisallowInterceptTouchEvent(false)
                    } else {
                        v.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    v.parent?.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        val mmVaCurrentK = mmVaWealthManager?.getK() ?: 0.0
        mmVaTvKChip.text = getString(R.string.k_chip_template, String.format(Locale.US, "%.2f", mmVaCurrentK))

        mmVaEtIncome.imeOptions = EditorInfo.IME_ACTION_NEXT
        mmVaEtExpenses.imeOptions = EditorInfo.IME_ACTION_DONE

        mmVaEtIncome.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                mmVaEtExpenses.requestFocus()
                true
            } else {
                false
            }
        }

        mmVaEtExpenses.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mmVaBtnSave.performClick()
                true
            } else {
                false
            }
        }

        mmVaBtnSave.setOnClickListener {
            val mmVaIncomeStr = mmVaEtIncome.text.toString().trim()
            val mmVaExpensesStr = mmVaEtExpenses.text.toString().trim()

            if (mmVaIncomeStr.isEmpty()) {
                mmVaEtIncome.error = getString(R.string.error_empty_field)
                return@setOnClickListener
            }
            if (mmVaExpensesStr.isEmpty()) {
                mmVaEtExpenses.error = getString(R.string.error_empty_field)
                return@setOnClickListener
            }

            val mmVaIncome = mmVaIncomeStr.toDoubleOrNull()
            val mmVaExpenses = mmVaExpensesStr.toDoubleOrNull()

            if (mmVaIncome == null || mmVaIncome < 0) {
                mmVaEtIncome.error = getString(R.string.error_invalid_number)
                return@setOnClickListener
            }
            if (mmVaExpenses == null || mmVaExpenses < 0) {
                mmVaEtExpenses.error = getString(R.string.error_invalid_number)
                return@setOnClickListener
            }
            if (mmVaExpenses > mmVaIncome) {
                mmVaEtExpenses.error = getString(R.string.error_expenses_exceed)
                return@setOnClickListener
            }

            val mmVaSavings = mmVaWealthManager?.calculateSavings(mmVaIncome, mmVaExpenses) ?: return@setOnClickListener
            val mmVaImm = context?.getSystemService<InputMethodManager>()
            mmVaImm?.hideSoftInputFromWindow(view.windowToken, 0)
            mmVaEtExpenses.clearFocus()
            parentFragmentManager.setFragmentResult(
                FinanceResultContract.MM_VA_RESULT_KEY,
                bundleOf(
                    FinanceResultContract.MM_VA_KEY_INCOME to mmVaIncome,
                    FinanceResultContract.MM_VA_KEY_EXPENSES to mmVaExpenses,
                    FinanceResultContract.MM_VA_KEY_SAVINGS to mmVaSavings
                )
            )
            requireActivity()
                .findViewById<ViewPager2>(R.id.mm_va_viewPager)
                .setCurrentItem(1, true)
        }
    }
}