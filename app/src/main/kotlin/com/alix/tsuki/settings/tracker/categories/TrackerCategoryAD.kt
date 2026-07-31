package com.alix.tsuki.settings.tracker.categories

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.core.model.FavouriteCategory
import com.alix.tsuki.core.ui.list.AdapterDelegateClickListenerAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.databinding.ItemCategoryCheckableMultipleBinding

fun trackerCategoryAD(
	listener: OnListItemClickListener<FavouriteCategory>,
) = adapterDelegateViewBinding<FavouriteCategory, FavouriteCategory, ItemCategoryCheckableMultipleBinding>(
	{ layoutInflater, parent -> ItemCategoryCheckableMultipleBinding.inflate(layoutInflater, parent, false) },
) {
	val eventListener = AdapterDelegateClickListenerAdapter(this, listener)
	itemView.setOnClickListener(eventListener)

	bind {
		binding.root.text = item.title
		binding.root.isChecked = item.isTrackingEnabled
	}
}
