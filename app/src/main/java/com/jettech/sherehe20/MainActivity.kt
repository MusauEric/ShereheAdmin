package com.jettech.sherehe20

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
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
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import com.jettech.sherehe20.adapter.AddProductAdapter
import com.jettech.sherehe20.model.AddProduct
import com.jettech.sherehe20.utils.Constants
import com.jettech.sherehe20.utils.OnClickImageListener
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.yalantis.ucrop.UCrop
import es.dmoral.toasty.Toasty
import java.io.File
import java.security.SecureRandom
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OnClickImageListener {

    var addedProductRecycler: RecyclerView? = null
    var addedProductAdapter: AddProductAdapter? = null
    var addProductList: List<AddProduct>? = null
    var drinkImage: String = ""
    private lateinit var auth: FirebaseAuth
    lateinit var progressDialog: Dialog
    var tillNumber: String = ""
    var payBill: String = ""
    var businessNumber: String = ""
    var address: String = ""
    var latCoord: String = ""
    var longCoord: String = ""
    var bSNumber: String = ""
    private var imageUri: Uri? = null
    private var file: File? = null
    private var downloadUrl: String? = null
    var getPosition: String = ""
    var imageType: Int? = null
    lateinit var dView: View
    var storageReference: StorageReference? = null
    private var mAuth: FirebaseAuth? = null
    private var firebaseFirestore: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore
        addedProductRecycler = findViewById(R.id.productRec)
        val editStore = findViewById<LinearLayout>(R.id.editStore)
        val mySwitch = findViewById<Switch>(R.id.ghostMode)
        val myStore = findViewById<Button>(R.id.myStore)

        getStoreInfo()
        imageUri = null
        mAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("images")
        firebaseFirestore = FirebaseFirestore.getInstance()


        editStore.setOnClickListener {
            editStore()
        }
        myStore.setOnClickListener {
            startActivity(Intent(this, MyStoreActivity::class.java))
        }

        mySwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            if (isChecked == true) {
                openedStore()
            } else {
                closeStore()
            }
        })


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

    private fun openedStore() {
        auth = Firebase.auth
        val user = Firebase.auth.currentUser

//        progressDialog = Dialog(this)
//        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        progressDialog.setContentView(R.layout.custom_dialog_progress)
//        val progressTv = progressDialog.findViewById(R.id.progress_tv) as TextView
//        progressTv.text = resources.getString(R.string.loading)
//        progressTv.setTextColor(ContextCompat.getColor(this, R.color.pink))
//        progressTv.textSize = 15F
//
//        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        progressDialog.setCancelable(false)
//        progressDialog.show()

        val db = Firebase.firestore

        val adminOddsData = hashMapOf(
            "status" to "1",
        )
        db.collection("storeowner").document(user!!.uid)

            .update(adminOddsData as Map<String, Any>)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    Toasty.success(
                        this@MainActivity,
                        "Store Opened",
                        Toasty.LENGTH_LONG
                    ).show()

                    // progressDialog.dismiss()
                    loadStore()

                }
            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    // progressDialog.dismiss()
                }
            })

    }

    private fun closeStore() {
        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore
//        progressDialog = Dialog(this)
//        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        progressDialog.setContentView(R.layout.custom_dialog_progress)
//        val progressTv = progressDialog.findViewById(R.id.progress_tv) as TextView
//        progressTv.text = resources.getString(R.string.loading)
//        progressTv.setTextColor(ContextCompat.getColor(this, R.color.pink))
//        progressTv.textSize = 15F
//
//        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        progressDialog.setCancelable(false)
//        progressDialog.show()

        val adminOddsData = hashMapOf(
            "status" to "0",
        )
        db.collection("storeowner").document(user!!.uid)
            .update(adminOddsData as Map<String, Any>)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    Toasty.success(
                        this@MainActivity,
                        "Store Closed",
                        Toasty.LENGTH_LONG
                    ).show()
                    // progressDialog.dismiss()
                    loadStore()

                }
            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    // progressDialog.dismiss()
                }
            })
    }

    private fun checkSwitch() {

        auth = Firebase.auth
        val user = Firebase.auth.currentUser

        val db = Firebase.firestore

        val mechRef = db.collection("storeowner").document(user!!.uid)
        mechRef.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    val userdata = document.data!!
                    val closeOpen = userdata.get("status").toString()

                    if (closeOpen.equals("0")) {
                        val mySwitch = findViewById<Switch>(R.id.ghostMode)
                        mySwitch.isChecked = false
                    } else {
                        val mySwitch = findViewById<Switch>(R.id.ghostMode)
                        mySwitch.isChecked = true
                        //  dialog.dismiss()

                    }

                }

            }
    }

    private fun setAddedProductRecycler(addProductDataList: List<AddProduct>) {
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        addedProductRecycler!!.layoutManager = layoutManager
        addedProductAdapter = AddProductAdapter(this, addProductDataList, this)
        addedProductRecycler!!.adapter = addedProductAdapter
    }

    private fun getStoreInfo() {

        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

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

                    loadStore()
                }

            }

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

            val addAddress = dialogView.findViewById<Button>(R.id.setNewLocation)
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
                        "businessNo" to businessNumber + " " + storeTitle.text.toString(),
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
                                        "businessNo" to businessNumber + " " + storeTitle.text.toString(),
                                        "ownerUid" to user!!.uid.toString(),
                                        "latCord" to latCoord.toString(),
                                        "longCord" to longCoord.toString(),
                                        "status" to "0",
                                        "address" to address.toString()
                                    )
                                    db.collection("storeowner").document(user!!.uid)
                                        .set(updateOwnerdata, SetOptions.merge())
                                    alertDialog.dismiss()
                                    loadStore()
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

    private fun loadStore() {

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
                    val bSnumber = findViewById<TextView>(R.id.bSnumber)


                    val name = document.data!!["storename"].toString()
                    val storeStatus = document.data!!["status"].toString()
                    val storeAddress = document.data!!["address"].toString()
                    val tillBill = document.data!!["businessNo"].toString()
                    storeName.text = name
                    storeLocation.text = storeAddress
                    bSnumber.text = tillBill

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

                    progressDialog.dismiss()

                }
            }


    }

    private fun editStore() {

        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

        val viewGroup = findViewById<ViewGroup>(R.id.rootEdit)
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.view_edit_store, viewGroup, false)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setCancelable(false)
        builder.setView(dialogView)

        val alertDialog: AlertDialog = builder.create()

        var sharedPref: SharedPreferences =
            getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
        val editor = sharedPref.edit()
        editor.putString(Constants.EDIT, "1")
        editor.apply()

        val docRef = db.collection("storeowner").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val addressLocationE =
                        dialogView.findViewById<TextView>(R.id.addressLocationEdit)
                    val storeTitleNameE = dialogView.findViewById<EditText>(R.id.storeTitleNameEdit)
                    val bsNoE = dialogView.findViewById<TextView>(R.id.bsNo)

                    val name = document.data!!["storename"].toString()
                    val businessNo = document.data!!["businessNo"].toString()
                    val storeAddress = document.data!!["address"].toString()

                    addressLocationE.text = storeAddress
                    storeTitleNameE.setText(name)
                    bsNoE.text = businessNo

                }
            }


        builder.apply {

//            val addAddress = dialogView.findViewById<Button>(R.id.setLNewlocation)
            val storeTitleNameE = dialogView.findViewById<EditText>(R.id.storeTitleNameEdit)
            val storeTillE = dialogView.findViewById<EditText>(R.id.storeTillE)

            val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupE)
            val done = dialogView.findViewById<Button>(R.id.doneE)
            val cancel = dialogView.findViewById<Button>(R.id.cancelE)

            val addressLoc = dialogView.findViewById<TextView>(R.id.addressLocation)


            radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
                run {
                    when (checkedId) {
                        R.id.radioTillE -> {
                            tillNumber = "TillNumber"
                            val tillPaybill = dialogView.findViewById<EditText>(R.id.storeTillE)
                            val bLayout = dialogView.findViewById<LinearLayout>(R.id.businessLE)
                            bLayout.visibility = View.VISIBLE
                            tillPaybill.hint = "Enter Till Number"

                            businessNumber = tillNumber


                        }
                        R.id.radioBillE -> {
                            val tillPaybill = dialogView.findViewById<EditText>(R.id.storeTillE)
                            payBill = "PayBill"
                            tillPaybill.hint = "Enter PayBill Number"
                            val bLayout = dialogView.findViewById<LinearLayout>(R.id.businessLE)
                            bLayout.visibility = View.VISIBLE

                            businessNumber = payBill


                        }

                    }
                }
            }

//            addAddress.setOnClickListener {
//
//                startActivity(Intent(context, MapActivity::class.java))
//
//            }

            done.setOnClickListener {

                if (storeTitleNameE.text!!.isEmpty()) {


                    Toasty.error(
                        this@MainActivity, "Enter Store Name", Toasty.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                if (storeTillE.text!!.isEmpty()) {

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

                    val i = Intent(context, MapActivity::class.java)
                    i.putExtra("storeName", storeTitleNameE.text.toString())
                    i.putExtra("businessNo", businessNumber + " " + storeTillE.text.toString())
                    context.startActivity(i)
                    progressDialog.dismiss()
                }


            }
            cancel.setOnClickListener {
                alertDialog.dismiss()
            }

        }

        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        checkSwitch()
    }

    private fun startGallary() {
        ImagePicker.with(this)
            .setFolderMode(true)
            .setFolderTitle("Album")
            .setRootDirectoryName(Config.ROOT_DIR_DCIM)
            .setDirectoryName("Hosp images")
            .setMultipleMode(false)
            .setShowNumberIndicator(true)
            .setMaxSize(1)
            .setLimitMessage("You can select only one image")
            .setRequestCode(100)
            .start();

    }

    @SuppressLint("CutPasteId")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, 100)) {
            val images: ArrayList<Image> = ImagePicker.getImages(data)
            // Do stuff with image's path or id. For example:

            for (image in images) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    imageUri = image.uri

                    startCrop(imageUri!!)
                    /*  Glide.with(this)
                        .load(image.uri)
                        .into(userPhoto)*/
                } else {
                    file = File(image.path)
                    startCrop(Uri.fromFile(file))
                    /* Glide.with(this)
                        .load(image.path)
                        .into(userPhoto)*/
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
//

//            val selectedImage = dView.findViewById<ImageView>(R.id.selectedImage)
//            Glide.with(this)
//                .load(resultUri)
//                .into(selectedImage!!)

            imageUri = resultUri

            dialogImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("RegisterActivity", "Crop error:", cropError)
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...

            Toasty.error(
                this,
                "Something went wrong  photo upload. Please try again.",
                Toast.LENGTH_LONG,
                true
            ).show()
        }

    }

    private fun dialogImage() {
        val viewGroup = findViewById<ViewGroup>(R.id.rootImage)
        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.showimage, viewGroup, false)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setCancelable(false)
        dView = dialogView
        builder.setView(dialogView)

        val alertDialog: AlertDialog = builder.create()


        val saveImage = dialogView.findViewById<Button>(R.id.saveImage)
        val cancelImage = dialogView.findViewById<Button>(R.id.cancelImage)

        val selectedImage = dView.findViewById<ImageView>(R.id.selectedImage)
        Glide.with(this)
            .load(imageUri)
            .into(selectedImage!!)



        builder.apply {


            saveImage.setOnClickListener {

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

                val user = Firebase.auth.currentUser
                val db = Firebase.firestore
                val userUid = user!!.uid

                val storageRef = FirebaseStorage.getInstance().reference
                val ref = storageRef.child("images/" + rand(1, 20) + "${imageUri!!.lastPathSegment}")
                val uploadTask = ref.putFile(imageUri!!)

                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    ref.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        downloadUrl = task.result.toString()
                        Log.e("RegisterActivity", downloadUrl!!)

                    }
                }.addOnCompleteListener {

                    var sharedPref: SharedPreferences =
                        getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
                    val editor = sharedPref.edit()
                    editor.putString(Constants.IMAGE, downloadUrl)
                    editor.apply()
                    progressDialog.dismiss()


                }
                alertDialog.dismiss()


            }
            cancelImage.setOnClickListener {

                var sharedPref: SharedPreferences =
                    getSharedPreferences(Constants.APP_SHARED_PREFERENCES, 0)
                val editor = sharedPref.edit()
                editor.clear()
                editor.commit()
                alertDialog.dismiss()
            }

        }
        alertDialog.show()


    }

    private fun startCrop(imageUri: Uri) {
        val destinationFileName =
            StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()

        UCrop.of(imageUri, Uri.fromFile(File(cacheDir, destinationFileName)))
            .start(this)

    }

    private fun rand(start: Int, end: Int): Int {
        require(start <= end) { "Illegal Argument" }
        val random = SecureRandom()
        random.setSeed(random.generateSeed(20))

        return random.nextInt(end - start + 1) + start
    }


    override fun getImage(position: Int) {

        getPosition = position.toString()

        startGallary()

    }


}






