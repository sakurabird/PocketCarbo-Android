package com.sakurafish.pockettoushituryou.shared.ext

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.replaceFragment(fragment: Fragment, @IdRes @LayoutRes layoutResId: Int) {
    val ft = supportFragmentManager.beginTransaction()
    ft.replace(layoutResId, fragment, fragment.javaClass.simpleName)
    ft.commit()
}