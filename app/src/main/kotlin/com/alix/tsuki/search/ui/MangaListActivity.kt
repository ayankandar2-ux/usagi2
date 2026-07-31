package com.alix.tsuki.search.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import com.alix.tsuki.R
import com.alix.tsuki.core.model.LocalMangaSource
import com.alix.tsuki.core.model.MangaSource
import com.alix.tsuki.core.model.getSummary
import com.alix.tsuki.core.model.getTitle
import com.alix.tsuki.core.model.isNsfw
import com.alix.tsuki.core.model.parcelable.ParcelableManga
import com.alix.tsuki.core.model.parcelable.ParcelableMangaListFilter
import com.alix.tsuki.core.nav.AppRouter
import com.alix.tsuki.core.nav.router
import com.alix.tsuki.core.ui.BaseActivity
import com.alix.tsuki.core.ui.model.titleRes
import com.alix.tsuki.core.util.ViewBadge
import com.alix.tsuki.core.util.ext.consumeSystemBarsInsets
import com.alix.tsuki.core.util.ext.end
import com.alix.tsuki.core.util.ext.getParcelableExtraCompat
import com.alix.tsuki.core.util.ext.getSerializableExtraCompat
import com.alix.tsuki.core.util.ext.getThemeColor
import com.alix.tsuki.core.util.ext.observe
import com.alix.tsuki.core.util.ext.setTextAndVisible
import com.alix.tsuki.core.util.ext.start
import com.alix.tsuki.databinding.ActivityMangaListBinding
import com.alix.tsuki.filter.ui.FilterCoordinator
import com.alix.tsuki.filter.ui.FilterHeaderFragment
import com.alix.tsuki.filter.ui.external.sheet.FilterSheetFragment as ExternalSheetFragment
import com.alix.tsuki.filter.ui.external.FilterMapper
import com.alix.tsuki.filter.ui.sheet.FilterSheetFragment
import com.alix.tsuki.list.ui.preview.PreviewFragment
import com.alix.tsuki.local.ui.LocalListFragment
import com.alix.tsuki.main.ui.owners.AppBarOwner
import tsuki.model.Manga
import tsuki.model.MangaListFilter
import tsuki.model.MangaSource
import tsuki.model.SortOrder
import com.alix.tsuki.remotelist.ui.RemoteListFragment
import kotlin.math.absoluteValue
import com.google.android.material.R as materialR

@AndroidEntryPoint
class MangaListActivity :
	BaseActivity<ActivityMangaListBinding>(),
	AppBarOwner, View.OnClickListener,
	FilterCoordinator.Owner,
	AppBarLayout.OnOffsetChangedListener {

	override val appBar: AppBarLayout
		get() = viewBinding.appbar

	override val filterCoordinator: FilterCoordinator
		get() = checkNotNull(findFilterOwner()) {
			"Cannot find FilterCoordinator.Owner fragment in ${supportFragmentManager.fragments}"
		}.filterCoordinator

	private lateinit var source: MangaSource

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(ActivityMangaListBinding.inflate(layoutInflater))
		val filter = intent.getParcelableExtraCompat<ParcelableMangaListFilter>(AppRouter.KEY_FILTER)?.filter
		val sortOrder = intent.getSerializableExtraCompat<SortOrder>(AppRouter.KEY_SORT_ORDER)
		source = MangaSource(intent.getStringExtra(AppRouter.KEY_SOURCE))
		setDisplayHomeAsUp(isEnabled = true, showUpAsClose = false)
		if (viewBinding.containerFilterHeader != null) {
			viewBinding.appbar.addOnOffsetChangedListener(this)
		}
		viewBinding.buttonOrder?.setOnClickListener(this)
		title = source.getTitle(this)
		initList(source, filter, sortOrder)
	}

	override fun isNsfwContent(): Flow<Boolean> = flowOf(source.isNsfw())

	override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
		val container = viewBinding.containerFilterHeader ?: return
		container.background = if (verticalOffset.absoluteValue < appBarLayout.totalScrollRange) {
			container.context.getThemeColor(materialR.attr.backgroundColor).toDrawable()
		} else {
			viewBinding.collapsingToolbarLayout?.contentScrim
		}
	}

	/**
	 * Only for landscape
	 */
	override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
		val barsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
		viewBinding.cardSide?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
			marginEnd = barsInsets.end(v) + resources.getDimensionPixelOffset(R.dimen.side_card_offset)
			topMargin = barsInsets.top + resources.getDimensionPixelOffset(R.dimen.grid_spacing_outer_double)
			bottomMargin = barsInsets.bottom + resources.getDimensionPixelOffset(R.dimen.side_card_offset)
		}
		viewBinding.appbar.updatePaddingRelative(
			top = barsInsets.top,
			end = if (viewBinding.cardSide == null) barsInsets.end(v) else 0,
			start = barsInsets.start(v),
		)
		return insets.consumeSystemBarsInsets(v, top = true, end = true)
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.button_order -> {
				val coordinator = findFilterOwner()?.filterCoordinator
				if (coordinator?.isDynamicFilter == true) {
					router.showSortSheet()
				} else router.showFilterSheet()
			}
		}
	}

	fun showPreview(manga: Manga): Boolean = setSideFragment(
		PreviewFragment::class.java,
		bundleOf(AppRouter.KEY_MANGA to ParcelableManga(manga)),
	)

	fun hidePreview() = setSideFragment(filterSheetClass(findFilterOwner()), null)

	private fun filterSheetClass(owner: FilterCoordinator.Owner?): Class<out Fragment> =
		if (owner?.filterCoordinator?.isDynamicFilter == true) {
			ExternalSheetFragment::class.java
		} else {
			FilterSheetFragment::class.java
		}

	private fun initList(source: MangaSource, filter: MangaListFilter?, sortOrder: SortOrder?) {
		val fm = supportFragmentManager
		val existingFragment = fm.findFragmentById(R.id.container)
		if (existingFragment is FilterCoordinator.Owner) {
			initFilter(existingFragment)
		} else {
			fm.commit {
				setReorderingAllowed(true)
				val fragment = if (source == LocalMangaSource) {
					LocalListFragment()
				} else {
					RemoteListFragment.newInstance(source)
				}
				replace(R.id.container, fragment)
				runOnCommit { initFilter(fragment) }
				if (filter != null || sortOrder != null) {
					runOnCommit(ApplyFilterRunnable(fragment, filter, sortOrder))
				}
			}
		}
	}

	private fun initFilter(filterOwner: FilterCoordinator.Owner) {
		if (viewBinding.containerSide != null) {
			setSideFragment(filterSheetClass(filterOwner), null)
		} else if (viewBinding.containerFilterHeader != null) {
			if (supportFragmentManager.findFragmentById(R.id.container_filter_header) == null) {
				supportFragmentManager.commit {
					setReorderingAllowed(true)
					replace(R.id.container_filter_header, FilterHeaderFragment::class.java, null)
				}
			}
		}
		val filter = filterOwner.filterCoordinator
		val chipSort = viewBinding.buttonOrder
		if (chipSort != null) {
			val filterBadge = ViewBadge(chipSort, this)
			filterBadge.setMaxCharacterCount(0)
			val isDynamic = filter.isDynamicFilter
			filter.observe().observe(this) { snapshot ->
				if (isDynamic) {
					val sortTag = snapshot.listFilter.tags.firstOrNull { it.key.startsWith(FilterMapper.SORT_KEY_PREFIX) }
					chipSort.text = sortTag?.title?.substringAfter(": ")
						?: snapshot.sortLabel
						?: getString(snapshot.sortOrder.titleRes)
					chipSort.isVisible = true
					filterBadge.counter = if (snapshot.listFilter.tags.any { !it.key.startsWith(FilterMapper.SORT_KEY_PREFIX) }) 1 else 0
				} else {
					chipSort.setTextAndVisible(snapshot.sortOrder.titleRes)
					filterBadge.counter = if (snapshot.listFilter.hasNonSearchOptions()) 1 else 0
				}
			}
		} else {
			filter.observe().map {
				it.listFilter.getSummary()
			}.flowOn(Dispatchers.Default)
				.observe(this) {
					supportActionBar?.subtitle = it
				}
		}
	}

	private fun findFilterOwner(): FilterCoordinator.Owner? {
		return supportFragmentManager.findFragmentById(R.id.container) as? FilterCoordinator.Owner
	}

	private fun setSideFragment(cls: Class<out Fragment>, args: Bundle?) = if (viewBinding.containerSide != null) {
		supportFragmentManager.commit {
			setReorderingAllowed(true)
			replace(R.id.container_side, cls, args)
		}
		true
	} else {
		false
	}

	private class ApplyFilterRunnable(
		private val filterOwner: FilterCoordinator.Owner,
		private val filter: MangaListFilter?,
		private val sortOrder: SortOrder?,
	) : Runnable {

		override fun run() {
			if (sortOrder != null) {
				filterOwner.filterCoordinator.setSortOrder(sortOrder)
			}
			if (filter != null) {
				filterOwner.filterCoordinator.setAdjusted(filter)
			}
		}
	}
}
