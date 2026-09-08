package com.example

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import com.example.data.local.AppDatabase
import com.example.data.model.DocType
import com.example.data.model.GeneratedDoc
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
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
        
        // Full screen cutout and layout flags to eliminate all system bars and icons on all sides of the screen
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        @Suppress("DEPRECATION")
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Pre-create WebView cache directories to prevent Chromium index / directory enumeration warnings
        try {
            val wasmDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm")
            if (!wasmDir.exists()) {
                wasmDir.mkdirs()
            }
            val jsDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/js")
            if (!jsDir.exists()) {
                jsDir.mkdirs()
            }
        } catch (_: Exception) {}

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val isNetworkAvailable = cm?.activeNetwork?.let {
            cm.getNetworkCapabilities(it)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } ?: false

        val webView = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // Use software layer rendering to completely bypass Mesa DRI render node queries and eliminate Mesa errors
            setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
            addJavascriptInterface(WebAppInterface(this@MainActivity), "AndroidBridge")
            
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
            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: android.webkit.PermissionRequest?) {
                    try {
                        request?.grant(request.resources)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                    android.util.Log.d(
                        "WebViewConsole",
                        "${consoleMessage?.message()} -- line ${consoleMessage?.lineNumber()}"
                    )
                    return true
                }
            }
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                allowFileAccess = true
                allowContentAccess = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                mediaPlaybackRequiresUserGesture = false
            }
            clearCache(true)
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
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

class WebAppInterface(private val context: Context) {
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
    fun isNetworkConnected(): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val network = cm?.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }

    @JavascriptInterface
    fun saveDocToRoom(title: String, docTypeStr: String, subject: String, grade: String, date: String, contentJson: String): Long {
        return try {
            val db = AppDatabase.getDatabase(context)
            val type = if (docTypeStr.equals("presentation", ignoreCase = true)) DocType.PRESENTATION else DocType.WORKSHEET
            val doc = GeneratedDoc(
                title = title,
                docType = type,
                subjectOrTopic = subject,
                gradeOrAudience = grade,
                dateCreated = date,
                contentJson = contentJson,
                slideCountOrQuestionCount = 5,
                styleOrType = "Default"
            )
            runBlocking {
                db.docDao().insertDoc(doc)
            }
        } catch (e: Exception) {
            -1L
        }
    }

    @JavascriptInterface
    fun getAllDocsFromRoom(): String {
        return try {
            val db = AppDatabase.getDatabase(context)
            val docs = runBlocking {
                db.docDao().getAllDocs().firstOrNull() ?: emptyList()
            }
            val arr = JSONArray()
            for (d in docs) {
                val obj = JSONObject()
                obj.put("id", "db-${d.id}")
                obj.put("dbId", d.id)
                obj.put("title", d.title)
                obj.put("type", d.docType.name.lowercase())
                obj.put("subject", d.subjectOrTopic)
                obj.put("grade", d.gradeOrAudience)
                obj.put("topic", d.title)
                obj.put("date", d.dateCreated)
                obj.put("contentJson", d.contentJson)
                arr.put(obj)
            }
            arr.toString()
        } catch (e: Exception) {
            "[]"
        }
    }

    @JavascriptInterface
    fun deleteDocFromRoom(id: Long): Boolean {
        return try {
            val db = AppDatabase.getDatabase(context)
            runBlocking {
                db.docDao().deleteDoc(id)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

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
