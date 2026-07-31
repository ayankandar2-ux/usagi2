package com.alix.tsuki.reader.ui.pager.reversed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.alix.tsuki.core.exceptions.resolve.ExceptionResolver
import com.alix.tsuki.core.os.NetworkState
import com.alix.tsuki.databinding.ItemPageBinding
import com.alix.tsuki.reader.domain.PageLoader
import com.alix.tsuki.reader.ui.config.ReaderSettings
import com.alix.tsuki.reader.ui.pager.BaseReaderAdapter

class ReversedPagesAdapter(
	private val lifecycleOwner: LifecycleOwner,
	loader: PageLoader,
	readerSettingsProducer: ReaderSettings.Producer,
	networkState: NetworkState,
	exceptionResolver: ExceptionResolver,
) : BaseReaderAdapter<ReversedPageHolder>(loader, readerSettingsProducer, networkState, exceptionResolver) {

	override fun onCreateViewHolder(
		parent: ViewGroup,
		loader: PageLoader,
		readerSettingsProducer: ReaderSettings.Producer,
		networkState: NetworkState,
		exceptionResolver: ExceptionResolver,
	) = ReversedPageHolder(
		owner = lifecycleOwner,
		binding = ItemPageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
		loader = loader,
		readerSettingsProducer = readerSettingsProducer,
		networkState = networkState,
		exceptionResolver = exceptionResolver,
	)
}
