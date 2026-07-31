package com.alix.tsuki.tracker.domain

import com.alix.tsuki.tracker.domain.model.MangaTracking
import javax.inject.Inject

class GetTracksUseCase @Inject constructor(
	private val repository: TrackingRepository,
) {

	suspend operator fun invoke(limit: Int): List<MangaTracking> {
		repository.updateTracks()
		return repository.getTracks(offset = 0, limit = limit)
	}
}
