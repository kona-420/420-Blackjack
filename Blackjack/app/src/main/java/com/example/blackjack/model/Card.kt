package com.example.blackjack.model

enum class Suit(val symbol: String, val isRed: Boolean) {
    SPADES("♠", false),
    HEARTS("♥", true),
    DIAMONDS("♦", true),
    CLUBS("♣", false)
}

enum class Rank(val label: String, val values: List<Int>) {
    TWO("2", listOf(2)),
    THREE("3", listOf(3)),
    FOUR("4", listOf(4)),
    FIVE("5", listOf(5)),
    SIX("6", listOf(6)),
    SEVEN("7", listOf(7)),
    EIGHT("8", listOf(8)),
    NINE("9", listOf(9)),
    TEN("10", listOf(10)),
    JACK("J", listOf(10)),
    QUEEN("Q", listOf(10)),
    KING("K", listOf(10)),
    ACE("A", listOf(1, 11))
}

data class Card(
    val suit: Suit,
    val rank: Rank
) {
    val isRed: Boolean get() = suit.isRed
}

fun createDeck(): MutableList<Card> {
    val deck = mutableListOf<Card>()
    for (suit in Suit.entries) {
        for (rank in Rank.entries) {
            deck.add(Card(suit, rank))
        }
    }
    deck.shuffle()
    return deck
}

fun calculateHandValue(cards: List<Card>): Int {
    if (cards.isEmpty()) return 0
    var total = 0
    var aces = 0
    for (card in cards) {
        if (card.rank == Rank.ACE) {
            aces++
            total += 11
        } else {
            total += card.rank.values.first()
        }
    }
    while (total > 21 && aces > 0) {
        total -= 10
        aces--
    }
    return total
}

fun isBlackjack(cards: List<Card>): Boolean {
    if (cards.size != 2) return false
    val value = calculateHandValue(cards)
    return value == 21
}
