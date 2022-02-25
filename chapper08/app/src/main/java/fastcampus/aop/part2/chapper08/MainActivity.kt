package fastcampus.aop.part2.chapper08

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val webView: WebView by lazy {
        findViewById(R.id.webView)
    }

    private val addressBar: EditText by lazy {
        findViewById(R.id.addressBar)
    }

    private val goHomeButton: ImageButton by lazy {
        findViewById(R.id.goHomeButton)
    }

    private val goForwardButton: ImageButton by lazy {
        findViewById(R.id.goForwardButton)
    }
    private val goBackButton: ImageButton by lazy {
        findViewById(R.id.goBackButton)
    }

    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLayout)
    }

    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        bindView()
    }

    //안드로이드 빽 버튼을 눌렀을때 동작
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled") //경고 무시
    private fun initViews() {

        webView.apply {
            //우리 앱 웹뷰에 화면을 뿌려주도록 화면을 가져온다.
            webViewClient = WebViewClient()
            //화면내 자바스크립트가 default로 막아져 있으므로 동작을 위해 true
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
            webChromeClient = WebChromeClient()
        }
    }

    private fun bindView() {
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        addressBar.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val loadingUrl = textView.text.toString()
                if (URLUtil.isNetworkUrl(loadingUrl)) {
                    webView.loadUrl(loadingUrl)
                } else {
                    webView.loadUrl("http://$loadingUrl")
                }
            }

            return@setOnEditorActionListener false
        }

        goBackButton.setOnClickListener {
            webView.goBack()
        }

        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    //상위에 있는 클래스에 접근
    inner class WebViewClient : android.webkit.WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            refreshLayout.isRefreshing = false
            progressBar.hide()
            goBackButton.isEnabled = webView.canGoBack()
            goForwardButton.isEnabled = webView.canGoForward()
            addressBar.setText(url)
        }
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            progressBar.progress = newProgress
        }
    }

    companion object {
        private const val DEFAULT_URL = "http://www.google.com"
    }
}