package com.alix.tsuki.core.ui.model

import tsuki.model.ContentRating

data class MangaOverride(
	val coverUrl: String?,
	val title: String?,
	val contentRating: ContentRating?,
)
