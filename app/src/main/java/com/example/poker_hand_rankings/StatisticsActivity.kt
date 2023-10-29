package com.example.poker_hand_rankings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.poker_hand_rankings.databinding.ActivityStatisticsBinding

class StatisticsActivity : AppCompatActivity() {
    private lateinit var statistics : ActivityStatisticsBinding
    private lateinit var model : PokerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statistics = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(statistics.root)

        model = ViewModelProvider(this)[PokerViewModel::class.java]

        val textViews = arrayOf(statistics.text1, statistics.text2, statistics.text3,
            statistics.text4, statistics.text5, statistics.text6,
            statistics.text7, statistics.text8, statistics.text9)

        statistics.submitButton.setOnClickListener {
            val userInput = statistics.editText.text.toString()

            if (userInput.isNotBlank()) { // 빈 값이 아니라면
                val timesToShuffle = userInput.toInt()
                model.shuffle(timesToShuffle) // 요청한 횟수 만큼 반복
            }

            Toast.makeText(this, "조회 완료되었습니다!", Toast.LENGTH_SHORT).show() //조회 완료 시 처리
        }

        // counts LiveData를 관찰하여 각 족보 횟수를 TextView에 표시
        model.counts.observe(this, Observer{ counts ->
            textViews[0].text = "스트레이트 플러시 (Straight Flush) : ${counts[0]}"
            textViews[1].text = "포카드 (Four of a Kind) : ${counts[1]}"
            textViews[2].text = "풀 하우스 (Full House) : ${counts[2]}"
            textViews[3].text = "플러시 (Flush) : ${counts[3]}"
            textViews[4].text = "스트레이트 (Straight) : ${counts[4]}"
            textViews[5].text = "쓰리 오브 어 카인드 (Three of a Kind) : ${counts[5]}"
            textViews[6].text = "투 페어 (Two Pair) : ${counts[6]}"
            textViews[7].text = "원 페어 (One Pair) : ${counts[7]}"
            textViews[8].text = "하이 카드 (High Card) : ${counts[8]}"
        })
    }
}