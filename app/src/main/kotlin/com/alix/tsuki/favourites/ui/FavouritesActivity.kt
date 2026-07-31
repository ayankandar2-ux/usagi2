package com.alix.tsuki.favourites.ui

import android.os.Bundle
import com.alix.tsuki.core.nav.AppRouter
import com.alix.tsuki.core.ui.FragmentContainerActivity
import com.alix.tsuki.favourites.ui.list.FavouritesListFragment

class FavouritesActivity : FragmentContainerActivity(FavouritesListFragment::class.java) {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val categoryTitle = intent.getStringExtra(AppRouter.KEY_TITLE)
		if (categoryTitle != null) {
			title = categoryTitle
		}
	}
}
