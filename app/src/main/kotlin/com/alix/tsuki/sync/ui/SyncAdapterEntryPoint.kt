package com.alix.tsuki.sync.ui

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.alix.tsuki.sync.domain.SyncHelper

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SyncAdapterEntryPoint {
	val syncHelperFactory: SyncHelper.Factory
}
