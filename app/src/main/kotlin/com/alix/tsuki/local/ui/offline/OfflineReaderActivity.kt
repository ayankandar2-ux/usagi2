package com.alix.tsuki.local.ui.offline

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import com.alix.tsuki.R
import com.alix.tsuki.core.exceptions.resolve.SnackbarErrorObserver
import com.alix.tsuki.core.nav.router
import com.alix.tsuki.core.os.OpenDocumentTreeHelper
import com.alix.tsuki.core.ui.BaseActivity
import com.alix.tsuki.core.util.ext.consumeAllSystemBarsInsets
import com.alix.tsuki.core.util.ext.observe
import com.alix.tsuki.core.util.ext.observeEvent
import com.alix.tsuki.core.util.ext.tryLaunch
import com.alix.tsuki.databinding.ActivityOfflineReaderBinding

@AndroidEntryPoint
class OfflineReaderActivity : BaseActivity<ActivityOfflineReaderBinding>() {

	private val viewModel: OfflineReaderViewModel by viewModels()

	private val pickFolderLauncher = OpenDocumentTreeHelper(
		activityResultCaller = this,
		flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION,
	) { uri ->
		if (uri != null) viewModel.onFolderPicked(uri)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(ActivityOfflineReaderBinding.inflate(layoutInflater))
		setDisplayHomeAsUp(isEnabled = true, showUpAsClose = false)
		title = getString(R.string.offline_reader)

		val adapter = OfflineFolderAdapter(
			onItemClick = { manga -> viewModel.onLibraryItemClick(manga) },
			onItemLongClick = { manga -> viewModel.onLibraryItemLongClick(manga) },
		)
		viewBinding.recyclerView.adapter = adapter

		viewBinding.fabSelectFolder.setOnClickListener {
			if (!pickFolderLauncher.tryLaunch(null)) {
				Snackbar.make(viewBinding.root, R.string.operation_not_supported, Snackbar.LENGTH_SHORT).show()
			}
		}

		viewModel.library.observe(this) { library ->
			adapter.submitList(library)
			viewBinding.textViewEmpty.isVisible = library.isEmpty() && viewModel.hasLoadedOnce.value
		}
		viewModel.isLoading.observe(this) { viewBinding.progressBar.isVisible = it }
		viewModel.onMangaReady.observeEvent(this) { manga ->
			router.openDetails(manga)
		}
		viewModel.onConfirmRemove.observeEvent(this) { manga ->
			MaterialAlertDialogBuilder(this)
				.setTitle(manga.title)
				.setMessage(R.string.offline_reader_remove_folder_confirm)
				.setPositiveButton(R.string.remove) { _, _ -> viewModel.onRemoveFolderConfirmed(manga) }
				.setNegativeButton(android.R.string.cancel, null)
				.show()
		}
		viewModel.onError.observeEvent(
			this,
			SnackbarErrorObserver(viewBinding.root, null, exceptionResolver, null),
		)
	}

	override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
		val barsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
		viewBinding.fabSelectFolder.updateLayoutParams<androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams> {
			rightMargin = topMargin + barsInsets.right
			leftMargin = topMargin + barsInsets.left
			bottomMargin = topMargin + barsInsets.bottom
		}
		viewBinding.appbar.updatePadding(
			left = barsInsets.left,
			right = barsInsets.right,
			top = barsInsets.top,
		)
		viewBinding.recyclerView.updatePadding(
			left = barsInsets.left,
			right = barsInsets.right,
			bottom = barsInsets.bottom,
		)
		return insets.consumeAllSystemBarsInsets()
	}
}
