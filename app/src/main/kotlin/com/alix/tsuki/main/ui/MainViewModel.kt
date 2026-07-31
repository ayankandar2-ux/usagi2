package com.alix.tsuki.main.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import com.alix.tsuki.core.exceptions.EmptyHistoryException
import com.alix.tsuki.core.github.AppUpdateRepository
import com.alix.tsuki.core.prefs.AppSettings
import com.alix.tsuki.core.prefs.observeAsFlow
import com.alix.tsuki.core.prefs.observeAsStateFlow
import com.alix.tsuki.core.ui.BaseViewModel
import com.alix.tsuki.core.util.ext.MutableEventFlow
import com.alix.tsuki.core.util.ext.call
import com.alix.tsuki.explore.data.MangaSourcesRepository
import com.alix.tsuki.history.data.HistoryRepository
import com.alix.tsuki.main.domain.ReadingResumeEnabledUseCase
import tsuki.model.Manga
import com.alix.tsuki.tracker.domain.TrackingRepository
import com.alix.tsuki.settings.sources.manage.plugins.UpdatePluginsProvider
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val historyRepository: HistoryRepository,
	private val appUpdateRepository: AppUpdateRepository,
	trackingRepository: TrackingRepository,
	private val settings: AppSettings,
	private val sourcesRepository: MangaSourcesRepository,
	private val updatePluginsProvider: UpdatePluginsProvider,
	readingResumeEnabledUseCase: ReadingResumeEnabledUseCase,
) : BaseViewModel() {

	var isUpdateDialogShown = false // alway shows at startup

	val onOpenReader = MutableEventFlow<Manga>()
	val onFirstStart = MutableEventFlow<Unit>()

	val isResumeEnabled = readingResumeEnabledUseCase()
		.withErrorHandling()
		.stateIn(
			scope = viewModelScope + Dispatchers.Default,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = false,
		)

	val appUpdate = appUpdateRepository.observeAvailableUpdate()

	val feedCounter = trackingRepository.observeUnreadUpdatesCount()
		.withErrorHandling()
		.stateIn(viewModelScope + Dispatchers.Default, SharingStarted.Lazily, 0)

	val isBottomNavPinned = settings.observeAsFlow(
		AppSettings.KEY_NAV_PINNED,
	) {
		isNavBarPinned
	}.flowOn(Dispatchers.Default)

	val isFloatingNav = settings.observeAsFlow(
		AppSettings.KEY_NAV_FLOATING,
	) {
		isFloatingNav
	}.flowOn(Dispatchers.Default)

	val isIncognitoModeEnabled = settings.observeAsStateFlow(
		scope = viewModelScope + Dispatchers.Default,
		key = AppSettings.KEY_INCOGNITO_MODE,
		valueProducer = { isIncognitoModeEnabled },
	)

	init {
		launchJob {
			if (settings.isCheckAppUpdateEnabled) {
				appUpdateRepository.fetchUpdate()
			}
		}
		launchJob {
			if (settings.isFirstLaunch) {
				settings.isFirstLaunch = false
				settings.isCheckAppUpdateEnabled = false
				onFirstStart.call(Unit)
			}
		}
	}

	fun openLastReader() {
		launchLoadingJob(Dispatchers.Default) {
			val manga = historyRepository.getLastOrNull() ?: throw EmptyHistoryException()
			onOpenReader.call(manga)
		}
	}

	fun setIncognitoMode(isEnabled: Boolean) {
		settings.isIncognitoModeEnabled = isEnabled
	}

	fun runAutoUpdate() {
		if (settings.isAutoPluginsEnabled) {
			launchJob(Dispatchers.Default) {
				updatePluginsProvider.runAutoUpdate(settings)
			}
		}
	}
}
