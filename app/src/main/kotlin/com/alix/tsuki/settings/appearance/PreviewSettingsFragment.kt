package com.alix.tsuki.settings.appearance

import android.os.Bundle
import androidx.preference.ListPreference
import dagger.hilt.android.AndroidEntryPoint
import com.alix.tsuki.R
import com.alix.tsuki.core.prefs.AppSettings
import com.alix.tsuki.core.prefs.DetailsUiMode
import com.alix.tsuki.core.ui.BasePreferenceFragment
import com.alix.tsuki.core.util.ext.setDefaultValueCompat
import tsuki.util.names
import com.alix.tsuki.settings.utils.PercentSummaryProvider
import com.alix.tsuki.settings.utils.SliderPreference

@AndroidEntryPoint
class PreviewSettingsFragment :
    BasePreferenceFragment(R.string.details_appearance) {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_details_appearance)

        findPreference<ListPreference>(AppSettings.KEY_DETAILS_UI)?.run {
            entryValues = DetailsUiMode.entries.names()
            setDefaultValueCompat(DetailsUiMode.MODERN.name)
        }

        findPreference<SliderPreference>(AppSettings.KEY_DETAILS_BACKDROP_BLUR_AMOUNT)
            ?.summaryProvider = PercentSummaryProvider()
    }
}
