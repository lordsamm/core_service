package com.example.coreservice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusText = findViewById<TextView>(R.id.status_text)
        val startBtn = findViewById<Button>(R.id.enable_btn)
        val stopBtn = findViewById<Button>(R.id.stop_btn)

        statusText.text = "Gas Optimizer"

        startBtn.setOnClickListener {
            statusText.text = "Gas Optimizer\nButton tapped"
        }

        stopBtn.setOnClickListener {
            statusText.text = "Gas Optimizer\nStopped"
        }
    }
}
