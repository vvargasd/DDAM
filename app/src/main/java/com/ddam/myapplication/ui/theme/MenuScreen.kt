package com.ddam.myapplication.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class MenuOption {
    SINGLE_PLAYER, TWO_PLAYERS, ONLINE
}

@Composable
fun MenuScreen(onOptionSelected: (MenuOption) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tic Tac Toe",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = { onOptionSelected(MenuOption.SINGLE_PLAYER) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Un jugador")
        }
        Button(
            onClick = { onOptionSelected(MenuOption.TWO_PLAYERS) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Dos jugadores")
        }
        Button(
            onClick = { onOptionSelected(MenuOption.ONLINE) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Online")
        }
    }
}