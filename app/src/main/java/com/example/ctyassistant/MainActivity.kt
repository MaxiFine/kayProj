package com.example.ctyassistant

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

const val tag = " Splash screen coming up"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        setDelaytime()
    }

    private fun setDelaytime() {

        val SPLASH_TIME_OUT = 2000

        Handler(Looper.getMainLooper()).postDelayed({

            // Your Code
            intent = Intent(this@MainActivity, mapActivity::class.java)
            startActivity(intent)

            Timber.tag(tag).d("time out of launch screen ")
        }, SPLASH_TIME_OUT.toLong())


    }


}