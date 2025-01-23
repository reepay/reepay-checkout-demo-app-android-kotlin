package com.billwerk.checkoutsheetdemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

// Message Signature to "Initiate WebView mode on Checkout"
data class InitMessage(val isWebView: Boolean, val userAgent: String?)

class CustomTabLauncher(private val context: Context) {
    private var customTabsClient: CustomTabsClient? = null
    private var customTabsSession: CustomTabsSession? = null
    private var onSessionReady: ((CustomTabsSession?) -> Unit)? = null

    // Origin of the website launched
    private val SOURCE_ORIGIN: Uri = Uri.parse(MainActivity.CHECKOUT_DOMAIN)

    // Origin where postMessage communications are sent
    private val TARGET_ORIGIN: Uri = Uri.parse(MainActivity.CHECKOUT_DOMAIN)

    private var relationshipValidated: Boolean = false
    private val gson = Gson()

    fun bindCustomTabsService(callback: (CustomTabsSession?) -> Unit) {
        onSessionReady = callback

        val packageName = CustomTabsClient.getPackageName(context, null)
        if (packageName == null) {
            Log.e("CustomTabLauncher", "No Custom Tabs provider found on the device.")
            callback(null)
            return
        }

        CustomTabsClient.bindCustomTabsService(
            context,
            packageName,
            object : CustomTabsServiceConnection() {
                override fun onCustomTabsServiceConnected(
                    name: ComponentName,
                    client: CustomTabsClient
                ) {
                    customTabsClient = client
                    customTabsClient?.warmup(0L)
                    customTabsSession = customTabsClient?.newSession(customTabsCallback)
                    onSessionReady?.invoke(customTabsSession)
                    Log.d("CustomTabLauncher", "Custom Tabs Service connected.")
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    customTabsClient = null
                    customTabsSession = null
                    Log.d("CustomTabLauncher", "Custom Tabs Service disconnected.")
                }
            })
    }

    private val customTabsCallback = object : CustomTabsCallback() {

        override fun onRelationshipValidationResult(
            relation: Int, requestedOrigin: Uri,
            result: Boolean, extras: Bundle?
        ) {
            Log.d("CustomTabLauncher", "Requested Origin: $requestedOrigin")
            Log.d("CustomTabLauncher", "Validation Result: $result")
            if (extras != null) {
                for (key in extras.keySet()) {
                    Log.d("CustomTabLauncher", "Extra: " + key + " = " + extras[key])
                }
            }
            relationshipValidated = result
        }

        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
            super.onNavigationEvent(navigationEvent, extras)

            if (navigationEvent == NAVIGATION_FINISHED) {
                if (!relationshipValidated) {
                    Log.e(
                        "CustomTabLauncher",
                        "Validation failed. Not starting PostMessage communication."
                    )
                }
            } else if (navigationEvent == TAB_HIDDEN) {
                Log.i(
                    "CustomTabLauncher",
                    "User closed Custom Tab"
                )
            }

            val result: Boolean =
                customTabsSession!!.requestPostMessageChannel(
                    SOURCE_ORIGIN,
                    TARGET_ORIGIN,
                    Bundle()
                )
            Log.d("CustomTabLauncher", "Requested PostMessage Channel: $result")
        }

        override fun onPostMessage(message: String, extras: Bundle?) {
            super.onPostMessage(message, extras)
            Log.d("CustomTabLauncher", "onPostMessage: $message")

            try {
                val mapType = object : TypeToken<Map<String, Any>>() {}.type
                val messageMap: Map<String, Any> = Gson().fromJson(message, mapType)
                for ((key, value) in messageMap) {
                    if (key == "event") {
                        handleEvents(value.toString())
                    }
                }
            } catch (e: JsonSyntaxException) {
                Log.e("CustomTabLauncher", "onPostMessage string: $message")
            }
        }

        override fun onMessageChannelReady(extras: Bundle?) {
            Log.d("CustomTabLauncher", "Message channel ready.")

            val message = InitMessage(true, "Custom Tabs / TWA")
            val messageJson = gson.toJson(message)
            val result: Int = customTabsSession!!.postMessage(messageJson, null)

            if (result == CustomTabsService.RESULT_SUCCESS) {
                Log.i("CustomTabLauncher", "postMessage: RESULT_SUCCESS")
            } else Log.e("CustomTabLauncher", "postMessage: RESULT_FAILURE")
        }
    }

    private fun handleEvents(event: String) {
        when (event) {
            "Init" -> Log.i("CustomTabLauncher", "Checkout started")
            "Open" -> Log.i("CustomTabLauncher", "Checkout opened")
            "Close" -> Log.i("CustomTabLauncher", "Checkout closed")
            "Accept" -> {
                Log.i("CustomTabLauncher", "Checkout accept")
                closeCustomTab()
            }

            "Cancel" -> {
                Log.d("CustomTabLauncher", "Checkout cancel")
                closeCustomTab()
            }

            "Error" -> Log.d("CustomTabLauncher", "Error occurred")
            else -> Log.d("CustomTabLauncher", "Unhandled event: $event")
        }
    }

    private fun closeCustomTab() {
        // Launch a new intent to bring your app to the foreground
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        context.startActivity(intent)
    }
}