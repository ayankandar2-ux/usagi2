package com.alix.tsuki.scrobbling.common.domain

import com.alix.tsuki.scrobbling.anilist.data.AniListRepository
import com.alix.tsuki.scrobbling.common.data.ScrobblerRepository
import com.alix.tsuki.scrobbling.common.domain.model.ScrobblerService
import com.alix.tsuki.scrobbling.kitsu.data.KitsuRepository
import com.alix.tsuki.scrobbling.mal.data.MALRepository
import com.alix.tsuki.scrobbling.shikimori.data.ShikimoriRepository
import javax.inject.Inject
import javax.inject.Provider

class ScrobblerRepositoryMap @Inject constructor(
	private val shikimoriRepository: Provider<ShikimoriRepository>,
	private val aniListRepository: Provider<AniListRepository>,
	private val malRepository: Provider<MALRepository>,
	private val kitsuRepository: Provider<KitsuRepository>,
) {

	operator fun get(scrobblerService: ScrobblerService): ScrobblerRepository = when (scrobblerService) {
		ScrobblerService.SHIKIMORI -> shikimoriRepository
		ScrobblerService.ANILIST -> aniListRepository
		ScrobblerService.MAL -> malRepository
		ScrobblerService.KITSU -> kitsuRepository
	}.get()
}
