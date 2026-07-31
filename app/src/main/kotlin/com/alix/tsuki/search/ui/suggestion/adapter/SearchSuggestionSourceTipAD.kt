package com.alix.tsuki.search.ui.suggestion.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.core.model.getSummary
import com.alix.tsuki.core.model.getTitle
import com.alix.tsuki.databinding.ItemSearchSuggestionSourceTipBinding
import com.alix.tsuki.search.ui.suggestion.SearchSuggestionListener
import com.alix.tsuki.search.ui.suggestion.model.SearchSuggestionItem

fun searchSuggestionSourceTipAD(
	listener: SearchSuggestionListener,
) =
	adapterDelegateViewBinding<SearchSuggestionItem.SourceTip, SearchSuggestionItem, ItemSearchSuggestionSourceTipBinding>(
		{ inflater, parent -> ItemSearchSuggestionSourceTipBinding.inflate(inflater, parent, false) },
	) {

		binding.root.setOnClickListener {
			listener.onSourceClick(item.source)
		}

		bind {
			binding.textViewTitle.text = item.source.getTitle(context)
			binding.textViewSubtitle.text = item.source.getSummary(context)
			binding.imageViewCover.setImageAsync(item.source)
		}
	}
