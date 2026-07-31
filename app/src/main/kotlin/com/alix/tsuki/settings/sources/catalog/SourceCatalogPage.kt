package com.alix.tsuki.settings.sources.catalog

import com.alix.tsuki.list.ui.ListModelDiffCallback
import com.alix.tsuki.list.ui.model.ListModel
import tsuki.model.ContentType

data class SourceCatalogPage(
	val type: ContentType,
	val items: List<SourceCatalogItem>,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean {
		return other is SourceCatalogPage && other.type == type
	}

	override fun getChangePayload(previousState: ListModel): Any {
		return ListModelDiffCallback.PAYLOAD_NESTED_LIST_CHANGED
	}
}
