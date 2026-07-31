package org.draken.usagi.local.data.pdf

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.draken.usagi.core.util.ext.printStackTraceDebug
import tsuki.util.runCatchingCancellable
import javax.inject.Inject
import javax.inject.Singleton

/** One file found while scanning an "Offline Reader" folder. */
sealed interface OfflineFile {
	val docUri: Uri
	val displayName: String

	data class Cbz(override val docUri: Uri, override val displayName: String) : OfflineFile
	data class Pdf(override val docUri: Uri, override val displayName: String) : OfflineFile
}

/**
 * Scans a folder picked via `ACTION_OPEN_DOCUMENT_TREE` for CBZ/ZIP and PDF files.
 *
 * Runs entirely on [Dispatchers.IO]. Every entry is inspected independently and wrapped
 * in its own try/catch (via [runCatchingCancellable]) so one corrupt/unreadable file or
 * one unreadable subfolder is skipped rather than aborting or crashing the whole scan.
 */
@Singleton
class OfflineFolderScanner @Inject constructor(
	@ApplicationContext private val context: Context,
) {

	suspend fun scan(treeUri: Uri, recursive: Boolean = true): List<OfflineFile> = withContext(Dispatchers.IO) {
		val rootDocId = runCatchingCancellable {
			DocumentsContract.getTreeDocumentId(treeUri)
		}.onFailure { e ->
			e.printStackTraceDebug()
		}.getOrNull() ?: return@withContext emptyList()

		val result = ArrayList<OfflineFile>()
		scanInto(treeUri, rootDocId, recursive, result)
		result.sortWith(offlineFileChapterOrder)
		result
	}

	/**
	 * Lists a directory's children with ONE cursor query returning document id, display name,
	 * and mime type for every entry at once.
	 *
	 * [DocumentFile.listFiles] plus per-entry `.isDirectory` / `.isFile` / `.name` / `.type`
	 * looks equivalent but isn't: each of those property reads on a [DocumentFile] fires its
	 * own separate query against the SAF content provider (nothing is cached), so a folder
	 * with N files did roughly 5N individual queries just to classify what's inside it before
	 * any manga/chapter object existed. Batching into one query per directory removes that
	 * per-file multiplier entirely — this is the fix for slow "folder → manga" creation.
	 */
	private fun scanInto(treeUri: Uri, documentId: String, recursive: Boolean, out: MutableList<OfflineFile>) {
		val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, documentId)
		val projection = arrayOf(
			DocumentsContract.Document.COLUMN_DOCUMENT_ID,
			DocumentsContract.Document.COLUMN_DISPLAY_NAME,
			DocumentsContract.Document.COLUMN_MIME_TYPE,
		)

		val cursor = runCatchingCancellable {
			context.contentResolver.query(childrenUri, projection, null, null, null)
		}.onFailure { e -> e.printStackTraceDebug() }.getOrNull() ?: return

		cursor.use {
			val idIdx = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
			val nameIdx = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
			val mimeIdx = it.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)

			while (it.moveToNext()) {
				runCatchingCancellable {
					val docId = it.getString(idIdx) ?: return@runCatchingCancellable
					val name = it.getString(nameIdx).orEmpty()
					val mime = it.getString(mimeIdx).orEmpty()

					when {
						mime == DocumentsContract.Document.MIME_TYPE_DIR && recursive ->
							scanInto(treeUri, docId, recursive, out)

						mime == DocumentsContract.Document.MIME_TYPE_DIR -> Unit
						name.isCbzName(mime) -> out += OfflineFile.Cbz(
							DocumentsContract.buildDocumentUriUsingTree(treeUri, docId),
							name.ifEmpty { docId },
						)

						name.isPdfName(mime) -> out += OfflineFile.Pdf(
							DocumentsContract.buildDocumentUriUsingTree(treeUri, docId),
							name.ifEmpty { docId },
						)

						else -> Unit
					}
				}.onFailure { e ->
					// A single unreadable/corrupt entry (bad permissions, mid-scan removal,
					// malformed provider metadata, etc.) must never abort the whole scan.
					e.printStackTraceDebug()
				}
			}
		}
	}

	private fun String.isCbzName(mime: String): Boolean {
		val lower = lowercase()
		return lower.endsWith(".cbz") || lower.endsWith(".zip") ||
			mime == "application/vnd.comicbook+zip" || mime == "application/zip"
	}

	private fun String.isPdfName(mime: String): Boolean {
		return lowercase().endsWith(".pdf") || mime == "application/pdf"
	}
}
