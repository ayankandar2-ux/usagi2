package com.alix.tsuki.tracker.domain

import com.alix.tsuki.core.prefs.AppSettings
import com.alix.tsuki.favourites.domain.FavouritesRepository
import com.alix.tsuki.list.domain.ListFilterOption
import com.alix.tsuki.list.domain.MangaListQuickFilter
import javax.inject.Inject

class UpdatesListQuickFilter @Inject constructor(
	private val favouritesRepository: FavouritesRepository,
	settings: AppSettings,
) : MangaListQuickFilter(settings) {

	override suspend fun getAvailableFilterOptions(): List<ListFilterOption> =
		favouritesRepository.getMostUpdatedCategories(
			limit = 4,
		).map {
			ListFilterOption.Favorite(it)
		}
}
