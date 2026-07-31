package com.alix.tsuki.core.nav

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.alix.tsuki.core.prefs.AppSettings

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppRouterEntryPoint {

	val settings: AppSettings
}
