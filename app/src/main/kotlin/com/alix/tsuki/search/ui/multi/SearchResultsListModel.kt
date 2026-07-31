package com.alix.tsuki.search.ui.multi

import android.content.Context
import androidx.annotation.StringRes
import com.alix.tsuki.core.model.getTitle
import com.alix.tsuki.list.ui.ListModelDiffCallback
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.list.ui.model.MangaListModel
import tsuki.model.MangaListFilter
import tsuki.model.MangaSource
import tsuki.model.SortOrder

data class SearchResultsListModel(
	@StringRes val titleResId: Int,
	val source: MangaSource,
	val listFilter: MangaListFilter?,
	val sortOrder: SortOrder?,
	val list: List<MangaListModel>,
	val error: Throwable?,
) : ListModel {

	fun getTitle(context: Context): String = if (titleResId != 0) {
		context.getString(titleResId)
	} else {
		source.getTitle(context)
	}

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is SearchResultsListModel && source == other.source && titleResId == other.titleResId
	}

	override fun getChangePayload(previousState: ListModel): Any? {
		return if (previousState is SearchResultsListModel && previousState.list != list) {
			ListModelDiffCallback.PAYLOAD_NESTED_LIST_CHANGED
		} else {
			super.getChangePayload(previousState)
		}
	}
}
