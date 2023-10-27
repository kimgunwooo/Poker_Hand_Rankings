package com.example.poker_hand_rankings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.poker_hand_rankings.databinding.ActivityMainBinding
import com.example.poker_hand_rankings.databinding.ActivityRandomBinding

class RandomActivity : AppCompatActivity() {
    private lateinit var main : ActivityRandomBinding
    private lateinit var model : LottoViewModel
    private lateinit var images : Array<ImageView?>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main = ActivityRandomBinding.inflate(layoutInflater)
        setContentView(main.root)

        // image 객체를 담는 배열 생성
        images = arrayOf(main.card1, main.card2, main.card3, main.card4, main.card5)

        // model 지정
        model = ViewModelProvider(this)[LottoViewModel::class.java]

        model.cards.observe(this, Observer {
            setImage()
        })

        main.btnShuffle.setOnClickListener() {
            model.shuffle()
        }
    }
    private fun setImage() {
        images.forEachIndexed { index, imageView ->
            val res = resources.getIdentifier(
                model.cards.value!![index],
                "drawable",
                packageName
            )
            Log.i("TEST!!!!!", model.cards.value!![index])
            imageView?.setImageResource(res)
        }
    }
}