package com.ddam.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.ddam.myapplication.ui.theme.GameScreen
import com.ddam.myapplication.ui.theme.MenuOption
import com.ddam.myapplication.ui.theme.MenuScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val menuOption = remember { mutableStateOf<MenuOption?>(null) }

            if (menuOption.value == null) {
                MenuScreen(onOptionSelected = { option -> menuOption.value = option })
            } else {
                GameScreen(menuOption.value!!)
            }
        }
    }
}