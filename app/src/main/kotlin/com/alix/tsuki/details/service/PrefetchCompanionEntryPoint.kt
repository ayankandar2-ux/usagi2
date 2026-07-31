package com.alix.tsuki.details.service

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.alix.tsuki.core.prefs.AppSettings

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PrefetchCompanionEntryPoint {
	val settings: AppSettings
}
