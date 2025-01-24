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
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import com.billwerk.checkoutsheetdemo.CustomTabLauncher.Companion.TARGET_ORIGIN
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

private val TAG = "TwaLauncher" // Trusted Web Activity

class TrustedWebActivityLauncher(private val context: Context) {

    companion object {
        var customTabsSession: CustomTabsSession? = null
        var isTwaRefreshed: Boolean = false
    }

    private val gson = Gson()

    private var customTabsServiceConnection: CustomTabsServiceConnection? = null
    private var relationshipValidated: Boolean = false
    private var eventMessageHandler: EventMessageHandler = EventMessageHandler(context, TAG)

    fun launchTwa(url: String, afterLaunch: (CustomTabsSession) -> Unit = {}) {
        customTabsServiceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                client.warmup(0L)

                customTabsSession = client.newSession(customTabsCallback)
                customTabsSession?.let {
                    val uri = Uri.parse(url)

                    TrustedWebActivityIntentBuilder(uri)
                        .build(it)
                        .launchTrustedWebActivity(context)

                    afterLaunch(it)
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {
                customTabsSession = null
                Log.d(TAG, "TWA disconnected.")
            }
        }

        val packageName = CustomTabsClient.getPackageName(context, null)
        if (packageName == null) {
            Log.e(TAG, "Chrome not found on the device.")
            return
        }

        val isBound = CustomTabsClient.bindCustomTabsService(
            context,
            packageName,
            customTabsServiceConnection!!
        )

        if (!isBound) {
            Log.e(TAG, "Failed to bind to Custom Tabs service")
        }
    }

    private val customTabsCallback: CustomTabsCallback = object : CustomTabsCallback() {
        override fun onRelationshipValidationResult(
            relation: Int, requestedOrigin: Uri,
            result: Boolean, extras: Bundle?
        ) {
            Log.d(TAG, "Requested Origin: $requestedOrigin")
            Log.d(TAG, "Relationship result: $result")
            relationshipValidated = result
        }

        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
            Log.d(TAG, "Navigation: $navigationEvent")

            if (!relationshipValidated) {
                if (navigationEvent != NAVIGATION_FINISHED) {
                    return
                }

                val result = customTabsSession?.requestPostMessageChannel(TARGET_ORIGIN)
                Log.d(TAG, "Requested PostMessage Channel: $result")
            }

            if (navigationEvent == NAVIGATION_FINISHED) {
                // Re-establish communication channel with Checkout on TWA refresh
                if (isTwaRefreshed) {
                    val result = customTabsSession?.requestPostMessageChannel(TARGET_ORIGIN)
                    Log.d(TAG, "Requested PostMessage Channel: $result")

                    val message = InitMessage(true, "Android (Trusted Web Activity)")
                    val messageJson = gson.toJson(message)
                    val isMessageSent = customTabsSession!!.postMessage(messageJson, null)
                    Log.i(
                        TAG,
                        "Re-init Checkout: ${isMessageSent == CustomTabsService.RESULT_SUCCESS}"
                    )

                    isTwaRefreshed = false
                }
            }
        }

        override fun onPostMessage(message: String, extras: Bundle?) {
            super.onPostMessage(message, extras)
            Log.d(TAG, "onPostMessage: $message")

            try {
                val mapType = object : TypeToken<Map<String, Any>>() {}.type
                val messageMap: Map<String, Any> = Gson().fromJson(message, mapType)
                for ((key, value) in messageMap) {
                    if (key == "event") {
                        eventMessageHandler.handle(value.toString())
                    }
                }
            } catch (e: JsonSyntaxException) {
                Log.e(TAG, "onPostMessage string: $message")
            }
        }

        override fun onMessageChannelReady(extras: Bundle?) {
            Log.d(TAG, "Message channel ready.")

            val message = InitMessage(true, "Custom Tabs / TWA")
            val messageJson = gson.toJson(message)
            val result = customTabsSession?.postMessage(messageJson, null)

            if (result == CustomTabsService.RESULT_SUCCESS) {
                Log.i("CustomTabLauncher", "postMessage: RESULT_SUCCESS")
            } else Log.e("CustomTabLauncher", "postMessage: RESULT_FAILURE")
        }
    }
}