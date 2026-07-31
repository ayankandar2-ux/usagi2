package com.alix.tsuki.history.ui

import android.content.Context
import com.alix.tsuki.core.ui.list.fastscroll.FastScroller
import com.alix.tsuki.list.ui.adapter.MangaListAdapter
import com.alix.tsuki.list.ui.adapter.MangaListListener
import com.alix.tsuki.list.ui.size.ItemSizeResolver

class HistoryListAdapter(
	listener: MangaListListener,
	sizeResolver: ItemSizeResolver,
) : MangaListAdapter(listener, sizeResolver), FastScroller.SectionIndexer {

	override fun getSectionText(context: Context, position: Int): CharSequence? {
		return findHeader(position)?.getText(context)
	}
}
