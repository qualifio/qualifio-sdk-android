# Campaigns

A **campaign** is the interactive Qualifio experience (quiz, contest, survey, and so on)
that you display to your users. The SDK can present campaigns in two ways:

- **[Launched](#launching-a-campaign)** in a
  [Chrome Custom Tab](https://developer.chrome.com/docs/android/custom-tabs) on top of your app.
- **[Inline](#inline-campaigns)** inside your own layout, with `QualifioInlineCampaignView` for
  Views or `QualifioInlineCampaign` for Jetpack Compose.

> Make sure the SDK is [configured](./02-configuration.md) before launching or embedding a campaign.
> To personalize campaigns with signed user data, register a
> [secure token provider](./04-secure-tokens.md) first.

## The campaign parameter

Both ways identify the campaign by its **slug**, passed as the `campaign` parameter. The slug
identifies the campaign within your Qualifio account. The SDK combines it with your configured
domain to build the campaign URL.

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `campaign` | `String` | No | `"sdk"` | The campaign slug to display. |

- When no campaign is given, the default slug `"sdk"` is used. This applies to
  `launchCampaign()`, `loadCampaign()`, `app:qualifioCampaign` and `QualifioInlineCampaign`.
- The slug may contain multiple path segments separated by `/` (for example
  `contests/summer-quiz`).
- A blank slug is rejected: `launchCampaign` throws an `IllegalArgumentException`, and inline
  views ignore it and log `Invalid campaign parameter.`

## Launching a campaign

Call `launchCampaign` with the **slug** of the campaign you want to display. It is a
`suspend` function, so call it from a coroutine:

```kotlin
import com.qualifio.Qualifio
import kotlinx.coroutines.launch

lifecycleScope.launch {
    Qualifio.launchCampaign("summer-quiz")
}
```

### Example: launch from a button (Jetpack Compose)

```kotlin
@Composable
fun PlayButton() {
    val scope = rememberCoroutineScope()

    Button(onClick = {
        scope.launch { Qualifio.launchCampaign("summer-quiz") }
    }) {
        Text("Play the quiz")
    }
}
```

### Launching from Java or with a callback

When calling from Java, or when you would rather not use a coroutine, use the callback
overload. It takes the campaign slug and a [`QualifioCallback`](./04-secure-tokens.md#the-qualifiocallback-interface):

```java
Qualifio.launchCampaign("summer-quiz", new QualifioCallback<Unit>() {
    @Override public void onSuccess(Unit result) { /* launched */ }
    @Override public void onFailure(Exception e) { /* handle the error */ }
});
```

### How the campaign is presented

When you launch a campaign, the SDK:

1. Builds the campaign URL from your configured domain and the campaign slug.
2. Acquires a [secure token](./04-secure-tokens.md), if a provider is registered, and
   appends it to the URL.
3. Opens the URL in a Chrome Custom Tab, applying any
   [Custom Tab customizations](./06-customizations.md) you registered.

The user dismisses the campaign with the Custom Tab's close button or the system back
gesture, returning to your app.

### Considerations

- **Not configured:** if the SDK has not been configured, `launchCampaign` throws an
  `IllegalStateException` (`No configuration set. Please call Qualifio.configure()`). Always
  configure first — see [Configuration](./02-configuration.md).
- **Launch failure:** if no browser can handle the Custom Tab, the call throws. Wrap
  `launchCampaign` in a `try/catch` (or handle `onFailure` in the callback overload) to
  surface problems gracefully.

## Inline campaigns

Use an inline campaign when it should be part of your screen instead of opening in a Custom Tab.
`QualifioInlineCampaignView` (Views) and `QualifioInlineCampaign` (Jetpack Compose) host the
campaign in a WebView. To style the view, see
[Customizations](./06-customizations.md#inline-campaign-appearance).

### Views (XML)

Add the view to an XML layout and set the campaign with `app:qualifioCampaign` (defaults to
`"sdk"`).

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical">

    <com.qualifio.views.QualifioInlineCampaignView
        android:id="@+id/qualifioInlineCampaignView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:qualifioCampaign="summer-quiz" />
</LinearLayout>
```

To show another campaign later, call `loadCampaign()`:

```kotlin
binding.qualifioInlineCampaignView.loadCampaign("winter-quiz")
```

### Views (code)

Create the view in code and add it to a container:

```kotlin
val campaignView = QualifioInlineCampaignView(
    context = context,
    campaign = "summer-quiz",
)

container.addView(
    campaignView,
    ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    ),
)
```

### Jetpack Compose

Use `QualifioInlineCampaign` in your Compose UI. It requires the `qualifio-compose` dependency,
see [Getting Started](./01-getting-started.md#1-add-the-dependency).

```kotlin
@Composable
fun InlineCampaign() {
    QualifioInlineCampaign(
        modifier = Modifier.fillMaxWidth(),
        campaign = "summer-quiz",
        autoScrollOnPageLoadEnabled = false,
    )
}
```

Changing `campaign` loads the new campaign.

### Attributes and properties

| XML attribute | Property | Default | Description |
|---------------|----------|---------|-------------|
| `app:qualifioCampaign` | `campaign` (read-only) | `"sdk"` | The campaign currently loaded, or pending until the view is attached (`null` if none was accepted). Change it with `loadCampaign()`. |
| `app:qualifioAutoScrollOnPageLoadEnabled` | `autoScrollOnPageLoadEnabled` | `true` | When the campaign moves to another page, scrolls the parent so the top of the view is visible. Not applied to the first page. |

### When the campaign loads

- The campaign loads once the view is **attached to a window**, not when it is created. A view
  that is never attached does not request a [secure token](./04-secure-tokens.md) or load anything.
- `loadCampaign()` starts loading immediately if the view is attached. Otherwise it replaces the
  pending campaign, so creating a view and calling `loadCampaign("winter-quiz")` before adding it
  to the layout loads only `winter-quiz`.

### Size and loading state

The inline view sizes its height to the campaign content, so use `wrap_content` for its height.
While a campaign loads, the view takes no space and shows no loader.

### Considerations

- **Not configured:** unlike `launchCampaign`, the inline view does not throw. It logs
  `No configuration set. Please call Qualifio.configure()` and does not load. Configure the SDK
  before creating inline views.

## Next steps

- [Secure Tokens](./04-secure-tokens.md) — personalize campaigns per user.
- [Customizations](./06-customizations.md) — style the Custom Tab or the inline view to match your brand.
- [Notifications](./05-notifications.md) — launch campaigns from Actito notifications.
