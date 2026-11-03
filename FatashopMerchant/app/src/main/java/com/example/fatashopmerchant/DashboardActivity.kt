package com.example.fatashopmerchant

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Notification badge
        findViewById<TextView>(R.id.notiCount)?.text = "7"

        // Extra options badges
        findViewById<TextView>(R.id.deliveredCount)?.text = "23"
        findViewById<TextView>(R.id.canceledCount)?.text = "3"
        findViewById<TextView>(R.id.returnCount)?.text = "1"
        findViewById<TextView>(R.id.failedCount)?.text = "0"

        // Wire popup to the menu tab and notification icon
        val tabMenu = findViewById<View>(R.id.tabMenu)
        val notiIcon = findViewById<View>(R.id.notiIcon)

        tabMenu?.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val popupView = inflater.inflate(R.layout.popup_simple_menu, null)
            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )
            popupWindow.isOutsideTouchable = true
            val rootView = findViewById<View>(android.R.id.content)
            rootView?.post {
                popupWindow.showAtLocation(rootView, android.view.Gravity.CENTER, 0, 0)
            }
        }
        notiIcon?.setOnClickListener { showPopupAtCenter() }

        // Add click listener to 'My Orders' tab
        findViewById<TextView>(R.id.tabOrders)?.setOnClickListener {
            startActivity(Intent(this, MerchantOrderActivity::class.java))
        }
    }

    private fun showPopupAtCenter() {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_menu_layout, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.popup_bg))
        popupWindow.isOutsideTouchable = true

        // Show the popup at center of the root layout
        val rootView = findViewById<View>(android.R.id.content)
        rootView?.post {
            popupWindow.showAtLocation(rootView, android.view.Gravity.CENTER, 0, 0)
        }

        // Hook internal popup item clicks
        popupView.findViewById<TextView>(R.id.option_profile)?.setOnClickListener {
            // TODO: navigate to profile
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.option_logout)?.setOnClickListener {
            // TODO: perform logout
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.option_settings)?.setOnClickListener {
            // TODO: open settings
            popupWindow.dismiss()
        }
    }
}
