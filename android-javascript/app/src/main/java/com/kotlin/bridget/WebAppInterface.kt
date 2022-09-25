package com.kotlin.bridget


import android.webkit.JavascriptInterface

class WebAppInterface(val onDownloadFile: onWebAppListener?) {
companion object{
    const val  javascriptInterfaceName = "Android"
}

    @JavascriptInterface
    fun showToast(message: String) {
        onDownloadFile?.showMessage(message)
    }

    @JavascriptInterface
    fun downloadFile(
        url: String?,
        fileName: String?,
        fileType: String?,
    ) {
        onDownloadFile?.downloadFile(CustomFile(url, fileName, fileType))
    }

}
