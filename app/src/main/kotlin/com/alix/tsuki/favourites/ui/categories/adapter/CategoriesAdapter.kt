package com.alix.tsuki.favourites.ui.categories.adapter

import com.alix.tsuki.core.ui.ReorderableListAdapter
import com.alix.tsuki.favourites.ui.categories.FavouriteCategoriesListListener
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.ListStateHolderListener
import com.alix.tsuki.list.ui.adapter.emptyStateListAD
import com.alix.tsuki.list.ui.adapter.loadingStateAD
import com.alix.tsuki.list.ui.model.ListModel

class CategoriesAdapter(
	onItemClickListener: FavouriteCategoriesListListener,
	listListener: ListStateHolderListener,
) : ReorderableListAdapter<ListModel>() {

	init {
		addDelegate(ListItemType.CATEGORY_LARGE, categoryAD(onItemClickListener))
		addDelegate(ListItemType.NAV_ITEM, allCategoriesAD(onItemClickListener))
		addDelegate(ListItemType.STATE_EMPTY, emptyStateListAD(listListener))
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
	}
}
