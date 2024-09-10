package com.billwerk.checkoutsheetdemo

import android.content.Intent
import com.billwerk.checkout.CheckoutSheet
import com.billwerk.checkout.CheckoutSheetConfig

class IntentHandler(private val checkoutSheet: CheckoutSheet) {
    fun handleIncomingAppRedirect(intent: Intent, config: CheckoutSheetConfig) {
        if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data
            if (uri != null) {
                checkoutSheet.open(config)
            }
        }
    }
}