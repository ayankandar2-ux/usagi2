package com.alix.tsuki.core

import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.SearchRecentSuggestions
import android.text.Html
import androidx.collection.arraySetOf
import androidx.core.content.ContextCompat
import androidx.room.InvalidationTracker
import androidx.work.WorkManager
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.allowRgb565
import coil3.svg.SvgDecoder
import coil3.util.DebugLogger
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import com.alix.tsuki.BuildConfig
import com.alix.tsuki.backups.domain.BackupObserver
import com.alix.tsuki.core.db.MangaDatabase
import com.alix.tsuki.core.exceptions.resolve.CaptchaHandler
import com.alix.tsuki.core.image.AvifImageDecoder
import com.alix.tsuki.core.image.CbzFetcher
import com.alix.tsuki.core.image.ExternalSourceFetcher
import com.alix.tsuki.core.image.MangaSourceHeaderInterceptor
import com.alix.tsuki.core.network.MangaHttpClient
import com.alix.tsuki.core.network.imageproxy.ImageProxyInterceptor
import com.alix.tsuki.core.network.webview.WebViewExecutor
import com.alix.tsuki.core.os.AppShortcutManager
import com.alix.tsuki.core.os.NetworkState
import com.alix.tsuki.core.parser.MangaLoaderContextImpl
import com.alix.tsuki.core.parser.favicon.FaviconFetcher
import com.alix.tsuki.core.prefs.AppSettings
import com.alix.tsuki.core.ui.image.CoilImageGetter
import com.alix.tsuki.core.ui.util.ActivityRecreationHandle
import com.alix.tsuki.core.util.FileSize
import com.alix.tsuki.core.util.ext.connectivityManager
import com.alix.tsuki.core.util.ext.isLowRamDevice
import com.alix.tsuki.details.ui.pager.pages.MangaPageFetcher
import com.alix.tsuki.details.ui.pager.pages.MangaPageKeyer
import com.alix.tsuki.local.data.CacheDir
import com.alix.tsuki.local.data.FaviconCache
import com.alix.tsuki.local.data.LocalStorageCache
import com.alix.tsuki.local.data.LocalStorageChanges
import com.alix.tsuki.local.data.PageCache
import com.alix.tsuki.local.domain.model.LocalManga
import com.alix.tsuki.main.domain.CoverRestoreInterceptor
import com.alix.tsuki.main.ui.protect.AppProtectHelper
import com.alix.tsuki.main.ui.protect.ScreenshotPolicyHelper
import tsuki.MangaLoaderContext
import com.alix.tsuki.search.ui.MangaSuggestionsProvider
import com.alix.tsuki.sync.domain.SyncController
import com.alix.tsuki.widget.WidgetUpdater
import tsuki.network.UserAgents
import org.draken.tsukimix.core.parser.tachiyomi.TachiyomiExtensionLoader as Loader
import org.draken.tsukimix.core.parser.tachiyomi.TachiyomiExtensionManager as Manager
import org.draken.tsukimix.core.parser.tachiyomi.TachiyomiInjektBridge as Bridge

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

	@Binds
	@Suppress("unused")
	fun bindMangaLoaderContext(mangaLoaderContextImpl: MangaLoaderContextImpl): MangaLoaderContext

	@Binds
	@Suppress("unused")
	fun bindImageGetter(coilImageGetter: CoilImageGetter): Html.ImageGetter

	companion object {

		@Provides
		@LocalizedAppContext
		fun provideLocalizedContext(
			@ApplicationContext context: Context,
		): Context = ContextCompat.getContextForLanguage(context)

		@Provides
		@Singleton
		fun provideNetworkState(
			@ApplicationContext context: Context,
			settings: AppSettings,
		) = NetworkState(context.connectivityManager, settings)

		@Provides
		@Singleton
		fun provideMangaDatabase(
			@ApplicationContext context: Context,
		): MangaDatabase = MangaDatabase(context)

		@Provides
		@Singleton
		fun provideCoil(
			@LocalizedAppContext context: Context,
			@MangaHttpClient okHttpClientProvider: Provider<OkHttpClient>,
			faviconFetcherFactory: FaviconFetcher.Factory,
			imageProxyInterceptor: ImageProxyInterceptor,
			pageFetcherFactory: MangaPageFetcher.Factory,
			coverRestoreInterceptor: CoverRestoreInterceptor,
			networkStateProvider: Provider<NetworkState>,
			captchaHandler: CaptchaHandler,
		): ImageLoader {
			val diskCacheFactory = {
				val rootDir = context.externalCacheDir ?: context.cacheDir
				DiskCache.Builder()
					.directory(rootDir.resolve(CacheDir.THUMBS.dir))
					.build()
			}
			val okHttpClientLazy = lazy {
				okHttpClientProvider.get().newBuilder().cache(null).build()
			}
			return ImageLoader.Builder(context)
				.interceptorCoroutineContext(Dispatchers.Default)
				.diskCache(diskCacheFactory)
				.logger(if (BuildConfig.DEBUG) DebugLogger() else null)
				.allowRgb565(context.isLowRamDevice())
				.eventListener(captchaHandler)
				.components {
					add(ExternalSourceFetcher.Factory())
					add(
						OkHttpNetworkFetcherFactory(
							callFactory = okHttpClientLazy::value,
							connectivityChecker = { networkStateProvider.get() },
						),
					)
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
						add(AnimatedImageDecoder.Factory())
					} else {
						add(GifDecoder.Factory())
					}
					add(SvgDecoder.Factory())
					add(CbzFetcher.Factory())
					add(AvifImageDecoder.Factory())
					add(faviconFetcherFactory)
					add(MangaPageKeyer())
					add(pageFetcherFactory)
					add(imageProxyInterceptor)
					add(coverRestoreInterceptor)
					add(MangaSourceHeaderInterceptor())
				}.build()
		}

		@Provides
		fun provideSearchSuggestions(
			@ApplicationContext context: Context,
		): SearchRecentSuggestions = MangaSuggestionsProvider.createSuggestions(context)

		@Provides
		@ElementsIntoSet
		fun provideDatabaseObservers(
			widgetUpdater: WidgetUpdater,
			appShortcutManager: AppShortcutManager,
			backupObserver: BackupObserver,
			syncController: SyncController,
		): Set<@JvmSuppressWildcards InvalidationTracker.Observer> = arraySetOf(
			widgetUpdater,
			appShortcutManager,
			backupObserver,
			syncController,
		)

		@Provides
		@ElementsIntoSet
		fun provideActivityLifecycleCallbacks(
			appProtectHelper: AppProtectHelper,
			activityRecreationHandle: ActivityRecreationHandle,
			screenshotPolicyHelper: ScreenshotPolicyHelper,
		): Set<@JvmSuppressWildcards Application.ActivityLifecycleCallbacks> = arraySetOf(
			appProtectHelper,
			activityRecreationHandle,
			screenshotPolicyHelper,
		)

		@Provides
		@Singleton
		@LocalStorageChanges
		fun provideMutableLocalStorageChangesFlow(): MutableSharedFlow<LocalManga?> = MutableSharedFlow()

		@Provides
		@LocalStorageChanges
		fun provideLocalStorageChangesFlow(
			@LocalStorageChanges flow: MutableSharedFlow<LocalManga?>,
		): SharedFlow<LocalManga?> = flow.asSharedFlow()

		@Provides
		fun provideWorkManager(
			@ApplicationContext context: Context,
		): WorkManager = WorkManager.getInstance(context)

		@Provides
		@Singleton
		@PageCache
		fun providePageCache(
			@ApplicationContext context: Context,
		) = LocalStorageCache(
			context = context,
			dir = CacheDir.PAGES,
			defaultSize = FileSize.MEGABYTES.convert(200, FileSize.BYTES),
			minSize = FileSize.MEGABYTES.convert(20, FileSize.BYTES),
		)

		@Provides
		@Singleton
		@FaviconCache
		fun provideFaviconCache(
			@ApplicationContext context: Context,
		) = LocalStorageCache(
			context = context,
			dir = CacheDir.FAVICONS,
			defaultSize = FileSize.MEGABYTES.convert(8, FileSize.BYTES),
			minSize = FileSize.MEGABYTES.convert(2, FileSize.BYTES),
		)

		@Provides
		@Singleton
		fun provideInjektBridge(
			@ApplicationContext context: Context,
			@MangaHttpClient httpClient: OkHttpClient,
			webViewExecutor: WebViewExecutor,
		): Bridge {
			return Bridge(
				context = context,
				httpClient = httpClient,
				defaultUserAgentProvider = {
					webViewExecutor.defaultUserAgent
						?.replace(Regex("; Android .*?\\)"), "; Android 16; K)")
						?.replace(Regex("Version/.* Chrome/"), "Chrome/")
						?: UserAgents.CHROME_MOBILE
				}
			)
		}

		@Provides
		@Singleton
		fun provideExternalLoader(injektBridge: Provider<Bridge>):
			Loader { return Loader { injektBridge.get() } }

		@Provides
		@Singleton
		fun provideExternalManager(@ApplicationContext context: Context, loader: Loader):
			Manager { return Manager(context, loader) }
	}
}
