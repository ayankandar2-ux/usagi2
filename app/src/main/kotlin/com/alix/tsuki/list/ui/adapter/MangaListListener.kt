package com.alix.tsuki.list.ui.adapter

import android.view.View
import com.alix.tsuki.core.ui.widgets.TipView

interface MangaListListener : MangaDetailsClickListener, ListStateHolderListener, ListHeaderClickListener,
	TipView.OnButtonClickListener, QuickFilterClickListener {

	fun onFilterClick(view: View?)
}
