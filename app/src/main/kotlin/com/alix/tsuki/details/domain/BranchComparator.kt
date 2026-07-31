package com.alix.tsuki.details.domain

import com.alix.tsuki.core.util.LocaleStringComparator
import com.alix.tsuki.details.ui.model.MangaBranch

class BranchComparator : Comparator<MangaBranch> {

	private val delegate = LocaleStringComparator()

	override fun compare(o1: MangaBranch, o2: MangaBranch): Int = delegate.compare(o1.name, o2.name)
}
