package com.alix.tsuki.details.ui.scrobbling

import com.alix.tsuki.core.nav.AppRouter
import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.list.ui.model.ListModel

class ScrollingInfoAdapter(
	router: AppRouter,
) : BaseListAdapter<ListModel>() {

	init {
		delegatesManager.addDelegate(scrobblingInfoAD(router))
	}
}
