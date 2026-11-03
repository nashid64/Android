package com.fatashop
import com.example.fatashop.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Views
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val createAccount = findViewById<TextView>(R.id.createAccount)

        // Login button click
        loginBtn.setOnClickListener {
            val emailTxt = email.text.toString().trim()
            val passTxt = password.text.toString().trim()

            // Open ShopkeeperActivity directly if fields are filled
            if (emailTxt.isNotEmpty() && passTxt.isNotEmpty()) {
                val intent = Intent(this, ShopkeeperActivity::class.java)
                startActivity(intent)
            }
        }

        // Forgot Password click (optional)
        forgotPassword.setOnClickListener {
            // TODO: implement forgot password functionality
        }

        // Create Account click

        }
    }

