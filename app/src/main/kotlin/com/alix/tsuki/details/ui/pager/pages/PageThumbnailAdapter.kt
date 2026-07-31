package com.alix.tsuki.details.ui.pager.pages

import android.content.Context
import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.ui.list.fastscroll.FastScroller
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.listHeaderAD
import com.alix.tsuki.list.ui.model.ListModel

class PageThumbnailAdapter(
	clickListener: OnListItemClickListener<PageThumbnail>,
) : BaseListAdapter<ListModel>(), FastScroller.SectionIndexer {

	init {
		addDelegate(ListItemType.PAGE_THUMB, pageThumbnailAD(clickListener))
		addDelegate(ListItemType.HEADER, listHeaderAD(null))
	}

	override fun getSectionText(context: Context, position: Int): CharSequence? {
		return findHeader(position)?.getText(context)
	}
}
