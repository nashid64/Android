package com.example.fatashopmerchant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = findViewById<EditText>(R.id.username)
        val pass = findViewById<EditText>(R.id.password)
        val btn = findViewById<Button>(R.id.loginBtn)

        btn.setOnClickListener {
            if (user.text.isEmpty() || pass.text.isEmpty()) {
                Toast.makeText(this, "Enter username & password", Toast.LENGTH_SHORT).show()
            } else {
                // Navigate to DashboardActivity
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish() // Optional: prevent back to login
            }
        }
    }
}
