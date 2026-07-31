package com.alix.tsuki.bookmarks.ui.adapter

import android.content.Context
import com.alix.tsuki.bookmarks.domain.Bookmark
import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.ui.list.fastscroll.FastScroller
import com.alix.tsuki.list.ui.adapter.ListHeaderClickListener
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.emptyStateListAD
import com.alix.tsuki.list.ui.adapter.errorStateListAD
import com.alix.tsuki.list.ui.adapter.listHeaderAD
import com.alix.tsuki.list.ui.adapter.loadingFooterAD
import com.alix.tsuki.list.ui.adapter.loadingStateAD
import com.alix.tsuki.list.ui.model.ListModel

class BookmarksAdapter(
	clickListener: OnListItemClickListener<Bookmark>,
	headerClickListener: ListHeaderClickListener?,
) : BaseListAdapter<ListModel>(), FastScroller.SectionIndexer {

	init {
		addDelegate(ListItemType.PAGE_THUMB, bookmarkLargeAD(clickListener))
		addDelegate(ListItemType.HEADER, listHeaderAD(headerClickListener))
		addDelegate(ListItemType.STATE_ERROR, errorStateListAD(null))
		addDelegate(ListItemType.FOOTER_LOADING, loadingFooterAD())
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
		addDelegate(ListItemType.STATE_EMPTY, emptyStateListAD(null))
	}

	override fun getSectionText(context: Context, position: Int): CharSequence? {
		return findHeader(position)?.getText(context)
	}
}
