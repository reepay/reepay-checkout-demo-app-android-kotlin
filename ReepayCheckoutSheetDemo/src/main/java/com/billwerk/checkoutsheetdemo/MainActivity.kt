package com.billwerk.checkoutsheetdemo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.billwerk.checkout.CheckoutEventPublisher
import com.billwerk.checkout.CheckoutSheet
import com.billwerk.checkout.CheckoutSheetConfig
import com.billwerk.checkout.SheetStyle
import com.billwerk.checkout.sheet.SDKEventMessage
import com.billwerk.checkout.sheet.SDKEventType
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var checkoutSheet: CheckoutSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val payButton: Button = findViewById(R.id.pay_button)

        // Initialize Checkout Sheet
        this.checkoutSheet = CheckoutSheet(this)

        // Example configuration
        val config = CheckoutSheetConfig(
            sessionId = "",
            sheetStyle = SheetStyle.FULL_SCREEN,
            dismissible = true,
            hideHeader = true,
            closeButtonIcon = R.drawable.button_close_icon,
            closeButtonText = R.string.close_button_text
        )

        // Open checkout sheet
        payButton.setOnClickListener {
            this.checkoutSheet.open(config)
        }

        // Subscribe to events
        listenForEvents()

        val intentHandler = IntentHandler(checkoutSheet)
        intentHandler.handleIncomingAppRedirect(intent, config)

    }

    private fun listenForEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                CheckoutEventPublisher.events.collect { message ->
                    handleEvents(message)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                CheckoutEventPublisher.userEvents.collect { message ->
                    Log.d("MyApp", "Colleted user event: ${message.event}")
                }
            }
        }
    }

    private fun handleEvents(message: SDKEventMessage) {
        val eventType: SDKEventType = message.event
        Log.d("MyApp", "Collected event: $eventType")

        when (eventType) {
            SDKEventType.Accept -> {
                Log.d("MyApp", "Invoice handle: ${message.data?.invoice}")
                Log.d("MyApp", "Customer handle: ${message.data?.customer}")
                Log.d("MyApp", "Payment method id: ${message.data?.payment_method}")
            }

            SDKEventType.Error -> {
                Log.d("MyApp", "Error type: ${message.data?.error}")
            }

            SDKEventType.Cancel -> {
                checkoutSheet.dismiss()
            }

            SDKEventType.Close -> {}
            SDKEventType.Open -> {}
            SDKEventType.Init -> {}
            else -> {
                Log.d("MyApp", "Unknown event: $eventType")
            }
        }
    }
}