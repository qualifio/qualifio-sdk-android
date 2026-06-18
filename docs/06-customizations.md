# Customizations

The SDK can be tailored to your application in two ways: customizing the **Custom Tab** that
presents the campaign so it matches your brand, and controlling the **logging** verbosity to
suit development or production. Both are optional — the SDK works with sensible defaults out
of the box.

- [Custom Tab appearance](#custom-tab-appearance) — style the browser overlay that shows the campaign.
- [Logging](#logging) — control the SDK's log output.

---

## Custom Tab appearance

Campaigns are presented in a [Chrome Custom Tab](https://developer.chrome.com/docs/android/custom-tabs).
Because the campaign content itself is hosted and styled by Qualifio, customization on the
app side applies to the **Custom Tab chrome** — the toolbar color, sharing options, animations,
and similar — through the standard `CustomTabsIntent.Builder` API.

Register a customizer with `setCustomTabsCustomizer`. The SDK invokes it with the builder it
uses to launch each campaign, so any options you set are applied:

```kotlin
import androidx.core.content.ContextCompat
import androidx.browser.customtabs.CustomTabsIntent
import com.qualifio.Qualifio

Qualifio.setCustomTabsCustomizer { builder ->
    val colors = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(ContextCompat.getColor(context, R.color.brand))
        .build()

    builder
        .setDefaultColorSchemeParams(colors)
        .setUrlBarHidingEnabled(true)
        .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
}
```

The customizer type is:

```kotlin
typealias QualifioCustomTabsCustomizer = (builder: CustomTabsIntent.Builder) -> Unit
```

| Aspect | Details |
|--------|---------|
| Argument | `builder` — the `CustomTabsIntent.Builder` the SDK will use to launch the campaign. |
| When | Invoked on every `launchCampaign`, just before the Custom Tab opens. |

> The SDK always calls `setShowTitle(true)` on the builder before invoking your customizer,
> so the campaign page title is shown by default. Anything you set in the customizer is
> applied on top and can override it.

Pass `null` to `setCustomTabsCustomizer` to remove a previously registered customizer.

---

## Logging

The SDK writes diagnostic messages to **Logcat** under the tag `Qualifio`. This is useful
while integrating — for example to confirm that configuration succeeded or to see why a
campaign did not launch.

### Enabling debug logging

By default, the SDK logs at **info** level and above (info, warnings, and errors). Verbose
**debug** messages are suppressed unless you opt in with a manifest metadata flag, inside
the `<application>` element of your `AndroidManifest.xml`:

```xml
<application>
    <meta-data
        android:name="com.qualifio.debug_logging_enabled"
        android:value="true" />
</application>
```

| Metadata name | Type | Default | Effect |
|---------------|------|---------|--------|
| `com.qualifio.debug_logging_enabled` | boolean | `false` | When `true`, debug-level messages are emitted (and prefixed with the originating class). When `false`, only info, warnings, and errors are logged. |

### Recommendation

- **Development:** set `com.qualifio.debug_logging_enabled` to `true` to see the full
  picture while integrating.
- **Production:** leave it at its default (`false`) to keep the log output to a minimum
  while still surfacing warnings and errors.

> Filter Logcat by the `Qualifio` tag to isolate the SDK's output.
