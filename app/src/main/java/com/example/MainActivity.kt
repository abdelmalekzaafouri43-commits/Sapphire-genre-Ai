package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {
    private var webViewInstance: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Fully hide status bars, navigation bars, and all system icons for true full-screen experience
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            HtmlAppScreen(onWebViewCreated = { webViewInstance = it })
        }
    }

    override fun onResume() {
        super.onResume()
        webViewInstance?.onResume()
    }

    override fun onPause() {
        webViewInstance?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        webViewInstance?.destroy()
        webViewInstance = null
        super.onDestroy()
    }
}

class WebAppInterface {
    @JavascriptInterface
    fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isBlank() || key == "MY_GEMINI_API_KEY" || key == "null") "" else key
        } catch (e: Exception) {
            ""
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HtmlAppScreen(onWebViewCreated: (WebView) -> Unit = {}) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                // Ensure safe software rendering fallback to eliminate host driver Mesa rendernode logs
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                
                addJavascriptInterface(WebAppInterface(), "AndroidBridge")
                
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        val key = try {
                            val k = BuildConfig.GEMINI_API_KEY
                            if (k.isBlank() || k == "MY_GEMINI_API_KEY" || k == "null") "" else k
                        } catch (e: Exception) {
                            ""
                        }
                        if (key.isNotEmpty()) {
                            view?.evaluateJavascript(
                                "if (window.onAndroidApiKeyLoaded) { window.onAndroidApiKeyLoaded('$key'); }",
                                null
                            )
                        }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                    }
                }
                webChromeClient = WebChromeClient()
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    allowFileAccess = true
                    allowContentAccess = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    mediaPlaybackRequiresUserGesture = false
                }
                loadUrl("file:///android_asset/index.html")
                onWebViewCreated(this)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
