package com.ansh.awsnotifier.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.ansh.awsnotifier.R
import com.ansh.awsnotifier.security.BiometricHelper
import com.ansh.awsnotifier.ui.theme.AppTheme

class BiometricAuthActivity : AppCompatActivity() {

    private lateinit var biometricHelper: BiometricHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                SplashBackdrop()
            }
        }

        biometricHelper = BiometricHelper(this)

        startAuthentication()
    }

    private fun startAuthentication() {
        biometricHelper.authenticate(
            onSuccess = {
                navigateToMain()
            },
            onError = { errorMsg ->
                Toast.makeText(this, "Authentication required: $errorMsg", Toast.LENGTH_SHORT)
                    .show()
                // If the user cancels the prompt or too many attempts, we close the app.
                finishAffinity()
            },
            onFailed = {
                // Biometric recognized but rejected (wrong finger). The prompt usually stays open,
                // so we might not need to do anything here unless we want to count attempts.
            }
        )
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        // Clear flags so user can't go back to Auth screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
private fun SplashBackdrop() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        setImageResource(R.mipmap.ic_launcher)
                    }
                },
                modifier = Modifier.size(120.dp)
            )
            Text(
                text = "AWS Notifier",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp)
            )
            CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
        }
    }
}
