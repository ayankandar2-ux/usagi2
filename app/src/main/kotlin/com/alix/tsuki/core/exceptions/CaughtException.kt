package com.alix.tsuki.core.exceptions

class CaughtException(
	override val cause: Throwable
) : RuntimeException("${cause.javaClass.simpleName}(${cause.message})", cause)
