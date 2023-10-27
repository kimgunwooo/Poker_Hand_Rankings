package com.example.poker_hand_rankings

import android.media.Image
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Locale

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
    private var _handRanking = MutableLiveData<String>()
    val cards : LiveData<Array<String>>
        get() = _cards

    val handRanking : LiveData<String>
        get() = _handRanking

    fun shuffle() {
        // 앞에 5개는 카드 이미지의 이름, 마지막 인자에는 족보 값이 들어간다.
        val newCards = Array(5) { "" }

        // 족보 계산에 사용할 card 정보 저장할 변할 수 있는 List
        var cardSV : MutableList<Card> = mutableListOf()

        // 초기화
        var randomSuit : CardSuit
        var randomValue : CardValue
        var randomCard : Card
        var tempStr = ""

        for (i in newCards.indices) {
            do {
                randomSuit = CardSuit.values().random()// 각 enum class 에서 무작위 값 가져오기
                randomValue = CardValue.values().random()
                randomCard = Card(randomSuit, randomValue) // Card 객체 만듦
                tempStr = getCardName(randomCard) // Card 객체로 해당 되는 image resource 이름으로 바꿔서 가져옴
            } while (newCards.contains(tempStr)) // 중복 체크
            cardSV.add(randomCard)
            newCards[i] = tempStr // 임시 객체에 저장
        }

        newCards.sort() // 낮은 값부터 출력

        // 족보 알고리즘
        var handRanking = checkPokerHandRanking(cardSV)
        Log.i("TEST!!!!", handRanking)
        _cards.value = newCards
        _handRanking.value = handRanking
    }

    private fun checkPokerHandRanking(cardSV: MutableList<Card>): String {
        // 카드 값과 슈트를 분리
        val values = cardSV.map { it.value }
        val suits = cardSV.map { it.suit }

        // 카드 값 카운트 맵
        val valueCountMap = values.groupingBy { it }.eachCount()

        // 슈트 카운트 맵
        val suitCountMap = suits.groupingBy { it }.eachCount()

        // 카드 값 오름차순으로 정렬
        val sortedValues = values.sortedBy { it.ordinal }

        // 포커 족보 확인

        // 스트레이트 플러시 (Straight Flush)
        if (isStraight(sortedValues) && isFlush(suitCountMap)) {
            val highCardValue = sortedValues.last()
            return "Straight Flush: ${highCardValue.valueName}"
        }

        // 포카드 (Four of a Kind)
        for ((value, count) in valueCountMap) {
            if (count == 4) {
                return "Four of a Kind: ${value.valueName}"
            }
        }

        // 풀 하우스 (Full House)
        val threeOfAKindValue = valueCountMap.entries.find { it.value == 3 }?.key
        val pairValue = valueCountMap.entries.find { it.value == 2 }?.key
        if (threeOfAKindValue != null && pairValue != null) {
            return "Full House: ${threeOfAKindValue.valueName} and ${pairValue.valueName}"
        }

        // 플러시 (Flush)
        if (isFlush(suitCountMap)) {
            val highCardValue = sortedValues.last()
            return "Flush: ${highCardValue.valueName}"
        }

        // 스트레이트 (Straight)
        if (isStraight(sortedValues)) {
            val highCardValue = sortedValues.last()
            return "Straight: ${highCardValue.valueName}"
        }

        // 쓰리 오브 어 카인드 (Three of a Kind)
        val threeOfAKindValues = valueCountMap.entries.filter { it.value == 3 }.map { it.key.valueName }
        if (threeOfAKindValues.isNotEmpty()) {
            return "Three of a Kind: ${threeOfAKindValues.joinToString(", ")}"
        }

        // 투 페어 (Two Pair)
        val pairValues = valueCountMap.entries.filter { it.value == 2 }.map { it.key.valueName }
        if (pairValues.size == 2) {
            return "Two Pair: ${pairValues.joinToString(", ")}"
        }

        // 원 페어 (One Pair)
        val pairValue2 = valueCountMap.entries.find { it.value == 2 }?.key
        if (pairValue2 != null) {
            return "One Pair: ${pairValue2.valueName}"
        }

        // 하이 카드 (High Card)
        val highCardValue = values.maxOrNull()
        return "High Card: ${highCardValue?.valueName ?: "No cards"}"

    }
    // 스트레이트 확인
    private fun isStraight(sortedValues: List<CardValue>): Boolean {
        for (i in 0 until sortedValues.size - 1) {
            if (sortedValues[i].ordinal + 1 != sortedValues[i + 1].ordinal) {
                return false
            }
        }
        return true
    }

    // 플러시 확인
    private fun isFlush(suitCountMap: Map<CardSuit, Int>): Boolean {
        for (count in suitCountMap.values) {
            if (count == 5) {
                return true
            }
        }
        return false
    }

    // 풀 하우스 확인
    private fun isFullHouse(valueCountMap: Map<CardValue, Int>): Boolean {
        var hasThreeOfAKind = false
        var hasOnePair = false

        for ((_, count) in valueCountMap) {
            if (count == 3) {
                hasThreeOfAKind = true
            } else if (count == 2) {
                hasOnePair = true
            }
        }

        return hasThreeOfAKind && hasOnePair
    }
}