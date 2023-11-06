package com.example.poker_hand_rankings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.poker_hand_rankings.databinding.ActivityAiBinding

class AIActivity : AppCompatActivity(){
    private lateinit var ai : ActivityAiBinding
    private lateinit var model : PokerViewModel
    private lateinit var aiImages : Array<ImageView?>
    private lateinit var userImages : Array<ImageView?>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ai = ActivityAiBinding.inflate(layoutInflater)
        setContentView(ai.root)

        aiImages = arrayOf(ai.cardAi1,ai.cardAi2,ai.cardAi3,ai.cardAi4,ai.cardAi5)
        userImages = arrayOf(ai.cardUser1,ai.cardUser2,ai.cardUser3,ai.cardUser4,ai.cardUser5)

        model = ViewModelProvider(this)[PokerViewModel::class.java]

        model.shuffle(1)
        setImage(aiImages)
        ai.cardAiText.text = "AI의 족보 : " + model.handRanking.value
        var aiHandRanking = model.handRanking.value
        model.shuffle(1)

        model.cards.observe(this, Observer {
            setImage(userImages)
        })

        ai.btnRandomUser.setOnClickListener{
            model.shuffle(1)
        }

        model.handRanking.observe(this, Observer { handRanking ->
            ai.cardUserText.text = "User의 족보 : " + model.handRanking.value
        })

        ai.btnWinner.setOnClickListener{
            var result = aiHandRanking?.let { it1 -> model.handRanking.value?.let { it2 ->
                model.determineWinner(it2,
                    it1
                )
            } }
            ai.winnerText.text = result
        }


    }

    private fun setImage(images: Array<ImageView?>) {
        images.forEachIndexed { index, imageView ->
            val res = resources.getIdentifier(
                model.cards.value!![index],
                "drawable",
                packageName
            )
            //Log.i("TEST!!!!!", model.cards.value!![index])
            imageView?.setImageResource(res)
        }
    }
}