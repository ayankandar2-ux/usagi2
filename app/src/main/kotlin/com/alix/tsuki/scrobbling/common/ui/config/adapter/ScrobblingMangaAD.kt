package com.alix.tsuki.scrobbling.common.ui.config.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.core.ui.list.AdapterDelegateClickListenerAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.databinding.ItemScrobblingMangaBinding
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.scrobbling.common.domain.model.ScrobblingInfo

fun scrobblingMangaAD(
	clickListener: OnListItemClickListener<ScrobblingInfo>,
) = adapterDelegateViewBinding<ScrobblingInfo, ListModel, ItemScrobblingMangaBinding>(
	{ layoutInflater, parent -> ItemScrobblingMangaBinding.inflate(layoutInflater, parent, false) },
) {

	AdapterDelegateClickListenerAdapter(this, clickListener).attach(itemView)

	bind {
		binding.imageViewCover.setImageAsync(item.coverUrl, null)
		binding.textViewTitle.text = item.title
		binding.ratingBar.rating = item.rating * binding.ratingBar.numStars
	}
}
