package com.alix.tsuki.reader.domain

import android.graphics.BitmapFactory
import android.util.Size
import androidx.core.net.toFile
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import okhttp3.OkHttpClient
import com.alix.tsuki.core.network.MangaHttpClient
import com.alix.tsuki.core.network.imageproxy.ImageProxyInterceptor as Interceptor
import com.alix.tsuki.core.parser.MangaDataRepository
import com.alix.tsuki.core.parser.MangaRepository
import com.alix.tsuki.core.prefs.AppSettings
import com.alix.tsuki.core.prefs.ReaderMode
import com.alix.tsuki.core.util.ext.isFileUri
import com.alix.tsuki.core.util.ext.isZipUri
import com.alix.tsuki.core.util.ext.printStackTraceDebug
import tsuki.model.Manga
import tsuki.model.MangaPage
import tsuki.util.runCatchingCancellable
import com.alix.tsuki.reader.ui.ReaderState
import java.io.InputStream
import java.util.zip.ZipFile
import javax.inject.Inject
import kotlin.math.roundToInt

class DetectReaderModeUseCase @Inject constructor(
	private val dataRepository: MangaDataRepository,
	private val settings: AppSettings,
	private val mangaRepositoryFactory: MangaRepository.Factory,
	@MangaHttpClient private val okHttpClient: OkHttpClient,
	private val interceptor: Interceptor,
) {

	suspend operator fun invoke(manga: Manga, state: ReaderState?): ReaderMode {
		dataRepository.getReaderMode(manga.id)?.let { return it }
		val defaultMode = settings.defaultReaderMode
		if (!settings.isReaderModeDetectionEnabled || defaultMode == ReaderMode.WEBTOON) {
			return defaultMode
		}
		val chapter = state?.let { manga.findChapterById(it.chapterId) }
			?: manga.chapters?.firstOrNull()
			?: error("There are no chapters in this manga")
		val repo = mangaRepositoryFactory.create(manga.source)
		val pages = repo.getPages(chapter)
		return runCatchingCancellable {
			val isWebtoon = guessMangaIsWebtoon(repo, pages)
			if (isWebtoon) ReaderMode.WEBTOON else defaultMode
		}.onSuccess {
			dataRepository.saveReaderMode(manga, it)
		}.onFailure {
			it.printStackTraceDebug()
		}.getOrDefault(defaultMode)
	}

	/**
	 * Automatic determine type of manga by page size
	 * @return ReaderMode.WEBTOON if page is wide
	 */
	private suspend fun guessMangaIsWebtoon(repository: MangaRepository, pages: List<MangaPage>): Boolean {
		val pageIndex = (pages.size * 0.3).roundToInt()
		val page = requireNotNull(pages.getOrNull(pageIndex)) { "No pages" }
		val url = repository.getPageUrl(page)
		val uri = url.toUri()

		val size = when {
			uri.isZipUri() -> runInterruptible(Dispatchers.IO) {
				ZipFile(uri.schemeSpecificPart).use { zip ->
					val entry = zip.getEntry(uri.fragment)
					zip.getInputStream(entry).use {
						getBitmapSize(it)
					}
				}
			}

			uri.isFileUri() -> runInterruptible(Dispatchers.IO) {
				uri.toFile().inputStream().use {
					getBitmapSize(it)
				}
			}

			else -> {
				repository.getPageResponse(page, okHttpClient, interceptor).use {
					runInterruptible(Dispatchers.IO) {
						getBitmapSize(it.body.byteStream())
					}
				}
			}
		}
		return size.width * MIN_WEBTOON_RATIO < size.height
	}

	companion object {

		private const val MIN_WEBTOON_RATIO = 1.8

		private fun getBitmapSize(input: InputStream?): Size {
			val options = BitmapFactory.Options().apply {
				inJustDecodeBounds = true
			}
			BitmapFactory.decodeStream(input, null, options)?.recycle()
			val imageHeight: Int = options.outHeight
			val imageWidth: Int = options.outWidth
			check(imageHeight > 0 && imageWidth > 0)
			return Size(imageWidth, imageHeight)
		}
	}
}
