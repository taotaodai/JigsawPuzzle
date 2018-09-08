package com.ttd.jipu.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import com.ttd.jipu.widget.LogoView
import com.ttd.jipu.R

class NavigationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_navigation)

        val vLogo = findViewById(R.id.v_logo)
        val animLogo = AnimationUtils.loadAnimation(this, R.anim.alpha_anim)

        val logoJipu = findViewById(R.id.logo_jipu) as LogoView

        animLogo.fillAfter = true
        animLogo.setAnimationListener(object :Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                logoJipu.visibility = View.VISIBLE
                logoJipu.startAnimation()
            }
        })
        vLogo.startAnimation(animLogo)

        val btnPlay = findViewById(R.id.btn_play) as Button
        val animPlay = AnimationUtils.loadAnimation(this, R.anim.alpha_anim)
        btnPlay.startAnimation(animPlay)
        val intent = Intent()
//        intent.setClass(this, GameTableActivity::class.java)
        intent.setClass(this, SelectionActivity::class.java)
        btnPlay.setOnClickListener {
            startActivity(intent)
        }

    }
}
