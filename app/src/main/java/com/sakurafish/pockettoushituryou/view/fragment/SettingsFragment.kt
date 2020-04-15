package com.sakurafish.pockettoushituryou.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.sakurafish.pockettoushituryou.BuildConfig
import com.sakurafish.pockettoushituryou.R
import com.sakurafish.pockettoushituryou.view.activity.WebViewActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        initLayout()
    }

    private fun initLayout() {

        val policy = findPreference(getString(R.string.PREF_PRIVACY_POLICY)) as Preference?
        policy?.setOnPreferenceClickListener {
            showPrivacyPolicy()
            true
        }
        val credit = findPreference(getString(R.string.PREF_CREDIT)) as Preference?
        credit?.setOnPreferenceClickListener {
            showCreditDialog()
            true
        }

        val mailDev = findPreference(getString(R.string.PREF_MAIL_TO_DEV)) as Preference?
        mailDev?.setOnPreferenceClickListener {
            showMailIntent()
            true
        }
    }

    private fun showPrivacyPolicy() {
        startActivity(WebViewActivity.createIntent(requireContext(), "file:///android_asset/www/privacy_policy.html", getString(R.string.setting_privacy_policy)))
    }

    private fun showCreditDialog() {
        startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
    }

    private fun showMailIntent() {
        val appVersion = BuildConfig.VERSION_NAME
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val device = if (model.startsWith(manufacturer)) {
            model
        } else {
            "$manufacturer $model"
        }
        val osVersion = android.os.Build.VERSION.SDK_INT
        val mailBody = requireContext().getString(R.string.setting_mail_to_dev_body, appVersion, device, osVersion)

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("sakurafish1@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_mail_to_dev_subject))
        intent.putExtra(Intent.EXTRA_TEXT, mailBody)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }
}