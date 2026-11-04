# Configuration

Before you can launch or embed any campaign, the SDK must be configured with your Qualifio
**domain**. Configuration tells the SDK where to load campaigns from.

You only need to configure the SDK **once** per process. Calling `configure` a second time
logs `Qualifio can only be configured once.` and is otherwise ignored.

There are two ways to configure the SDK: **automatically** from a `qualifio-services.json`
file (recommended), or **manually** in code.

## Automatic configuration

If you would rather not hard-code the domain in your application, add a
`qualifio-services.json` file and let the SDK configure itself at app startup.

Create the file at the root of your app module:

```json
// app/qualifio-services.json
{
  "domain": "your-account.qualifio.com"
}
```

The SDK's Gradle plugin reads this file at build time and turns it into an Android string
resource. Apply the plugin in your app module's `build.gradle.kts`:

```kotlin
plugins {
    id("com.qualifio.gradle.qualifio-services") version "{{version}}"
}
```

> Replace `{{version}}` with the latest published version of the plugin — see the
> [qualifio-services-gradle-plugin releases](https://github.com/qualifio/qualifio-services-gradle-plugin/releases).

At startup, the SDK's bundled content provider detects the generated resource and calls
`configure` for you — no code required. This is useful when the same build is deployed
across multiple flavors or environments, each supplying a different `qualifio-services.json`.

Automatic configuration is **enabled by default**. You can turn it off with a manifest
metadata flag (see [Manifest metadata](#manifest-metadata) below), for example when you
need to resolve the domain at runtime and want to call `configure` yourself.

## Manual configuration

Call `configure` with a `QualifioConfiguration`, as early as possible in your application's
lifecycle (for example in `Application.onCreate`):

```kotlin
import com.qualifio.Qualifio
import com.qualifio.QualifioConfiguration

Qualifio.configure(
    context = context,
    configuration = QualifioConfiguration("your-account.qualifio.com"),
)
```

This is the right approach when the domain is only known at runtime — for example when it
depends on a remote setting. Disable automatic configuration (see below) so the two paths
don't compete.

### The `domain` field

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `domain` | `String` | Yes | The Qualifio domain that serves your campaigns, e.g. `your-account.qualifio.com`. |

## Checking whether the SDK is configured

Use `isConfigured` to find out whether the SDK has already been configured:

```kotlin
if (!Qualifio.isConfigured) {
    Qualifio.configure(context, QualifioConfiguration("your-account.qualifio.com"))
}
```

The current configuration is also available through `Qualifio.configuration`, which is
`null` until the SDK is configured.

## Manifest metadata

The SDK reads a few optional `<meta-data>` flags from your `AndroidManifest.xml`, inside
the `<application>` element:

```xml
<application>
    <meta-data
        android:name="com.qualifio.auto_configuration_enabled"
        android:value="false" />
</application>
```

| Metadata name | Type | Default | Purpose |
|---------------|------|---------|---------|
| `com.qualifio.auto_configuration_enabled` | boolean | `true` | Whether the SDK configures itself from `qualifio-services.json` at startup. Set to `false` to configure manually. |
| `com.qualifio.debug_logging_enabled` | boolean | `false` | Enables verbose debug logging — see [Customizations](./06-customizations.md#logging). |

## Next steps

- [Campaigns](./03-campaigns.md) — launch or embed a campaign once configured.
- [Secure Tokens](./04-secure-tokens.md) — attach signed user data to campaigns.
