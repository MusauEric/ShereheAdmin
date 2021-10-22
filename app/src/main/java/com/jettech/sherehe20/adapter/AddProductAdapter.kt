package com.jettech.sherehe20.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jettech.sherehe20.R
import com.jettech.sherehe20.model.AddProduct

class AddProductAdapter(var context: Context, addedproductsList: List<AddProduct>) :
    RecyclerView.Adapter<AddProductAdapter.AddProductViewHolder>() {
    var addproductList: List<AddProduct>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddProductViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_add_drinks, parent, false)
        return AddProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddProductViewHolder, position: Int) {
        holder.name.setText(addproductList[position].name)
//        holder.description.setText(recentlyViewedList[position].description)
//        holder.price.setText(recentlyViewedList[position].price)
//        holder.qty.setText(recentlyViewedList[position].quantity)
//        holder.unit.setText(recentlyViewedList[position].unit)
        //holder.bg.setBackgroundResource(recentlyViewedList[position].imageUrl)
        val path  = holder.bg.setBackgroundResource(addproductList[position].imageUrl)

        Glide.with(context)
            .load(path)
            .override(40, 90)
            .into(holder.bg)

        holder.itemView.setOnClickListener {
            val i = Intent(context, AddProduct::class.java)
            i.putExtra("name", addproductList[position].name)
            i.putExtra("price", addproductList[position].price)
            i.putExtra("desc", addproductList[position].description)
            i.putExtra("qty", addproductList[position].quantity)
            i.putExtra("unit", addproductList[position].unit)
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return addproductList.size
    }

    class AddProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        //        var description: TextView
//        var price: TextView
//        var qty: TextView
//        var unit: TextView
        var bg: ImageView

        init {
            name = itemView.findViewById(R.id.product_name)
//            description = itemView.findViewById(R.id.description)
//            price = itemView.findViewById(R.id.price)
//            qty = itemView.findViewById(R.id.qty)
//            unit = itemView.findViewById(R.id.unit)
            bg = itemView.findViewById(R.id.recently_layout)
        }
    }

    init {
        this.addproductList = addedproductsList
    }
}







