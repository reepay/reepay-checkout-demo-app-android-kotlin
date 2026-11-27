# AGENTS.md

Instructions for coding agents working in this repository.

## Android skills

Use the installed [Android Skills](https://github.com/android/skills) plugin when a task matches one of its skills. Do not copy those skills into this repository.

## What this is

Sample app for the Frisbii Checkout Sheet SDK, `com.github.reepay:reepay-android-checkout-sheet`. Checkout is hosted at `https://checkout.frisbii.com`. The app shows three ways to open it: the SDK sheet, a local WebView sheet, and a Chrome Custom Tab.

Packages stay `com.billwerk.checkoutsheetdemo`. Do not rename them unless asked.

## Layout

The only module is `ReepayCheckoutSheetDemo`. UI is Views and XML under `res/layout`, not Compose.

- `MainActivity` wires the three entry buttons, builds `CheckoutSheetConfig`, and collects SDK events.
- `CheckoutSheet` comes from the SDK. Events arrive on `CheckoutEventPublisher.events` and `CheckoutEventPublisher.userEvents`.
- `MyWebView` and `MyWebViewListener` are the in-app WebView sheet, separate from the SDK.
- `CustomTabLauncher` opens checkout in a Custom Tab. `IntentHandler` resumes the SDK sheet after an app redirect (`reepaydemoapp` scheme).

`sessionId` in `MainActivity` is an empty local placeholder. Do not commit a real session id.

## SDK dependency

Committed default is JitPack: `settings.gradle.kts` includes `https://jitpack.io`, and the app depends on `com.github.reepay:reepay-android-checkout-sheet`.

To build against a local checkout of `reepay-android-checkout-sheet`, use the commented Option 2 in `settings.gradle.kts` and `ReepayCheckoutSheetDemo/build.gradle.kts`. Leave Option 1 as the committed default.

## Build

AGP 9.5.2, Kotlin 2.4.21, Gradle 9.9.1. minSdk 26, compileSdk 37, targetSdk 36, JVM target 11. `kotlin.code.style=official`.

Debug and release both minify with R8. Update `ReepayCheckoutSheetDemo/proguard-rules.pro` when you add reflection or a JavaScript interface.

```bash
./gradlew :ReepayCheckoutSheetDemo:assembleDebug
```

There is no test source set yet. Test dependencies are already declared.

## Do not

- Commit `local.properties`, keystores, or `build/` output.
- Introduce Jetpack Compose, or change which SDK dependency option is committed, unless the task asks for that.
