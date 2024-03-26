⚠️ This SDK is under development and not yet officially supported.

---

# billwerk-checkout-sdk-test
 
This demo app demonstrates how to build a checkout flow using CheckoutSheet, an embeddable component that currently supports card payments with a single integration

## Getting Started

### Option 1: Load CheckoutSheet dependency through Jitpack

Request a personal access token (`JITPACK_SECRET`) to Billwerk's Jitpack and store it as an environment variable

Make sure the `dependencyResolutionManagement` code block is uncommented in `settings.gradle.kts` 

Uncomment the dependency in `build.gradle.kts`:

```
implementation("com.github.reepay:reepay-checkout-demo-app-android-kotlin:TAG")
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
