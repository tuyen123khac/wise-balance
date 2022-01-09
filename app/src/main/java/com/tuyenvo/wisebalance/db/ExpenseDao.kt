package com.tuyenvo.wisebalance.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tuyenvo.wisebalance.models.ExpenseItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expenseItem: ExpenseItem)

    @Delete
    suspend fun deleteExpenseItem(expenseItem: ExpenseItem)

    @Query("SELECT * FROM expense_table")
    fun getAllExpenses() : Flow<List<ExpenseItem>>

    @Query("SELECT amount FROM expense_table")
    fun getAllExpensesAmount(): Flow<List<Double>>
}