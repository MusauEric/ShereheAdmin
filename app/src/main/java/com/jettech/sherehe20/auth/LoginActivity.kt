package com.jettech.sherehe20.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.tabs.TabLayout
import com.jettech.sherehe20.R
import com.jettech.sherehe20.adapter.LoginAdapter

class LoginActivity : AppCompatActivity() {
    var lottieAnimationView: LottieAnimationView? = null
    var button: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val  tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val  viewPager = findViewById<ViewPager>(R.id.viewPager)
        val lottieAnimationView = findViewById<LottieAnimationView>(R.id.imageView3)
        tabLayout.addTab(tabLayout.newTab().setText("Login"))
        tabLayout.addTab(tabLayout.newTab().setText("Signup"))
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)
        val adapter = LoginAdapter(supportFragmentManager, this, tabLayout.getTabCount())
        viewPager.setAdapter(adapter)
        tabLayout.setupWithViewPager(viewPager)
        //viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))



    }



}