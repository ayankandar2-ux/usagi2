package com.alix.tsuki.scrobbling.common.ui.config.adapter

import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.emptyStateListAD
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.scrobbling.common.domain.model.ScrobblingInfo

class ScrobblingMangaAdapter(
	clickListener: OnListItemClickListener<ScrobblingInfo>,
) : BaseListAdapter<ListModel>() {

	init {
		addDelegate(ListItemType.HEADER, scrobblingHeaderAD())
		addDelegate(ListItemType.STATE_EMPTY, emptyStateListAD(null))
		addDelegate(ListItemType.MANGA_SCROBBLING, scrobblingMangaAD(clickListener))
	}
}
