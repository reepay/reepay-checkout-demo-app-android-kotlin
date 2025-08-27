package com.billwerk.checkoutsheetdemo

import android.content.Intent
import com.billwerk.checkout.sheet.CheckoutSheet
import com.billwerk.checkout.sheet.CheckoutSheetConfig

class IntentHandler(private val checkoutSheet: CheckoutSheet) {
    fun handleIncomingAppRedirect(intent: Intent, config: CheckoutSheetConfig) {
        if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data

            val returnUrl = uri?.getQueryParameter("returnUrl")

            if (returnUrl != null) {
                checkoutSheet.presentCheckoutReturnUrl(config, returnUrl)
            }
        }
    }
}