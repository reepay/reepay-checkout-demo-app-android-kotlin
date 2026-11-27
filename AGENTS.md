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

To build against a local SDK checkout, enable Option 2 in `settings.gradle.kts` and set `projectDir` to the local `checkout` module. In `ReepayCheckoutSheetDemo/build.gradle.kts`, replace the JitPack dependency with `implementation(project(":checkout"))`. Keep JitPack as the committed default.

## Build

Read the toolchain from the Gradle files and keep it as declared there. Do not bump AGP, Kotlin, Gradle, SDK levels, or the JVM target unless the task is that upgrade.

- Plugin versions: root `build.gradle.kts`
- `minSdk`, `compileSdk`, `targetSdk`, and the JVM target: `ReepayCheckoutSheetDemo/build.gradle.kts`
- Gradle wrapper: `gradle/wrapper/gradle-wrapper.properties`
- Kotlin style: `kotlin.code.style` in `gradle.properties`

Debug and release both minify with R8. Update `ReepayCheckoutSheetDemo/proguard-rules.pro` when you add reflection or a JavaScript interface.

```bash
./gradlew :ReepayCheckoutSheetDemo:assembleDebug
```

There is no test source set yet. Test dependencies are already declared.

## Do not

- Commit `local.properties`, keystores, or `build/` output.
- Introduce Jetpack Compose, or change which SDK dependency option is committed, unless the task asks for that.
