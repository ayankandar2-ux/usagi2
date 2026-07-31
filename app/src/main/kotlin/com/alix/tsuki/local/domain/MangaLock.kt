package com.alix.tsuki.local.domain

import com.alix.tsuki.core.util.MultiMutex
import tsuki.model.Manga
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaLock @Inject constructor() : MultiMutex<Manga>()
