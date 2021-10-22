package com.jettech.sherehe20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.tabs.TabLayout
import com.jettech.sherehe20.adapter.AddProductAdapter
import com.jettech.sherehe20.adapter.LoginAdapter
import com.jettech.sherehe20.model.AddProduct

class MainActivity : AppCompatActivity() {

    var addedProductRecycler: RecyclerView? = null
    var addedProductAdapter: AddProductAdapter? = null
    var addProductList: List<AddProduct>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addedProductRecycler = findViewById(R.id.productRec)


        // adding data to model
        addProductList = ArrayList<AddProduct>()
        (addProductList as ArrayList<AddProduct>).add(
            AddProduct(
                "Whiskey & Spirits",
                "Watermelon has high water content and also provides some fiber.",
                "₹ 80",
                "1",
                "KG",
                R.drawable.cognacliq

            )
        )
        (addProductList as ArrayList<AddProduct>).add(
            AddProduct(
                "Wine & Champagne",
                "Papayas are spherical or pear-shaped fruits that can be as long as 20 inches.",
                "₹ 85",
                "1",
                "KG",
                R.drawable.champagne

            )
        )
        (addProductList as ArrayList<AddProduct>).add(
            AddProduct(
                "Beer,Cider & Alcopop",
                "The strawberry is a highly nutritious fruit, loaded with vitamin C.",
                "₹ 30",
                "1",
                "KG",
                R.drawable.beer

            )
        )
        (addProductList as ArrayList<AddProduct>).add(
            AddProduct(
                "Soft drinks & mixers",
                "Full of nutrients like vitamin C, vitamin K, vitamin E, folate, and potassium.",
                "₹ 30",
                "1",
                "PC",
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
}