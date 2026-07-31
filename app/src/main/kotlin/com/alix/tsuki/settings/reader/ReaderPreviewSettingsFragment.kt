package com.alix.tsuki.settings.reader

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import dagger.hilt.android.AndroidEntryPoint
import com.alix.tsuki.R
import com.alix.tsuki.core.nav.router
import com.alix.tsuki.core.prefs.AppSettings
import com.alix.tsuki.core.prefs.ReaderAnimation
import com.alix.tsuki.core.prefs.ReaderBackground
import com.alix.tsuki.core.prefs.ReaderControl
import com.alix.tsuki.core.ui.BasePreferenceFragment
import com.alix.tsuki.core.util.ext.setDefaultValueCompat
import com.alix.tsuki.settings.utils.MultiSummaryProvider
import com.alix.tsuki.settings.utils.SliderPreference
import tsuki.util.mapToSet
import tsuki.util.names

@AndroidEntryPoint
class ReaderPreviewSettingsFragment :
	BasePreferenceFragment(R.string.reader_appearance) {

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		addPreferencesFromResource(R.xml.pref_reader)
		val pattern = getString(R.string.percent_string_pattern)
		val summary = Preference.SummaryProvider<SliderPreference> { pref ->
			pattern.format((150 - pref.value.coerceIn(50, 100)).toString())
		}
		findPreference<SliderPreference>(AppSettings.KEY_READER_TOP_BAR_OPACITY)?.summaryProvider = summary
		findPreference<SliderPreference>(AppSettings.KEY_READER_BOTTOM_BAR_OPACITY)?.summaryProvider = summary

		findPreference<ListPreference>(AppSettings.KEY_READER_ORIENTATION)?.run {
			entryValues = arrayOf(
				ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED.toString(), ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR.toString(),
				ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT.toString(), ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE.toString(),
			)
			setDefaultValueCompat(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED.toString())
		}
		findPreference<MultiSelectListPreference>(AppSettings.KEY_READER_CONTROLS)?.run {
			entryValues = ReaderControl.entries.names()
			setDefaultValueCompat(ReaderControl.DEFAULT.mapToSet { it.name })
			summaryProvider = MultiSummaryProvider(R.string.none)
		}
		findPreference<ListPreference>(AppSettings.KEY_READER_BACKGROUND)?.run {
			entryValues = ReaderBackground.entries.names()
			setDefaultValueCompat(ReaderBackground.DEFAULT.name)
		}
		findPreference<ListPreference>(AppSettings.KEY_READER_ANIMATION)?.run {
			entryValues = ReaderAnimation.entries.names()
			setDefaultValueCompat(ReaderAnimation.DEFAULT.name)
		}
	}

	override fun onPreferenceTreeClick(preference: Preference): Boolean {
		return when (preference.key) {
			AppSettings.KEY_READER_TAP_ACTIONS -> {
				router.openReaderTapGridSettings()
				true
			}

			else -> super.onPreferenceTreeClick(preference)
		}
	}
}
