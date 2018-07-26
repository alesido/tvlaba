package org.alsi.android.remote.retrofit.file

import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.alsi.android.remote.retrofit.RetrofitServiceBuilder
import java.io.*

/**
 * Created on 12/10/17.
 */

class RetrofitFileDataSource(fileSourceUrlString: String) {

    private val service: RetrofitFileService

    init {
        val restServiceBuilder = RetrofitServiceBuilder(RetrofitFileService::class.java,
                fileSourceUrlString)
        this.service = restServiceBuilder.build()
    }


    fun downloadFile(fileUrlString: String): Observable<ResponseBody> {
        return service.downloadFile(fileUrlString)
    }

    fun downloadAndSaveFile(inputFileUrlString: String, outputFilePath: String): Completable {
        return service.downloadFile(inputFileUrlString)
                .flatMapCompletable { fileDownloadResponseBody ->
                    Completable.fromObservable(Observable.just(
                                saveDownloadedFile(fileDownloadResponseBody, outputFilePath)))
                }
    }

    private fun saveDownloadedFile(fileDownloadResponseBody: ResponseBody, outputFilePath: String): Boolean {

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val outputFile = File(outputFilePath)
            if (outputFile.exists())
                outputFile.delete()

            val fileReader = ByteArray(4096)
            var fileSizeDownloaded: Long = 0

            inputStream = fileDownloadResponseBody.byteStream()
            outputStream = FileOutputStream(outputFile)

            while (true) {
                val read = inputStream!!.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()
            }
            outputStream.flush()

            return true
        } catch (e: Exception) {
            return false
        } finally {
            try {
                inputStream?.close()

                outputStream?.close()
            } catch (iox: IOException) {
            }

        }
    }
}
