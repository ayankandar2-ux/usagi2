package com.alix.tsuki.core.parser

import android.content.Context
import androidx.annotation.AnyThread
import androidx.collection.ArrayMap
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import com.alix.tsuki.core.cache.MemoryContentCache
import com.alix.tsuki.core.model.LocalMangaSource
import com.alix.tsuki.core.model.MangaSourceInfo
import com.alix.tsuki.core.model.TestMangaSource
import com.alix.tsuki.core.model.UnknownMangaSource
import com.alix.tsuki.core.network.CommonHeaders
import com.alix.tsuki.core.network.imageproxy.ImageProxyInterceptor as Interceptor
import com.alix.tsuki.core.parser.external.ExternalMangaSource
import com.alix.tsuki.core.parser.external.ExternalMangaRepository
import com.alix.tsuki.core.parser.tachiyomi.ExternalMangaRepository as ExternalRepository
import org.draken.tsukimix.core.parser.tachiyomi.model.TachiyomiMangaSource as ExternalSource
import com.alix.tsuki.local.data.LocalMangaRepository
import tsuki.MangaLoaderContext
import tsuki.model.Manga
import tsuki.model.MangaChapter
import tsuki.model.MangaListFilter
import tsuki.model.MangaListFilterCapabilities
import tsuki.model.MangaListFilterOptions
import tsuki.model.MangaPage
import tsuki.model.MangaSource
import tsuki.model.SortOrder
import com.alix.tsuki.core.model.MangaSourceRegistry
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

interface MangaRepository {

	val source: MangaSource

	val sortOrders: Set<SortOrder>

	var defaultSortOrder: SortOrder

	val filterCapabilities: MangaListFilterCapabilities

	suspend fun getList(offset: Int, order: SortOrder?, filter: MangaListFilter?): List<Manga>

	suspend fun getDetails(manga: Manga): Manga

	suspend fun getPages(chapter: MangaChapter): List<MangaPage>

	suspend fun getPageUrl(page: MangaPage): String

	suspend fun getPageRequest(page: MangaPage): Request {
		return createPageRequest(getPageUrl(page), page.source)
	}

	suspend fun getPageResponse(page: MangaPage, okHttp: OkHttpClient, interceptor: Interceptor): Response {
		return interceptor.interceptPageRequest(getPageRequest(page), okHttp)
	}

	suspend fun getFilterOptions(): MangaListFilterOptions

	suspend fun getExternalFilters(): Any? = null

	suspend fun getRelated(seed: Manga): List<Manga>

	suspend fun find(manga: Manga): Manga? {
		val list = getList(0, SortOrder.RELEVANCE, MangaListFilter(query = manga.title))
		return list.find { x -> x.id == manga.id }
	}

	@Singleton
	class Factory @Inject constructor(
		@ApplicationContext private val context: Context,
		private val localMangaRepository: LocalMangaRepository,
		private val loaderContext: MangaLoaderContext,
		private val contentCache: MemoryContentCache,
		private val mirrorSwitcher: MirrorSwitcher,
	) {

		private val cache = ArrayMap<MangaSource, WeakReference<MangaRepository>>()
		private var cacheVersion = -1

		@AnyThread
		fun create(source: MangaSource): MangaRepository {
			val currentVersion = MangaSourceRegistry.version
			if (cacheVersion != currentVersion) {
				synchronized(cache) {
					if (cacheVersion != currentVersion) {
						cache.clear()
						cacheVersion = currentVersion
					}
				}
			}

			when (source) {
				is MangaSourceInfo -> return create(source.mangaSource)
				LocalMangaSource -> return localMangaRepository
				UnknownMangaSource -> return EmptyMangaRepository(source)
			}
			cache[source]?.get()?.let { return it }
			return synchronized(cache) {
				cache[source]?.get()?.let { return it }
				val repository = createRepository(source)
				if (repository != null) {
					cache[source] = WeakReference(repository)
					repository
				} else {
					EmptyMangaRepository(source)
				}
			}
		}

		private fun createRepository(source: MangaSource): MangaRepository? = when (source) {
			TestMangaSource -> TestMangaRepository(
				loaderContext = loaderContext,
				cache = contentCache,
			)

			is ExternalMangaSource -> if (source.isAvailable(context)) {
				ExternalMangaRepository(
					contentResolver = context.contentResolver,
					source = source,
					cache = contentCache,
				)
			} else {
				EmptyMangaRepository(source)
			}

			is ExternalSource -> try {
				ExternalRepository(
					context = context,
					source = source,
					cache = contentCache,
				)
			} catch (_: Throwable) {
				EmptyMangaRepository(source)
			}

			else -> try {
				MangaParserRepository(
					compoundSource = source,
					parser = loaderContext.newParserInstance(source),
					cache = contentCache,
					mirrorSwitcher = mirrorSwitcher,
				)
			} catch (_: Throwable) {
				EmptyMangaRepository(source)
			}
		}
	}

	companion object {

		fun createPageRequest(pageUrl: String, mangaSource: MangaSource) = Request.Builder()
			.url(pageUrl)
			.get()
			.header(CommonHeaders.ACCEPT, "image/webp,image/png;q=0.9,image/jpeg,*/*;q=0.8")
			.cacheControl(CommonHeaders.CACHE_CONTROL_NO_STORE)
			.tag(MangaSource::class.java, mangaSource)
			.build()
	}
}
