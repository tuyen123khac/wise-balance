package com.tuyenvo.wisebalance.util

import com.tuyenvo.wisebalance.enums.ExpenseType
import java.text.DecimalFormat
import java.util.*

val <T> T.exhaustive: T
    get() = this

object Utils{
    fun convertExpenseTypeToString(type: ExpenseType?): String {
        if (type == null) {
            return ""
        }
        return type.toString().replace("_", " ").lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }
    }

    fun convertStringToExpenseType(string: String): ExpenseType? {
        return when(string) {
            "Must have" -> { ExpenseType.MUST_HAVE
            }

            "Nice to have" -> {
                ExpenseType.NICE_TO_HAVE
            }

            "Wasted" -> {
                ExpenseType.WASTED
            }

            else -> null
        }
    }

    fun formatMoneyNumber(amount: Double) : String {
        if (amount == 0.0) {
            return "0.0"
        }
        val digitFormat = DecimalFormat("#,###.##")
        return digitFormat.format(amount)
    }
}
