package com.billwerk.checkoutsheetdemo

import android.content.Intent
import android.net.Uri
import com.billwerk.checkout.CheckoutSheet
import com.billwerk.checkout.CheckoutSheetConfig

class IntentHandler(private val checkoutSheet: CheckoutSheet) {
    fun handleIncomingAppRedirect(intent: Intent, config: CheckoutSheetConfig) {
        if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data
            if (uri != null) {
                val returnUrl = extractReturnUrl(uri.toString())
                if (returnUrl != null) {
                    checkoutSheet.presentCheckoutReturnUrl(config, returnUrl)
                } else {
                    checkoutSheet.open(config)
                }
            }
        }
    }

    private fun extractReturnUrl(url: String): String? {
        val uri = Uri.parse(url)
        return uri.getQueryParameter("returnUrl")
    }
}