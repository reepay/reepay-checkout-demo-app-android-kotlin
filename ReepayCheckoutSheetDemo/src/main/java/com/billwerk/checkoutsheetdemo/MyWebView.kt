package com.billwerk.checkoutsheetdemo

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyWebView(private val context: Context) {

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    fun showWebViewBottomSheet(sessionUrl: String) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetView = View.inflate(context, R.layout.bottom_sheet_dialog, null)

        val webView = bottomSheetView.findViewById<WebView>(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()

        webView.isVerticalScrollBarEnabled = true
        webView.scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY

        webView.addJavascriptInterface(
            MyWebViewListener(context, bottomSheetDialog),
            "AndroidWebViewListener"
        )

        // Enable Google Pay on Android WebView
        // https://developers.googleblog.com/en/adding-support-for-google-pay-within-android-webview/
        if (WebViewFeature.isFeatureSupported(
                WebViewFeature.PAYMENT_REQUEST)
            ) {
            WebSettingsCompat.setPaymentRequestEnabled(webView.settings, true);
        }

        webView.loadUrl(sessionUrl)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()

        val closeButton = bottomSheetView.findViewById<LinearLayout>(R.id.closeButton)
        closeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }
}
