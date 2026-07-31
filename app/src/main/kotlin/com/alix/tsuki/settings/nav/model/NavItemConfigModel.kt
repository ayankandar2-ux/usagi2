package com.alix.tsuki.settings.nav.model

import androidx.annotation.StringRes
import com.alix.tsuki.core.prefs.NavItem
import com.alix.tsuki.list.ui.model.ListModel

data class NavItemConfigModel(
	val item: NavItem,
	@StringRes val disabledHintResId: Int,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is NavItemConfigModel && other.item == item
	}
}
