package com.billwerk.checkoutsheetdemo

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.TextView
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

        webView.addJavascriptInterface(MyWebViewListener(context, bottomSheetDialog), "AndroidWebViewListener")

        webView.loadUrl(sessionUrl)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.show()

        val closeButton = bottomSheetView.findViewById<ImageButton>(R.id.closeButton)
        val closeText = bottomSheetView.findViewById<TextView>(R.id.closeText)
        closeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        closeText.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }
}
