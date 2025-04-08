package com.ssafy.locket.presentation.login

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.locket.presentation.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LoginActivity"
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val notificationCheckViewModel: NotificationCheckViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra("notification")?.let {
            notificationCheckViewModel.setIconClicked()
            Log.d(TAG, "onCreate: setIconClicked")
        }
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        intent.getStringExtra("notification")?.let {
            notificationCheckViewModel.setIconClicked()
            Log.d(TAG, "onNewIntent: setIconClicked")
        }
    }
}