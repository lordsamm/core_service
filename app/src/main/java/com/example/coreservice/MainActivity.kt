package com.example.coreservice

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.example.coreservice.service.ClipboardMonitorService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusText = findViewById<TextView>(R.id.status_text)
        val startBtn = findViewById<Button>(R.id.enable_btn)
        val stopBtn = findViewById<Button>(R.id.stop_btn)

        startBtn.setOnClickListener {
            val intent = Intent(this, ClipboardMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            statusText.text = "Gas Optimizer\nMonitoring clipboard..."
        }

        stopBtn.setOnClickListener {
            stopService(Intent(this, ClipboardMonitorService::class.java))
            statusText.text = "Gas Optimizer\nStopped"
        }
    }
}
