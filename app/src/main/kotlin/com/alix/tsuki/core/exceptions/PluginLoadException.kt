package com.alix.tsuki.core.exceptions

data class PluginLoadException(
	val name: String,
	val e: Throwable,
)
