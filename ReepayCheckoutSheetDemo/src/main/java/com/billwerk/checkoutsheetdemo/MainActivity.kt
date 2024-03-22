package com.billwerk.checkoutsheetdemo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.billwerk.checkout.CheckoutEvent
import com.billwerk.checkout.CheckoutSheet
import com.billwerk.checkout.CheckoutSheetConfig
import com.billwerk.checkout.SheetStyle
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val payButton: Button = findViewById(R.id.pay_button)

        // Initialize Checkout Sheet
        val checkoutSheet = CheckoutSheet(this)

        // Example configuration
        val config = CheckoutSheetConfig(
            sessionId = "",
            acceptURL = "", // Has to be identical to the one defined in the checkout session
            cancelURL = "",
            sheetStyle = SheetStyle.FULL_SCREEN,
            dismissible = true
        )

        // Open checkout sheet
        payButton.setOnClickListener {
            checkoutSheet.open(config)
        }

        // Subscribe to events
        listenForEvents()

    }

    private fun listenForEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                CheckoutEvent.events.collect { eventType ->
                    Log.d("MyApp", "Collected event ${eventType}")
                }
            }
        }
    }
}