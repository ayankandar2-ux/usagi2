package com.alix.tsuki.search.ui.suggestion.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.core.ui.widgets.ChipsView
import com.alix.tsuki.databinding.ItemSearchSuggestionTagsBinding
import tsuki.model.MangaTag
import com.alix.tsuki.search.ui.suggestion.SearchSuggestionListener
import com.alix.tsuki.search.ui.suggestion.model.SearchSuggestionItem

fun searchSuggestionTagsAD(
	listener: SearchSuggestionListener,
) = adapterDelegateViewBinding<SearchSuggestionItem.Tags, SearchSuggestionItem, ItemSearchSuggestionTagsBinding>(
	{ layoutInflater, parent -> ItemSearchSuggestionTagsBinding.inflate(layoutInflater, parent, false) },
) {

	binding.chipsGenres.onChipClickListener = ChipsView.OnChipClickListener { _, data ->
		listener.onTagClick(data as? MangaTag ?: return@OnChipClickListener)
	}

	bind {
		binding.chipsGenres.setChips(item.tags)
	}
}
