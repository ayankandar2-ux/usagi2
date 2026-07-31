package com.alix.tsuki.core.parser

import com.alix.tsuki.core.cache.MemoryContentCache
import com.alix.tsuki.core.model.TestMangaSource
import tsuki.MangaLoaderContext

@Suppress("unused")
class TestMangaRepository(
	private val loaderContext: MangaLoaderContext,
	cache: MemoryContentCache
) : EmptyMangaRepository(TestMangaSource)
