package com.alix.tsuki.search.ui.multi.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.ui.list.fastscroll.FastScroller
import com.alix.tsuki.list.ui.MangaSelectionDecoration
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.MangaListListener
import com.alix.tsuki.list.ui.adapter.buttonFooterAD
import com.alix.tsuki.list.ui.adapter.emptyStateListAD
import com.alix.tsuki.list.ui.adapter.errorStateListAD
import com.alix.tsuki.list.ui.adapter.loadingFooterAD
import com.alix.tsuki.list.ui.adapter.loadingStateAD
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.list.ui.size.ItemSizeResolver
import com.alix.tsuki.search.ui.multi.SearchResultsListModel

class SearchAdapter(
	listener: MangaListListener,
	itemClickListener: OnListItemClickListener<SearchResultsListModel>,
	sizeResolver: ItemSizeResolver,
	selectionDecoration: MangaSelectionDecoration,
) : BaseListAdapter<ListModel>(), FastScroller.SectionIndexer {

	init {
		val pool = RecycledViewPool()
		addDelegate(
			ListItemType.MANGA_NESTED_GROUP,
			searchResultsAD(
				sharedPool = pool,
				sizeResolver = sizeResolver,
				selectionDecoration = selectionDecoration,
				listener = listener,
				itemClickListener = itemClickListener,
			),
		)
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
		addDelegate(ListItemType.FOOTER_LOADING, loadingFooterAD())
		addDelegate(ListItemType.STATE_EMPTY, emptyStateListAD(listener))
		addDelegate(ListItemType.STATE_ERROR, errorStateListAD(listener))
		addDelegate(ListItemType.FOOTER_BUTTON, buttonFooterAD(listener))
	}

	override fun getSectionText(context: Context, position: Int): CharSequence? {
		return (items.getOrNull(position) as? SearchResultsListModel)?.getTitle(context)
	}
}
