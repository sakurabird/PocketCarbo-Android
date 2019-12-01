package com.sakurafish.pockettoushituryou.view.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
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
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("sakurafish1@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_mail_to_dev2))
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_mail_to_dev3))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }
}