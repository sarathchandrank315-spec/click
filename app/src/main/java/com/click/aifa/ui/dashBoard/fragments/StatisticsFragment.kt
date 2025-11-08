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
            val incomeTab = tabLayout2.newTab().setCustomView(R.layout.custom_tab_item)
            val expenseTab = tabLayout2.newTab().setCustomView(R.layout.custom_tab_item)

            incomeTab.customView?.findViewById<TextView>(R.id.tabText)?.text = "Income"
            expenseTab.customView?.findViewById<TextView>(R.id.tabText)?.text = "Expense"

            tabLayout2.addTab(incomeTab, true) // Select Income by default
            tabLayout2.addTab(expenseTab)
            tabLayout2.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val view = tab.customView ?: return
                    val text = view.findViewById<TextView>(R.id.tabText)

                    if (tab.position == 0) {
                        // Income Tab
                        view.setBackgroundResource(R.drawable.income_selected_bg)
                        text.setTextColor(Color.WHITE)
                    } else {
                        // Expense Tab
                        view.setBackgroundResource(R.drawable.expense_selected_bg)
                        text.setTextColor(Color.WHITE)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    val view = tab?.customView ?: return
                    val text = view.findViewById<TextView>(R.id.tabText)

                    view.setBackgroundResource(R.drawable.tab_background_shape)
                    text.setTextColor(Color.DKGRAY)
                }

                override fun onTabReselected(p0: TabLayout.Tab?) {
                    //TODO("Not yet implemented")
                }
            })
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
