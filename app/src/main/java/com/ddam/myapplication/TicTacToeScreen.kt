package com.ddam.myapplication

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun TicTacToeScreen() {
    val configuration = LocalConfiguration.current

    // Detectar orientación
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    // Diseño adaptado según la orientación
    if (isPortrait) {
        PortraitLayout()
    } else {
        LandscapeLayout()
    }
}

@Composable
fun PortraitLayout() {
    // Diseño para modo retrato
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TicTacToeGrid()
        Spacer(modifier = Modifier.height(16.dp))
        PlayerTurnDisplay()
        Spacer(modifier = Modifier.height(8.dp))
        RestartButton()
    }
}

@Composable
fun LandscapeLayout() {
    // Diseño para modo paisaje
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            TicTacToeGrid()
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerTurnDisplay()
            Spacer(modifier = Modifier.height(16.dp))
            RestartButton()
        }
    }
}

@Composable
fun TicTacToeGrid() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Espacio reservado para la cuadrícula
    }
}

@Composable
fun PlayerTurnDisplay() {
    Text("Turno del jugador")
}

@Composable
fun RestartButton() {
    Button(onClick = { /* Lógica de reinicio */ }) {
        Text("Reiniciar")
    }
}