package com.jettech.sherehe20.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.jettech.sherehe20.auth.LoginTabFragment
import com.jettech.sherehe20.auth.signupTabFragment

class LoginAdapter(fm: FragmentManager?, private val context: Context, var totalTabs: Int) :
    FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return LoginTabFragment()
            }
            1 -> {
                return signupTabFragment()
            }
        }
        return Fragment()
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
