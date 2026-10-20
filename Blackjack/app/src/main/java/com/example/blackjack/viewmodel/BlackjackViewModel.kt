package com.example.blackjack.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.blackjack.model.Card
import com.example.blackjack.model.Rank
import com.example.blackjack.model.calculateHandValue
import com.example.blackjack.model.createDeck
import com.example.blackjack.model.isBlackjack

enum class GameState {
    BETTING,
    PLAYING,
    DEALER_TURN,
    ROUND_OVER
}

enum class GameResult {
    NONE,
    WIN,
    LOSS,
    PUSH,
    BLACKJACK,
    DEALER_BUST,
    PLAYER_BUST
}

class BlackjackViewModel : ViewModel() {
    var deck: MutableList<Card> = mutableListOf()
        private set

    var playerHand by mutableStateOf<List<Card>>(emptyList())
        private set

    var dealerHand by mutableStateOf<List<Card>>(emptyList())
        private set

    var gameState by mutableStateOf(GameState.BETTING)
        private set

    var gameResult by mutableStateOf(GameResult.NONE)
        private set

    var message by mutableStateOf("Place your bet to start!")
        private set

    var chips by mutableStateOf(1000)
        private set

    var currentBet by mutableStateOf(50)
        private set

    var gamesPlayed by mutableStateOf(0)
        private set
    var wins by mutableStateOf(0)
        private set
    var losses by mutableStateOf(0)
        private set
    var pushes by mutableStateOf(0)
        private set

    init {
        resetDeck()
    }

    private fun resetDeck() {
        deck = createDeck()
    }

    private fun drawCard(): Card {
        if (deck.size < 15) {
            resetDeck()
        }
        return deck.removeAt(0)
    }

    fun setBet(amount: Int) {
        if (gameState == GameState.BETTING) {
            if (amount <= chips && amount > 0) {
                currentBet = amount
            }
        }
    }

    fun adjustBet(delta: Int) {
        if (gameState == GameState.BETTING) {
            val newBet = (currentBet + delta).coerceIn(10, chips.coerceAtLeast(10))
            currentBet = newBet
        }
    }

    fun startRound() {
        if (chips <= 0) {
            message = "You are out of chips! Re-buy to continue."
            return
        }
        if (currentBet > chips) {
            currentBet = chips
        }

        chips -= currentBet
        playerHand = emptyList()
        dealerHand = emptyList()
        gameResult = GameResult.NONE

        // Deal initial cards: Player, Dealer, Player, Dealer
        val pCards = mutableListOf(drawCard(), drawCard())
        val dCards = mutableListOf(drawCard(), drawCard())

        playerHand = pCards
        dealerHand = dCards
        gameState = GameState.PLAYING

        // Check for immediate blackjack
        val playerBJ = isBlackjack(pCards)
        val dealerBJ = isBlackjack(dCards)

        if (playerBJ || dealerBJ) {
            gameState = GameState.ROUND_OVER
            gamesPlayed++
            if (playerBJ && dealerBJ) {
                gameResult = GameResult.PUSH
                chips += currentBet
                pushes++
                message = "Both have Blackjack! Push."
            } else if (playerBJ) {
                gameResult = GameResult.BLACKJACK
                val payout = (currentBet * 2.5).toInt()
                chips += payout
                wins++
                message = "Blackjack! You win 3:2 payout! 🎉"
            } else {
                gameResult = GameResult.LOSS
                losses++
                message = "Dealer has Blackjack. You lose."
            }
        } else {
            message = "Your turn. Choose Hit or Stand."
        }
    }

    fun hit() {
        if (gameState != GameState.PLAYING) return

        val newCard = drawCard()
        playerHand = playerHand + newCard
        val playerValue = calculateHandValue(playerHand)

        if (playerValue > 21) {
            gameState = GameState.ROUND_OVER
            gameResult = GameResult.PLAYER_BUST
            gamesPlayed++
            losses++
            message = "Bust! You went over 21. Dealer wins."
        } else if (playerValue == 21) {
            stand()
        }
    }

    fun doubleDown() {
        if (gameState != GameState.PLAYING || playerHand.size != 2) return
        if (chips < currentBet) {
            message = "Not enough chips to Double Down!"
            return
        }

        chips -= currentBet
        currentBet *= 2

        val newCard = drawCard()
        playerHand = playerHand + newCard
        val playerValue = calculateHandValue(playerHand)

        if (playerValue > 21) {
            gameState = GameState.ROUND_OVER
            gameResult = GameResult.PLAYER_BUST
            gamesPlayed++
            losses++
            message = "Bust on Double Down! Dealer wins."
        } else {
            stand()
        }
    }

    fun stand() {
        if (gameState != GameState.PLAYING) return
        gameState = GameState.DEALER_TURN
        message = "Dealer's turn..."

        dealerPlay()
    }

    private fun dealerPlay() {
        var dValue = calculateHandValue(dealerHand)
        // Dealer hits on soft 17 or < 17
        while (dValue < 17) {
            val newCard = drawCard()
            dealerHand = dealerHand + newCard
            dValue = calculateHandValue(dealerHand)
        }

        evaluateGame()
    }

    private fun evaluateGame() {
        gameState = GameState.ROUND_OVER
        gamesPlayed++

        val pValue = calculateHandValue(playerHand)
        val dValue = calculateHandValue(dealerHand)

        when {
            dValue > 21 -> {
                gameResult = GameResult.DEALER_BUST
                chips += currentBet * 2
                wins++
                message = "Dealer busts ($dValue)! You win! 🏆"
            }
            pValue > dValue -> {
                gameResult = GameResult.WIN
                chips += currentBet * 2
                wins++
                message = "You win! ($pValue vs Dealer $dValue) 🎉"
            }
            pValue < dValue -> {
                gameResult = GameResult.LOSS
                losses++
                message = "Dealer wins. ($dValue vs Your $pValue)"
            }
            else -> {
                gameResult = GameResult.PUSH
                chips += currentBet
                pushes++
                message = "Push! Tie game ($pValue)."
            }
        }
    }

    fun rebuy() {
        if (chips <= 0) {
            chips = 1000
            currentBet = 50
            message = "Re-bought $1,000 chips! Good luck!"
        }
    }

    fun resetStats() {
        gamesPlayed = 0
        wins = 0
        losses = 0
        pushes = 0
    }
}
