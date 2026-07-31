package com.alix.tsuki.details.domain

import com.alix.tsuki.core.parser.MangaRepository
import com.alix.tsuki.core.util.ext.printStackTraceDebug
import tsuki.model.Manga
import tsuki.util.runCatchingCancellable
import javax.inject.Inject

class RelatedMangaUseCase @Inject constructor(
	private val mangaRepositoryFactory: MangaRepository.Factory,
) {

	suspend operator fun invoke(seed: Manga) = runCatchingCancellable {
		mangaRepositoryFactory.create(seed.source).getRelated(seed)
	}.onFailure {
		it.printStackTraceDebug()
	}.getOrNull()
}
