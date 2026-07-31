package com.alix.tsuki.scrobbling.common.ui.selector.adapter

import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.ListStateHolderListener
import com.alix.tsuki.list.ui.adapter.loadingFooterAD
import com.alix.tsuki.list.ui.adapter.loadingStateAD
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.scrobbling.common.domain.model.ScrobblerManga

class ScrobblerSelectorAdapter(
	clickListener: OnListItemClickListener<ScrobblerManga>,
	stateHolderListener: ListStateHolderListener,
) : BaseListAdapter<ListModel>() {

	init {
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
		addDelegate(ListItemType.MANGA_SCROBBLING, scrobblingMangaAD(clickListener))
		addDelegate(ListItemType.FOOTER_LOADING, loadingFooterAD())
		addDelegate(ListItemType.HINT_EMPTY, scrobblerHintAD(stateHolderListener))
	}
}
