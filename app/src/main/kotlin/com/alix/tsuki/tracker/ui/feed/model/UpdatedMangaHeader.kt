package com.alix.tsuki.tracker.ui.feed.model

import com.alix.tsuki.list.ui.ListModelDiffCallback
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.list.ui.model.MangaListModel

data class UpdatedMangaHeader(
	val list: List<MangaListModel>,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is UpdatedMangaHeader
	}

	override fun getChangePayload(previousState: ListModel): Any {
		return ListModelDiffCallback.PAYLOAD_NESTED_LIST_CHANGED
	}
}
