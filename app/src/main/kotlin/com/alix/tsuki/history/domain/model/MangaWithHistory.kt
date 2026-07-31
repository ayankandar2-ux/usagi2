package com.alix.tsuki.history.domain.model

import com.alix.tsuki.core.model.MangaHistory
import tsuki.model.Manga

data class MangaWithHistory(
	val manga: Manga,
	val history: MangaHistory
)
