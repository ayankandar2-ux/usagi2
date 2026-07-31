package com.alix.tsuki.details.ui.related

import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.alix.tsuki.R
import com.alix.tsuki.core.ui.list.ListSelectionController
import com.alix.tsuki.list.ui.MangaListFragment

@AndroidEntryPoint
class RelatedListFragment : MangaListFragment() {

	override val viewModel by viewModels<RelatedListViewModel>()
	override val isSwipeRefreshEnabled = false

	override fun onScrolledToEnd() = Unit

	override fun onCreateActionMode(
		controller: ListSelectionController,
		menuInflater: MenuInflater,
		menu: Menu
	): Boolean {
		menuInflater.inflate(R.menu.mode_remote, menu)
		return super.onCreateActionMode(controller, menuInflater, menu)
	}
}

