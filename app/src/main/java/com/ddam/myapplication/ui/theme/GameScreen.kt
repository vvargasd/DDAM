package com.ddam.myapplication.ui.theme


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseReference




@Composable
fun GameScreen(menuOption: MenuOption) {
    when (menuOption) {
        MenuOption.SINGLE_PLAYER -> SinglePlayerGame()
        MenuOption.TWO_PLAYERS -> TwoPlayerGame()
        MenuOption.ONLINE -> OnlineGame()
    }
}

@Composable
fun SinglePlayerGame() {
    // Estado del tablero, jugador actual, ganador y empate
    val boardState = remember { mutableStateOf(List(9) { "" }) }
    val currentPlayer = remember { mutableStateOf("X") } // El jugador siempre es "X"
    val winner = remember { mutableStateOf<String?>(null) }
    val isDraw = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Tablero de juego
        TicTacToeGrid(
            boardState = boardState.value,
            onCellClick = { index ->
                handleSinglePlayerMove(
                    index,
                    boardState,
                    currentPlayer,
                    winner,
                    isDraw
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
        )

        // Mensaje de estado del juego
        Text(
            text = gameStatus(winner, isDraw, currentPlayer),
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Botón para reiniciar el juego
        RestartButton(boardState, currentPlayer, winner, isDraw)
    }
}

@Composable
fun TwoPlayerGame() {
    val boardState = remember { mutableStateOf(List(9) { "" }) }
    val currentPlayer = remember { mutableStateOf("X") } // El jugador siempre es "X"
    val winner = remember { mutableStateOf<String?>(null) }
    val isDraw = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TicTacToeGrid(
            boardState = boardState.value,
            onCellClick = { index ->
                handlePlayerMove(
                    index,
                    boardState,
                    currentPlayer,
                    winner,
                    isDraw
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
        )

        Text(
            text = gameStatus(winner, isDraw, currentPlayer),
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        RestartButton(boardState, currentPlayer, winner, isDraw)
    }
}

data class GameData(
    val board: List<String> = List(9) { "" },
    val currentPlayer: String = "X",
    val player1: String? = null,
    val player2: String? = null,
    val winner: String? = null,
    val isDraw: Boolean = false
)



@Composable
fun OnlineGame() {
    val database = FirebaseDatabase.getInstance()
    val gamesRef = database.getReference("games")

    val roomId = remember { mutableStateOf<String?>(null) }
    val playerId = "Player_${System.currentTimeMillis()}"
    val roomList = remember { mutableStateOf<List<String>>(emptyList()) }
    val showRoomList = remember { mutableStateOf(false) }

    if (roomId.value == null) {
        // Pantalla inicial para crear o unirse a una sala
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tic Tac Toe Online", fontSize = 24.sp, modifier = Modifier.padding(bottom = 32.dp))

            // Botón para crear una sala
            Button(onClick = {
                roomId.value = createGameRoom(gamesRef, playerId)
            }) {
                Text("Crear Sala")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para mostrar la lista de salas
            Button(onClick = {
                fetchAvailableRooms(gamesRef) { rooms ->
                    roomList.value = rooms
                    showRoomList.value = true
                }
            }) {
                Text("Unirse a una Sala")
            }

            // Mostrar la lista de salas
            if (showRoomList.value) {
                Text("Salas disponibles:", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
                LazyColumn {
                    items(roomList.value) { room ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    joinGameRoom(gamesRef, room, playerId) { success ->
                                        if (success) {
                                            roomId.value= room
                                            showRoomList.value = false
                                        }
                                    }
                                }
                        ) {
                            Text("Sala: $room", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    } else {
        // Pantalla del juego una vez que el jugador se ha unido
        OnlineGameRoom(gamesRef, roomId.value!!, playerId)
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

private fun handlePlayerMove(
    index: Int,
    boardState: MutableState<List<String>>,
    currentPlayer: MutableState<String>,
    winner: MutableState<String?>,
    isDraw: MutableState<Boolean>
) {
    // Verifica si la celda está vacía y el juego no ha terminado
    if (boardState.value[index].isEmpty() && winner.value == null && !isDraw.value) {
        // Actualiza el tablero con el movimiento del jugador actual
        boardState.value = boardState.value.toMutableList().apply {
            this[index] = currentPlayer.value
        }

        // Comprueba si hay un ganador
        val detectedWinner = checkWinner(boardState.value)
        if (detectedWinner != null) {
            winner.value = detectedWinner
        } else if (isBoardFull(boardState.value)) {
            // Comprueba si el tablero está lleno (empate)
            isDraw.value = true
        } else {
            // Cambia el turno al otro jugador
            currentPlayer.value = if (currentPlayer.value == "X") "O" else "X"
        }
    }
}

private fun gameStatus(
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


private fun handleSinglePlayerMove(
    index: Int,
    boardState: MutableState<List<String>>,
    currentPlayer: MutableState<String>,
    winner: MutableState<String?>,
    isDraw: MutableState<Boolean>
) {
    // Verifica si la celda está vacía y el juego no ha terminado
    if (boardState.value[index].isEmpty() && winner.value == null && !isDraw.value) {
        // Jugador hace su movimiento
        boardState.value = boardState.value.toMutableList().apply { this[index] = currentPlayer.value }
        val detectedWinner = checkWinner(boardState.value)
        if (detectedWinner != null) {
            winner.value = detectedWinner
        } else if (isBoardFull(boardState.value)) {
            // Comprueba si el tablero está lleno (empate)
            isDraw.value = true
        } else {
            // Turno de la IA
            currentPlayer.value = "O"
            handleAIMove(boardState, currentPlayer, winner, isDraw)
        }
    }
}


private fun handleAIMove(
    boardState: MutableState<List<String>>,
    currentPlayer: MutableState<String>,
    winner: MutableState<String?>,
    isDraw: MutableState<Boolean>
) {
    val bestMove = findBestMove(boardState.value)
    if (bestMove != -1) {
        boardState.value = boardState.value.toMutableList().apply {
            this[bestMove] = "O" // La IA siempre juega con "O"
        }

        // Verificar si la IA gana o es empate
        val detectedWinner = checkWinner(boardState.value)
        if (detectedWinner != null) {
            winner.value = detectedWinner
        } else if (isBoardFull(boardState.value)) {
            isDraw.value = true
        } else {
            // Cambiar el turno de vuelta al jugador
            currentPlayer.value = "X"
        }
    }
}

private fun minimax(board: List<String>, depth: Int, isMaximizing: Boolean): Int {
    val winner = checkWinner(board)
    if (winner != null) {
        return when (winner) {
            "O" -> 10 - depth // La IA gana
            "X" -> depth - 10 // El jugador gana
            else -> 0         // Empate
        }
    }

    if (isBoardFull(board)) return 0 // Tablero lleno, empate

    return if (isMaximizing) {
        // Turno de la IA
        var bestScore = Int.MIN_VALUE
        for (i in board.indices) {
            if (board[i].isEmpty()) {
                val newBoard = board.toMutableList().apply { this[i] = "O" }
                val score = minimax(newBoard, depth + 1, false)
                bestScore = maxOf(bestScore, score)
            }
        }
        bestScore
    } else {
        // Turno del jugador
        var bestScore = Int.MAX_VALUE
        for (i in board.indices) {
            if (board[i].isEmpty()) {
                val newBoard = board.toMutableList().apply { this[i] = "X" }
                val score = minimax(newBoard, depth + 1, true)
                bestScore = minOf(bestScore, score)
            }
        }
        bestScore
    }
}

private fun findBestMove(board: List<String>): Int {
    var bestScore = Int.MIN_VALUE
    var bestMove = -1

    for (i in board.indices) {
        if (board[i].isEmpty()) {
            // Simula la jugada
            val newBoard = board.toMutableList().apply { this[i] = "O" }
            val moveScore = minimax(newBoard, 0, false)

            if (moveScore > bestScore) {
                bestScore = moveScore
                bestMove = i
            }
        }
    }
    return bestMove
}

// Actualizar movimiento en Firebase
fun makeMove(
    gamesRef: DatabaseReference,
    roomId: String,
    board: List<String>,
    index: Int,
    playerId: String
) {
    val updatedBoard = board.toMutableList()
    val currentPlayerSymbol = if (playerId.contains("Player_1")) "X" else "O"
    updatedBoard[index] = currentPlayerSymbol
    val nextPlayerSymbol = if (currentPlayerSymbol == "X") "O" else "X"

    gamesRef.child(roomId).updateChildren(
        mapOf(
            "board" to updatedBoard,
            "currentPlayer" to nextPlayerSymbol
        )
    )
}


// Pantalla principal del juego online
@Composable
fun OnlineGameRoom(gamesRef: DatabaseReference, roomId: String, playerId: String) {
    val boardState = remember { mutableStateOf(List(9) { "" }) }
    val currentPlayer = remember { mutableStateOf("X") }
    val winner = remember { mutableStateOf<String?>(null) }
    val isDraw = remember { mutableStateOf(false) }
    val playerSymbol = remember { mutableStateOf("") }

    // Escucha actualizaciones del juego
    LaunchedEffect(roomId) {
        gamesRef.child(roomId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val gameData = snapshot.getValue(GameData::class.java)
                if (gameData != null) {
                    playerSymbol.value = if (gameData.player1 == playerId) "X" else "O"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error: ${error.message}")
            }
        })
    }

    // UI del tablero y estado del juego
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Sala: $roomId", fontSize = 18.sp)

        TicTacToeGrid(
            boardState = boardState.value,
            onCellClick = { index ->
                if (boardState.value[index].isEmpty() &&
                    winner.value == null &&
                    currentPlayer.value == playerSymbol.value // Ahora comparamos símbolos
                ) {
                    makeMove(gamesRef, roomId, boardState.value, index, playerId)
                }
            }
        )

        Text(
            text = gameStatus(winner, isDraw, currentPlayer),
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// Crear una sala nueva
fun createGameRoom(gamesRef: DatabaseReference, playerId: String): String {
    val roomId = "room_${System.currentTimeMillis()}"
    val initialGame = GameData(
        player1 = playerId,
        player2 = null
    )
    gamesRef.child(roomId).setValue(initialGame)
    return roomId
}

// Unirse a una sala existente
fun joinGameRoom(
    gamesRef: DatabaseReference,
    roomId: String,
    playerId: String,
    onResult: (Boolean) -> Unit
) {
    gamesRef.child(roomId).child("player2").setValue(playerId)
        .addOnSuccessListener {
            onResult(true) // Se unió correctamente
        }
        .addOnFailureListener {
            onResult(false) // Error al unirse
        }
}

fun fetchAvailableRooms(
    gamesRef: DatabaseReference,
    onResult: (List<String>) -> Unit
) {
    gamesRef.get().addOnSuccessListener { snapshot ->
        val roomList = mutableListOf<String>()
        for (roomSnapshot in snapshot.children) {
            val roomId = roomSnapshot.key // El ID de la sala
            val roomData = roomSnapshot.getValue(GameData::class.java)

            // Solo mostrar salas que no estén llenas (player2 == null)
            if (roomId != null && roomData?.player2 == null) {
                roomList.add(roomId)
            }
        }
        onResult(roomList) // Devuelve la lista de IDs de salas disponibles
    }.addOnFailureListener {
        println("Error al obtener salas: ${it.message}")
        onResult(emptyList())
    }
}