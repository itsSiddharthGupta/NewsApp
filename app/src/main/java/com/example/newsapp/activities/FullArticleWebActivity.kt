package com.example.newsapp.activities

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityFullArticleWebBinding

class FullArticleWebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullArticleWebBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_article_web)

        val baseUrl = intent.getStringExtra("NEWS_URL")!!
        Log.e("Url", baseUrl)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Log.e("Webview Error", error.toString())
                binding.progressBar.visibility = View.GONE
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                if (handler != null){
                    handler.proceed();
                } else {
                    super.onReceivedSslError(view, null, error);
                }
            }
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE
            }
        }
        binding.webBack.setOnClickListener {
            onBackPressed()
        }
        binding.webView.settings.javaScriptEnabled = false
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.loadUrl(baseUrl)
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) binding.webView.goBack() else {
            finish()
        }
    }
}