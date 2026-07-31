package com.alix.tsuki.list.ui.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.R
import com.alix.tsuki.core.ui.list.AdapterDelegateClickListenerAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.util.ext.setTooltipCompat
import com.alix.tsuki.databinding.ItemMangaGridBinding
import com.alix.tsuki.list.ui.ListModelDiffCallback.Companion.PAYLOAD_PROGRESS_CHANGED
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.list.ui.model.MangaGridModel
import com.alix.tsuki.list.ui.model.MangaListModel
import com.alix.tsuki.list.ui.size.ItemSizeResolver

fun mangaGridItemAD(
	sizeResolver: ItemSizeResolver,
	clickListener: OnListItemClickListener<MangaListModel>,
) = adapterDelegateViewBinding<MangaGridModel, ListModel, ItemMangaGridBinding>(
	{ inflater, parent -> ItemMangaGridBinding.inflate(inflater, parent, false) },
) {

	AdapterDelegateClickListenerAdapter(this, clickListener).attach(itemView)
	sizeResolver.attachToView(itemView, binding.textViewTitle, binding.progressView)

	bind { payloads ->
		itemView.setTooltipCompat(item.getSummary(context))
		binding.textViewTitle.text = item.title
		binding.textViewTitle.isVisible = !item.isTitleHidden
		binding.progressView.setProgress(item.progress, PAYLOAD_PROGRESS_CHANGED in payloads)
		with(binding.iconsView) {
			clearIcons()
			if (item.isSaved) addIcon(R.drawable.ic_storage)
			if (item.isFavorite) addIcon(R.drawable.ic_heart_outline)
			isVisible = iconsCount > 0
		}
		binding.imageViewCover.setImageAsync(item.coverUrl, item.manga)
		binding.badge.number = item.counter
		binding.badge.isVisible = item.counter > 0
	}
}
