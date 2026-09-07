package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private var webViewInstance: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Fully hide status bars, navigation bars, and all system icons for true full-screen experience
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
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
        }

        webViewInstance = webView
        setContentView(webView)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
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
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val supportedModels = listOf(
        "gemini-3.5-flash",
        "gemini-flash-latest",
        "gemini-3.1-flash-lite-preview",
        "gemini-3.1-pro-preview"
    )

    @JavascriptInterface
    fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isBlank() || key == "MY_GEMINI_API_KEY" || key == "null") "" else key.trim()
        } catch (e: Exception) {
            ""
        }
    }

    @JavascriptInterface
    fun executeGemini(userApiKey: String, prompt: String, preferredModel: String): String {
        val effectiveKey = if (userApiKey.isNotBlank() && userApiKey != "MY_GEMINI_API_KEY") {
            userApiKey.trim()
        } else {
            getApiKey()
        }

        if (effectiveKey.isBlank()) {
            val errObj = JSONObject()
            errObj.put("success", false)
            errObj.put("error", "No API key provided. Please enter your Gemini API Key in Settings.")
            return errObj.toString()
        }

        val modelsToTry = mutableListOf<String>()
        if (preferredModel.isNotBlank() && preferredModel in supportedModels) {
            modelsToTry.add(preferredModel)
        }
        for (m in supportedModels) {
            if (m !in modelsToTry) {
                modelsToTry.add(m)
            }
        }

        var lastError = "Unable to connect to Google Gemini API"

        for (model in modelsToTry) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$effectiveKey"
                
                val reqJson = JSONObject()
                val contentsArr = JSONArray()
                val contentObj = JSONObject()
                val partsArr = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", prompt)
                partsArr.put(partObj)
                contentObj.put("parts", partsArr)
                contentsArr.put(contentObj)
                reqJson.put("contents", contentsArr)

                val body = reqJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = httpClient.newCall(request).execute()
                val resBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val resJson = JSONObject(resBody)
                    val candidates = resJson.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCand = candidates.getJSONObject(0)
                        val content = firstCand.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text", "")
                            val successObj = JSONObject()
                            successObj.put("success", true)
                            successObj.put("text", text)
                            successObj.put("model", model)
                            return successObj.toString()
                        }
                    }
                    lastError = "Response format unexpected from model $model"
                } else {
                    val errMessage = try {
                        val errObj = JSONObject(resBody).optJSONObject("error")
                        errObj?.optString("message") ?: "HTTP ${response.code}"
                    } catch (e: Exception) {
                        "HTTP ${response.code}: $resBody"
                    }
                    lastError = "[$model] $errMessage"
                }
            } catch (e: Exception) {
                lastError = "[$model] ${e.localizedMessage ?: e.message}"
            }
        }

        val errObj = JSONObject()
        errObj.put("success", false)
        errObj.put("error", lastError)
        return errObj.toString()
    }
}
