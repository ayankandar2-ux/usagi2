package com.alix.tsuki.list.ui.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.core.util.ext.setTextAndVisible
import com.alix.tsuki.databinding.ItemEmptyCardBinding
import com.alix.tsuki.list.ui.model.EmptyHint
import com.alix.tsuki.list.ui.model.ListModel

fun emptyHintAD(
	listener: ListStateHolderListener,
) = adapterDelegateViewBinding<EmptyHint, ListModel, ItemEmptyCardBinding>(
	{ inflater, parent -> ItemEmptyCardBinding.inflate(inflater, parent, false) },
) {

	binding.buttonRetry.setOnClickListener { listener.onEmptyActionClick() }

	bind {
		binding.icon.setImageAsync(item.icon)
		binding.textPrimary.setText(item.textPrimary)
		binding.textSecondary.setTextAndVisible(item.textSecondary)
		binding.buttonRetry.setTextAndVisible(item.actionStringRes)
	}
}
