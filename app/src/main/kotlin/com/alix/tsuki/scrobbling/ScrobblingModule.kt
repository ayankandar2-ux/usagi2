package com.alix.tsuki.scrobbling

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import okhttp3.OkHttpClient
import com.alix.tsuki.BuildConfig
import com.alix.tsuki.core.db.MangaDatabase
import com.alix.tsuki.core.network.BaseHttpClient
import com.alix.tsuki.core.network.CurlLoggingInterceptor
import com.alix.tsuki.scrobbling.anilist.data.AniListAuthenticator
import com.alix.tsuki.scrobbling.anilist.data.AniListInterceptor
import com.alix.tsuki.scrobbling.anilist.domain.AniListScrobbler
import com.alix.tsuki.scrobbling.common.data.ScrobblerStorage
import com.alix.tsuki.scrobbling.common.domain.Scrobbler
import com.alix.tsuki.scrobbling.common.domain.model.ScrobblerService
import com.alix.tsuki.scrobbling.common.domain.model.ScrobblerType
import com.alix.tsuki.scrobbling.kitsu.data.KitsuAuthenticator
import com.alix.tsuki.scrobbling.kitsu.data.KitsuInterceptor
import com.alix.tsuki.scrobbling.kitsu.data.KitsuRepository
import com.alix.tsuki.scrobbling.kitsu.domain.KitsuScrobbler
import com.alix.tsuki.scrobbling.mal.data.MALAuthenticator
import com.alix.tsuki.scrobbling.mal.data.MALInterceptor
import com.alix.tsuki.scrobbling.mal.domain.MALScrobbler
import com.alix.tsuki.scrobbling.shikimori.data.ShikimoriAuthenticator
import com.alix.tsuki.scrobbling.shikimori.data.ShikimoriInterceptor
import com.alix.tsuki.scrobbling.shikimori.domain.ShikimoriScrobbler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScrobblingModule {

	@Provides
	@Singleton
	@ScrobblerType(ScrobblerService.SHIKIMORI)
	fun provideShikimoriHttpClient(
		@BaseHttpClient baseHttpClient: OkHttpClient,
		authenticator: ShikimoriAuthenticator,
		@ScrobblerType(ScrobblerService.SHIKIMORI) storage: ScrobblerStorage,
	): OkHttpClient = baseHttpClient.newBuilder().apply {
		authenticator(authenticator)
		addInterceptor(ShikimoriInterceptor(storage))
	}.build()

	@Provides
	@Singleton
	@ScrobblerType(ScrobblerService.MAL)
	fun provideMALHttpClient(
		@BaseHttpClient baseHttpClient: OkHttpClient,
		authenticator: MALAuthenticator,
		@ScrobblerType(ScrobblerService.MAL) storage: ScrobblerStorage,
	): OkHttpClient = baseHttpClient.newBuilder().apply {
		authenticator(authenticator)
		addInterceptor(MALInterceptor(storage))
	}.build()

	@Provides
	@Singleton
	@ScrobblerType(ScrobblerService.ANILIST)
	fun provideAniListHttpClient(
		@BaseHttpClient baseHttpClient: OkHttpClient,
		authenticator: AniListAuthenticator,
		@ScrobblerType(ScrobblerService.ANILIST) storage: ScrobblerStorage,
	): OkHttpClient = baseHttpClient.newBuilder().apply {
		authenticator(authenticator)
		addInterceptor(AniListInterceptor(storage))
	}.build()

	@Provides
	@Singleton
	fun provideKitsuRepository(
		@ApplicationContext context: Context,
		@ScrobblerType(ScrobblerService.KITSU) storage: ScrobblerStorage,
		database: MangaDatabase,
		authenticator: KitsuAuthenticator,
		@BaseHttpClient baseHttpClient: OkHttpClient,
	): KitsuRepository {
		val okHttp = baseHttpClient.newBuilder().apply {
			authenticator(authenticator)
			addInterceptor(KitsuInterceptor(storage))
			if (BuildConfig.DEBUG) {
				addInterceptor(CurlLoggingInterceptor())
			}
		}.build()
		return KitsuRepository(context, okHttp, storage, database)
	}

	@Provides
	@Singleton
	@ScrobblerType(ScrobblerService.ANILIST)
	fun provideAniListStorage(
		@ApplicationContext context: Context,
	): ScrobblerStorage = ScrobblerStorage(context, ScrobblerService.ANILIST)

	@Provides
	@Singleton
	@ScrobblerType(ScrobblerService.SHIKIMORI)
	fun provideShikimoriStorage(
		@ApplicationContext context: Context,
	): ScrobblerStorage = ScrobblerStorage(context, ScrobblerService.SHIKIMORI)

	@Provides
	@Singleton
	@ScrobblerType(ScrobblerService.MAL)
	fun provideMALStorage(
		@ApplicationContext context: Context,
	): ScrobblerStorage = ScrobblerStorage(context, ScrobblerService.MAL)

	@Provides
	@Singleton
	@ScrobblerType(ScrobblerService.KITSU)
	fun provideKitsuStorage(
		@ApplicationContext context: Context,
	): ScrobblerStorage = ScrobblerStorage(context, ScrobblerService.KITSU)

	@Provides
	@ElementsIntoSet
	fun provideScrobblers(
		shikimoriScrobbler: ShikimoriScrobbler,
		aniListScrobbler: AniListScrobbler,
		malScrobbler: MALScrobbler,
		kitsuScrobbler: KitsuScrobbler
	): Set<@JvmSuppressWildcards Scrobbler> = setOf(shikimoriScrobbler, aniListScrobbler, malScrobbler, kitsuScrobbler)
}
