package com.tuyenvo.wisebalance.viewmodels

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.tuyenvo.wisebalance.db.ExpenseDao
import com.tuyenvo.wisebalance.models.ExpenseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
) : ViewModel() {

    val expenses = expenseDao.getAllExpenses().asLiveData()
    val totalSpending = expenseDao.getAllExpensesAmount().asLiveData()

    private val expenseEventChannel = Channel<ExpenseEvent>()
    val expenseEvent = expenseEventChannel.receiveAsFlow()

    fun onAddNewExpenseItem() = viewModelScope.launch {
        expenseEventChannel.send(ExpenseEvent.NavigateToAddExpenseScreen)
    }

    fun onExpenseSelected(item: ExpenseItem) = viewModelScope.launch {
        expenseEventChannel.send(ExpenseEvent.NavigateToEditExpenseScreen(item))
    }

    sealed class ExpenseEvent {
        object NavigateToAddExpenseScreen : ExpenseEvent()
        data class NavigateToEditExpenseScreen(val item: ExpenseItem) : ExpenseEvent()
    }
}