package com.example.assignment_vahan

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.assignment_vahan.databinding.ActivityMainBinding
import com.example.assignment_vahan.databinding.ActivityWebviewBinding

class webviewActivity : AppCompatActivity() {
    lateinit var binding: ActivityWebviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)

        setContentView(binding.root)


        val url = intent.getStringExtra("url")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.webviewId.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            binding.webviewId.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            binding.webviewId.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        if (url != null) {
            binding.webviewId.settings.javaScriptEnabled = true
            binding.webviewId.clearCache(true)

            binding.webviewId.clearHistory()
            binding.webviewId.settings.userAgentString =
                "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"
            binding.webviewId.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressbarId.visibility = View.GONE
                    binding.webviewId.visibility = View.VISIBLE
                }
            }
            val fixedUrl = if (url.startsWith("http://")) {
                "https://" + url.substring(7)
            } else {
                url
            }
            binding.webviewId.loadUrl(fixedUrl)
            Toast.makeText(this,"You are opening $url",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }


    }


}