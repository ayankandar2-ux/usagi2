package com.alix.tsuki.core.exceptions

class SyncApiException(
	message: String,
	val code: Int,
) : RuntimeException(message)
