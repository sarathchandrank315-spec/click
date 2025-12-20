package com.click.aifa.ui.dashBoard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.click.aifa.databinding.FragmentHomeBinding
import com.click.aifa.ui.addTransaction.adapter.TransactionAdapter
import com.click.aifa.viewmodel.IncomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var incomeViewModel: IncomeViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1️⃣ Adapter
        transactionAdapter = TransactionAdapter()

        // 2️⃣ RecyclerView setup (ONLY ONCE)
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }

        // 3️⃣ ViewModel
        incomeViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]

        // 4️⃣ Observe LiveData (Realtime updates)
        incomeViewModel.allIncomeList.observe(viewLifecycleOwner) { list ->
            transactionAdapter.submitList(list)
        }
        incomeViewModel.totalIncome.observe(viewLifecycleOwner) {
            binding.txtIncome.text = "₹ $it"
        }

        incomeViewModel.totalExpense.observe(viewLifecycleOwner) {
            binding.txtExpense.text = "₹ $it"
        }

        incomeViewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            incomeViewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
                binding.tvBalance.text = "₹ ${income - expense}"
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
