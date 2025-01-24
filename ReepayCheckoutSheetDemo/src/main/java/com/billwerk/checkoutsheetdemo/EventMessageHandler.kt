package com.billwerk.checkoutsheetdemo

import android.content.Context
import android.content.Intent
import android.util.Log
import com.billwerk.checkoutsheetdemo.TrustedWebActivityLauncher.Companion.isTwaRefreshed

class EventMessageHandler(private val context: Context, private val tag: String) {

    fun handle(event: String) {
        Log.i(tag, "Event received: $event")

        when (event) {
            "Init" -> Log.i(tag, "Checkout started")
            "Open" -> Log.i(tag, "Checkout opened")
            "Close" -> {
                Log.i(tag, "Checkout closed")
                isTwaRefreshed = true
            }

            "Accept" -> {
                Log.i(tag, "Checkout accept")
                close()
            }

            "Cancel" -> {
                Log.d(tag, "Checkout cancel")
                close()
            }

            "Error" -> Log.d(tag, "Error occurred")
            else -> Log.d(tag, "Unhandled event: $event")
        }
    }

    private fun close() {
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(mainIntent)
    }
}