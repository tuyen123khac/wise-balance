package com.tuyenvo.wisebalance.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.tuyenvo.wisebalance.R
import com.tuyenvo.wisebalance.databinding.ActivitySignInBinding
import com.tuyenvo.wisebalance.db.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

@AndroidEntryPoint
class SignInActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val TAG = "SignInActivity"

    companion object {
        private const val REQUEST_SIGN_IN = 1
    }

    private lateinit var binding: ActivitySignInBinding
    private val preferencesManager : PreferencesManager
        get() {
            return PreferencesManager(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpView()
        setUpListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    private fun setUpView() {
        supportActionBar?.hide()
    }

    private fun setUpListener() {
        binding.signInWithGoogle.setOnClickListener {
            requestSignIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener { account ->
                        val scopes = listOf(SheetsScopes.SPREADSHEETS)
                        val credential = GoogleAccountCredential.usingOAuth2(this, scopes)
                        credential.selectedAccount = account.account

                        val gsonFactory = GsonFactory.getDefaultInstance()
                        val httpTransport = NetHttpTransport()
                        val service = Sheets.Builder(httpTransport, gsonFactory, credential)
                            .setApplicationName(getString(R.string.app_name))
                            .build()

                        createSpreadsheet(service)
                    }
                    .addOnFailureListener {e ->
                        Log.e(TAG, "onActivityResult: exception: $e")
                    }
            }
        }
    }


    private fun createSpreadsheet(service : Sheets) {
        var spreadsheet = Spreadsheet()
            .setProperties(SpreadsheetProperties().setTitle(getString(R.string.app_name)))

        launch(Dispatchers.Default) {
            Log.e(TAG, "createSpreadsheet: previousid: ${preferencesManager.preferencesFlow.first()}")
            if (preferencesManager.preferencesFlow.first().sheetId.isEmpty()) {
                spreadsheet = service.spreadsheets().create(spreadsheet).execute()
                preferencesManager.setSheetId(spreadsheet.spreadsheetId)
                Log.e(TAG, "createSpreadsheet: ID: ${spreadsheet.spreadsheetId}")
                Log.e(TAG, "createSpreadsheet: URL: ${spreadsheet.spreadsheetUrl}")
                Log.e(TAG, "createSpreadsheet: properties: ${spreadsheet.properties}")
            }
        }
    }

    private fun requestSignIn() {
        GoogleSignIn.getLastSignedInAccount(this)?.also { account ->
            Log.e(TAG, "requestSignIn: getLastSignedInAccount: ${account.displayName}")
        }


        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(SheetsScopes.SPREADSHEETS))
            .build()

        val client = GoogleSignIn.getClient(this, signInOptions)
        startActivityForResult(client.signInIntent, REQUEST_SIGN_IN)
    }
}