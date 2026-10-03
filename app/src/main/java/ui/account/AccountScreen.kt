package com.example.onestopfootball.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AccountScreen(
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        if (isLoggedIn) {

            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "demo@onestopfootball.com"
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

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

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "You can browse One Stop Football without logging in."
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = onLoginClick
            ) {

                Text("Login")
            }
        }
    }
}