package com.example.monument_hunting.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.monument_hunting.ui.composables.ComposeLoginView
import com.example.monument_hunting.ui.theme.MonumentHuntingTheme
import com.example.monument_hunting.view_models.LoginSignupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {

    val loginSignupViewModel: LoginSignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch{
            loginSignupViewModel.verifyToken()
        }
        setContent {
            MonumentHuntingTheme {
                ComposeLoginView()
            }
        }
    }


}