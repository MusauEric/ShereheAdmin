package com.jettech.sherehe20.adapter

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jettech.sherehe20.Helper.AnimationUtil
import com.jettech.sherehe20.Helper.AnimationUtil.shakeView
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
    var wine: String = ""
    var wineQty: String = ""
    var beersQty: String = ""
    var beers: String = ""
    var SoftDrinksQty: String = ""
    var SoftDrinks: String = ""
    var discWhisky : String = ""
    var discWine : String = ""
    var discBeer : String = ""
    var discSoft : String = ""
    var discount : String = ""

    lateinit var alertDialog : AlertDialog


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddProductViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_add_drinks, parent, false)




        return AddProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddProductViewHolder, position: Int) {
        holder.name.setText(addproductList[position].name)
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
             alertDialog = builder.create()
            val layoutWhiskey = dialogView.findViewById<LinearLayout>(R.id.layoutWhiskey)
            val layoutWine = dialogView.findViewById<LinearLayout>(R.id.layoutWine)
            val layoutBeers = dialogView.findViewById<LinearLayout>(R.id.layoutBeers)
            val layoutSoftDrinks = dialogView.findViewById<LinearLayout>(R.id.layoutSoftDrinks)
            val cancelWhiskey = dialogView.findViewById<Button>(R.id.cancelWhiskey)
            val cancelWine = dialogView.findViewById<Button>(R.id.cancelWine)
            val cancelBeers = dialogView.findViewById<Button>(R.id.cancelBeers)
            val buttonWhiskey = dialogView.findViewById<Button>(R.id.buttonWhiskey)
            val buttonWines = dialogView.findViewById<Button>(R.id.buttonWine)
            val buttonBeers = dialogView.findViewById<Button>(R.id.buttonBeers)
            val buttonSoftDrinks = dialogView.findViewById<Button>(R.id.buttonSoftDrinks)
            val cancelSoftDrinks = dialogView.findViewById<Button>(R.id.cancelSoftDrinks)
            val imageWhiskey = dialogView.findViewById<Button>(R.id.imageWhiskey)
            val imageWine = dialogView.findViewById<Button>(R.id.imageWine)
            val imageBeers = dialogView.findViewById<Button>(R.id.imageBeers)
            val imageSoftDrinks = dialogView.findViewById<Button>(R.id.imageSoftDrinks)


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


                val priceWhiskey = dv.findViewById<EditText>(R.id.priceWhiskey)
                val discountWhiskey = dv.findViewById<EditText>(R.id.discountWhiskey)
                val finalWhiskey = dv.findViewById<TextView>(R.id.finalWhiskyPrice)

                val priceWine = dv.findViewById<EditText>(R.id.priceWine)
                val discountWine = dv.findViewById<EditText>(R.id.discountWine)
                val finalWinePrice = dv.findViewById<TextView>(R.id.finalWinePrice)

                val priceBeers = dv.findViewById<EditText>(R.id.priceBeers)
                val discountBeers = dv.findViewById<EditText>(R.id.discountBeers)
                val finalBeerPrice = dv.findViewById<TextView>(R.id.finalBeerPrice)

                val priceSoftDrinks = dv.findViewById<EditText>(R.id.priceSoftDrinks)
                val discountSoftDrinks = dv.findViewById<EditText>(R.id.discountSoftDrinks)
                val finalSoftPrice = dv.findViewById<TextView>(R.id.finalSoftPrice)

                priceWhiskey.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if (priceWhiskey.text!!.isEmpty() || priceWhiskey.text.toString() == "0") {

                            shakeView(priceWhiskey,context)
                            Toasty.error(
                                context, "Please enter Price", Toasty.LENGTH_LONG
                            ).show()



                        }
                        else {

                            if (discountWhiskey.text.toString().equals("")){

                                discWhisky = (0).toString()
                            }else{
                                discWhisky = discountWhiskey.text.toString()
                            }

                            val discount = (100 - discWhisky.toInt())
                            val finalP =  (s.toString().toInt().toInt()*((discount)*(0.01))).toInt()

                            finalWhiskey.text = finalP.toString()


                        }


                    }
                })
                discountWhiskey.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if (priceWhiskey.text!!.isEmpty()) {

                            shakeView(discountWhiskey,context)
                            Toasty.error(
                                context, "Enter Drink Price", Toasty.LENGTH_LONG
                            ).show()

                            if(discountWhiskey.text!!.count() > 2){
                                shakeView(discountWhiskey,context)
                                Toasty.error(
                                    context, "Incorrect value", Toasty.LENGTH_LONG
                                ).show()
                            }


                        }
                        else {
                            if (discountWhiskey.text.toString().equals("")){

                                discWhisky = (0).toString()
                            }else{
                                discWhisky = s.toString()
                            }


                            val discount = (100 - discWhisky.toString().toInt())
                            val originalPrice = priceWhiskey.text
                            val finalP =  (originalPrice.toString().toInt()*((discount)*(0.01))).toInt()

                            finalWhiskey.text = finalP.toInt().toString()


                        }


                    }
                })

                priceWine.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if (priceWine.text!!.isEmpty() || priceWine.text.toString() == "0") {

                            shakeView(priceWhiskey,context)
                            Toasty.error(
                                context, "Please enter Price", Toasty.LENGTH_LONG
                            ).show()



                        }
                        else {

                            if (discountWine.text.toString().equals("")){

                                discWine = (0).toString()
                            }else{
                                discWine = discountWine.text.toString()
                            }

                            val discount = (100 - discWine.toInt())
                            val finalP =  (s.toString().toInt().toInt()*((discount)*(0.01))).toInt()

                            finalWinePrice.text = finalP.toString()


                        }


                    }
                })
                discountWine.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if (priceWine.text!!.isEmpty()) {

                            shakeView(priceWine,context)
                            Toasty.error(
                                context, "Enter Drink Price", Toasty.LENGTH_LONG
                            ).show()

                            if(discountWine.text!!.count() > 2){
                                shakeView(discountWine,context)
                                Toasty.error(
                                    context, "Incorrect value", Toasty.LENGTH_LONG
                                ).show()
                            }


                        }
                        else {
                            if (discountWine.text.toString().equals("")){

                                discWine = (0).toString()
                            }else{
                                discWine = s.toString()
                            }


                            val discount = (100 - discWine.toString().toInt())
                            val originalPrice = priceWine.text
                            val finalP =  (originalPrice.toString().toInt()*((discount)*(0.01))).toInt()

                            finalWinePrice.text = finalP.toInt().toString()


                        }


                    }
                })

                priceBeers.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if (priceBeers.text!!.isEmpty() || priceBeers.text.toString() == "0") {

                            shakeView(priceBeers,context)
                            Toasty.error(
                                context, "Please enter Price", Toasty.LENGTH_LONG
                            ).show()



                        }
                        else {

                            if (discountBeers.text.toString().equals("")){

                                discBeer = (0).toString()
                            }else{
                                discBeer = discountBeers.text.toString()
                            }

                            val discount = (100 - discBeer.toInt())
                            val finalP =  (s.toString().toInt().toInt()*((discount)*(0.01))).toInt()

                            finalBeerPrice.text = finalP.toString()


                        }


                    }
                })
                discountBeers.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if (priceBeers.text!!.isEmpty()) {

                            shakeView(priceBeers,context)
                            Toasty.error(
                                context, "Enter Drink Price", Toasty.LENGTH_LONG
                            ).show()

                            if(discountBeers.text!!.count() > 2){
                                shakeView(discountBeers,context)
                                Toasty.error(
                                    context, "Incorrect value", Toasty.LENGTH_LONG
                                ).show()
                            }


                        }
                        else {
                            if (discountBeers.text.toString().equals("")){

                                discBeer = (0).toString()
                            }else{
                                discBeer  = s.toString()
                            }


                            val discount = (100 - discBeer .toString().toInt())
                            val originalPrice = priceBeers.text
                            val finalP =  (originalPrice.toString().toInt()*((discount)*(0.01))).toInt()

                            finalBeerPrice.text = finalP.toInt().toString()


                        }


                    }
                })

                priceSoftDrinks.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if (priceSoftDrinks.text!!.isEmpty() || priceSoftDrinks.text.toString() == "0") {

                            shakeView(priceSoftDrinks,context)
                            Toasty.error(
                                context, "Please enter Price", Toasty.LENGTH_LONG
                            ).show()

                        }
                        else {

                            if (discountSoftDrinks.text.toString().equals("")){

                                discSoft = (0).toString()
                            }else{
                                discSoft = discountSoftDrinks.text.toString()
                            }

                            val discount = (100 - discSoft.toInt())
                            val finalP =  (s.toString().toInt().toInt()*((discount)*(0.01))).toInt()

                            finalSoftPrice.text = finalP.toString()


                        }


                    }
                })
                discountSoftDrinks.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if (priceSoftDrinks.text!!.isEmpty()) {

                            shakeView(priceSoftDrinks,context)
                            Toasty.error(
                                context, "Enter Drink Price", Toasty.LENGTH_LONG
                            ).show()

                            if(discountSoftDrinks.text!!.count() > 2){
                                shakeView(discountSoftDrinks,context)
                                Toasty.error(
                                    context, "Incorrect value", Toasty.LENGTH_LONG
                                ).show()
                            }


                        }
                        else {
                            if (discountSoftDrinks.text.toString().equals("")){

                                discSoft = (0).toString()
                            }else{
                                discSoft = s.toString()
                            }


                            val discount = (100 - discSoft.toString().toInt())
                            val originalPrice = priceSoftDrinks.text
                            val finalP =  (originalPrice.toString().toInt()*((discount)*(0.01))).toInt()

                            finalSoftPrice.text = finalP.toInt().toString()


                        }


                    }
                })

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
                    var sharedPref: SharedPreferences =
                        context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.commit()
                    alertDialog.dismiss()
                }
                cancelWine.setOnClickListener {
                    var sharedPref: SharedPreferences =
                        context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.commit()
                    alertDialog.dismiss()
                }
                cancelBeers.setOnClickListener {
                    var sharedPref: SharedPreferences =
                        context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.commit()
                    alertDialog.dismiss()
                }
                cancelSoftDrinks.setOnClickListener {
                    var sharedPref: SharedPreferences =
                        context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.commit()
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

                            val sharedPref: SharedPreferences =
                                context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
                            val image: String =
                                sharedPref.getString(Constants.IMAGE, "").toString().trim()

                            imageUri = image.toUri()

                            if (nameWhiskey.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter drink name.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }
                            if (priceWhiskey.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter drink price.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }
                            if (discountWhiskey.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter discount.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }


                            if (unitWhiskey.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
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

                        val nameWine = dialogView.findViewById<EditText>(R.id.nameWine)
                        val priceWine = dialogView.findViewById<EditText>(R.id.priceWine)
                        val discountWine =
                            dialogView.findViewById<EditText>(R.id.discountWine)
                        val unitWine = dialogView.findViewById<EditText>(R.id.unitWines)

                        val  wineChoice =
                            dialogView.findViewById<AppCompatSpinner>(R.id.categoryWine)
                        val  wineChoiceAdapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_item,
                            context.resources.getStringArray(
                                R.array.wines
                            )
                        )
                        wineChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        wineChoice.adapter = wineChoiceAdapter

                        wineChoice.onItemSelectedListener =
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


                                    wine = selectedItemwhiskey
                                }
                            }


                        val quantityWineChoices =
                            dialogView.findViewById<AppCompatSpinner>(R.id.quantityWine)
                        val quantityWineChoiceAdapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_item,
                            context.resources.getStringArray(
                                R.array.quantity
                            )
                        )
                        quantityWineChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        quantityWineChoices.adapter = quantityWineChoiceAdapter

                        quantityWineChoices.onItemSelectedListener =
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

                                    wineQty = selectedItemQty
                                }
                            }

                        buttonWines.setOnClickListener {

                            val sharedPref: SharedPreferences =
                                context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
                            val image: String =
                                sharedPref.getString(Constants.IMAGE, "").toString().trim()
                            Log.d("mumooo", image)

                            imageUri = image.toUri()

                            if (nameWine.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter drink name.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }
                            if (priceWine.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter drink price.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }
                            if (discountWine.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter discount.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }

                            if (unitWine.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
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

                            saveWine()
                        }

                    }
                    2 -> {

                        val nameBeers = dialogView.findViewById<EditText>(R.id.nameBeers)
                        val priceBeers = dialogView.findViewById<EditText>(R.id.priceBeers)
                        val discountBeers =
                            dialogView.findViewById<EditText>(R.id.discountBeers)
                        val unitBeers = dialogView.findViewById<EditText>(R.id.unitBeers)

                        val  beersChoice =
                            dialogView.findViewById<AppCompatSpinner>(R.id.categoryBeers)
                        val  beersChoiceAdapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_item,
                            context.resources.getStringArray(
                                R.array.beers
                            )
                        )
                        beersChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        beersChoice.adapter = beersChoiceAdapter

                        beersChoice.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedItembeers =
                                        parent!!.getItemAtPosition(position).toString()


                                    beers = selectedItembeers
                                }
                            }


                        val quantityBeersChoices =
                            dialogView.findViewById<AppCompatSpinner>(R.id.quantityBeers)
                        val quantityBeersChoiceAdapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_item,
                            context.resources.getStringArray(
                                R.array.quantity
                            )
                        )
                        quantityBeersChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        quantityBeersChoices.adapter = quantityBeersChoiceAdapter

                        quantityBeersChoices.onItemSelectedListener =
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

                                    beersQty = selectedItemQty
                                }
                            }

                        buttonBeers.setOnClickListener {

                            val sharedPref: SharedPreferences =
                                context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
                            val image: String =
                                sharedPref.getString(Constants.IMAGE, "").toString().trim()
                            Log.d("mumooo", image)

                            imageUri = image.toUri()

                            if (nameBeers.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter drink name.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }
                            if (priceBeers.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter drink price.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }
                            if (discountBeers.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter discount.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }

                            if (unitBeers.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
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

                            saveBeers()
                        }

                    }
                    3 -> {

                        val nameSoftDrinks = dialogView.findViewById<EditText>(R.id.nameSoftDrinks)
                        val priceSoftDrinks = dialogView.findViewById<EditText>(R.id.priceSoftDrinks)
                        val discountSoftDrinks =
                            dialogView.findViewById<EditText>(R.id.discountSoftDrinks)
                        val unitSoftDrinks = dialogView.findViewById<EditText>(R.id.unitSoftDrinks)

                        val  SoftDrinksChoice =
                            dialogView.findViewById<AppCompatSpinner>(R.id.categorySoftDrinks)
                        val  SoftDrinksChoiceAdapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_item,
                            context.resources.getStringArray(
                                R.array.softdrinks
                            )
                        )
                        SoftDrinksChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        SoftDrinksChoice.adapter = SoftDrinksChoiceAdapter

                        SoftDrinksChoice.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val selectedItemSoftDrinks =
                                        parent!!.getItemAtPosition(position).toString()


                                    SoftDrinks = selectedItemSoftDrinks
                                }
                            }


                        val quantitySoftDrinksChoices =
                            dialogView.findViewById<AppCompatSpinner>(R.id.quantitySoftDrinks)
                        val quantitySoftDrinksChoiceAdapter = ArrayAdapter(
                            context,
                            android.R.layout.simple_spinner_item,
                            context.resources.getStringArray(
                                R.array.quantity
                            )
                        )
                        quantitySoftDrinksChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        quantitySoftDrinksChoices.adapter = quantitySoftDrinksChoiceAdapter

                        quantitySoftDrinksChoices.onItemSelectedListener =
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

                                    SoftDrinksQty = selectedItemQty
                                }
                            }

                        buttonSoftDrinks.setOnClickListener {

                            val sharedPref: SharedPreferences =
                                context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
                            val image: String =
                                sharedPref.getString(Constants.IMAGE, "").toString().trim()
                            Log.d("mumooo", image)

                            imageUri = image.toUri()

                            if (nameSoftDrinks.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter drink name.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }
                            if (priceSoftDrinks.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter drink price.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }
                            if (discountSoftDrinks.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
                                    "Enter discount.",
                                    Toast.LENGTH_LONG,
                                    true
                                )
                                    .show()

                                return@setOnClickListener
                            }

                            if (unitSoftDrinks.text.toString().isEmpty()) {

                                Toasty.error(
                                    context,
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

                            SoftDrinksBeers()
                        }

                    }
                }

                alertDialog.show()
            }

        }
    }

    private fun SoftDrinksBeers() {

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

        val nameSoftDrinks = dv.findViewById<EditText>(R.id.nameSoftDrinks)
        val priceSoftDrinks = dv.findViewById<EditText>(R.id.priceSoftDrinks)
        val discountSoftDrinks = dv.findViewById<EditText>(R.id.discountSoftDrinks)
        val unitSoftDrinks = dv.findViewById<EditText>(R.id.unitSoftDrinks)
        val finalSoftPrice = dv.findViewById<TextView>(R.id.finalSoftPrice)

        if (discountSoftDrinks.text!!.isEmpty() || discountSoftDrinks.text.toString() == "0"){

            discount = "noDiscount"
        }
        else{
            discount = "discount"
        }


        val mechRef = db.collection("storeowner").document(user!!.uid)
        mechRef.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    val userdata = document.data!!
                    val storeid = userdata.get("storeid").toString()

                    val data = hashMapOf(
                        "drinkImage" to image.toString(),
                        "drinkName" to nameSoftDrinks.text.toString(),
                        "ownerUid" to user!!.uid.toString(),
                        "drinkPrice" to finalSoftPrice.text.toString(),
                        "drinkDiscount" to discount.toString(),
                        "drinkUnit" to unitSoftDrinks.text.toString(),
                        "drinkQty" to SoftDrinksQty.toString(),
                        "SoftDrinks" to SoftDrinks.toString(),
                    )
                    db.collection("storeowner").document(user!!.uid)
                        .collection("store").document(storeid).collection("SoftDrinks")
                        .add(data)
                        .addOnCompleteListener {
                            val SoftDrinksstoreId = it.result.id
                            val newDocdata = hashMapOf("drinkStoreId" to SoftDrinksstoreId)
                            db.collection("storeowner").document(user!!.uid)
                                .collection("store").document(storeid).collection("SoftDrinks")
                                .document(SoftDrinksstoreId)
                                .set(newDocdata, SetOptions.merge())
                                .addOnCompleteListener {

                                    val data = hashMapOf(
                                        "drinkImage" to image.toString(),
                                        "drinkName" to nameSoftDrinks.text.toString(),
                                        "ownerUid" to user!!.uid.toString(),
                                        "drinkPrice" to finalSoftPrice.text.toString(),
                                        "drinkDiscount" to discount.toString(),
                                        "drinkUnit" to unitSoftDrinks.text.toString(),
                                        "drinkQty" to SoftDrinksQty.toString(),
                                        "SoftDrinks" to SoftDrinks.toString(),
                                        "drinkStoreId" to SoftDrinksstoreId
                                    )
                                    db.collection("storeowner").document(user!!.uid)
                                        .collection("drinks")
                                        .add(data)
                                        .addOnCompleteListener {
                                            val SoftDrinksDrinkId = it.result.id
                                            val newDrinkdata =
                                                hashMapOf("drinkId" to SoftDrinksDrinkId)
                                            db.collection("storeowner").document(user!!.uid)
                                                .collection("drinks").document(SoftDrinksDrinkId)
                                                .set(newDrinkdata, SetOptions.merge())
                                                .addOnCompleteListener {
                                                    val newAlldata =
                                                        hashMapOf("allDrinksId" to SoftDrinksDrinkId)
                                                    db.collection("storeowner").document(user!!.uid)
                                                        .collection("store").document(storeid)
                                                        .collection("SoftDrinks")
                                                        .document(SoftDrinksstoreId)
                                                        .set(newAlldata, SetOptions.merge())
                                                        .addOnSuccessListener {
                                                            progressDialog.dismiss()
                                                            alertDialog.dismiss()
                                                            Toasty.success(
                                                                context,
                                                                "Drink Added.",
                                                                Toast.LENGTH_LONG,
                                                                true
                                                            ).show()
                                                        }


                                                }
                                        }

//

                                }


                        }

                }
            }


    }

    private fun saveBeers() {

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

        val nameBeers = dv.findViewById<EditText>(R.id.nameBeers)
        val priceBeers = dv.findViewById<EditText>(R.id.priceBeers)
        val discountBeers = dv.findViewById<EditText>(R.id.discountBeers)
        val unitBeers = dv.findViewById<EditText>(R.id.unitBeers)
        val finalBeerPrice = dv.findViewById<TextView>(R.id.finalBeerPrice)
        if (discountBeers.text!!.isEmpty() || discountBeers.text.toString() == "0"){

            discount = "noDiscount"
        }
        else{
            discount = "discount"
        }

        val mechRef = db.collection("storeowner").document(user!!.uid)
        mechRef.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    val userdata = document.data!!
                    val storeid = userdata.get("storeid").toString()

                    val data = hashMapOf(
                        "drinkImage" to image.toString(),
                        "drinkName" to nameBeers.text.toString(),
                        "ownerUid" to user!!.uid.toString(),
                        "drinkPrice" to finalBeerPrice.text.toString(),
                        "drinkDiscount" to discount.toString(),
                        "drinkUnit" to unitBeers.text.toString(),
                        "drinkQty" to beersQty.toString(),
                        "beers" to beers.toString(),
                    )
                    db.collection("storeowner").document(user!!.uid)
                        .collection("store").document(storeid).collection("beers")
                        .add(data)
                        .addOnCompleteListener {
                            val beerstoreId = it.result.id
                            val newDocdata = hashMapOf("drinkStoreId" to beerstoreId)
                            db.collection("storeowner").document(user!!.uid)
                                .collection("store").document(storeid).collection("beers")
                                .document(beerstoreId)
                                .set(newDocdata, SetOptions.merge())
                                .addOnCompleteListener {

                                    val data = hashMapOf(
                                        "drinkImage" to image.toString(),
                                        "drinkName" to nameBeers.text.toString(),
                                        "ownerUid" to user!!.uid.toString(),
                                        "drinkPrice" to finalBeerPrice.text.toString(),
                                        "drinkDiscount" to discount.toString(),
                                        "drinkUnit" to unitBeers.text.toString(),
                                        "drinkQty" to beersQty.toString(),
                                        "beers" to beers.toString(),
                                        "drinkStoreId" to beerstoreId
                                    )
                                    db.collection("storeowner").document(user!!.uid)
                                        .collection("drinks")
                                        .add(data)
                                        .addOnCompleteListener {
                                            val beerDrinkId = it.result.id
                                            val newDrinkdata =
                                                hashMapOf("drinkId" to beerDrinkId)
                                            db.collection("storeowner").document(user!!.uid)
                                                .collection("drinks").document(beerDrinkId)
                                                .set(newDrinkdata, SetOptions.merge())
                                                .addOnCompleteListener {
                                                    val newAlldata =
                                                        hashMapOf("allDrinkId" to beerDrinkId)
                                                    db.collection("storeowner").document(user!!.uid)
                                                        .collection("store").document(storeid)
                                                        .collection("beers")
                                                        .document(beerstoreId)
                                                        .set(newAlldata, SetOptions.merge())
                                                        .addOnSuccessListener {
                                                            progressDialog.dismiss()
                                                            alertDialog.dismiss()
                                                            Toasty.success(
                                                                context,
                                                                "Drink Added.",
                                                                Toast.LENGTH_LONG,
                                                                true
                                                            ).show()
                                                        }


                                                }
                                        }

//

                                }


                        }

                }
            }


    }

    private fun saveWine() {
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

        val nameWine = dv.findViewById<EditText>(R.id.nameWine)
        val priceWine = dv.findViewById<EditText>(R.id.priceWine)
        val discountWine = dv.findViewById<EditText>(R.id.discountWine)
        val unitWine = dv.findViewById<EditText>(R.id.unitWines)

        val finalWinePrice = dv.findViewById<TextView>(R.id.finalWinePrice)
        if (discountWine.text!!.isEmpty() || discountWine.text.toString() == "0"){

            discount = "noDiscount"
        }
        else{
            discount = "discount"
        }

        val mechRef = db.collection("storeowner").document(user!!.uid)
        mechRef.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    val userdata = document.data!!
                    val storeid = userdata.get("storeid").toString()

                    val data = hashMapOf(
                        "drinkImage" to image.toString(),
                        "drinkName" to nameWine.text.toString(),
                        "ownerUid" to user!!.uid.toString(),
                        "drinkPrice" to finalWinePrice.text.toString(),
                        "drinkDiscount" to discount.toString(),
                        "drinkUnit" to unitWine.text.toString(),
                        "drinkQty" to wineQty.toString(),
                        "wine" to wine.toString(),
                    )
                    db.collection("storeowner").document(user!!.uid)
                        .collection("store").document(storeid).collection("wine")
                        .add(data)
                        .addOnCompleteListener {
                            val winestoreId = it.result.id
                            val newDocdata = hashMapOf("drinkStoreId" to winestoreId)
                            db.collection("storeowner").document(user!!.uid)
                                .collection("store").document(storeid).collection("wine")
                                .document(winestoreId)
                                .set(newDocdata, SetOptions.merge())
                                .addOnCompleteListener {

                                    val data = hashMapOf(
                                        "drinkImage" to image.toString(),
                                        "drinkName" to nameWine.text.toString(),
                                        "ownerUid" to user!!.uid.toString(),
                                        "drinkPrice" to finalWinePrice.text.toString(),
                                        "drinkDiscount" to discount.toString(),
                                        "drinkUnit" to unitWine.text.toString(),
                                        "drinkQty" to wineQty.toString(),
                                        "wine" to wine.toString(),
                                        "drinkStoreId" to winestoreId
                                    )
                                    db.collection("storeowner").document(user!!.uid)
                                        .collection("drinks")
                                        .add(data)
                                        .addOnCompleteListener {
                                            val wineDrinkId = it.result.id
                                            val newDrinkdata =
                                                hashMapOf("drinkId" to wineDrinkId)
                                            db.collection("storeowner").document(user!!.uid)
                                                .collection("drinks").document(wineDrinkId)
                                                .set(newDrinkdata, SetOptions.merge())
                                                .addOnCompleteListener {
                                                    val newAlldata =
                                                        hashMapOf("allDrinkId" to wineDrinkId)
                                                    db.collection("storeowner").document(user!!.uid)
                                                        .collection("store").document(storeid)
                                                        .collection("wine")
                                                        .document(winestoreId)
                                                        .set(newAlldata, SetOptions.merge())
                                                        .addOnSuccessListener {
                                                            progressDialog.dismiss()
                                                            alertDialog.dismiss()
                                                            Toasty.success(
                                                                context,
                                                                "Drink Added.",
                                                                Toast.LENGTH_LONG,
                                                                true
                                                            ).show()
                                                        }


                                                }
                                        }

//

                                }


                        }

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
        val finalWhiskeyPrice = dv.findViewById<TextView>(R.id.finalWhiskyPrice)
         if (discountWhiskey.text!!.isEmpty() || discountWhiskey.text.toString() == "0"){

             discount = "noDiscount"
         }
         else{
             discount = "discount"
         }

        val mechRef = db.collection("storeowner").document(user!!.uid)
        mechRef.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    val userdata = document.data!!
                    val storeid = userdata.get("storeid").toString()

                    val data = hashMapOf(
                        "drinkImage" to image.toString(),
                        "drinkName" to nameWhiskey.text.toString(),
                        "ownerUid" to user!!.uid.toString(),
                        "drinkPrice" to finalWhiskeyPrice.text.toString(),
                        "drinkDiscount" to discount.toString(),
                        "drinkUnit" to unitWhiskey.text.toString(),
                        "drinkQty" to whiskeyQty.toString(),
                        "whiskey" to whiskey.toString(),
                    )
                    db.collection("storeowner").document(user!!.uid)
                        .collection("store").document(storeid).collection("whiskey")
                        .add(data)
                        .addOnCompleteListener {
                            val whiskeystoreId = it.result.id
                            val newDocdata = hashMapOf("drinkStoreId" to whiskeystoreId)
                            db.collection("storeowner").document(user!!.uid)
                                .collection("store").document(storeid).collection("whiskey")
                                .document(whiskeystoreId)
                                .set(newDocdata, SetOptions.merge())
                                .addOnCompleteListener {

                                    val data = hashMapOf(
                                        "drinkImage" to image.toString(),
                                        "drinkName" to nameWhiskey.text.toString(),
                                        "ownerUid" to user!!.uid.toString(),
                                        "drinkPrice" to finalWhiskeyPrice.text.toString(),
                                        "drinkDiscount" to discount.toString(),
                                        "drinkUnit" to unitWhiskey.text.toString(),
                                        "drinkQty" to whiskeyQty.toString(),
                                        "whiskey" to whiskey.toString(),
                                        "drinkStoreId" to whiskeystoreId
                                    )
                                    db.collection("storeowner").document(user!!.uid)
                                        .collection("drinks")
                                        .add(data)
                                        .addOnCompleteListener {
                                            val whiskeyDrinkId = it.result.id
                                            val newDrinkdata =
                                                hashMapOf("drinkId" to whiskeyDrinkId)
                                            db.collection("storeowner").document(user!!.uid)
                                                .collection("drinks").document(whiskeyDrinkId)
                                                .set(newDrinkdata, SetOptions.merge())
                                                .addOnCompleteListener {
                                                    val newAlldata =
                                                        hashMapOf("allDrinkId" to whiskeyDrinkId)
                                                    db.collection("storeowner").document(user!!.uid)
                                                        .collection("store").document(storeid)
                                                        .collection("whiskey")
                                                        .document(whiskeystoreId)
                                                        .set(newAlldata, SetOptions.merge())
                                                        .addOnSuccessListener {
                                                            progressDialog.dismiss()
                                                            alertDialog.dismiss()
                                                            Toasty.success(
                                                                context,
                                                                "Drink Added.",
                                                                Toast.LENGTH_LONG,
                                                                true
                                                            ).show()
                                                        }


                                                }
                                        }

//

                                }


                        }

                }
            }


    }

    override fun getItemCount(): Int {
        return addproductList.size
    }

    class AddProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView

        var bg: ImageView

        init {
            name = itemView.findViewById(R.id.product_name)
            bg = itemView.findViewById(R.id.recently_layout)
        }
    }

    init {
        this.addproductList = addedproductsList
        this.onclickimagelistener = onClickImageListener
    }

}







