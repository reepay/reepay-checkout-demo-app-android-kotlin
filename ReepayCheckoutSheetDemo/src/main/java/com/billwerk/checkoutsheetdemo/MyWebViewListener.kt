package com.billwerk.checkoutsheetdemo

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyWebViewListener(private val context: Context, private val dialog: BottomSheetDialog) {

    @JavascriptInterface
    fun postMessage(jsonMessage: String) {
        Log.d("AndroidWebViewListener", "Received: $jsonMessage")
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        val messageMap: Map<String, Any> = Gson().fromJson(jsonMessage, mapType)

        // Iterate through all keys and log their values
        for ((key, value) in messageMap) {
            if (key == "event") {
            if(key == "event"){
                handleEvents(value.toString())
            }
        }
    }

    @JavascriptInterface
    fun postUserEventMessage(jsonMessage: String) {
        Log.d("AndroidWebViewListener", "Received: $jsonMessage")
    }

    private fun handleEvents(event: String) {
        when (event) {
            "Init" -> Log.d("AndroidWebViewListener", "Checkout started")
            "Open" -> Log.d("AndroidWebViewListener", "Checkout opened")
            "Close" -> Log.d("AndroidWebViewListener", "Checkout closed")
            "Accept" -> dialog.dismiss()
            "Cancel" -> dialog.dismiss()
            "Error" -> Log.d("AndroidWebViewListener", "Error occurred")
            else -> Log.d("AndroidWebViewListener", "Unhandled event: $event")
        }
    }
}