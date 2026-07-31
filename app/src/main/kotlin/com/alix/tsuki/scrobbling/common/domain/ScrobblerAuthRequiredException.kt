package com.alix.tsuki.scrobbling.common.domain

import okio.IOException
import com.alix.tsuki.scrobbling.common.domain.model.ScrobblerService

class ScrobblerAuthRequiredException(
	val scrobbler: ScrobblerService,
) : IOException()
