package com.alix.tsuki.download.ui.list

import androidx.lifecycle.LifecycleOwner
import com.alix.tsuki.core.ui.BaseListAdapter
import com.alix.tsuki.list.ui.adapter.ListItemType
import com.alix.tsuki.list.ui.adapter.emptyStateListAD
import com.alix.tsuki.list.ui.adapter.listHeaderAD
import com.alix.tsuki.list.ui.adapter.loadingStateAD
import com.alix.tsuki.list.ui.model.ListModel

class DownloadsAdapter(
	lifecycleOwner: LifecycleOwner,
	listener: DownloadItemListener,
) : BaseListAdapter<ListModel>() {

	init {
		addDelegate(ListItemType.DOWNLOAD, downloadItemAD(lifecycleOwner, listener))
		addDelegate(ListItemType.STATE_LOADING, loadingStateAD())
		addDelegate(ListItemType.STATE_EMPTY, emptyStateListAD(null))
		addDelegate(ListItemType.HEADER, listHeaderAD(null))
	}
}
