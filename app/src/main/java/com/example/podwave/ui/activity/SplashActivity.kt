package com.example.podwave.ui.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.podwave.R

class SplashActivity : AppCompatActivity() {

    private lateinit var redView: View
    private lateinit var blackView: View
    private lateinit var logo: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        initVariables()

        val animator = valueAnimator()

        animator?.start()

        animator?.doOnEnd {
            startActivity(Intent(this, MainActivity::class.java))
            Animatoo.animateSlideLeft(this)
            finish()
        }
    }
    //🔴🔴
    private fun valueAnimator(): ValueAnimator? {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1500
        animator.interpolator = AccelerateInterpolator()
        animator.interpolator = AccelerateInterpolator()
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            val screenWidth = resources.displayMetrics.widthPixels

            val redTranslation = -screenWidth * value
            val blackTranslation = screenWidth * value

            redView.translationX = redTranslation
            blackView.translationX = blackTranslation
        }
        return animator
    }

    //▶️▶️
    private fun initVariables() {
        redView = findViewById(R.id.red_layout)
        blackView = findViewById(R.id.black_layout)
        logo = findViewById(R.id.logo_layout)
    }
}