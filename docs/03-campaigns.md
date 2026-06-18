# Campaigns

A **campaign** is the interactive Qualifio experience (quiz, contest, survey, and so on)
that you display to your users. The SDK presents campaigns in a
[Chrome Custom Tab](https://developer.chrome.com/docs/android/custom-tabs) launched on top
of your app, so the user stays within your experience without switching to a separate
browser app.

> Make sure the SDK is [configured](./02-configuration.md) before launching a campaign.

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

The slug identifies the campaign within your Qualifio account. The SDK combines it with
your configured domain to build the campaign URL and opens it in the Custom Tab.

### The campaign parameter

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `campaign` | `String` | No | `"sdk"` | The campaign slug to display. |

- If you call `launchCampaign()` with **no argument**, the default slug `"sdk"` is used.
- The slug may contain multiple path segments separated by `/` (for example
  `contests/summer-quiz`).

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
overload. It takes the campaign slug and a [`QualifioCallback`](./04-secure-tokens.md):

```java
Qualifio.launchCampaign("summer-quiz", new QualifioCallback<Unit>() {
    @Override public void onSuccess(Unit result) { /* launched */ }
    @Override public void onFailure(Exception e) { /* handle the error */ }
});
```

## How the campaign is presented

When you launch a campaign, the SDK:

1. Builds the campaign URL from your configured domain and the campaign slug.
2. Acquires a [secure token](./04-secure-tokens.md), if a provider is registered, and
   appends it to the URL.
3. Opens the URL in a Chrome Custom Tab, applying any
   [Custom Tab customizations](./06-customizations.md) you registered.

The user dismisses the campaign with the Custom Tab's close button or the system back
gesture, returning to your app.

## Considerations

- **Not configured:** if the SDK has not been configured, `launchCampaign` throws an
  `IllegalStateException` (`No configuration set. Please call Qualifio.configure()`). Always
  configure first — see [Configuration](./02-configuration.md).
- **Launch failure:** if no browser can handle the Custom Tab, the call throws. Wrap
  `launchCampaign` in a `try/catch` (or handle `onFailure` in the callback overload) to
  surface problems gracefully.
- **Personalization:** to attach signed user data to the campaign, register a secure token
  provider before launching — see [Secure Tokens](./04-secure-tokens.md).

## Next steps

- [Secure Tokens](./04-secure-tokens.md) — personalize campaigns per user.
- [Customizations](./06-customizations.md) — style the Custom Tab to match your brand.
- [Notifications](./05-notifications.md) — launch campaigns from Actito notifications.
