package com.pujaad.coopmart.view.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.pujaad.coopmart.databinding.ActivitySplashBinding
import com.pujaad.coopmart.view.login.Awal
import kotlinx.coroutines.Runnable

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(mainLooper).postDelayed(Runnable {
            val i = Intent(this@SplashActivity, Awal::class.java)
            startActivity(i)
            finish()
        }, 2000)
    }
}