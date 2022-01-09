package com.tuyenvo.wisebalance.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.tuyenvo.wisebalance.R
import com.tuyenvo.wisebalance.adapters.ExpensesAdapter
import com.tuyenvo.wisebalance.databinding.FragmentExpenseListBinding
import com.tuyenvo.wisebalance.models.ExpenseItem
import com.tuyenvo.wisebalance.ui.activities.MainActivity
import com.tuyenvo.wisebalance.util.Constants
import com.tuyenvo.wisebalance.util.FileUtils
import com.tuyenvo.wisebalance.util.Utils
import com.tuyenvo.wisebalance.util.exhaustive
import com.tuyenvo.wisebalance.viewmodels.ExpenseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ExpenseListFragment : Fragment(R.layout.fragment_expense_list),
    ExpensesAdapter.OnItemClickListener {
    private val TAG = "ExpenseListFragment"
    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var expenses: List<ExpenseItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val binding = FragmentExpenseListBinding.bind(view)
        val expenseAdapter = ExpensesAdapter(this)


        binding.apply {
            expenseRecyclerView.apply {
                adapter = expenseAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            addExpenseButton.setOnClickListener {
                viewModel.onAddNewExpenseItem()

            }

            exportToExcel.setOnClickListener {
                exportDatabaseToCSVFile()
            }
        }

        viewModel.expenses.observe(viewLifecycleOwner) {
            expenses = it
            expenseAdapter.submitList(it)
        }

        viewModel.totalSpending.observe(viewLifecycleOwner) { amountList ->
            var sum = 0.0
            for (amount in amountList) {
                sum += amount
            }
            binding.totalSpending.text = Utils.formatMoneyNumber(sum)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.expenseEvent.collect { event ->
                when (event) {
                    is ExpenseViewModel.ExpenseEvent.NavigateToAddExpenseScreen -> {
                        val action =
                            ExpenseListFragmentDirections.actionExpenseListFragmentToAddEditExpenseFragment(
                                null, "New Expense"
                            )
                        findNavController().navigate(action)
                    }

                    is ExpenseViewModel.ExpenseEvent.NavigateToEditExpenseScreen -> {
                        val action =
                            ExpenseListFragmentDirections.actionExpenseListFragmentToAddEditExpenseFragment(
                                event.item, "Edit Expense Item"
                            )
                        findNavController().navigate(action)
                    }

                }
            }.exhaustive
        }
    }

    override fun onItemClick(item: ExpenseItem) {
        viewModel.onExpenseSelected(item)
    }

    private fun exportDatabaseToCSVFile() {
        val csvFile = FileUtils.generateFile(requireContext(), Constants.CSV_FILE_NAME)

        if (csvFile == null) {
            Toast.makeText(requireContext(), "Error when getting CSV file", Toast.LENGTH_SHORT)
                .show()
        } else {
            csvWriter().open(csvFile, append = false) {
                writeRow(listOf("Index", "Created Date", "Name", "Amount", "Type", "Description"))
                for (item in expenses) {
                    writeRow(listOf(
                        item.id,
                        item.createdDateFormatted,
                        item.name,
                        item.amountFormatted,
                        Utils.convertExpenseTypeToString(item.type),
                        item.description,
                    ))
                }
            }

            Toast.makeText(requireContext(), "Exported csv file", Toast.LENGTH_SHORT).show()
            val intent = FileUtils.goToFileIntent(requireContext(), csvFile)
            startActivity(intent)
        }
    }
}