package com.alix.tsuki.core.exceptions

import tsuki.model.Manga

class UnsupportedSourceException(
	message: String?,
	val manga: Manga?,
) : IllegalArgumentException(message)
