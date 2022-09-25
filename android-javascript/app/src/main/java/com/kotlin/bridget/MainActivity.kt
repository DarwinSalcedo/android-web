package com.kotlin.bridget

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.bridget.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), onWebAppListener {
    private var reference: Long = 0
    private lateinit var binding: ActivityMainBinding

    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val downloadService =
                getSystemService(Context.DOWNLOAD_SERVICE) as (DownloadManager)

            val uriFileStored = downloadService.getUriForDownloadedFile(reference)
            val mimeType = downloadService.getMimeTypeForDownloadedFile(reference)
            val chooserIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uriFileStored)
            }

            startActivity(Intent.createChooser(chooserIntent, "Share this file"))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() {
        binding.mainWebview.loadUrl("https://android-js-interface.web.app")
        binding.mainWebview.settings.javaScriptEnabled = true
        binding.mainWebview.addJavascriptInterface(WebAppInterface(this),
            WebAppInterface.javascriptInterfaceName)
    }

    override fun downloadFile(file: CustomFile) {
        val downloadManager = DownloadManager.Request(Uri.parse(file.url))
        downloadManager.apply {
            setTitle(file.fileName)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS,
                file.fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        }

        reference = (getSystemService(Context.DOWNLOAD_SERVICE) as (DownloadManager)).enqueue(
            downloadManager)

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
    }

}