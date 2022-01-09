package com.tuyenvo.wisebalance.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuyenvo.wisebalance.db.ExpenseDao
import com.tuyenvo.wisebalance.models.ExpenseItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(private val expenseDao: ExpenseDao) : ViewModel() {

    fun addNewExpenseItem(item: ExpenseItem) = viewModelScope.launch {
        expenseDao.upsert(item)
    }
}