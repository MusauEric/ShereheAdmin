package com.jettech.sherehe20.adapter

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jettech.sherehe20.MainActivity
import com.jettech.sherehe20.R
import com.jettech.sherehe20.model.AddProduct
import com.jettech.sherehe20.utils.Constants
import com.jettech.sherehe20.utils.OnClickImageListener
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import es.dmoral.toasty.Toasty
import java.security.AccessControlContext
import java.security.AccessController.getContext
import java.util.*


class AddProductAdapter(
    var context: Context,
    addedproductsList: List<AddProduct>,
    onClickImageListener: OnClickImageListener
) :
    RecyclerView.Adapter<AddProductAdapter.AddProductViewHolder>() {


    var addproductList: List<AddProduct>
    lateinit var onclickimagelistener: OnClickImageListener
    var activity: Activity? = null
    var ctt: AccessControlContext? = getContext()
    lateinit var dv: View
    private var imageUri: Uri? = null
    var whiskey: String = ""
    var whiskeyQty: String = ""


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
            dv = dialogView
            builder.setView(dialogView)
            val alertDialog: AlertDialog = builder.create()
            val layoutWhiskey = dialogView.findViewById<LinearLayout>(R.id.layoutWhiskey)
            val layoutWine = dialogView.findViewById<LinearLayout>(R.id.layoutWine)
            val layoutBeers = dialogView.findViewById<LinearLayout>(R.id.layoutBeers)
            val layoutSoftDrinks = dialogView.findViewById<LinearLayout>(R.id.layoutSoftDrinks)
            val cancelWhiskey = dialogView.findViewById<Button>(R.id.cancelWhiskey)
            val cancelWine = dialogView.findViewById<Button>(R.id.cancelWine)
            val cancelBeers = dialogView.findViewById<Button>(R.id.cancelBeers)
            val buttonWhiskey = dialogView.findViewById<Button>(R.id.buttonWhiskey)
            val cancelSoftDrinks = dialogView.findViewById<Button>(R.id.cancelSoftDrinks)
            val imageWhiskey = dialogView.findViewById<ImageView>(R.id.imageWhiskey)
            val imageWine = dialogView.findViewById<ImageView>(R.id.imageWine)
            val imageBeers = dialogView.findViewById<ImageView>(R.id.imageBeers)
            val imageSoftDrinks = dialogView.findViewById<ImageView>(R.id.imageSoftDrinks)


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

                imageWhiskey.setOnClickListener {
                    onclickimagelistener.getImage(position)
                }
                imageWine.setOnClickListener {
                    onclickimagelistener.getImage(position)
                }
                imageBeers.setOnClickListener {
                    onclickimagelistener.getImage(position)
                }
                imageSoftDrinks.setOnClickListener {
                    onclickimagelistener.getImage(position)
                }
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

                val selectedItem = position

                when (selectedItem) {
                    0 -> {

                        val nameWhiskey = dialogView.findViewById<EditText>(R.id.nameWhiskey)
                        val priceWhiskey = dialogView.findViewById<EditText>(R.id.priceWhiskey)
                        val discountWhiskey =
                            dialogView.findViewById<EditText>(R.id.discountWhiskey)
                        val unitWhiskey = dialogView.findViewById<EditText>(R.id.unitWhiskey)

                        val whiskeyChoice =
                            dialogView.findViewById<AppCompatSpinner>(R.id.categoryWhiskey)
                        val whiskeyChoiceAdapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_item,
                            context.resources.getStringArray(
                                R.array.whiskey
                            )
                        )
                        whiskeyChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        whiskeyChoice.adapter = whiskeyChoiceAdapter

                        whiskeyChoice.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedItemwhiskey =
                                        parent!!.getItemAtPosition(position).toString()


                                    whiskey = selectedItemwhiskey
                                }
                            }


                        val quantityChoices =
                            dialogView.findViewById<AppCompatSpinner>(R.id.quantityWhiskey)
                        val quantityChoiceAdapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_item,
                            context.resources.getStringArray(
                                R.array.quantity
                            )
                        )
                        quantityChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        quantityChoices.adapter = quantityChoiceAdapter

                        quantityChoices.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedItemQty =
                                        parent!!.getItemAtPosition(position).toString()

                                    whiskeyQty = selectedItemQty
                                }
                            }

                        buttonWhiskey.setOnClickListener {

                            if (nameWhiskey.text.toString().isEmpty()) {

                                Toasty.error(
                                    activity!!,
                                    "Enter drink name.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }
                            if (priceWhiskey.text.toString().isEmpty()) {

                                Toasty.error(
                                    activity!!,
                                    "Enter drink price.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }
                            if (discountWhiskey.text.toString().isEmpty()) {

                                Toasty.error(
                                    activity!!,
                                    "Enter discount.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }

                            if (unitWhiskey.text.toString().isEmpty()) {

                                Toasty.error(
                                    activity!!,
                                    "Enter Available unit.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }
                            if (imageUri == null) {
                                Toasty.error(
                                    context,
                                    "Enter drink photo.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }

                            saveWhiskey()
                        }

                    }
                    1 -> {

                    }
                    2 -> {

                    }
                    3 -> {

                    }
                }

                alertDialog.show()
            }

        }
    }

    private fun saveWhiskey() {
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore
        val sharedPref: SharedPreferences =
            context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
        val image: String = sharedPref.getString(Constants.IMAGE, "").toString().trim()

        var progressDialog = Dialog(context)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.custom_dialog_progress)
        val progressTv = progressDialog.findViewById(R.id.progress_tv) as TextView
        progressTv.text = context.resources.getString(R.string.loading)
        progressTv.setTextColor(ContextCompat.getColor(context, R.color.pink))
        progressTv.textSize = 15F
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val nameWhiskey = dv.findViewById<EditText>(R.id.nameWhiskey)
        val priceWhiskey = dv.findViewById<EditText>(R.id.priceWhiskey)
        val discountWhiskey = dv.findViewById<EditText>(R.id.discountWhiskey)
        val unitWhiskey = dv.findViewById<EditText>(R.id.unitWhiskey)

//        val data = hashMapOf(
//            "drinkImage" to image.toString(),
//            "nameWhiskey" to nameWhiskey.toString(),
//            "ownerUid" to user!!.uid.toString(),
//            "priceWhiskey" to priceWhiskey.toString(),
//            "discountWhiskey" to discountWhiskey.toString(),
//            "unitWhiskey" to unitWhiskey.toString(),
//            "whiskeyQty" to whiskeyQty.toString(),
//            "whiskey" to whiskey.toString(),
//        )
//        db.collection("storeowner").document(user!!.uid)
//            .collection("store")
//            .add(data)
//            .addOnCompleteListener {
//                val storeId = it.result.id
//                val newDocdata = hashMapOf("storeid" to storeId)
//                db.collection("storeowner").document(user!!.uid)
//                    .collection("store").document(storeId)
//                    .set(newDocdata, SetOptions.merge())
//                    .addOnCompleteListener {
//
//                        progressDialog.dismiss()
//
//                    }
//
//
//            }
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
            this.onclickimagelistener = onClickImageListener
        }

    }







