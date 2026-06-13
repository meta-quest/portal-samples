# Portal Sample App

A minimal Android application for Meta Portal touch and TV devices. This sample demonstrates how to structure a Portal project, follow UI guidelines, and use device features (camera, microphone) with correct SDK configuration for Portal hardware.

## Quick Start with AI Tooling

The easiest way to get started is with the **portal** skill — it gives your AI coding tool full knowledge of Portal constraints, design requirements, and the build/deploy loop, so you can just describe what you want to build. The skill runs on the Meta VR CLI.

- **Use the skill:** get the **portal** skill from [agentic-tools](https://github.com/meta-quest/agentic-tools). The first time you use it, it auto-installs the Meta VR CLI for you.
- **Don't want the Meta VR CLI?** It's required for the skill, so build manually instead — see [Getting Started](#getting-started) below.

## Getting Started

1. Set up your Portal device with ADB enabled and connect via USB-C
2. Open this project in your preferred IDE
3. Build and run the app

For full setup instructions, see the [Portal development documentation](https://developers.meta.com/horizon/documentation/android-apps/portal-development/).

```bash
# Build the debug APK
./gradlew assembleDebug

# Install on your connected Portal
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Project Structure

- `app/src/main/java/com/meta/portal/sampleapp/`
  - `MainActivity.kt` - Entry point with Scaffold layout and top app bar
  - `UiElementsSection.kt` - Material 3 UI controls (buttons, text field, slider, checkbox, switch, radio buttons, dropdown, card)
  - `PermissionsSection.kt` - Runtime permission requests (camera, location, contacts, audio) with status indicators
  - `CameraSection.kt` - CameraX live preview with front/back camera selection
  - `AudioRecorderSection.kt` - Audio recording and playback using MediaRecorder/MediaPlayer
  - `ui/theme/` - Compose theme configuration (colors, typography, theme)
- `app/src/main/res/` - Android resources (layouts, strings, drawables)
- `app/src/main/AndroidManifest.xml` - App manifest with camera, location, contacts, and audio permissions

## Portal Design Requirements

Portal devices have specific design requirements due to their form factor (tabletop displays and TV). Review the [design requirements documentation](https://developers.meta.com/horizon/documentation/android-apps/portal-design-requirements), or use our AI tooling to ensure that whatever you build is compatible with Portal hardware.

## References

- [Portal development documentation](https://developers.meta.com/horizon/documentation/android-apps/portal-development/)
- [Design requirements](https://developers.meta.com/horizon/documentation/android-apps/portal-design-requirements)
- [AI tooling](https://developers.meta.com/horizon/documentation/android-apps/portal-ai-tooling)
- [Agentic tools (Meta VR CLI)](https://github.com/meta-quest/agentic-tools)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
