package com.alix.tsuki.browser

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import dagger.hilt.android.AndroidEntryPoint
import com.alix.tsuki.core.model.MangaSource
import com.alix.tsuki.core.nav.AppRouter
import com.alix.tsuki.core.network.CommonHeaders
import com.alix.tsuki.core.network.proxy.ProxyProvider
import com.alix.tsuki.core.network.webview.adblock.AdBlock
import com.alix.tsuki.core.parser.MangaRepository
import com.alix.tsuki.core.parser.MangaParserRepository
import com.alix.tsuki.core.ui.BaseActivity
import com.alix.tsuki.core.util.ext.configureForParser
import com.alix.tsuki.core.util.ext.consumeAll
import com.alix.tsuki.databinding.ActivityBrowserBinding
import tsuki.model.MangaSource
import tsuki.util.nullIfEmpty
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseBrowserActivity : BaseActivity<ActivityBrowserBinding>(), BrowserCallback {

	@Inject
	lateinit var proxyProvider: ProxyProvider

	@Inject
	lateinit var mangaRepositoryFactory: MangaRepository.Factory

	@Inject
	lateinit var adBlock: AdBlock

	private lateinit var onBackPressedCallback: WebViewBackPressedCallback

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (!setContentViewWebViewSafe { ActivityBrowserBinding.inflate(layoutInflater) }) {
			return
		}
		viewBinding.webView.webChromeClient = ProgressChromeClient(viewBinding.progressBar)
		onBackPressedCallback = WebViewBackPressedCallback(viewBinding.webView)
		onBackPressedDispatcher.addCallback(onBackPressedCallback)

		val mangaSource = MangaSource(intent?.getStringExtra(AppRouter.KEY_SOURCE))
		val repository = mangaRepositoryFactory.create(mangaSource) as? MangaParserRepository
		val userAgent = intent?.getStringExtra(AppRouter.KEY_USER_AGENT)?.nullIfEmpty()
			?: repository?.getRequestHeaders()?.get(CommonHeaders.USER_AGENT)
		viewBinding.webView.configureForParser(userAgent)

		onCreate2(savedInstanceState, mangaSource, repository)
	}

	protected abstract fun onCreate2(
		savedInstanceState: Bundle?,
		source: MangaSource,
		repository: MangaParserRepository?
	)

	override fun onApplyWindowInsets(
		v: View,
		insets: WindowInsetsCompat
	): WindowInsetsCompat {
		val type = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
		val barsInsets = insets.getInsets(type)
		viewBinding.webView.updatePadding(
			left = barsInsets.left,
			right = barsInsets.right,
			bottom = barsInsets.bottom,
		)
		viewBinding.appbar.updatePadding(
			left = barsInsets.left,
			right = barsInsets.right,
			top = barsInsets.top,
		)
		return insets.consumeAll(type)
	}

	override fun onPause() {
		viewBinding.webView.onPause()
		super.onPause()
	}

	override fun onResume() {
		super.onResume()
		viewBinding.webView.onResume()
	}

	override fun onDestroy() {
		super.onDestroy()
		if (hasViewBinding()) {
			viewBinding.webView.stopLoading()
			viewBinding.webView.destroy()
		}
	}

	override fun onLoadingStateChanged(isLoading: Boolean) {
		viewBinding.progressBar.isVisible = isLoading
	}

	override fun onTitleChanged(title: CharSequence, subtitle: CharSequence?) {
		this.title = title
		supportActionBar?.subtitle = subtitle
	}

	override fun onHistoryChanged() {
		onBackPressedCallback.onHistoryChanged()
	}
}
