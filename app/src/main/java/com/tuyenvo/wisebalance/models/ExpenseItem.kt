package com.tuyenvo.wisebalance.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tuyenvo.wisebalance.enums.ExpenseType
import com.tuyenvo.wisebalance.util.Utils
import kotlinx.parcelize.Parcelize
import java.text.DateFormat
import java.text.DecimalFormat

@Entity(tableName = "expense_table")
@Parcelize
data class ExpenseItem(
    val name: String?,
    val type: ExpenseType?,
    val amount: Double,
    val description: String?,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
) : Parcelable {
    val createdDateFormatted : String
        get() = DateFormat.getDateTimeInstance().format(created)
    val amountFormatted : String
        get() {
            return Utils.formatMoneyNumber(amount)
        }
}