package com.tuyenvo.wisebalance.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tuyenvo.wisebalance.R
import com.tuyenvo.wisebalance.databinding.ItemExpenseBinding
import com.tuyenvo.wisebalance.enums.ExpenseType
import com.tuyenvo.wisebalance.models.ExpenseItem
import com.tuyenvo.wisebalance.util.Utils

class ExpensesAdapter(private val listener: OnItemClickListener) :
    ListAdapter<ExpenseItem, ExpensesAdapter.ExpensesViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpensesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpensesViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ExpensesViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val expenseItem = getItem(position)
                        listener.onItemClick(expenseItem)
                    }
                }
            }
        }

        fun bind(item: ExpenseItem) {
            binding.apply {
                expenseCreated.text = item.createdDateFormatted
                expenseName.text = item.name
                expenseType.text = Utils.convertExpenseTypeToString(item.type)
                expenseAmount.text = item.amountFormatted
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: ExpenseItem)
    }

    class DiffCallback : DiffUtil.ItemCallback<ExpenseItem>() {
        override fun areItemsTheSame(oldItem: ExpenseItem, newItem: ExpenseItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ExpenseItem, newItem: ExpenseItem) =
            oldItem == newItem
    }
}