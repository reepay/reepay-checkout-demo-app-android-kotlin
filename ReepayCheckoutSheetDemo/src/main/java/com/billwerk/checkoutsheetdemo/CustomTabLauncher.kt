/**
 * Custom Tabs requires your app to be verified via Digital Asset Links:
 * https://developer.android.com/training/app-links/verify-android-applinks#web-assoc
 */

package com.billwerk.checkoutsheetdemo

import android.content.ComponentName
import android.content.Context
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

private val TAG = "ActLauncher" // Android Custom Tabs

// Message Signature to "Initiate WebView mode on Checkout"
data class InitMessage(val isWebView: Boolean, val userAgent: String?)

class CustomTabLauncher(private val context: Context) {

    companion object {
        // Origin of the website launched
        val SOURCE_ORIGIN: Uri = Uri.parse(MainActivity.CHECKOUT_DOMAIN)

        // Origin where postMessage communications are sent
        val TARGET_ORIGIN: Uri = Uri.parse(MainActivity.CHECKOUT_DOMAIN)
    }

    private var customTabsClient: CustomTabsClient? = null
    private var customTabsSession: CustomTabsSession? = null
    private var onSessionReady: ((CustomTabsSession?) -> Unit)? = null

    private var relationshipValidated: Boolean = false
    private val gson = Gson()

    fun bindCustomTabsService(callback: (CustomTabsSession?) -> Unit) {
        onSessionReady = callback

        val packageName = CustomTabsClient.getPackageName(context, null)
        if (packageName == null) {
            Log.e(TAG, "No Custom Tabs provider found on the device.")
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
                    Log.d(TAG, "Custom Tabs Service connected.")
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    customTabsClient = null
                    customTabsSession = null
                    Log.d(TAG, "Custom Tabs Service disconnected.")
                }
            })
    }

    private val customTabsCallback = object : CustomTabsCallback() {

        override fun onRelationshipValidationResult(
            relation: Int, requestedOrigin: Uri,
            result: Boolean, extras: Bundle?
        ) {
            Log.d(TAG, "[Requested Origin: $requestedOrigin] [Validation: $result]")
            if (extras != null) {
                for (key in extras.keySet()) {
                    Log.d(TAG, "Extra: " + key + " = " + extras[key])
                }
            }
            relationshipValidated = result
        }

        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
            super.onNavigationEvent(navigationEvent, extras)

            if (navigationEvent == NAVIGATION_FINISHED) {
                if (!relationshipValidated) {
                    Log.e(
                        TAG,
                        "Validation failed. Not starting PostMessage communication."
                    )
                }
            } else if (navigationEvent == TAB_HIDDEN) {
                Log.i(
                    TAG,
                    "User closed Custom Tab"
                )
            }

            val result: Boolean =
                customTabsSession!!.requestPostMessageChannel(
                    SOURCE_ORIGIN,
                    TARGET_ORIGIN,
                    Bundle()
                )
            Log.d(TAG, "Requested PostMessage Channel: $result")
        }

        override fun onPostMessage(message: String, extras: Bundle?) {
            super.onPostMessage(message, extras)
            Log.d(TAG, "onPostMessage: $message")

            try {
                val mapType = object : TypeToken<Map<String, Any>>() {}.type
                val messageMap: Map<String, Any> = Gson().fromJson(message, mapType)
                for ((key, value) in messageMap) {
                    if (key == "event") {
                        EventMessageHandler(context, TAG).handle(value.toString())
                    }
                }
            } catch (e: JsonSyntaxException) {
                Log.e(TAG, "onPostMessage string: $message")
            }
        }

        override fun onMessageChannelReady(extras: Bundle?) {
            Log.d(TAG, "Message channel ready.")

            val message = InitMessage(true, "Android (Custom Tabs)")
            val messageJson = gson.toJson(message)
            val result: Int = customTabsSession!!.postMessage(messageJson, null)

            if (result == CustomTabsService.RESULT_SUCCESS) {
                Log.i(TAG, "postMessage: RESULT_SUCCESS")
            } else Log.e(TAG, "postMessage: RESULT_FAILURE")
        }
    }

}