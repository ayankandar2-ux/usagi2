package com.alix.tsuki.settings.search

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.R
import com.alix.tsuki.core.ui.list.AdapterDelegateClickListenerAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.util.ext.textAndVisible
import com.alix.tsuki.databinding.ItemPreferenceBinding

fun settingsItemAD(
	listener: OnListItemClickListener<SettingsItem>,
) = adapterDelegateViewBinding<SettingsItem, SettingsItem, ItemPreferenceBinding>(
	{ layoutInflater, parent -> ItemPreferenceBinding.inflate(layoutInflater, parent, false) },
) {

	AdapterDelegateClickListenerAdapter(this, listener).attach()
	val breadcrumbsSeparator = getString(R.string.breadcrumbs_separator)

	bind {
		binding.textViewTitle.text = item.title
		binding.textViewSummary.textAndVisible = item.breadcrumbs.joinToString(breadcrumbsSeparator)
	}
}
