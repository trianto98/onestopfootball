package com.example.onestopfootball.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AccountScreen(
    username: String?,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (username != null) {

            Text(
                text = "Halo, $username!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text("Welcome to One Stop Football.")

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = onLogoutClick
            ) {
                Text("Logout")
            }

        } else {

            Text(
                text = "Guest User",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text("You can browse football content without an account.")

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onLoginClick
            ) {
                Text("Login")
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onRegisterClick
            ) {
                Text("Create Account")
            }
        }
    }
}