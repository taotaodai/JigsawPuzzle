package com.example.jipu

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnReset = findViewById(R.id.btn_reset) as Button
        val jpvMain = findViewById(R.id.jpv_main) as JipuView

        btnReset.setOnClickListener {
            jpvMain.initPuzzle()
            jpvMain.invalidate()
        }

    }
}
