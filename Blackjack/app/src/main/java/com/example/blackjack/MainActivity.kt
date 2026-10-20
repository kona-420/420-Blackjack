package io.github.kona420.blackjack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.blackjack.ui.BlackjackScreen
import com.example.blackjack.viewmodel.BlackjackViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BlackjackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackjackScreen(viewModel = viewModel)
        }
    }
}
