package com.alix.tsuki.picker.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import com.alix.tsuki.core.ui.BaseViewModel
import com.alix.tsuki.core.util.ext.MutableEventFlow
import com.alix.tsuki.core.util.ext.call
import com.alix.tsuki.reader.ui.PageSaveHelper
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PageImagePickViewModel @Inject constructor() : BaseViewModel() {

	val onFileReady = MutableEventFlow<File>()

	fun savePageToTempFile(pageSaveHelper: PageSaveHelper, task: PageSaveHelper.Task) {
		launchLoadingJob(Dispatchers.Default) {
			val file = pageSaveHelper.saveToTempFile(task)
			onFileReady.call(file)
		}
	}
}
