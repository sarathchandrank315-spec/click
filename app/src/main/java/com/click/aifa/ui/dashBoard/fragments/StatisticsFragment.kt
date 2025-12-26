package com.click.aifa.ui.dashBoard.fragments

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.click.aifa.R
import com.click.aifa.data.TransactionEntity
import com.click.aifa.data.enums.TransactionType
import com.click.aifa.databinding.FragmentOverviewBinding
import com.click.aifa.ui.addTransaction.adapter.TransactionAdapter
import com.click.aifa.viewmodel.IncomeViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.ai.type.imagenGenerationConfig
import java.time.Month
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

class StatisticsFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private lateinit var incomeViewModel: IncomeViewModel
    private val binding get() = _binding!!
    private lateinit var transactionAdapter: TransactionAdapter
    val calendar = Calendar.getInstance()
    val years = mutableListOf<Int>()
    val months = mutableListOf<String>()


    private lateinit var transactionList: List<TransactionEntity>
    var selectedYear = calendar.get(Calendar.YEAR)
    var selectedMonth = calendar.get(Calendar.MONTH) + 1
    var startYear = calendar.get(Calendar.YEAR)
    var startMonth = calendar.get(Calendar.MONTH) + 1
    var selectedTType = TransactionType.INCOME

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        transactionAdapter = TransactionAdapter()
        binding.recyclerTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTransactions.adapter = transactionAdapter
        binding.spYear.setText(selectedYear.toString())
        binding.spMonth.setText(monthName(selectedMonth))
        years.add(selectedYear)
        months.add(monthName(selectedMonth))
        binding.spYear.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                years
            )
        )
        binding.spYear.keyListener = null      // disable typing
        binding.spYear.setOnClickListener {
            binding.spYear.showDropDown()       // force show dropdown
        }
        binding.spMonth.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                months
            )
        )
        binding.spMonth.keyListener = null      // disable typing
        binding.spMonth.setOnClickListener {
            binding.spMonth.showDropDown()       // force show dropdown
        }
        incomeViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        incomeViewModel.allIncomeList.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) return@observe
            transactionList = list
            val firstEntry = list.minByOrNull { it.date }!!
            val (startY, startM) = getYearMonth(firstEntry.date)
            startYear = startY
            startMonth = startM
            val currentCal = Calendar.getInstance()
            val currentYear = currentCal.get(Calendar.YEAR)
            years.clear()
            years.addAll((startYear..currentYear).toList())
            _binding?.spYear?.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, years)
            )
            getWeeklySummaryForMonth(transactionList, selectedYear, selectedMonth)

        }
        _binding?.spYear?.setOnItemClickListener { _, _, pos, _ ->
            selectedYear = years[pos]

            months.addAll(getMonthsForYear(selectedYear, startYear, startMonth))
            _binding?.spMonth?.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, months)
            )
            getWeeklySummaryForMonth(transactionList, selectedYear, selectedMonth)
        }
        _binding?.spMonth?.setOnItemClickListener { _, _, pos, _ ->
            selectedMonth = Month.valueOf(
                months[pos].uppercase()
            ).value
            getWeeklySummaryForMonth(transactionList, selectedYear, selectedMonth)
        }

        setupTabs()

        return binding.root
    }

    private fun setupTabs() {
        with(binding) {
            btnIncome.setOnClickListener {
                btnIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                btnIncome.setBackgroundResource(R.drawable.bg_tab_income)
                btnExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                btnExpense.background = null
                selectedTType = TransactionType.INCOME
                getWeeklySummaryForMonth(transactionList, selectedYear, selectedMonth)
            }
            btnExpense.setOnClickListener {
                btnExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                btnExpense.setBackgroundResource(R.drawable.bg_tab_expense)
                btnIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                btnIncome.background = null
                selectedTType = TransactionType.EXPENSE
                getWeeklySummaryForMonth(transactionList, selectedYear, selectedMonth)
            }
        }
    }

    private fun setupBarChart(barChart: BarChart, data1: List<WeeklySummary>) {
        val incomeEntries =
            listOf(
                BarEntry(1f, data1.getOrNull(0)?.income?.toFloat() ?: 0F),
                BarEntry(2f, data1.getOrNull(1)?.income?.toFloat() ?: 0F),
                BarEntry(3f, data1.getOrNull(2)?.income?.toFloat() ?: 0F),
                BarEntry(4f, data1.getOrNull(3)?.income?.toFloat() ?: 0F)
            )

        val expenseEntries = listOf(
            BarEntry(1f, data1.getOrNull(0)?.expense?.toFloat() ?: 0F),
            BarEntry(2f, data1.getOrNull(1)?.expense?.toFloat() ?: 0F),
            BarEntry(3f, data1.getOrNull(2)?.expense?.toFloat() ?: 0F),
            BarEntry(4f, data1.getOrNull(3)?.expense?.toFloat() ?: 0F)

        )

        val incomeSet = BarDataSet(incomeEntries, "Income").apply {
            color = Color.parseColor("#7C4DFF")
        }
        val expenseSet = BarDataSet(expenseEntries, "Expenses").apply {
            color = Color.parseColor("#FF7043")
        }
        val groupSpace = 0.4f
        val barSpace = 0.02f
        val barWidth = 0.3f
        val startX = 1f
        val groupCount = 4
        val data = BarData(incomeSet, expenseSet)
        data.barWidth = barWidth

        barChart.data = data
        barChart.groupBars(startX, groupSpace, barSpace)
        barChart.description.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setDrawGridBackground(false)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = startX - 0.5f
// 👉 END RANGE
        xAxis.axisMaximum =
            startX + barChart.barData.getGroupWidth(groupSpace, barSpace) * groupCount
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.valueFormatter = IndexAxisValueFormatter(
            listOf("", "Week 1", "Week 2", "Week 3", "Week 4")
        )

        barChart.axisLeft.axisMinimum = 0.5f
        barChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun filterByMonth(
        list: List<TransactionEntity>,
        year: Int,
        month: Int
    ): List<TransactionEntity> {
        return list.filter {
            getYear(it.date) == year && getMonth(it.date) == month
        }
    }

    fun getWeekOfMonth(timestamp: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return cal.get(Calendar.WEEK_OF_MONTH)
    }

    fun getYear(timestamp: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return cal.get(Calendar.YEAR)
    }

    fun getMonth(timestamp: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return cal.get(Calendar.MONTH) + 1 // 1–12
    }

    fun getWeeklySummaryForMonth(
        list: List<TransactionEntity>,
        year: Int,
        month: Int
    ) {

        val monthData = filterByMonth(list, year, month)
        monthData.let { transactions ->
            val income = transactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }

            val expense = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
            binding.tvTotalIncome.text = "₹ ${String.format("%.2f", income)}"
            binding.tvTotalExpense.text = "₹ ${String.format("%.2f", expense)}"
        }

        transactionAdapter.submitList(monthData.filter { it.type == selectedTType })
        val data = monthData
            .groupBy { getWeekOfMonth(it.date) }
            .map { (week, transactions) ->

                val income = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

                val expense = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                WeeklySummary(
                    week = week,
                    income = income,
                    expense = expense
                )
            }
            .sortedBy { it.week }
        setupBarChart(binding.barChart, data)
    }

    fun getYearMonth(timestamp: Long): Pair<Int, Int> {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return cal.get(Calendar.YEAR) to (cal.get(Calendar.MONTH) + 1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthsForYear(selectedYear: Int, startYear: Int, startMonth: Int): List<String> {

        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH) + 1

        return when {
            selectedYear == startYear && selectedYear == currentYear ->
                (startMonth..currentMonth).map { monthName(it) }

            selectedYear == startYear ->
                (startMonth..12).map { monthName(it) }

            selectedYear == currentYear ->
                (1..currentMonth).map { monthName(it) }

            else ->
                (1..12).map { monthName(it) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun monthName(month: Int): String =
        Month.of(month).name

}

data class WeeklySummary(
    val week: Int,
    val income: Double,
    val expense: Double
)
