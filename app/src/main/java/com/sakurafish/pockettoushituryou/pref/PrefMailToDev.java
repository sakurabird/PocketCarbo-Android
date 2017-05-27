package com.sakurafish.pockettoushituryou.pref;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.sakurafish.pockettoushituryou.R;

public class PrefMailToDev extends Preference {
    public PrefMailToDev(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(final View view) {
        TextView t = (TextView) view.findViewById(R.id.text);
        t.setText(R.string.setting_mail_to_dev);
        super.onBindView(view);
    }
}
