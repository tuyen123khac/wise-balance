package com.tuyenvo.wisebalance.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tuyenvo.wisebalance.di.ApplicationScope
import com.tuyenvo.wisebalance.enums.ExpenseType
import com.tuyenvo.wisebalance.models.ExpenseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [ExpenseItem::class], version = 1)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun getExpenseDao() : ExpenseDao

    class Callback @Inject constructor(
        private val database: Provider<ExpenseDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().getExpenseDao()

            applicationScope.launch {
                dao.upsert(ExpenseItem("Nap card", ExpenseType.WASTED, 15000.0,"Lien1 Minh"))
                dao.upsert(ExpenseItem("Shopping", ExpenseType.NICE_TO_HAVE, 150000.500, "Wee4kend"))
                dao.upsert(ExpenseItem("Buy food", ExpenseType.MUST_HAVE, 1000000.0, "Buy food"))
            }
        }
    }
}