package com.alix.tsuki.list.ui.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.core.util.ext.setTextAndVisible
import com.alix.tsuki.databinding.ItemInfoBinding
import com.alix.tsuki.list.ui.model.InfoModel
import com.alix.tsuki.list.ui.model.ListModel

fun infoAD() = adapterDelegateViewBinding<InfoModel, ListModel, ItemInfoBinding>(
	{ layoutInflater, parent -> ItemInfoBinding.inflate(layoutInflater, parent, false) },
) {

	bind {
		binding.textViewTitle.setText(item.title)
		binding.textViewBody.setTextAndVisible(item.text)
		binding.textViewTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
			item.icon, 0, 0, 0,
		)
	}
}
