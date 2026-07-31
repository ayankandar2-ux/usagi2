package com.alix.tsuki.explore.ui.adapter

import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.explore.ui.model.MangaSourceItem
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.emptyHintAD
import com.alix.tsuki.list.ui.adapter.listHeaderAD
import com.alix.tsuki.list.ui.adapter.loadingStateAD
import com.alix.tsuki.list.ui.model.ListModel
import tsuki.model.Manga

class ExploreAdapter(
	listener: ExploreListEventListener,
	clickListener: OnListItemClickListener<MangaSourceItem>,
	mangaClickListener: OnListItemClickListener<Manga>,
) : BaseListAdapter<ListModel>() {

	init {
		addDelegate(ListItemType.EXPLORE_BUTTONS, exploreButtonsAD(listener))
		addDelegate(
			ListItemType.EXPLORE_SUGGESTION,
			exploreRecommendationItemAD(mangaClickListener),
		)
		addDelegate(ListItemType.HEADER, listHeaderAD(listener))
		addDelegate(ListItemType.EXPLORE_SOURCE_LIST, exploreSourceListItemAD(clickListener))
		addDelegate(ListItemType.EXPLORE_SOURCE_GRID, exploreSourceGridItemAD(clickListener))
		addDelegate(ListItemType.HINT_EMPTY, emptyHintAD(listener))
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
	}
}
