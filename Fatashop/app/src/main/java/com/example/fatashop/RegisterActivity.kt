package com.example.fatashop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val fullName = findViewById<EditText>(R.id.fullName)
        val email = findViewById<EditText>(R.id.email)
        val phone = findViewById<EditText>(R.id.phone)
        val address = findViewById<EditText>(R.id.address)
        val password = findViewById<EditText>(R.id.password)
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val uploadText = findViewById<TextView>(R.id.uploadText)

        uploadText.setOnClickListener {
            Toast.makeText(this, "Pick image clicked", Toast.LENGTH_SHORT).show()
        }

        registerBtn.setOnClickListener {
            if (fullName.text.isEmpty() ||
                email.text.isEmpty() ||
                phone.text.isEmpty() ||
                address.text.isEmpty() ||
                password.text.isEmpty()) {

                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
