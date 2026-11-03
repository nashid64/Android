package com.fatashop
import com.example. fatashop.R
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShopkeeperActivity : AppCompatActivity() {

    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopkeeper)

        val productName = findViewById<EditText>(R.id.productName)
        val productPrice = findViewById<EditText>(R.id.productPrice)
        val addProductBtn = findViewById<Button>(R.id.addProductBtn)
        val productRecyclerView = findViewById<RecyclerView>(R.id.productList)

        val adapter = ProductAdapter(productList)
        productRecyclerView.layoutManager = LinearLayoutManager(this)
        productRecyclerView.adapter = adapter

        addProductBtn.setOnClickListener {
            val name = productName.text.toString().trim()
            val price = productPrice.text.toString().trim()

            if (name.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                productList.add(Product(name, price, R.drawable.ic_person_placeholder))
                adapter.notifyDataSetChanged()
                productName.text.clear()
                productPrice.text.clear()
                Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

data class Product(val name: String, val price: String, val imageRes: Int)
