package com.alix.tsuki.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import com.alix.tsuki.core.ui.BaseViewModel
import com.alix.tsuki.core.model.isExternalSource
import com.alix.tsuki.explore.data.MangaSourcesRepository
import javax.inject.Inject

@HiltViewModel
class RootSettingsViewModel @Inject constructor(
	private val sourcesRepository: MangaSourcesRepository,
) : BaseViewModel() {

	val totalSourcesCount: Int
		get() = sourcesRepository.allMangaSources.count { !it.isExternalSource() }

	val enabledSourcesCount = sourcesRepository.observeEnabledSourcesCount()
		.withErrorHandling()
		.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Eagerly, -1)
}
