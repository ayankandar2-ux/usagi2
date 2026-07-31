package com.alix.tsuki.core.image

import coil3.intercept.Interceptor
import coil3.network.httpHeaders
import coil3.request.ImageResult
import com.alix.tsuki.core.model.unwrap
import com.alix.tsuki.core.network.CommonHeaders
import com.alix.tsuki.core.util.ext.mangaSourceKey

class MangaSourceHeaderInterceptor : Interceptor {

	override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
		val mangaSource = chain.request.extras[mangaSourceKey]?.unwrap() ?: return chain.proceed()
		val request = chain.request
		val newHeaders = request.httpHeaders.newBuilder()
			.set(CommonHeaders.MANGA_SOURCE, mangaSource.name)
			.build()
		val newRequest = request.newBuilder()
			.httpHeaders(newHeaders)
			.build()
		return chain.withRequest(newRequest).proceed()
	}
}
