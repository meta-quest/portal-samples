# Portal Sample App

A minimal Android application for Meta Portal touch and TV devices. This sample demonstrates how to structure a Portal project, follow UI guidelines, and use device features (camera, microphone) with correct SDK configuration for Portal hardware.

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

## AI Tooling

[Horizon Debug Bridge (hzdb)](https://github.com/meta-quest/agentic-tools) wraps ADB with higher-level commands and includes an MCP server that connects directly to your AI coding tool:

```bash
npx -y @meta-quest/hzdb --version     # Run without installing
npm install -g @meta-quest/hzdb        # Or install globally
hzdb mcp install <your-tool>           # Connect to your AI coding tool
```

Supported tools: `claude-code`, `cursor`, `vscode`, `android-studio`, `codex`, `zed`, `windsurf`, `gemini-cli`. Run `hzdb mcp install --help` for the full list.

This repo includes an `AGENTS.md` file that AI coding tools read automatically to understand Portal constraints and how to work with the project.

## References

- [Portal development documentation](https://developers.meta.com/horizon/documentation/android-apps/portal-development/)
- [Horizon OS design requirements](https://developers.meta.com/horizon/documentation/android-apps/portal-design-requirements)
- [Agentic tools (hzdb)](https://github.com/meta-quest/agentic-tools)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
