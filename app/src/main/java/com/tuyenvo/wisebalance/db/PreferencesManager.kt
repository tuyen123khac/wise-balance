package com.tuyenvo.wisebalance.db

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

data class ExpensePreferences(val sheetId: String, val isSignedIn: Boolean)
@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private object PreferencesKeys {
        val SHEET_ID = preferencesKey<String>("SHEET_ID")
        val IS_SIGNED_IN = preferencesKey<Boolean>("IS_SIGNED_IN")
    }

    private val dataStore = context.createDataStore("expense_preference")

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if ( exception is IOException) {
                Log.e(TAG, "Error reading preferences - $exception")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferencesFlow ->
            val sheetId = preferencesFlow[PreferencesKeys.SHEET_ID] ?: ""
            val isSignedIn = preferencesFlow[PreferencesKeys.IS_SIGNED_IN] ?: false
            ExpensePreferences(sheetId, isSignedIn)
        }

    suspend fun setSheetId(sheetId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHEET_ID] = sheetId
        }
    }

    suspend fun setIsSignedIn(value : Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_SIGNED_IN] = value
        }
    }

}