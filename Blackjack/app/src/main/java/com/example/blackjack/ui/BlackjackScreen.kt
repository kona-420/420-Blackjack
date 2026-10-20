package com.example.blackjack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blackjack.model.Card
import com.example.blackjack.model.calculateHandValue
import com.example.blackjack.viewmodel.BlackjackViewModel
import com.example.blackjack.viewmodel.GameState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlackjackScreen(viewModel: BlackjackViewModel) {
    var showStatsDialog by remember { mutableStateOf(false) }

    val feltGreen = Color(0xFF0C3B2E)
    val feltDark = Color(0xFF06231A)
    val goldAccent = Color(0xFFFFD700)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🃏", fontSize = 24.sp)
                        Text(
                            "Royal Blackjack",
                            fontWeight = FontWeight.Bold,
                            color = goldAccent
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showStatsDialog = true }) {
                        Text("📊", fontSize = 22.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = feltDark
                )
            )
        },
        containerColor = feltGreen
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(feltGreen, feltDark)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Info Bar (Chips & Bet summary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, goldAccent.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🪙", fontSize = 18.sp)
                            Text(
                                "Chips: $${viewModel.chips}",
                                color = goldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, goldAccent.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("💰", fontSize = 18.sp)
                            Text(
                                "Bet: $${viewModel.currentBet}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Dealer Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val dealerScore = if (viewModel.gameState == GameState.PLAYING && viewModel.dealerHand.isNotEmpty()) {
                        calculateHandValue(listOf(viewModel.dealerHand.first()))
                    } else {
                        calculateHandValue(viewModel.dealerHand)
                    }

                    Text(
                        text = "DEALER ${if (viewModel.gameState != GameState.PLAYING && viewModel.dealerHand.isNotEmpty()) "($dealerScore)" else if (viewModel.dealerHand.isNotEmpty()) "(?)" else ""}",
                        color = goldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    CardRow(
                        cards = viewModel.dealerHand,
                        hideSecondCard = viewModel.gameState == GameState.PLAYING
                    )
                }

                // Message / Status Banner
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, goldAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = viewModel.message,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Player Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val playerScore = calculateHandValue(viewModel.playerHand)
                    Text(
                        text = "PLAYER ${if (viewModel.playerHand.isNotEmpty()) "($playerScore)" else ""}",
                        color = goldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    CardRow(cards = viewModel.playerHand, hideSecondCard = false)
                }

                // Controls & Betting Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (viewModel.gameState) {
                        GameState.BETTING -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Select Your Bet",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    listOf(10, 25, 50, 100, 250, 500).forEach { amount ->
                                        ChipButton(
                                            amount = amount,
                                            isSelected = viewModel.currentBet == amount,
                                            enabled = amount <= viewModel.chips,
                                            onClick = { viewModel.setBet(amount) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (viewModel.chips <= 0) {
                                        Button(
                                            onClick = { viewModel.rebuy() },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp),
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Text("Re-buy $1,000 Chips", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.startRound() },
                                            colors = ButtonDefaults.buttonColors(containerColor = goldAccent),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(54.dp),
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Text(
                                                "DEAL CARDS",
                                                color = Color.Black,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        GameState.PLAYING -> {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { viewModel.hit() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(54.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("HIT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.stand() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(54.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("STAND", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }

                                if (viewModel.playerHand.size == 2 && viewModel.chips >= viewModel.currentBet) {
                                    Button(
                                        onClick = { viewModel.doubleDown() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(54.dp),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text("2x", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        GameState.DEALER_TURN -> {
                            Text(
                                "Dealer is drawing...",
                                color = goldAccent,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        GameState.ROUND_OVER -> {
                            Button(
                                onClick = {
                                    if (viewModel.chips <= 0) {
                                        viewModel.rebuy()
                                    } else {
                                        viewModel.startRound()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = goldAccent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    if (viewModel.chips <= 0) "RE-BUY CHIPS" else "PLAY NEXT ROUND",
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showStatsDialog) {
        AlertDialog(
            onDismissRequest = { showStatsDialog = false },
            title = { Text("Game Statistics", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val winRate = if (viewModel.gamesPlayed > 0) {
                        (viewModel.wins * 100) / viewModel.gamesPlayed
                    } else 0

                    Text("Games Played: ${viewModel.gamesPlayed}")
                    Text("Wins: ${viewModel.wins} ($winRate%)")
                    Text("Losses: ${viewModel.losses}")
                    Text("Pushes: ${viewModel.pushes}")
                    Text("Current Chips: $${viewModel.chips}")
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatsDialog = false }) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.resetStats()
                    showStatsDialog = false
                }) {
                    Text("Reset Stats", color = Color.Red)
                }
            }
        )
    }
}

@Composable
fun CardRow(cards: List<Card>, hideSecondCard: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy((-24).dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        if (cards.isEmpty()) {
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(96.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            )
        } else {
            cards.forEachIndexed { index, card ->
                val isHidden = hideSecondCard && index == 1
                CardView(card = card, isHidden = isHidden)
            }
        }
    }
}

@Composable
fun CardView(card: Card, isHidden: Boolean) {
    val cardColor = if (card.isRed) Color(0xFFD32F2F) else Color(0xFF212121)

    Box(
        modifier = Modifier
            .width(68.dp)
            .height(100.dp)
            .shadow(6.dp, RoundedCornerShape(8.dp))
            .background(if (isHidden) Color(0xFF1565C0) else Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        if (isHidden) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("♠♥", color = Color.White, fontSize = 20.sp)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${card.rank.label}\n${card.suit.symbol}",
                    color = cardColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 14.sp
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = card.suit.symbol,
                        color = cardColor,
                        fontSize = 28.sp
                    )
                }
                Text(
                    text = "${card.rank.label}\n${card.suit.symbol}",
                    color = cardColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun ChipButton(
    amount: Int,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.5f)
    val backgroundColor = when (amount) {
        10 -> Color(0xFFB71C1C)
        25 -> Color(0xFF1B5E20)
        50 -> Color(0xFF0D47A1)
        100 -> Color(0xFF4A148C)
        250 -> Color(0xFFE65100)
        else -> Color(0xFF37474F)
    }

    Box(
        modifier = modifier
            .height(44.dp)
            .shadow(if (isSelected) 6.dp else 2.dp, CircleShape)
            .clip(CircleShape)
            .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.4f))
            .border(if (isSelected) 2.5.dp else 1.dp, borderColor, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$$amount",
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.4f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
