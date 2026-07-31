package com.alix.tsuki.list.ui.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.core.ui.widgets.ChipsView
import com.alix.tsuki.databinding.ItemQuickFilterBinding
import com.alix.tsuki.list.domain.ListFilterOption
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.list.ui.model.QuickFilter

fun quickFilterAD(
	listener: QuickFilterClickListener,
) = adapterDelegateViewBinding<QuickFilter, ListModel, ItemQuickFilterBinding>(
	{ layoutInflater, parent -> ItemQuickFilterBinding.inflate(layoutInflater, parent, false) }
) {

	binding.chipsTags.onChipClickListener = ChipsView.OnChipClickListener { chip, data ->
		if (data is ListFilterOption) {
			listener.onFilterOptionClick(data)
		}
	}

	bind {
		binding.chipsTags.setChips(item.items)
	}
}
