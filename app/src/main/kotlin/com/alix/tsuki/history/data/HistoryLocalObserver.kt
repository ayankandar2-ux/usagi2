package com.alix.tsuki.history.data

import dagger.Reusable
import com.alix.tsuki.core.db.MangaDatabase
import com.alix.tsuki.core.db.entity.toManga
import com.alix.tsuki.core.db.entity.toMangaTags
import com.alix.tsuki.history.domain.model.MangaWithHistory
import com.alix.tsuki.list.domain.ListFilterOption
import com.alix.tsuki.list.domain.ListSortOrder
import com.alix.tsuki.local.data.index.LocalMangaIndex
import com.alix.tsuki.local.domain.LocalObserveMapper
import tsuki.model.Manga
import javax.inject.Inject

@Reusable
class HistoryLocalObserver @Inject constructor(
	localMangaIndex: LocalMangaIndex,
	private val db: MangaDatabase,
) : LocalObserveMapper<HistoryWithManga, MangaWithHistory>(localMangaIndex) {

	fun observeAll(
		order: ListSortOrder,
		filterOptions: Set<ListFilterOption>,
		limit: Int
	) = db.getHistoryDao().observeAll(order, filterOptions, limit).mapToLocal()

	override fun toManga(e: HistoryWithManga) = e.manga.toManga(e.tags.toMangaTags(), null)

	override fun toResult(e: HistoryWithManga, manga: Manga) = MangaWithHistory(
		manga = manga,
		history = e.history.toMangaHistory(),
	)
}
