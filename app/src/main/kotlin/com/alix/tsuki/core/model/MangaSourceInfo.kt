package com.alix.tsuki.core.model

import tsuki.model.MangaSource

data class MangaSourceInfo(
	val mangaSource: MangaSource,
	val isEnabled: Boolean,
	val isPinned: Boolean,
) : MangaSource by mangaSource
