package com.example.spacemission.adapter

import android.widget.TextView

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter


@BindingAdapter("android:text")
fun setText(view: TextView, value: Int) {
    view.text = Integer.toString(value)
}

@InverseBindingAdapter(attribute = "android:text")
fun getText(view: TextView): Int {
    return view.text.toString().toInt()
}



