package com.jettech.sherehe20.Helper

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import com.jettech.sherehe20.R

object AnimationUtil{
    fun shakeView(view: View, context: Context?) {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake_view)
        view.startAnimation(shake)
    }

    fun slideup(view: View, context: Context?) {
        val shake = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        view.startAnimation(shake)
    }

    fun slideleft(view: View, context: Context?) {
        val shake = AnimationUtils.loadAnimation(context, R.anim.slide_from_left)
        view.startAnimation(shake)
    }

    fun slideright(view: View, context: Context?) {
        val shake = AnimationUtils.loadAnimation(context, R.anim.slide_from_right)
        view.startAnimation(shake)
    }
}