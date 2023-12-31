package com.example.poker_hand_rankings

import android.util.Log
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

class PokerViewModel : ViewModel() {
    private var _cards = MutableLiveData<Array<String>>()
    private var _handRanking = MutableLiveData<String>()
    private var _counts = MutableLiveData<Array<Int>>()
    val cards : LiveData<Array<String>>
        get() = _cards

    val handRanking : LiveData<String>
        get() = _handRanking

    val counts : LiveData<Array<Int>>
        get() = _counts

    // Simulate 에 쓰일 족보 갯수 정리
    private lateinit var newCounts : Array<Int>

    fun shuffle(times: Int) {
        newCounts = Array(9){0}
        repeat(times) {
            // 앞에 5개는 카드 이미지의 이름, 마지막 인자에는 족보 값이 들어간다.
            val newCards = Array(5) { "" }

            // 족보 계산에 사용할 card 정보 저장할 변할 수 있는 List
            var cardSV: MutableList<Card> = mutableListOf()

            // 초기화
            var randomSuit: CardSuit
            var randomValue: CardValue
            var randomCard: Card
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
            var handRanking = checkPokerHandRanking(cardSV, newCounts)

            Log.i("TEST!!!!", handRanking)

            // 적용
            _cards.value = newCards
            _handRanking.value = handRanking
            _counts.value = newCounts
        }
    }
    // 각 족보의 우선순위를 정의한 맵
    private val handRankingPriority = mapOf(
        "Straight Flush" to 9,
        "Four of a Kind" to 8,
        "Full House" to 7,
        "Flush" to 6,
        "Straight" to 5,
        "Three of a Kind" to 4,
        "Two Pair" to 3,
        "One Pair" to 2,
        "High Card" to 1
    )

    // 누가 이겼는지 결과를 반환하는 함수
    fun determineWinner(userHand: String, aiHand: String): String {
        val (userPriority, userHandName) = extractHandName(userHand)
        val (aiPriority, aiHandName) = extractHandName(aiHand)
        Log.i("test", "$userPriority+$aiPriority")
        val userHandPriority = handRankingPriority[userPriority] ?: 0
        val aiHandPriority = handRankingPriority[aiPriority] ?: 0

        return when {
            userHandPriority > aiHandPriority -> "사용자 승리"
            userHandPriority < aiHandPriority -> "AI 승리"
            else -> compareEqualHands(userHandName, aiHandName)
        }
    }


    // handRanking의 결과가 들어오면 ':'를 기준으로 족보 값과, 숫자 값을 비교하는 함수
    private fun extractHandName(handRanking: String): Pair<String, String> {
        val colonIndex = handRanking.indexOf(":")
        if (colonIndex != -1) {
            val beforeColon = handRanking.substring(0, colonIndex).trim()
            val afterColon = handRanking.substring(colonIndex + 1).trim()
            return Pair(beforeColon, afterColon)
        } else {
            val trimmedHandRanking = handRanking.trim()
            return Pair(trimmedHandRanking, trimmedHandRanking)
        }
    }

    val cardValuePriority = mapOf(
        "2" to 2,
        "3" to 3,
        "4" to 4,
        "5" to 5,
        "6" to 6,
        "7" to 7,
        "8" to 8,
        "9" to 9,
        "10" to 10,
        "jack" to 11,
        "queen" to 12,
        "king" to 13,
        "ace" to 14
    )

    private fun compareEqualHands(userHandName: String, aiHandName: String): String {
        val userPriority = cardValuePriority[userHandName] ?: 0
        val aiPriority = cardValuePriority[aiHandName] ?: 0

        return when {
            userPriority > aiPriority -> "사용자 승리"
            userPriority < aiPriority -> "AI 승리"
            else -> "무승부"
        }
    }



    private fun checkPokerHandRanking(cardSV: MutableList<Card>, newCounts: Array<Int>): String {
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
            newCounts[0]++
            return "Straight Flush: ${highCardValue.valueName}"
        }

        // 포카드 (Four of a Kind)
        for ((value, count) in valueCountMap) {
            if (count == 4) {
                newCounts[1]++
                return "Four of a Kind: ${value.valueName}"
            }
        }

        // 풀 하우스 (Full House)
        val threeOfAKindValue = valueCountMap.entries.find { it.value == 3 }?.key
        val pairValue = valueCountMap.entries.find { it.value == 2 }?.key
        if (threeOfAKindValue != null && pairValue != null) {
            newCounts[2]++
            return "Full House: ${threeOfAKindValue.valueName} and ${pairValue.valueName}"
        }

        // 플러시 (Flush)
        if (isFlush(suitCountMap)) {
            val highCardValue = sortedValues.last()
            newCounts[3]++
            return "Flush: ${highCardValue.valueName}"
        }

        // 스트레이트 (Straight)
        if (isStraight(sortedValues)) {
            val highCardValue = sortedValues.last()
            newCounts[4]++
            return "Straight: ${highCardValue.valueName}"
        }

        // 쓰리 오브 어 카인드 (Three of a Kind)
        val threeOfAKindValues = valueCountMap.entries.filter { it.value == 3 }.map { it.key.valueName }
        if (threeOfAKindValues.isNotEmpty()) {
            newCounts[5]++
            return "Three of a Kind: ${threeOfAKindValues.joinToString(", ")}"
        }

        // 투 페어 (Two Pair)
        val pairValues = valueCountMap.entries.filter { it.value == 2 }.map { it.key.valueName }
        if (pairValues.size == 2) {
            newCounts[6]++
            return "Two Pair: ${pairValues.joinToString(", ")}"
        }

        // 원 페어 (One Pair)
        val pairValue2 = valueCountMap.entries.find { it.value == 2 }?.key
        if (pairValue2 != null) {
            newCounts[7]++
            return "One Pair: ${pairValue2.valueName}"
        }

        // 하이 카드 (High Card)
        val highCardValue = values.maxOrNull()
        newCounts[8]++
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