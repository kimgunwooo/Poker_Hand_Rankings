package com.example.poker_hand_rankings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.poker_hand_rankings.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var main : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        // 바인딩 처리
        main = ActivityMainBinding.inflate(layoutInflater);
        setContentView(main.root)

        // main 이미지 설정
        main.card1.setImageResource(R.drawable.c_king_of_hearts2)

        main.btnRandom.setOnClickListener{
            val intent = Intent(this, RandomActivity::class.java)
            startActivity(intent)
        }

        main.btnStatistics.setOnClickListener{
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }
    }
}