package com.alix.tsuki.core.exceptions

import com.alix.tsuki.core.model.UnknownMangaSource
import tsuki.model.MangaSource
import tsuki.network.CloudFlareHelper

class CloudFlareBlockedException(
	override val url: String,
	source: MangaSource?,
) : CloudFlareException("Blocked by CloudFlare", CloudFlareHelper.PROTECTION_BLOCKED) {

	override val source: MangaSource = source ?: UnknownMangaSource
}
