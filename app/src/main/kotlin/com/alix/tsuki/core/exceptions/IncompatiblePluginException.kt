package com.alix.tsuki.core.exceptions

class IncompatiblePluginException(
	val name: String?,
	cause: Throwable?,
) : RuntimeException(cause)
