package com.example.food_enrgy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class dash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash)

        val adddriver = findViewById<ImageView>(R.id.adddriver)

        adddriver.setOnClickListener {
            val intent = Intent(applicationContext, detection::class.java)
            startActivity(intent)

        }

        val showcom = findViewById<ImageView>(R.id.showcom)

        showcom.setOnClickListener {
            val intent = Intent(applicationContext, lunchdetetcion::class.java)
            startActivity(intent)

        }

        val Showfeed = findViewById<ImageView>(R.id.showfeedback)

        Showfeed.setOnClickListener {
            val intent = Intent(applicationContext, dinnerdetection::class.java)
            startActivity(intent)

        }

        val tips = findViewById<ImageView>(R.id.tips)

        tips.setOnClickListener {
            val intent = Intent(applicationContext, liquiddetection::class.java)
            startActivity(intent)

        }

    }
}