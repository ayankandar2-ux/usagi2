package com.alix.tsuki.settings.tracker.categories

import com.alix.tsuki.core.model.FavouriteCategory
import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.core.ui.list.OnListItemClickListener

class TrackerCategoriesConfigAdapter(
	listener: OnListItemClickListener<FavouriteCategory>,
) : BaseListAdapter<FavouriteCategory>() {

	init {
		delegatesManager.addDelegate(trackerCategoryAD(listener))
	}
}
