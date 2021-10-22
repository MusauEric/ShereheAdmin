package com.jettech.sherehe20

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.green
import android.graphics.Color.red
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jettech.sherehe20.adapter.AddProductAdapter
import com.jettech.sherehe20.adapter.LoginAdapter
import com.jettech.sherehe20.model.AddProduct
import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity() {

    var addedProductRecycler: RecyclerView? = null
    var addedProductAdapter: AddProductAdapter? = null
    var addProductList: List<AddProduct>? = null
    private lateinit var auth: FirebaseAuth
    lateinit var progressDialog: Dialog
    var tillNumber: String = ""
    var payBill: String = ""
    var businessNumber: String = ""
    var address: String = ""
    var latCoord: String = ""
    var longCoord: String = ""
    var bSNumber: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore
        addedProductRecycler = findViewById(R.id.productRec)


        progressDialog = Dialog(this)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.custom_dialog_progress)
        val progressTv = progressDialog.findViewById(R.id.progress_tv) as TextView
        progressTv.text = resources.getString(R.string.loading)
        progressTv.setTextColor(ContextCompat.getColor(this, R.color.pink))
        progressTv.textSize = 15F

        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)
        progressDialog.show()



        db.collection("storeowner").document(user!!.uid).collection("store")
            .get().addOnSuccessListener { documentSnapshots ->
                val document = documentSnapshots.documents.toString()

                if (document.contains("[]")) {
                    addStore()
                } else {

                    loadStore ()

                }

            }


// adding data to model
        addProductList = ArrayList<AddProduct>()
        (addProductList as ArrayList<AddProduct>)
            .add(
                AddProduct(
                    "Whiskey & Spirits",
                    R.drawable.cognacliq

                )
            )
        (addProductList as ArrayList<AddProduct>).add(
            AddProduct(
                "Wine & Champagne",
                R.drawable.champagne

            )
        )
        (addProductList as ArrayList<AddProduct>).add(
            AddProduct(
                "Beer,Cider & Alcopop",
                R.drawable.beer

            )
        )
        (addProductList as ArrayList<AddProduct>).add(
            AddProduct(
                "Soft drinks & mixers",
                R.drawable.softdrink,

                )
        )

        setAddedProductRecycler(addProductList as ArrayList<AddProduct>)


    }

    private fun setAddedProductRecycler(addProductDataList: List<AddProduct>) {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        addedProductRecycler!!.layoutManager = layoutManager
        addedProductAdapter = AddProductAdapter(this, addProductDataList)
        addedProductRecycler!!.adapter = addedProductAdapter
    }

    private fun addStore() {

        progressDialog.dismiss()

        val user = Firebase.auth.currentUser
        val db = Firebase.firestore


        val viewGroup = findViewById<ViewGroup>(R.id.root)
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.view_add_store, viewGroup, false)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setCancelable(false)
        builder.setView(dialogView)

        val alertDialog: AlertDialog = builder.create()

        builder.apply {

            val addAddress = dialogView.findViewById<LinearLayout>(R.id.setLocation)
            val storeTitle = dialogView.findViewById<EditText>(R.id.storeTitle)
            val storeTitleName = dialogView.findViewById<EditText>(R.id.storeTitleName)

            val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
            val done = dialogView.findViewById<Button>(R.id.done)
            val addressLoc = dialogView.findViewById<TextView>(R.id.addressLocation)

            val i = intent
            val lat = i.getStringExtra("latitude")

            if (lat == null) {

                startActivity(Intent(context, MapActivity::class.java))

            } else {
                val o = intent
                longCoord = o.getStringExtra("longitude")!!
                address = o.getStringExtra("address")!!
                latCoord = o.getStringExtra("latitude")!!
                addressLoc.text = address.toString()

            }


            radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
                run {
                    when (checkedId) {
                        R.id.radioTill -> {
                            tillNumber = "TillNumber"
                            val tillPaybill = dialogView.findViewById<EditText>(R.id.storeTitle)
                            val bLayout = dialogView.findViewById<LinearLayout>(R.id.businessL)
                            bLayout.visibility = View.VISIBLE
                            tillPaybill.hint = "Enter Till Number"

                            businessNumber = tillNumber


                        }
                        R.id.radioBill -> {
                            val tillPaybill = dialogView.findViewById<EditText>(R.id.storeTitle)
                            payBill = "PayBill"
                            tillPaybill.hint = "Enter PayBill Number"
                            val bLayout = dialogView.findViewById<LinearLayout>(R.id.businessL)
                            bLayout.visibility = View.VISIBLE

                            businessNumber = payBill


                        }

                    }
                }
            }

            addAddress.setOnClickListener {

                startActivity(Intent(context, MapActivity::class.java))

            }

            done.setOnClickListener {

                if (storeTitleName.text!!.isEmpty()) {


                    Toasty.error(
                        this@MainActivity, "Enter Store Name", Toasty.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                if (storeTitle.text!!.isEmpty()) {

                    Toasty.error(
                        this@MainActivity, "Enter Business number", Toasty.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                } else {

                    progressDialog = Dialog(context)
                    progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    progressDialog.setContentView(R.layout.custom_dialog_progress)
                    val progressTv = progressDialog.findViewById(R.id.progress_tv) as TextView
                    progressTv.text = resources.getString(R.string.loading)
                    progressTv.setTextColor(ContextCompat.getColor(context, R.color.pink))
                    progressTv.textSize = 15F

                    progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    progressDialog.setCancelable(false)
                    progressDialog.show()




                    val user = Firebase.auth.currentUser
                    val db = Firebase.firestore

                    val data = hashMapOf(
                        "storename" to storeTitleName.text.toString(),
                        "businessNo" to  businessNumber +" "+storeTitle.text.toString(),
                        "ownerUid" to user!!.uid.toString(),
                        "latCord" to latCoord.toString(),
                        "longCord" to longCoord.toString(),
                        "address" to address.toString()
                    )
                    db.collection("storeowner").document(user!!.uid)
                        .collection("store")
                        .add(data)
                        .addOnCompleteListener {
                            val storeId = it.result.id
                            val newDocdata = hashMapOf("storeid" to storeId)
                            db.collection("storeowner").document(user!!.uid)
                                .collection("store").document(storeId)
                                .set(newDocdata, SetOptions.merge())
                                .addOnCompleteListener {
                                    val updateOwnerdata = hashMapOf(
                                        "storeid" to storeId,
                                        "storename" to storeTitleName.text.toString(),
                                        "businessNo" to businessNumber +" "+storeTitle.text.toString(),
                                        "ownerUid" to user!!.uid.toString(),
                                        "latCord" to latCoord.toString(),
                                        "longCord" to longCoord.toString(),
                                        "status" to "0",
                                        "address" to address.toString()
                                    )
                                    db.collection("storeowner").document(user!!.uid)
                                        .set(updateOwnerdata, SetOptions.merge())
                                    alertDialog.dismiss()
                                    loadStore ()
                                    progressDialog.dismiss()
                                    Toasty.success(
                                        context,
                                        "Store Created.",
                                        Toast.LENGTH_LONG,
                                        true
                                    ).show()
                                }

                        }

                }


            }




            alertDialog.show()
        }


    }

    private fun loadStore (){
        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

        val docRef = db.collection("storeowner").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val storeName = findViewById<TextView>(R.id.storeName)
                    val storeLocation = findViewById<TextView>(R.id.storeLocation)
                    val status = findViewById<TextView>(R.id.status)

                    val name = document.data!!["storename"].toString()
                    val storeStatus = document.data!!["status"].toString()
                    val storeAddress = document.data!!["address"].toString()
                    storeName.text = name
                    storeLocation.text = storeAddress

                    if (storeStatus.contains("0")) {
                        status.text = "CLOSED"
                        status.setTextColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.quantum_googred
                            )
                        )
                    } else {
                        status.text = "OPEN"
                        status.setTextColor(
                            ContextCompat.getColor(
                                this@MainActivity,
                                R.color.quantum_googgreen
                            )
                        )
                    }

                }
            }

        progressDialog.dismiss()

    }


}