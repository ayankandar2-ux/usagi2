package com.alix.tsuki.list.ui.model

import com.alix.tsuki.core.ui.widgets.ChipsView
import com.alix.tsuki.list.ui.ListModelDiffCallback

data class QuickFilter(
	val items: List<ChipsView.ChipModel>,
) : ListModel {

	override fun areItemsTheSame(other: ListModel): Boolean = other is QuickFilter

	override fun getChangePayload(previousState: ListModel) = ListModelDiffCallback.PAYLOAD_NESTED_LIST_CHANGED
}
