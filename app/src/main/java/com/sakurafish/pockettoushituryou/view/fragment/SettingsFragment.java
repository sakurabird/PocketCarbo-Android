package com.sakurafish.pockettoushituryou.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.view.activity.WebViewActivity;

import de.psdev.licensesdialog.LicensesDialog;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Context mContext;

    public static SettingsFragment getInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        addPreferencesFromResource(R.xml.preferences);

        initLayout();
    }

    private void initLayout() {
        Preference credit = findPreference(getString(R.string.PREF_CREDIT));
        credit.setOnPreferenceClickListener(preference -> {
            showCreditDialog();
            return false;
        });

        Preference mailDev = findPreference(getString(R.string.PREF_MAIL_TO_DEV));
        mailDev.setOnPreferenceClickListener(preference -> {
            showMailIntent();
            return false;
        });

        Preference policy = findPreference(getString(R.string.PREF_PRIVACY_POLICY));
        policy.setOnPreferenceClickListener(preference -> {
            mContext.startActivity(WebViewActivity.createIntent(mContext
                    , "http://www.pockettoushituryou.com/static_pages/privacy_policy"
                    , getString(R.string.setting_privacy_policy)));
            return false;
        });

    }

    private void showCreditDialog() {
        new LicensesDialog.Builder(getActivity())
                .setNotices(R.raw.notices)
                .setIncludeOwnLicense(true)
                .build()
                .show();
    }

    private void showMailIntent() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sakurafish1@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_mail_to_dev2));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_mail_to_dev3));
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }
}
