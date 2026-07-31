package com.alix.tsuki.favourites.domain.model

import com.alix.tsuki.core.model.MangaSource

data class Cover(
	val url: String?,
	val source: String,
) {
	val mangaSource by lazy { MangaSource(source) }
}
