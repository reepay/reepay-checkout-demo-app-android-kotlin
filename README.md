⚠️ This SDK is under development and not yet officially supported.

---

# billwerk-checkout-sdk-test

This demo app demonstrates how to build a checkout flow using CheckoutSheet, an embeddable component that currently supports card payments with a single integration

## Getting Started

### Option 1: Load CheckoutSheet dependency through Jitpack

(This is the default configuration)

Make sure the `dependencyResolutionManagement` code block is uncommented in `settings.gradle.kts`

Uncomment the Checkout SDK dependency in `build.gradle.kts`, and replace `TAG` with the desired version number (e.g. `1.0.2`)

```
implementation("com.github.reepay:reepay-android-checkout-sheet:TAG")
```

Run the project

### Option 2: Load CheckoutSheet manually

Clone https://github.com/reepay/reepay-android-checkout-sheet to your computer

Copy the absolute path to the repository

In `settings.gradle.kts`, uncomment the lines and insert the correct path to the repository:
```
include(":checkout")
project(":checkout").projectDir = file("/PATH/TO/reepay-android-checkout-sheet/checkout")
```

Uncomment the dependency in `build.gradle.kts`
```
implementation(project(":checkout"))
```
