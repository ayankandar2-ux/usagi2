package com.alix.tsuki.list.ui.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.databinding.ItemButtonFooterBinding
import com.alix.tsuki.list.ui.model.ButtonFooter
import com.alix.tsuki.list.ui.model.ListModel

fun buttonFooterAD(
	listener: ListStateHolderListener,
) = adapterDelegateViewBinding<ButtonFooter, ListModel, ItemButtonFooterBinding>(
	{ inflater, parent -> ItemButtonFooterBinding.inflate(inflater, parent, false) },
) {

	binding.button.setOnClickListener {
		listener.onFooterButtonClick()
	}

	bind {
		binding.button.setText(item.textResId)
	}
}
