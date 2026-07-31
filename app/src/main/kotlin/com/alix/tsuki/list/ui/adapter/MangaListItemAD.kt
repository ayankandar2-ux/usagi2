package com.alix.tsuki.list.ui.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.core.ui.list.AdapterDelegateClickListenerAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.util.ext.setTooltipCompat
import com.alix.tsuki.core.util.ext.textAndVisible
import com.alix.tsuki.databinding.ItemMangaListBinding
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.list.ui.model.MangaCompactListModel
import com.alix.tsuki.list.ui.model.MangaListModel

fun mangaListItemAD(
	clickListener: OnListItemClickListener<MangaListModel>,
) = adapterDelegateViewBinding<MangaCompactListModel, ListModel, ItemMangaListBinding>(
	{ inflater, parent -> ItemMangaListBinding.inflate(inflater, parent, false) },
) {

	AdapterDelegateClickListenerAdapter(this, clickListener).attach(itemView)

	bind {
		itemView.setTooltipCompat(item.getSummary(context))
		binding.textViewTitle.text = item.title
		binding.textViewSubtitle.textAndVisible = item.subtitle
		binding.imageViewCover.setImageAsync(item.coverUrl, item.manga)
		binding.badge.number = item.counter
		binding.badge.isVisible = item.counter > 0
	}
}
