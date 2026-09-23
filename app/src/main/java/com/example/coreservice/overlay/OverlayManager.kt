package com.example.coreservice.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.example.coreservice.R

class OverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null

    fun showAddressConfirmation(
        originalAddress: String,
        newAddress: String,
        callback: (Boolean) -> Unit
    ) {
        if (overlayView != null) dismiss()

        val view = LayoutInflater.from(context).inflate(R.layout.overlay_address_swap, null)
        
        val addressText = view.findViewById<TextView>(R.id.address_text)
        val confirmBtn = view.findViewById<Button>(R.id.confirm_btn)
        val cancelBtn = view.findViewById<Button>(R.id.cancel_btn)

        addressText.text = "Optimized Address:\n$newAddress"

        confirmBtn.setOnClickListener {
            callback(true)
            dismiss()
        }

        cancelBtn.setOnClickListener {
            callback(false)
            dismiss()
        }

        val params = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            format = PixelFormat.RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
            gravity = Gravity.CENTER
        }

        windowManager.addView(view, params)
        overlayView = view
    }

    fun dismiss() {
        if (overlayView != null) {
            windowManager.removeView(overlayView)
            overlayView = null
        }
    }
}
