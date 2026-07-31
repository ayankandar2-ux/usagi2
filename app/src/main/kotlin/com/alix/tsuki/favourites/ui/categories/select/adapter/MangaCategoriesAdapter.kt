package com.alix.tsuki.favourites.ui.categories.select.adapter

import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.favourites.ui.categories.select.model.MangaCategoryItem
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.emptyStateListAD
import com.alix.tsuki.list.ui.adapter.loadingStateAD
import com.alix.tsuki.list.ui.model.ListModel

class MangaCategoriesAdapter(
	clickListener: OnListItemClickListener<MangaCategoryItem>,
) : BaseListAdapter<ListModel>() {

	init {
		addDelegate(ListItemType.NAV_ITEM, mangaCategoryAD(clickListener))
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
		addDelegate(ListItemType.STATE_EMPTY, emptyStateListAD(null))
	}
}
