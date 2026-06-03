# Portal Sample App

A minimal Android app for building on discontinued Meta Portal touch and TV devices. Demonstrates UI controls, runtime permissions, camera preview, and audio recording/playback — preconfigured for Portal hardware constraints.

## Quick Start: Portal Development Skill

The easiest way to work with this project is to load the **portal-development** skill from [hzdb](https://github.com/meta-quest/agentic-tools). It covers Portal hardware constraints, design requirements, toolchain setup, the build/deploy loop, and debugging — all in one self-contained skill:

```bash
npx -y @meta-quest/hzdb --version          # Install hzdb (requires Node.js 20+)
hzdb mcp install <your-tool>               # Connect to your AI coding tool
```

Load it with `/read-skill portal-development` or by mentioning Portal when hzdb's MCP server is connected.

If you prefer to work without the skill, the rest of this file contains everything you need.

## Project Structure

```
PortalSampleApp/
  app/
    src/main/
      java/com/meta/portal/sampleapp/
        MainActivity.kt          # Single-activity Compose app with scrollable showcase
        UiElementsSection.kt     # Material 3 controls: buttons, text field, slider, checkbox, switch, radio, dropdown, card
        PermissionsSection.kt    # Runtime permission requests with granted/denied status indicators
        CameraSection.kt        # CameraX live preview with front/back camera selection
        AudioRecorderSection.kt  # Audio recording (MediaRecorder) and playback (MediaPlayer)
        ui/theme/                # Compose theme (Color.kt, Theme.kt, Type.kt)
      res/
        values/strings.xml       # All user-facing strings
        drawable/                # Launcher icon vectors
      AndroidManifest.xml        # Permissions: CAMERA, LOCATION, CONTACTS, RECORD_AUDIO
    build.gradle.kts             # App module: dependencies, SDK config
  gradle/libs.versions.toml      # Version catalog (Compose, CameraX, etc.)
  build.gradle.kts               # Root build file
  settings.gradle.kts            # Repository and module config
```

## Build & Deploy

Prerequisites: JDK 17, Android Studio, Portal with ADB enabled and USB-C connected.

```bash
./gradlew assembleDebug                                    # Build debug APK
adb install app/build/outputs/apk/debug/app-debug.apk     # Install on Portal
adb shell am start -n com.meta.portal.sampleapp/.MainActivity  # Launch
```

If hzdb is installed:
```bash
hzdb app install app/build/outputs/apk/debug/app-debug.apk
hzdb app launch com.meta.portal.sampleapp
```

## Architecture

Single-activity Compose app. `MainActivity` extends `ComponentActivity` — all UI is Jetpack Compose inside `setContent { }`.

### UI Structure

```
MainActivity (ComponentActivity)
  └─ SampleAppTheme (Material 3)
      └─ Scaffold + TopAppBar
          └─ ShowcaseScreen (vertically scrollable Column)
              ├─ UiElementsSection()
              ├─ PermissionsSection()
              ├─ CameraSection()
              └─ AudioRecorderSection()
```

### Key Composables

| Composable | Purpose |
|---|---|
| `ShowcaseScreen()` | Root scrollable column containing all demo sections |
| `UiElementsSection()` | Interactive Material 3 controls (buttons, text field, slider, checkbox, switch, radio buttons, dropdown, card) |
| `PermissionsSection()` | Requests camera, location, contacts, audio permissions individually or all at once |
| `PermissionRow()` | Reusable row showing permission name, granted/denied badge, and request button |
| `CameraSection()` | CameraX preview with camera enumeration and front/back selection via filter chips |
| `CameraViewfinder()` | AndroidView wrapping CameraX PreviewView with lifecycle-aware binding |
| `AudioRecorderSection()` | State machine for recording audio (MediaRecorder) and playing it back (MediaPlayer) |

## Key Dependencies

| Dependency | Purpose |
|---|---|
| `androidx.activity:activity-compose` | Compose integration with ComponentActivity |
| `androidx.compose.material3` | Material 3 UI components and theming |
| `androidx.camera:camera-*` | CameraX for camera preview (camera-core, camera2, lifecycle, view) |
| `androidx.lifecycle:lifecycle-runtime-ktx` | Lifecycle-aware coroutines |

Versions are managed in `gradle/libs.versions.toml`.

## Supported Portal Devices

| Device | minSdkVersion | Connection |
|---|---|---|
| Portal (1st and 2nd gen) | 28 / 29 | USB-C |
| Portal Mini | 29 | USB-C |
| Portal+ (1st and 2nd gen) | 28 / 29 | USB-C |
| Portal Go | 29 | USB-C (under rubber cover) |
| Portal TV | 29 | USB-C |

## Platform Constraints (Portal)

- **No Google Mobile Services (GMS):** Maps, Sign-In, Push, In-App Billing, Firebase will not function. Use non-GMS alternatives.
- **SDK versions:** Set `minSdkVersion` to 28 and `targetSdkVersion` to 29 for maximum compatibility. Portal runs older AOSP.
- **Contacts/Credentials:** Access to device contacts and device credentials (e.g. Facebook) is not possible.
- **Available features via standard Android permissions:** Camera, Microphone, Speaker, Bluetooth, Network, Touch input & keyboard, Storage write.
- **Storage delete:** Deleting files from the device requires `adb shell rm`; there is no API for this.

## Portal Manifest Requirements

For Portal touch devices, the activity must include:
```xml
<intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.LAUNCHER" />
    <category android:name="android.intent.category.DEFAULT" />
</intent-filter>
```

For Portal TV, replace LAUNCHER with LEANBACK_LAUNCHER:
```xml
<intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.LEANBACK_LAUNCHER" />
    <category android:name="android.intent.category.DEFAULT" />
</intent-filter>
```

## Design Guidelines for Portal

| Requirement | Details |
|---|---|
| **Dark theme** | System overlay (back, home, Wi-Fi) is white with no background — use dark theme or add a dark scrim over the top 64dp |
| **Reserve top 64dp** | System overlay strip floats above your app with no automatic inset — add top padding or use fitsSystemWindows |
| **Touch targets** | Make all touchable elements at least 52dp tall; leave 16dp spacing between targets |
| **Text** | Body: 18sp (Inter); Headings: 24sp Bold; never below 14sp |
| **Color** | Background: `#1A1A1A`. Surfaces: `#2B2B2B`. Primary actions: Meta blue `#0866FF` with near-white text `#F0F0F0`. Body text: `#DADADA`. |
| **Landscape-first** | All touch Portals are landscape; Portal TV is landscape (fixed) |
| **D-pad navigation (Portal TV)** | No touchscreen — every element needs a visible focus state; set nextFocusUp/Down/Left/Right |

## App Icon Requirements

Portal renders icons at 192–280dp using a non-standard launcher:

- Supply a **512×512px (or larger) PNG** in `mipmap-xxxhdpi/`
- **Adaptive icons (`mipmap-anydpi-v26`) are not supported** — use a regular PNG
- Declare `android:icon` on your activity in AndroidManifest.xml
- For Portal TV, use `android:banner` instead — without either, the app won't appear on the home screen
- Portal only reads from `mipmap-xxxhdpi/` (other density folders are ignored)

## Common Modifications

- **Change target device:** Update `minSdkVersion`/`targetSdkVersion` in `app/build.gradle.kts`
- **Add a dependency:** Add to `gradle/libs.versions.toml` under `[libraries]`, reference in `app/build.gradle.kts`
- **Change app name:** Update `android:label` in `AndroidManifest.xml` and `app_name` in `res/values/strings.xml`
- **Change package name:** Update `namespace`/`applicationId` in `app/build.gradle.kts`, `android:name` in manifest, and Kotlin package declarations
- **Add a new section:** Create a new `@Composable` function, add it to `ShowcaseScreen()` in `MainActivity.kt` with a `HorizontalDivider()` separator
- **Support Portal TV:** Add a `LEANBACK_LAUNCHER` intent filter and `android:banner` to the manifest; add D-pad focus handling to all interactive elements
- **Change app icon:** Replace `ic_launcher.webp` in `mipmap-xxxhdpi/` with a 512×512px PNG; remove `mipmap-anydpi-v26` adaptive icon if present

## References

- [Portal development documentation](https://developers.meta.com/horizon/documentation/android-apps/portal-development)
- [Design requirements](https://developers.meta.com/horizon/documentation/android-apps/portal-design-requirements)
- [AI tooling](https://developers.meta.com/horizon/documentation/android-apps/portal-ai-tooling)
- [Agentic tools (hzdb)](https://github.com/meta-quest/agentic-tools)
