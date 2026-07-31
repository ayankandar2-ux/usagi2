package com.alix.tsuki.search.ui.suggestion.adapter

import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.databinding.ItemSearchSuggestionQueryHintBinding
import com.alix.tsuki.search.domain.SearchKind
import com.alix.tsuki.search.ui.suggestion.SearchSuggestionListener
import com.alix.tsuki.search.ui.suggestion.model.SearchSuggestionItem

fun searchSuggestionQueryHintAD(
	listener: SearchSuggestionListener,
) = adapterDelegateViewBinding<SearchSuggestionItem.Hint, SearchSuggestionItem, ItemSearchSuggestionQueryHintBinding>(
	{ inflater, parent -> ItemSearchSuggestionQueryHintBinding.inflate(inflater, parent, false) },
) {

	val viewClickListener = View.OnClickListener { _ ->
		listener.onQueryClick(item.query, SearchKind.SIMPLE, true)
	}

	binding.root.setOnClickListener(viewClickListener)

	bind {
		binding.root.text = item.query
	}
}
