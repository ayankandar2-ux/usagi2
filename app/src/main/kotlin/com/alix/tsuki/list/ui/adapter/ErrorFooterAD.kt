package com.alix.tsuki.list.ui.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.core.util.ext.getDisplayMessage
import com.alix.tsuki.databinding.ItemErrorFooterBinding
import com.alix.tsuki.list.ui.model.ErrorFooter
import com.alix.tsuki.list.ui.model.ListModel

fun errorFooterAD(
	listener: ListStateHolderListener?,
) = adapterDelegateViewBinding<ErrorFooter, ListModel, ItemErrorFooterBinding>(
	{ inflater, parent -> ItemErrorFooterBinding.inflate(inflater, parent, false) },
) {

	if (listener != null) {
		binding.root.setOnClickListener {
			listener.onRetryClick(item.exception)
		}
	}

	bind {
		binding.textViewTitle.text = item.exception.getDisplayMessage(context.resources)
	}
}
