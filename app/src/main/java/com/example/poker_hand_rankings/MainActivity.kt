package com.example.poker_hand_rankings

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import com.example.poker_hand_rankings.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnClickListener{
    private lateinit var main : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        // 바인딩 처리
        main = ActivityMainBinding.inflate(layoutInflater);
        setContentView(main.root)

        // main 이미지 설정
        main.card1?.setImageResource(R.drawable.c_red_joker)

        main.btnRandom.setOnClickListener(this)
        main.btnStatistics.setOnClickListener(this)
        main.btnAI.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        val toClass = when (p0?.id){
            main.btnRandom.id -> { RandomActivity::class.java }
            main.btnStatistics.id -> { StatisticsActivity::class.java }
            main.btnAI.id -> {AIActivity::class.java}
            else -> return
        }
        val intent = Intent(this,toClass)
        startActivity(intent)
    }
}