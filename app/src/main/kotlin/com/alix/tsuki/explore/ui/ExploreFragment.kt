package com.alix.tsuki.explore.ui

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ActionMode
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import com.alix.tsuki.R
import com.alix.tsuki.core.exceptions.resolve.SnackbarErrorObserver
import com.alix.tsuki.core.model.LocalMangaSource
import com.alix.tsuki.core.model.externalPackageName
import com.alix.tsuki.core.nav.router
import com.alix.tsuki.core.prefs.AppSettings
import com.alix.tsuki.core.ui.BaseFragment
import com.alix.tsuki.core.ui.dialog.BigButtonsAlertDialog
import com.alix.tsuki.core.ui.list.ListSelectionController
import com.alix.tsuki.core.ui.list.OnListItemClickListener
import com.alix.tsuki.core.ui.util.RecyclerViewOwner
import com.alix.tsuki.core.ui.util.ReversibleActionObserver
import com.alix.tsuki.core.ui.util.SpanSizeResolver
import com.alix.tsuki.core.util.ext.addMenuProvider
import com.alix.tsuki.core.util.ext.consumeAllSystemBarsInsets
import com.alix.tsuki.core.util.ext.findAppCompatDelegate
import com.alix.tsuki.core.util.ext.observe
import com.alix.tsuki.core.util.ext.observeEvent
import com.alix.tsuki.core.util.ext.systemBarsInsets
import com.alix.tsuki.databinding.FragmentExploreBinding
import com.alix.tsuki.explore.ui.adapter.ExploreAdapter
import com.alix.tsuki.explore.ui.adapter.ExploreListEventListener
import com.alix.tsuki.explore.ui.model.MangaSourceItem
import com.alix.tsuki.list.ui.adapter.TypedListSpacingDecoration
import com.alix.tsuki.list.ui.model.ListHeader
import tsuki.model.Manga
import javax.inject.Inject

@AndroidEntryPoint
class ExploreFragment :
	BaseFragment<FragmentExploreBinding>(),
	RecyclerViewOwner,
	ExploreListEventListener,
	OnListItemClickListener<MangaSourceItem>, ListSelectionController.Callback {

	private val viewModel by viewModels<ExploreViewModel>()
	private var exploreAdapter: ExploreAdapter? = null
	private var sourceSelectionController: ListSelectionController? = null

	@Inject
	lateinit var settings: AppSettings

	override val recyclerView: RecyclerView?
		get() = viewBinding?.recyclerView

	override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentExploreBinding {
		return FragmentExploreBinding.inflate(inflater, container, false)
	}

	override fun onViewBindingCreated(binding: FragmentExploreBinding, savedInstanceState: Bundle?) {
		super.onViewBindingCreated(binding, savedInstanceState)
		exploreAdapter = ExploreAdapter(this, this) { manga, _ ->
			router.openDetails(manga)
		}
		sourceSelectionController = ListSelectionController(
			appCompatDelegate = checkNotNull(findAppCompatDelegate()),
			decoration = SourceSelectionDecoration(binding.root.context),
			registryOwner = this,
			callback = this,
		)
		with(binding.recyclerView) {
			adapter = exploreAdapter
			setHasFixedSize(true)
			SpanSizeResolver(this, resources.getDimensionPixelSize(R.dimen.explore_grid_width)).attach()
			addItemDecoration(TypedListSpacingDecoration(context, false))
			checkNotNull(sourceSelectionController).attachToRecyclerView(this)
		}
		addMenuProvider(ExploreMenuProvider(router))
		viewModel.content.observe(viewLifecycleOwner, checkNotNull(exploreAdapter))
		viewModel.onError.observeEvent(viewLifecycleOwner, SnackbarErrorObserver(binding.recyclerView, this))
		viewModel.onOpenManga.observeEvent(viewLifecycleOwner, ::onOpenManga)
		viewModel.onActionDone.observeEvent(viewLifecycleOwner, ReversibleActionObserver(binding.recyclerView))
		viewModel.isGrid.observe(viewLifecycleOwner, ::onGridModeChanged)
		viewModel.onShowSuggestionsTip.observeEvent(viewLifecycleOwner) {
			showSuggestionsTip()
		}
	}

	override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
		val barsInsets = insets.systemBarsInsets
		val basePadding = v.resources.getDimensionPixelOffset(R.dimen.list_spacing_normal)
		viewBinding?.recyclerView?.setPadding(
			/* left = */ barsInsets.left + basePadding,
			/* top = */ basePadding,
			/* right = */ barsInsets.right + basePadding,
			/* bottom = */ barsInsets.bottom + basePadding,
		)
		return insets.consumeAllSystemBarsInsets()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		sourceSelectionController = null
		exploreAdapter = null
	}

	override fun onResume() {
		super.onResume()
		viewModel.runAutoUpdate()
	}

	override fun onListHeaderClick(item: ListHeader, view: View) {
		if (item.payload == R.id.nav_suggestions) {
			router.openSuggestions()
		} else if (viewModel.isAllSourcesEnabled.value) {
			router.openManageSources()
		} else {
			router.openSourcesCatalog()
		}
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.button_local -> router.openList(LocalMangaSource, null, null)
			R.id.button_bookmarks -> router.openBookmarks()
			R.id.button_more -> router.openSuggestions()
			R.id.button_downloads -> router.openDownloads()
			R.id.button_random -> viewModel.openRandom()
		}
	}

	override fun onItemClick(item: MangaSourceItem, view: View) {
		if (sourceSelectionController?.onItemClick(item.id) == true) {
			return
		}
		router.openList(item.source, null, null)
	}

	override fun onItemLongClick(item: MangaSourceItem, view: View): Boolean {
		return sourceSelectionController?.onItemLongClick(view, item.id) == true
	}

	override fun onItemContextClick(item: MangaSourceItem, view: View): Boolean {
		return sourceSelectionController?.onItemContextClick(view, item.id) == true
	}

	override fun onRetryClick(error: Throwable) = Unit

	override fun onEmptyActionClick() = router.openSourcesCatalog()

	override fun onSelectionChanged(controller: ListSelectionController, count: Int) {
		viewBinding?.recyclerView?.invalidateItemDecorations()
	}

	override fun onCreateActionMode(
		controller: ListSelectionController,
		menuInflater: MenuInflater,
		menu: Menu
	): Boolean {
		menuInflater.inflate(R.menu.mode_source, menu)
		return true
	}

	override fun onPrepareActionMode(controller: ListSelectionController, mode: ActionMode?, menu: Menu): Boolean {
		val selectedSources = viewModel.sourcesSnapshot(controller.peekCheckedIds())
		val isSingleSelection = selectedSources.size == 1
		menu.findItem(R.id.action_settings).isVisible = isSingleSelection
		menu.findItem(R.id.action_shortcut).isVisible = isSingleSelection
		menu.findItem(R.id.action_pin).isVisible = selectedSources.all { !it.isPinned }
		menu.findItem(R.id.action_unpin).isVisible = selectedSources.all { it.isPinned }
		menu.findItem(R.id.action_disable)?.isVisible = !viewModel.isAllSourcesEnabled.value &&
			selectedSources.all { it.mangaSource.externalPackageName() == null
				&& it.mangaSource !is LocalMangaSource
				&& it.mangaSource !is com.alix.tsuki.core.model.TestMangaSource
				&& it.mangaSource !is com.alix.tsuki.core.model.UnknownMangaSource
			}
		menu.findItem(R.id.action_delete)?.isVisible = selectedSources.all { it.mangaSource.externalPackageName() != null }
		return super.onPrepareActionMode(controller, mode, menu)
	}

	override fun onActionItemClicked(controller: ListSelectionController, mode: ActionMode?, item: MenuItem): Boolean {
		val selectedSources = viewModel.sourcesSnapshot(controller.peekCheckedIds())
		if (selectedSources.isEmpty()) {
			return false
		}
		when (item.itemId) {
			R.id.action_settings -> {
				val source = selectedSources.singleOrNull() ?: return false
				router.openSourceSettings(source)
				mode?.finish()
			}

			R.id.action_disable -> {
				viewModel.disableSources(selectedSources)
				mode?.finish()
			}

			R.id.action_delete -> {
				selectedSources.mapNotNullTo(LinkedHashSet()) { it.mangaSource.externalPackageName() }
					.forEach(::uninstallExternalPackage)
				mode?.finish()
			}

			R.id.action_shortcut -> {
				val source = selectedSources.singleOrNull() ?: return false
				viewModel.requestPinShortcut(source)
				mode?.finish()
			}

			R.id.action_pin -> {
				viewModel.setSourcesPinned(selectedSources, isPinned = true)
				mode?.finish()
			}

			R.id.action_unpin -> {
				viewModel.setSourcesPinned(selectedSources, isPinned = false)
				mode?.finish()
			}

			else -> return false
		}
		return true
	}

	private fun onOpenManga(manga: Manga) {
		router.openDetails(manga)
	}

	private fun onGridModeChanged(isGrid: Boolean) {
		requireViewBinding().recyclerView.layoutManager = if (isGrid) {
			GridLayoutManager(requireContext(), 4).also { lm ->
				lm.spanSizeLookup = ExploreGridSpanSizeLookup(checkNotNull(exploreAdapter), lm)
			}
		} else {
			LinearLayoutManager(requireContext())
		}
	}

	private fun showSuggestionsTip() {
		val listener = DialogInterface.OnClickListener { _, which ->
			viewModel.respondSuggestionTip(which == DialogInterface.BUTTON_POSITIVE)
		}
		BigButtonsAlertDialog.Builder(requireContext())
			.setIcon(R.drawable.ic_suggestion)
			.setTitle(R.string.suggestions_enable_prompt)
			.setPositiveButton(R.string.enable, listener)
			.setNegativeButton(R.string.no_thanks, listener)
			.create()
			.show()
	}

	private fun uninstallExternalPackage(packageName: String) {
		val uri = Uri.fromParts("package", packageName, null)
		val action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			Intent.ACTION_DELETE
		} else {
			@Suppress("DEPRECATION")
			Intent.ACTION_UNINSTALL_PACKAGE
		}
		context?.startActivity(Intent(action, uri))
	}
}
