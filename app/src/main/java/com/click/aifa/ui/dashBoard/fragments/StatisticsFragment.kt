package com.click.aifa.ui.dashBoard.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.click.aifa.R
import com.click.aifa.databinding.FragmentOverviewBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout

class StatisticsFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        setupBarChart(binding.barChart)
        setupTabs()
        return binding.root
    }

    private fun setupTabs() {
        with(binding) {
            btnIncome.setOnClickListener {
                btnIncome.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                btnIncome.setBackgroundResource(R.drawable.bg_tab_income)
                btnExpense.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                btnExpense.background=null
            }
            btnExpense.setOnClickListener {
                btnExpense.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                btnExpense.setBackgroundResource(R.drawable.bg_tab_expense)
                btnIncome.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                btnIncome.background=null
            }
        }
    }

    private fun setupBarChart(barChart: BarChart) {
        val incomeEntries = listOf(
            BarEntry(1f, 2000f),
            BarEntry(2f, 1000f),
            BarEntry(3f, 1800f),
            BarEntry(4f, 2200f)
        )

        val expenseEntries = listOf(
            BarEntry(1f, 1200f),
            BarEntry(2f, 800f),
            BarEntry(3f, 1100f),
            BarEntry(4f, 900f)
        )

        val incomeSet = BarDataSet(incomeEntries, "Income").apply {
            color = Color.parseColor("#7C4DFF")
        }
        val expenseSet = BarDataSet(expenseEntries, "Expenses").apply {
            color = Color.parseColor("#FF7043")
        }

        val data = BarData(incomeSet, expenseSet)
        data.barWidth = 0.3f

        barChart.data = data
        barChart.groupBars(0f, 0.4f, 0.02f)
        barChart.description.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setDrawGridBackground(false)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
            listOf("Week 1", "Week 2", "Week 3", "Week 4")
        )

        barChart.axisLeft.axisMinimum = 0f
        barChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
