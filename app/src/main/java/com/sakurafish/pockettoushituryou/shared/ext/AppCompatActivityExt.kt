package com.sakurafish.pockettoushituryou.shared.ext

import android.content.Intent
import android.net.Uri
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.replaceFragment(fragment: Fragment, @IdRes @LayoutRes layoutResId: Int) {
    val ft = supportFragmentManager.beginTransaction()
    ft.replace(layoutResId, fragment, fragment.javaClass.simpleName)
    ft.commit()
}

fun AppCompatActivity.goBrowser(@NonNull url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(intent)
}