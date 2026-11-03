package com.fatashop
//import com.example.fatashop.Product
import com.example.fatashop.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.productNameItem)
        val price: TextView = itemView.findViewById(R.id.productPriceItem)
        val image: ImageView = itemView.findViewById(R.id.productImageItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.name.text = product.name
        holder.price.text = "৳ ${product.price}"
        holder.image.setImageResource(product.imageRes)
    }

    override fun getItemCount(): Int = products.size
}
