package com.example.poker_hand_rankings

import android.media.Image
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class CardSuit(val suitName: String) { // 카드의 이름
    SPADES("spades"),
    DIAMONDS("diamonds"),
    HEARTS("hearts"),
    CLUBS("clubs")
}

enum class CardValue(val valueName: String) { // 카드의 숫자
    ACE("ace"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("jack"),
    QUEEN("queen"),
    KING("king")
}

data class Card(val suit: CardSuit, val value: CardValue)

private fun getCardName(c: Card): String { // enum class 에서 해당되는 값을 찾아와 image resource 이름으로 반환
    when (c.value.valueName) {
        "jack","queen","king" -> return "c_${c.value.valueName}_of_${c.suit.suitName}2"
    }
    return "c_${c.value.valueName}_of_${c.suit.suitName}"
}

class LottoViewModel : ViewModel() {
    private var _cards = MutableLiveData<Array<String>>()
    val cards : LiveData<Array<String>>
        get() = _cards

    fun shuffle() {
        val newCards = Array<String>(5) { "" }  // 임시
        var tempStr = ""

        for (i in newCards.indices) {
            do {
                var randomSuit = CardSuit.values().random()
                var randomValue = CardValue.values().random()
                var randomCard = Card(randomSuit, randomValue)
                tempStr = getCardName(randomCard)
            } while (newCards.contains(tempStr))

            newCards[i] = tempStr
        }
        newCards.sort()

        _cards.value = newCards
    }
}