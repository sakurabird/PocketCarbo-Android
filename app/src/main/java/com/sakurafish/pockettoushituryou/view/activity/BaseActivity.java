package com.sakurafish.pockettoushituryou.view.activity;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public abstract class BaseActivity extends AppCompatActivity {

    final void replaceFragment(@NonNull Fragment fragment, @IdRes @LayoutRes int layoutResId) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(layoutResId, fragment, fragment.getClass().getSimpleName());
        ft.commit();
    }

    protected void goBrowser(@NonNull final String url) {
        final Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
