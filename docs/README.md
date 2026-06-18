# Qualifio Android SDK — Documentation

The Qualifio Android SDK lets you embed and launch Qualifio campaigns directly inside your
Android application, presenting them in a Chrome Custom Tab on top of your app without
sending the user to a separate browser app.

This documentation is organized by topic. If you are integrating the SDK for the first
time, start with **Getting Started**.

## Table of contents

| Page | Description |
|------|-------------|
| [Getting Started](./01-getting-started.md) | Add the SDK with Gradle and launch your first campaign. |
| [Configuration](./02-configuration.md) | Configure the SDK with your Qualifio domain (automatic or manual). |
| [Campaigns](./03-campaigns.md) | Launch campaigns. |
| [Secure Tokens](./04-secure-tokens.md) | Personalize campaigns with signed user data. |
| [Notifications](./05-notifications.md) | Display campaigns triggered by Actito remote notifications and inbox messages. |
| [Customizations](./06-customizations.md) | Style the Custom Tab and control the SDK's logging. |

## Requirements

- Android **API level 23** (Android 6.0) or higher.
- A device or emulator with a browser that supports
  [Chrome Custom Tabs](https://developer.chrome.com/docs/android/custom-tabs) (Chrome and
  most modern Android browsers).
- A Qualifio account and at least one published campaign.

## At a glance

```kotlin
import com.qualifio.Qualifio
import com.qualifio.QualifioConfiguration

// 1. Configure once, at application startup.
Qualifio.configure(context, QualifioConfiguration("your-account.qualifio.com"))

// 2. Launch a campaign whenever you need to (from a coroutine).
Qualifio.launchCampaign("my-campaign-slug")
```

## Support

For account setup, campaign creation, and platform questions, visit
[qualifio.com](https://qualifio.com) or contact your Qualifio representative.
