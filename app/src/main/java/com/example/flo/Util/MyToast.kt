@file:Suppress("DEPRECATION")

package com.example.flo.Util

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.flo.R
import com.example.flo.databinding.ToastLikeBinding

object MyToast {

    fun createToast(context: Context, message: String): Toast? {
        val binding: ToastLikeBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.toast_like, null, false)

        binding.toastTv.text = message

        return Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 170.toPx())
            duration = Toast.LENGTH_SHORT
            view = binding.root
        }
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}