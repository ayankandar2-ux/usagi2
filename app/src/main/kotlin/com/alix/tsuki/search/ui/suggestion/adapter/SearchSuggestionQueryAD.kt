package com.alix.tsuki.search.ui.suggestion.adapter

import android.view.View
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.R
import com.alix.tsuki.databinding.ItemSearchSuggestionQueryBinding
import com.alix.tsuki.search.domain.SearchKind
import com.alix.tsuki.search.ui.suggestion.SearchSuggestionListener
import com.alix.tsuki.search.ui.suggestion.model.SearchSuggestionItem

fun searchSuggestionQueryAD(
	listener: SearchSuggestionListener,
) =
	adapterDelegateViewBinding<SearchSuggestionItem.RecentQuery, SearchSuggestionItem, ItemSearchSuggestionQueryBinding>(
		{ inflater, parent -> ItemSearchSuggestionQueryBinding.inflate(inflater, parent, false) },
	) {

		val viewClickListener = View.OnClickListener { v ->
			listener.onQueryClick(item.query, SearchKind.SIMPLE, v.id != R.id.button_complete)
		}

		binding.root.setOnClickListener(viewClickListener)
		binding.buttonComplete.setOnClickListener(viewClickListener)

		bind {
			binding.textViewTitle.text = item.query
		}
	}
