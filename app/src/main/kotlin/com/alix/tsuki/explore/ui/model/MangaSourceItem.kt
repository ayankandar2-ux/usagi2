package com.alix.tsuki.explore.ui.model

import com.alix.tsuki.core.model.MangaSourceInfo
import com.alix.tsuki.list.ui.model.ListModel
import tsuki.util.longHashCode

data class MangaSourceItem(
	val source: MangaSourceInfo,
	val isGrid: Boolean,
) : ListModel {

	val id: Long = source.name.longHashCode()

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is MangaSourceItem && other.source == source
	}
}
