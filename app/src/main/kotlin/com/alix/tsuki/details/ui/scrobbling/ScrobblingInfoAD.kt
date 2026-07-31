package com.alix.tsuki.details.ui.scrobbling

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.R
import com.alix.tsuki.core.nav.AppRouter
import com.alix.tsuki.databinding.ItemScrobblingInfoBinding
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.scrobbling.common.domain.model.ScrobblingInfo

fun scrobblingInfoAD(
	router: AppRouter,
) = adapterDelegateViewBinding<ScrobblingInfo, ListModel, ItemScrobblingInfoBinding>(
	{ layoutInflater, parent -> ItemScrobblingInfoBinding.inflate(layoutInflater, parent, false) },
) {
	binding.root.setOnClickListener {
		router.showScrobblingInfoSheet(bindingAdapterPosition)
	}

	bind {
		binding.imageViewCover.setImageAsync(item.coverUrl)
		binding.textViewTitle.setText(item.scrobbler.titleResId)
		binding.imageViewIcon.setImageResource(item.scrobbler.iconResId)
		binding.ratingBar.rating = item.rating * binding.ratingBar.numStars
		binding.textViewStatus.text = item.status?.let {
			context.resources.getStringArray(R.array.scrobbling_statuses).getOrNull(it.ordinal)
		}
	}
}
