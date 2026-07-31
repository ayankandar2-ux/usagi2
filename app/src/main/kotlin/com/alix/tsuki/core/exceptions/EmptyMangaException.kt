package com.alix.tsuki.core.exceptions

import com.alix.tsuki.details.ui.pager.EmptyMangaReason
import tsuki.model.Manga

class EmptyMangaException(
    val reason: EmptyMangaReason?,
    val manga: Manga,
    cause: Throwable?
) : IllegalStateException(cause)
