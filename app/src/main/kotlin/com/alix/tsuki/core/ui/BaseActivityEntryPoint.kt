package com.alix.tsuki.core.ui

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.alix.tsuki.core.exceptions.resolve.ExceptionResolver
import com.alix.tsuki.core.prefs.AppSettings

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BaseActivityEntryPoint {

	val settings: AppSettings

	val exceptionResolverFactory: ExceptionResolver.Factory
}
