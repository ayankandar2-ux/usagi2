package com.alix.tsuki.history.domain

import com.alix.tsuki.core.os.NetworkState
import com.alix.tsuki.core.prefs.AppSettings
import com.alix.tsuki.history.data.HistoryRepository
import com.alix.tsuki.list.domain.ListFilterOption
import com.alix.tsuki.list.domain.MangaListQuickFilter
import javax.inject.Inject

class HistoryListQuickFilter @Inject constructor(
	private val settings: AppSettings,
	private val repository: HistoryRepository,
	networkState: NetworkState,
) : MangaListQuickFilter(settings) {

	init {
		setFilterOption(ListFilterOption.Downloaded, !networkState.value)
	}

	override suspend fun getAvailableFilterOptions(): List<ListFilterOption> = buildList {
		add(ListFilterOption.Downloaded)
		if (settings.isTrackerEnabled) {
			add(ListFilterOption.Macro.NEW_CHAPTERS)
		}
		add(ListFilterOption.Macro.COMPLETED)
		add(ListFilterOption.Macro.FAVORITE)
		add(ListFilterOption.NOT_FAVORITE)
		if (!settings.isNsfwContentDisabled) {
			add(ListFilterOption.Macro.NSFW)
		}
		repository.getPopularTags(5).mapTo(this) {
			ListFilterOption.Tag(it)
		}
		repository.getPopularSources(4).mapTo(this) {
			ListFilterOption.Source(it)
		}
	}
}
