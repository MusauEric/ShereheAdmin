package com.jettech.sherehe20.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
        val path = holder.bg.setBackgroundResource(addproductList[position].imageUrl)

        Glide.with(context)
            .load(path)
            .override(40, 90)
            .into(holder.bg)

        holder.itemView.setOnClickListener {

            val user = Firebase.auth.currentUser
            val db = Firebase.firestore

            val viewGroup = it.findViewById<ViewGroup>(R.id.rootAddDrinks)
            val dialogView: View =
                LayoutInflater.from(context).inflate(R.layout.view_add_drinks, viewGroup, false)
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                .setCancelable(false)
            builder.setView(dialogView)
            val alertDialog: AlertDialog = builder.create()
            val layoutWhiskey = dialogView.findViewById<LinearLayout>(R.id.layoutWhiskey)
            val layoutWine = dialogView.findViewById<LinearLayout>(R.id.layoutWine)
            val layoutBeers = dialogView.findViewById<LinearLayout>(R.id.layoutBeers)
            val layoutSoftDrinks = dialogView.findViewById<LinearLayout>(R.id.layoutSoftDrinks)

            val  cancelWhiskey = dialogView.findViewById<Button>(R.id.cancelWhiskey)
            val  cancelWine = dialogView.findViewById<Button>(R.id.cancelWine)
            val  cancelBeers = dialogView.findViewById<Button>(R.id.cancelBeers)
            val  cancelSoftDrinks = dialogView.findViewById<Button>(R.id.cancelSoftDrinks)


            val selectedItem = position

            when (selectedItem) {
                0 -> {
                    layoutWhiskey.visibility = View.VISIBLE
                    layoutWine.visibility = View.GONE
                    layoutBeers.visibility = View.GONE
                    layoutSoftDrinks.visibility = View.GONE
                }
                1 -> {
                    layoutWhiskey.visibility = View.GONE
                    layoutWine.visibility = View.VISIBLE
                    layoutBeers.visibility = View.GONE
                    layoutSoftDrinks.visibility = View.GONE
                }
                2 -> {
                    layoutWhiskey.visibility = View.GONE
                    layoutWine.visibility = View.GONE
                    layoutBeers.visibility = View.VISIBLE
                    layoutSoftDrinks.visibility = View.GONE
                }
                3 -> {
                    layoutWhiskey.visibility = View.GONE
                    layoutWine.visibility = View.GONE
                    layoutBeers.visibility = View.GONE
                    layoutSoftDrinks.visibility = View.VISIBLE

                }
            }

            builder.apply {

//                val addAddress = dialogView.findViewById<Button>(R.id.setLocation)
//                val storeTitle = dialogView.findViewById<EditText>(R.id.storeTitle)
//                val storeTitleName = dialogView.findViewById<EditText>(R.id.storeTitleName)
//                val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
//                val done = dialogView.findViewById<Button>(R.id.done)
//                val addressLoc = dialogView.findViewById<TextView>(R.id.addressLocation)


                cancelWhiskey.setOnClickListener {
                    alertDialog.dismiss()
                }
                cancelWine.setOnClickListener {
                    alertDialog.dismiss()
                }
                cancelBeers.setOnClickListener {
                    alertDialog.dismiss()
                }
                cancelSoftDrinks.setOnClickListener {
                    alertDialog.dismiss()
                }


//                addAddress.setOnClickListener {
//
//                }
//
//                done.setOnClickListener {
//
//                    if (storeTitleName.text!!.isEmpty()) {
//
//
//                        Toasty.error(
//                            this@MainActivity, "Enter Store Name", Toasty.LENGTH_LONG
//                        ).show()
//                        return@setOnClickListener
//                    }
//                    if (storeTitle.text!!.isEmpty()) {
//
//                        Toasty.error(
//                            this@MainActivity, "Enter Business number", Toasty.LENGTH_LONG
//                        ).show()
//                        return@setOnClickListener
//                    } else {
//
//                        progressDialog = Dialog(context)
//                        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                        progressDialog.setContentView(R.layout.custom_dialog_progress)
//                        val progressTv = progressDialog.findViewById(R.id.progress_tv) as TextView
//                        progressTv.text = resources.getString(R.string.loading)
//                        progressTv.setTextColor(ContextCompat.getColor(context, R.color.pink))
//                        progressTv.textSize = 15F
//
//                        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                        progressDialog.setCancelable(false)
//                        progressDialog.show()
//
//
//                        val user = Firebase.auth.currentUser
//                        val db = Firebase.firestore
//
//                        val data = hashMapOf(
//                            "storename" to storeTitleName.text.toString(),
//                            "businessNo" to businessNumber + " " + storeTitle.text.toString(),
//                            "ownerUid" to user!!.uid.toString(),
//                            "latCord" to latCoord.toString(),
//                            "longCord" to longCoord.toString(),
//                            "address" to address.toString()
//                        )
//                        db.collection("storeowner").document(user!!.uid)
//                            .collection("store")
//                            .add(data)
//                            .addOnCompleteListener {
//                                val storeId = it.result.id
//                                val newDocdata = hashMapOf("storeid" to storeId)
//                                db.collection("storeowner").document(user!!.uid)
//                                    .collection("store").document(storeId)
//                                    .set(newDocdata, SetOptions.merge())
//                                    .addOnCompleteListener {
//                                        val updateOwnerdata = hashMapOf(
//                                            "storeid" to storeId,
//                                            "storename" to storeTitleName.text.toString(),
//                                            "businessNo" to businessNumber + " " + storeTitle.text.toString(),
//                                            "ownerUid" to user!!.uid.toString(),
//                                            "latCord" to latCoord.toString(),
//                                            "longCord" to longCoord.toString(),
//                                            "status" to "0",
//                                            "address" to address.toString()
//                                        )
//                                        db.collection("storeowner").document(user!!.uid)
//                                            .set(updateOwnerdata, SetOptions.merge())
//                                        alertDialog.dismiss()
//                                        loadStore()
//                                        progressDialog.dismiss()
//                                        Toasty.success(
//                                            context,
//                                            "Store Created.",
//                                            Toast.LENGTH_LONG,
//                                            true
//                                        ).show()
//                                    }
//
//                            }
//
//                    }
//
//
//                }

                alertDialog.show()
            }


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







