# Getting Started

This guide walks you through adding the Qualifio Android SDK to your app and launching your
first campaign.

## Installation

### 1. Add the dependency

The SDK is published to **Maven Central** as an Android library (AAR). Add it to your app
module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.qualifio:qualifio:{{version}}")
}
```

If you are using Jetpack Compose and want to embed inline campaigns, also add the following
dependency:

```kotlin
dependencies {
    implementation("com.qualifio:qualifio-compose:{{version}}")
}
```

> Replace `{{version}}` with the release you want to use (for example the
> [latest published version](https://github.com/qualifio/qualifio-sdk-android/releases)).
> Pin an exact version in production so a new release can never change your app's behavior
> unexpectedly; bump it deliberately when you are ready to upgrade.

Make sure Maven Central is available in your build in `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```

### 2. Minimum SDK

The library requires a `minSdk` of **23** (Android 6.0). Set it in your app module if it is
lower:

```kotlin
android {
    defaultConfig {
        minSdk = 23
    }
}
```

## Your first campaign

Once installed, integrating a campaign takes two steps.

### 1. Configure the SDK

The SDK needs your Qualifio **domain** before it can launch campaigns. The simplest path is
to let it configure **automatically** at app startup from a `qualifio-services.json` file —
see [Configuration](./02-configuration.md) for the full setup. To configure explicitly in
code instead, call `configure` once, as early as possible (for example in your
`Application.onCreate`):

```kotlin
import com.qualifio.Qualifio
import com.qualifio.QualifioConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Qualifio.configure(
            context = this,
            configuration = QualifioConfiguration("your-account.qualifio.com"),
        )
    }
}
```

### 2. Launch a campaign

Call `launchCampaign` with the slug of the campaign you want to display. The SDK opens the
campaign in a Chrome Custom Tab on top of your app:

```kotlin
import com.qualifio.Qualifio
import kotlinx.coroutines.launch

// launchCampaign is a suspend function — call it from a coroutine.
lifecycleScope.launch {
    Qualifio.launchCampaign("summer-quiz")
}
```

`launchCampaign` is a `suspend` function, so call it from a coroutine scope. From Java, or
when you prefer a callback, use the [callback overload](./03-campaigns.md#launching-from-java-or-with-a-callback).

To show the campaign inside your own screen instead, see
[Inline campaigns](./03-campaigns.md#inline-campaigns).

## Next steps

- [Configuration](./02-configuration.md) — automatic vs. manual configuration.
- [Campaigns](./03-campaigns.md) — launch campaigns or embed them inline.
- [Secure Tokens](./04-secure-tokens.md) — personalize campaigns with user data.
- [Customizations](./06-customizations.md) — style the Custom Tab or the inline view to match your brand.
- [Notifications](./05-notifications.md) — trigger campaigns from Actito.
