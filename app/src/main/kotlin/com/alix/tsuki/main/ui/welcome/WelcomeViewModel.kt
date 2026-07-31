package com.alix.tsuki.main.ui.welcome

import dagger.hilt.android.lifecycle.HiltViewModel
import com.alix.tsuki.core.ui.BaseViewModel
import com.alix.tsuki.explore.data.MangaSourcesRepository
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
	private val repository: MangaSourcesRepository,
) : BaseViewModel() {

	init {
		// Mark sources badge as seen so the "new sources" badge
		// doesn't appear right after the welcome screen is dismissed
		launchJob {
			repository.clearNewSourcesBadge()
		}
	}
}
