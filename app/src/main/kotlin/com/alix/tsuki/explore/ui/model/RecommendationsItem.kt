package com.alix.tsuki.explore.ui.model

import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.list.ui.model.MangaCompactListModel

data class RecommendationsItem(
	val manga: List<MangaCompactListModel>
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is RecommendationsItem
	}
}
