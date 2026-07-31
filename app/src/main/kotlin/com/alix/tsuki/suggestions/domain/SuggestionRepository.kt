package com.alix.tsuki.suggestions.domain

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import com.alix.tsuki.core.db.MangaDatabase
import com.alix.tsuki.core.db.entity.toEntities
import com.alix.tsuki.core.db.entity.toEntity
import com.alix.tsuki.core.db.entity.toManga
import com.alix.tsuki.core.db.entity.toMangaTagsList
import com.alix.tsuki.core.model.toMangaSources
import com.alix.tsuki.core.util.ext.mapItems
import com.alix.tsuki.list.domain.ListFilterOption
import tsuki.model.Manga
import tsuki.model.MangaSource
import tsuki.model.MangaTag
import com.alix.tsuki.suggestions.data.SuggestionEntity
import com.alix.tsuki.suggestions.data.SuggestionWithManga
import javax.inject.Inject

class SuggestionRepository @Inject constructor(
	private val db: MangaDatabase,
) {

	fun observeAll(): Flow<List<Manga>> {
		return db.getSuggestionDao().observeAll().mapItems {
			it.toManga()
		}
	}

	fun observeAll(limit: Int, filterOptions: Set<ListFilterOption>): Flow<List<Manga>> {
		return db.getSuggestionDao().observeAll(limit, filterOptions).mapItems {
			it.toManga()
		}
	}

	suspend fun getRandomList(limit: Int): List<Manga> {
		return db.getSuggestionDao().getRandom(limit).map {
			it.toManga()
		}
	}

	suspend fun clear() {
		db.getSuggestionDao().deleteAll()
	}

	suspend fun isEmpty(): Boolean {
		return db.getSuggestionDao().count() == 0
	}

	suspend fun getTopTags(limit: Int): List<MangaTag> {
		return db.getSuggestionDao().getTopTags(limit)
			.toMangaTagsList()
	}

	suspend fun getTopSources(limit: Int): List<MangaSource> {
		return db.getSuggestionDao().getTopSources(limit)
			.toMangaSources()
	}

	suspend fun replace(suggestions: Iterable<MangaSuggestion>) {
		db.withTransaction {
			db.getSuggestionDao().deleteAll()
			suggestions.forEach { (manga, relevance) ->
				val tags = manga.tags.toEntities()
				db.getTagsDao().upsert(tags)
				db.getMangaDao().upsert(manga.toEntity(), tags)
				db.getSuggestionDao().upsert(
					SuggestionEntity(
						mangaId = manga.id,
						relevance = relevance,
						createdAt = System.currentTimeMillis(),
					),
				)
			}
		}
	}

	private fun SuggestionWithManga.toManga() = manga.toManga(emptySet(), null)
}
