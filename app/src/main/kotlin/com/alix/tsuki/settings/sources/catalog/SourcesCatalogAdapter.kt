package com.alix.tsuki.settings.sources.catalog

import android.content.Context
import com.alix.tsuki.core.model.getTitle
import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.ui.list.fastscroll.FastScroller
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.loadingStateAD
import com.alix.tsuki.list.ui.model.ListModel

class SourcesCatalogAdapter(
	listener: OnListItemClickListener<SourceCatalogItem.Source>,
) : BaseListAdapter<ListModel>(), FastScroller.SectionIndexer {

	init {
		addDelegate(ListItemType.CHAPTER_LIST, sourceCatalogItemSourceAD(listener))
		addDelegate(ListItemType.HINT_EMPTY, sourceCatalogItemHintAD())
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
	}

	override fun getSectionText(context: Context, position: Int): CharSequence? {
		return (items.getOrNull(position) as? SourceCatalogItem.Source)?.source?.getTitle(context)?.take(1)
	}
}
