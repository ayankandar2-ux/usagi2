package com.alix.tsuki.tracker.ui.feed.adapter

import androidx.core.content.ContextCompat
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.R
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.util.ext.drawableStart
import com.alix.tsuki.core.util.ext.getQuantityStringSafe
import com.alix.tsuki.databinding.ItemFeedBinding
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.tracker.ui.feed.model.FeedItem

fun feedItemAD(
	clickListener: OnListItemClickListener<FeedItem>,
) = adapterDelegateViewBinding<FeedItem, ListModel, ItemFeedBinding>(
	{ inflater, parent -> ItemFeedBinding.inflate(inflater, parent, false) },
) {
	val indicatorNew = ContextCompat.getDrawable(context, R.drawable.ic_new)

	itemView.setOnClickListener {
		clickListener.onItemClick(item, it)
	}

	bind {
		binding.imageViewCover.setImageAsync(item.imageUrl, item.manga.source)
		binding.textViewTitle.text = item.title
		binding.textViewSummary.text = context.resources.getQuantityStringSafe(
			R.plurals.new_chapters,
			item.count,
			item.count,
		)
		binding.textViewSummary.drawableStart = if (item.isNew) {
			indicatorNew
		} else {
			null
		}
	}
}
