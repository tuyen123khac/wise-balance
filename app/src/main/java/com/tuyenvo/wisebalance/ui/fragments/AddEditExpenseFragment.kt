package com.tuyenvo.wisebalance.ui.fragments

import android.app.DatePickerDialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.tuyenvo.wisebalance.R
import com.tuyenvo.wisebalance.databinding.FragmentAddEditExpenseBinding
import com.tuyenvo.wisebalance.models.ExpenseItem
import com.tuyenvo.wisebalance.util.Utils
import com.tuyenvo.wisebalance.viewmodels.AddEditExpenseViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddEditExpenseFragment : Fragment(R.layout.fragment_add_edit_expense),
    AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {
    private val TAG = "AddEditExpenseFragment"

    private lateinit var datePicker: DatePickerDialog
    private lateinit var binding: FragmentAddEditExpenseBinding
    private val viewModel: AddEditExpenseViewModel by viewModels()
    private lateinit var expenseTypeSpinnerValue: String
    private var createDateValue = System.currentTimeMillis()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEditExpenseBinding.bind(view)

        setUpView()
        initialDatePickerDialog()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        expenseTypeSpinnerValue = p0?.getItemAtPosition(p2).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun setUpView() {
        binding.apply {

            addNumerChangedListener(binding.expenseAmount)

            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.type_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                typeSpinner.adapter = adapter
            }

            typeSpinner.onItemSelectedListener = this@AddEditExpenseFragment

            createdDateClickable.setOnClickListener {
                showDatePickerDialog()
            }

            saveButton.setOnClickListener {
                val amount = if (expenseAmount.text.toString().isNotEmpty()) {
                    expenseAmount.text.toString().toDouble()
                } else {
                    0.0
                }

                if (amount == 0.0) {
                    showWarningDialog()
                } else {
                    val newExpenseItem = ExpenseItem(
                        expenseName.text.toString(),
                        Utils.convertStringToExpenseType(expenseTypeSpinnerValue),
                        amount,
                        description.text.toString(),
                        createDateValue
                    )
                    Log.e(TAG, "setUpView: saveButton" + newExpenseItem)
                    viewModel.addNewExpenseItem(newExpenseItem)
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun initialDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        datePicker = DatePickerDialog(requireContext(), this, year, month, day)
    }

    private fun showDatePickerDialog() {
        datePicker.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val formattedMonth = month + 1
        binding.createdDate.text = "$dayOfMonth/$formattedMonth/$year"
        val calendar = GregorianCalendar(year, month, dayOfMonth)
        createDateValue = calendar.timeInMillis
    }

    private fun showWarningDialog(){
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog)
            .setCancelable(true)
            .setMessage("You have not entered amount, this item won't be saved")
            .setTitle("Amount is 0")
            .setPositiveButton("OK") { dialog, which ->

            }
            .show()
    }

    private fun addNumerChangedListener(textInputEditText: TextInputEditText){
        textInputEditText.addTextChangedListener(
            object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        return
                    }
                    binding.expenseAmount.removeTextChangedListener(this)
                    binding.expenseAmount.setText(Utils.formatMoneyNumber(s.toString().replace(",","").toDouble()))
                    binding.expenseAmount.setSelection(binding.expenseAmount.length())
                    binding.expenseAmount.addTextChangedListener(this)
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            }
        )
    }
}