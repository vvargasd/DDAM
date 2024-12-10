package com.ddam.myapplication
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ddam.myapplication.ui.theme.TTTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TTTTheme {
                val boardState = rememberSaveable { mutableStateOf(List(9) { "" }) }
                val currentPlayer = rememberSaveable { mutableStateOf("X") }
                val winner = rememberSaveable { mutableStateOf<String?>(null) }
                val isDraw = rememberSaveable { mutableStateOf(false) }

                val configuration = LocalConfiguration.current
                val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

                if (isPortrait) {
                    PortraitLayout(
                        boardState = boardState,
                        currentPlayer = currentPlayer,
                        winner = winner,
                        isDraw = isDraw
                    )
                } else {
                    LandscapeLayout(
                        boardState = boardState,
                        currentPlayer = currentPlayer,
                        winner = winner,
                        isDraw = isDraw
                    )
                }
            }
        }
    }

    private fun makeRandomMove(
        boardState: MutableState<List<String>>,
        currentPlayer: MutableState<String>,
        winner: MutableState<String?>,
        isDraw: MutableState<Boolean>
    ) {
        if (winner.value == null && !isDraw.value) {
            val emptyCells = boardState.value.mapIndexedNotNull { index, cell ->
                if (cell.isEmpty()) index else null
            }

            if (emptyCells.isNotEmpty()) {
                val randomIndex = emptyCells.random()
                boardState.value = boardState.value.toMutableList().apply {
                    this[randomIndex] = "O"
                }

                val detectedWinner = checkWinner(boardState.value)
                if (detectedWinner != null) {
                    winner.value = detectedWinner
                } else if (isBoardFull(boardState.value)) {
                    isDraw.value = true
                } else {
                    currentPlayer.value = "X"
                }
            }
        }
    }

    private fun checkWinner(board: List<String>): String? {
        val winningCombinations = listOf(
            listOf(0, 1, 2), // Fila 1
            listOf(3, 4, 5), // Fila 2
            listOf(6, 7, 8), // Fila 3
            listOf(0, 3, 6), // Columna 1
            listOf(1, 4, 7), // Columna 2
            listOf(2, 5, 8), // Columna 3
            listOf(0, 4, 8), // Diagonal principal
            listOf(2, 4, 6)  // Diagonal secundaria
        )

        for (combination in winningCombinations) {
            val (a, b, c) = combination
            if (board[a] == board[b] && board[b] == board[c] && board[a].isNotEmpty()) {
                return board[a]
            }
        }
        return null
    }

    private fun isBoardFull(board: List<String>): Boolean {
        return board.none { it.isEmpty() }
    }


@Composable
fun PortraitLayout(
    boardState: MutableState<List<String>>,
    currentPlayer: MutableState<String>,
    winner: MutableState<String?>,
    isDraw: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TicTacToeGrid(
            boardState = boardState.value,
            onCellClick = { index -> handleMove(index, boardState, currentPlayer, winner, isDraw) },
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
        )
        Text(
            text = gameStatus(winner, isDraw, currentPlayer),
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Black
        )
        RestartButton(boardState, currentPlayer, winner, isDraw)
    }
}

@Composable
fun LandscapeLayout(
    boardState: MutableState<List<String>>,
    currentPlayer: MutableState<String>,
    winner: MutableState<String?>,
    isDraw: MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TicTacToeGrid(
            boardState = boardState.value,
            onCellClick = { index -> handleMove(index, boardState, currentPlayer, winner, isDraw) },
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = gameStatus(winner, isDraw, currentPlayer),
                fontSize = 20.sp,
                color = Color.Black
            )
            RestartButton(boardState, currentPlayer, winner, isDraw)
        }
    }
}

@Composable
fun RestartButton(
    boardState: MutableState<List<String>>,
    currentPlayer: MutableState<String>,
    winner: MutableState<String?>,
    isDraw: MutableState<Boolean>
) {
    Button(
        onClick = {
            boardState.value = List(9) { "" }
            currentPlayer.value = "X"
            winner.value = null
            isDraw.value = false
        },
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text("Reiniciar")
    }
}

fun gameStatus(
    winner: MutableState<String?>,
    isDraw: MutableState<Boolean>,
    currentPlayer: MutableState<String>
): String {
    return when {
        winner.value != null -> "¡Ganador: ${winner.value}!"
        isDraw.value -> "¡Es un empate!"
        else -> "Turno del jugador: ${currentPlayer.value}"
    }
}

@Composable
fun TicTacToeGrid(
    boardState: List<String>,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        for (row in 0..2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0..2) {
                    val index = row * 3 + col
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .border(2.dp, Color.Black)
                            .clickable { onCellClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = boardState[index],
                            fontSize = 24.sp,
                            color = when (boardState[index]) {
                                "X" -> Color.Blue
                                "O" -> Color.Red
                                else -> Color.Black
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun handleMove(
    index: Int,
    boardState: MutableState<List<String>>,
    currentPlayer: MutableState<String>,
    winner: MutableState<String?>,
    isDraw: MutableState<Boolean>
) {
    if (winner.value == null && !isDraw.value && boardState.value[index].isEmpty()) {
        boardState.value = boardState.value.toMutableList().apply { this[index] = currentPlayer.value }
        val detectedWinner = MainActivity().checkWinner(boardState.value)
        if (detectedWinner != null) {
            winner.value = detectedWinner
        } else if (MainActivity().isBoardFull(boardState.value)) {
            isDraw.value = true
        } else {
            currentPlayer.value = if (currentPlayer.value == "X") "O" else "X"
        }
    }
}
}
