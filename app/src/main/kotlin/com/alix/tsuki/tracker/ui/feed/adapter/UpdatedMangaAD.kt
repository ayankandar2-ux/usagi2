package com.alix.tsuki.tracker.ui.feed.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.alix.tsuki.R
import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.databinding.ItemListGroupBinding
import com.alix.tsuki.list.ui.adapter.ListHeaderClickListener
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.mangaGridItemAD
import com.alix.tsuki.list.ui.model.ListHeader
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.list.ui.model.MangaListModel
import com.alix.tsuki.list.ui.size.ItemSizeResolver
import com.alix.tsuki.tracker.ui.feed.model.UpdatedMangaHeader

fun updatedMangaAD(
	sizeResolver: ItemSizeResolver,
	listener: OnListItemClickListener<MangaListModel>,
	headerClickListener: ListHeaderClickListener,
) = adapterDelegateViewBinding<UpdatedMangaHeader, ListModel, ItemListGroupBinding>(
	{ layoutInflater, parent -> ItemListGroupBinding.inflate(layoutInflater, parent, false) },
) {

	val adapter = BaseListAdapter<ListModel>()
		.addDelegate(ListItemType.MANGA_GRID, mangaGridItemAD(sizeResolver, listener))
	binding.recyclerView.adapter = adapter
	binding.buttonMore.setOnClickListener { v ->
		headerClickListener.onListHeaderClick(ListHeader(0, payload = item), v)
	}
	binding.textViewTitle.setText(R.string.updates)
	binding.buttonMore.setText(R.string.more)

	bind {
		adapter.items = item.list
	}
}
