package com.alix.tsuki.picker.ui.manga

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import com.alix.tsuki.R
import com.alix.tsuki.core.parser.MangaDataRepository
import com.alix.tsuki.core.prefs.AppSettings
import com.alix.tsuki.favourites.domain.FavouritesRepository
import com.alix.tsuki.history.data.HistoryRepository
import com.alix.tsuki.list.domain.MangaListMapper
import com.alix.tsuki.list.ui.MangaListViewModel
import com.alix.tsuki.list.ui.model.ListHeader
import com.alix.tsuki.list.ui.model.ListModel
import com.alix.tsuki.list.ui.model.LoadingState
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow
import com.alix.tsuki.local.data.LocalStorageChanges
import com.alix.tsuki.local.domain.model.LocalManga

@HiltViewModel
class MangaPickerViewModel @Inject constructor(
	private val settings: AppSettings,
	mangaDataRepository: MangaDataRepository,
	private val historyRepository: HistoryRepository,
	private val favouritesRepository: FavouritesRepository,
	private val mangaListMapper: MangaListMapper,
	@LocalStorageChanges localStorageChanges: SharedFlow<LocalManga?>,
) : MangaListViewModel(settings, mangaDataRepository, localStorageChanges) {

	override val content: StateFlow<List<ListModel>>
		get() = flow {
			emit(loadList())
		}.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Lazily, listOf(LoadingState))

	override fun onRefresh() = Unit

	override fun onRetry() = Unit

	private suspend fun loadList() = buildList {
		val history = historyRepository.getList(0, Int.MAX_VALUE)
		if (history.isNotEmpty()) {
			add(ListHeader(R.string.history))
			mangaListMapper.toListModelList(this, history, settings.listMode)
		}
		val categories = favouritesRepository.observeCategoriesForLibrary().first()
		for (category in categories) {
			val favorites = favouritesRepository.getManga(category.id)
			if (favorites.isNotEmpty()) {
				add(ListHeader(category.title))
				mangaListMapper.toListModelList(this, favorites, settings.listMode)
			}
		}
	}
}
