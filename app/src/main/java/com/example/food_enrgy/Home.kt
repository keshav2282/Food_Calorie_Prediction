package com.example.food_enrgy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottom = findViewById<BottomNavigationView>(R.id.bottom)

        bottom.setOnNavigationItemSelectedListener {
            when(it.itemId)
            {




                R.id.feedback ->
                {

                    //Toast.makeText(applicationContext,"food Classification",Toast.LENGTH_LONG).show()
                    val i = Intent(applicationContext,dash::class.java)
                    startActivity(i)
                    true
                }
                R.id.showre ->
                {

                   // Toast.makeText(applicationContext,"BMI Calculate",Toast.LENGTH_LONG).show()
                    val i = Intent(applicationContext,BMI::class.java)
                    startActivity(i)
                    true
                }
//                R.id.profile ->
//                {
//
//                    //Toast.makeText(applicationContext,"Graph",Toast.LENGTH_LONG).show()
//                    val i = Intent(applicationContext,MainActivity::class.java)
//                    startActivity(i)
//                    true
//                }



                else -> {false}
            }
        }

    }
    }
