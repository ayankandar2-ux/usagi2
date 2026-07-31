package com.alix.tsuki.filter.ui.model

import com.alix.tsuki.list.ui.ListModelDiffCallback
import com.alix.tsuki.list.ui.model.ListModel
import tsuki.model.MangaTag

data class TagCatalogItem(
	val tag: MangaTag,
	val isChecked: Boolean,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is TagCatalogItem && other.tag == tag
	}

	override fun getChangePayload(previousState: ListModel): Any? {
		return if (previousState is TagCatalogItem && previousState.isChecked != isChecked) {
			ListModelDiffCallback.PAYLOAD_CHECKED_CHANGED
		} else {
			super.getChangePayload(previousState)
		}
	}
}
