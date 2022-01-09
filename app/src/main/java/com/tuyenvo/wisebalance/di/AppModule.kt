package com.tuyenvo.wisebalance.di

import android.app.Application
import androidx.room.Room
import com.tuyenvo.wisebalance.db.ExpenseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: ExpenseDatabase.Callback
    ) =
        Room.databaseBuilder(app, ExpenseDatabase::class.java, "expenses_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback) // This callback will be called after .build() method
            .build()

    @Provides
    fun provideExpenseDao(db: ExpenseDatabase) = db.getExpenseDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope