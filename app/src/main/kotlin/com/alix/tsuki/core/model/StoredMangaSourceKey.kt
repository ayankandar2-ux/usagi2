package com.alix.tsuki.core.model

import tsuki.model.MangaSource

fun mangaSourceFromStoredKey(key: String?): MangaSource = MangaSource(key)
