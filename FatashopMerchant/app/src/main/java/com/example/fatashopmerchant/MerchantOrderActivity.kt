package com.example.fatashopmerchant

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity 

class MerchantOrderActivity : AppCompatActivity() {
    private var productPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_merchant_order)

        val etName = findViewById<EditText>(R.id.etProductName)
        val etPrice = findViewById<EditText>(R.id.etProductPrice)
        val etQty = findViewById<EditText>(R.id.etProductQuantity)
        val btnAdd = findViewById<Button>(R.id.btnAddProduct)
        val btnPhoto = findViewById<Button>(R.id.btnChoosePhoto)
        val ivPhoto = findViewById<ImageView>(R.id.ivProductPhoto)
        val llOrderList = findViewById<LinearLayout>(R.id.llOrderList)

        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                productPhotoUri = it
                ivPhoto.setImageURI(it)
            }
        }

        btnPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnAdd.setOnClickListener {
            val name = etName.text.toString().trim()
            val price = etPrice.text.toString().trim()
            val qty = etQty.text.toString().trim()
            if (name.isEmpty() || price.isEmpty() || qty.isEmpty()) {
                Toast.makeText(this, "Enter all product details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val itemView = LayoutInflater.from(this).inflate(R.layout.product_item_card, llOrderList, false)
            val itemPhoto = itemView.findViewById<ImageView>(R.id.ivItemPhoto)
            val itemName = itemView.findViewById<TextView>(R.id.tvItemName)
            val itemPrice = itemView.findViewById<TextView>(R.id.tvItemPrice)
            val itemQty = itemView.findViewById<TextView>(R.id.tvItemQty)
            val itemTotal = itemView.findViewById<TextView>(R.id.tvItemTotal)
            val btnDelete = itemView.findViewById<Button>(R.id.btnDeleteItem)
            if (productPhotoUri != null) itemPhoto.setImageURI(productPhotoUri)
            itemName.text = name
            itemPrice.text = price
            itemQty.text = qty
            // Calculate and set item total
            val priceValue = price.toDoubleOrNull() ?: 0.0
            val qtyValue = qty.toIntOrNull() ?: 0
            val totalValue = priceValue * qtyValue
            itemTotal.text = String.format("%.2f", totalValue)
            btnDelete.setOnClickListener {
                llOrderList.removeView(itemView)
                updateOrderTotal(llOrderList)
            }
            llOrderList.addView(itemView, 0)
            etName.text.clear()
            etPrice.text.clear()
            etQty.text.clear()
            ivPhoto.setImageResource(android.R.drawable.ic_menu_camera)
            productPhotoUri = null
            updateOrderTotal(llOrderList)
        }
    }

    // Helper to update the order total
    private fun updateOrderTotal(llOrderList: LinearLayout) {
        var total = 0.0
        for (i in 0 until llOrderList.childCount) {
            val itemView = llOrderList.getChildAt(i)
            val itemTotal = itemView.findViewById<TextView>(R.id.tvItemTotal)
            val value = itemTotal.text.toString().toDoubleOrNull() ?: 0.0
            total += value
        }
        val tvOrderTotal = findViewById<TextView>(R.id.tvOrderTotal)
        tvOrderTotal.text = String.format("%.2f \uD83D\uDCB0", total)
    }
}
