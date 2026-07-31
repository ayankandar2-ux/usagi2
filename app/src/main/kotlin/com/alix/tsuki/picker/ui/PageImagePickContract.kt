package com.alix.tsuki.picker.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.alix.tsuki.core.model.parcelable.ParcelableManga
import com.alix.tsuki.core.nav.AppRouter
import tsuki.model.Manga

class PageImagePickContract : ActivityResultContract<Manga?, Uri?>() {

	override fun createIntent(context: Context, input: Manga?): Intent =
		Intent(context, PageImagePickActivity::class.java)
			.putExtra(AppRouter.KEY_MANGA, input?.let { ParcelableManga(it) })

	override fun parseResult(resultCode: Int, intent: Intent?): Uri? = intent?.data
}
