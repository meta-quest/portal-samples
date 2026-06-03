# Meta Portal Android App Style Guide

A prescriptive reference for building Jetpack Compose apps that conform to Meta Horizon OS and Portal design requirements. Follow every rule here exactly — deviations produce visible inconsistencies on the device.

---

## 1. Theme setup

### Always force dark theme
Portal renders a white system overlay on top of apps. Use a dark background so content remains visible underneath it.

```kotlin
// MainActivity.kt
setContent {
    SampleAppTheme(darkTheme = true) { ... }
}
```

### Disable dynamic color
On Android 12+, `dynamicColor = true` silently overrides every custom color in your theme with OS-generated wallpaper colors. Always set it to `false`.

```kotlin
@Composable
fun SampleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,   // NEVER set true — kills all custom colors on Android 12+
    content: @Composable () -> Unit
) { ... }
```

---

## 2. Color palette

Define these exact values in `Color.kt`. Do not use pure `#000000` or `#FFFFFF` — Meta Horizon guidelines prohibit them.

```kotlin
// Primary — Meta blue
val MetaBlue         = Color(0xFF0866FF)   // button fill, primary actions
val MetaBlueLight    = Color(0xFFD4E3FF)   // primaryContainer in light theme
val MetaBlueDark     = Color(0xFFA8C8FF)   // unused in Portal (dark always), kept for completeness
val MetaBlueDarkCont = Color(0xFF004CB0)   // primaryContainer in dark theme
val OnMetaBlue       = Color(0xFFF0F0F0)   // text/icon ON a MetaBlue surface
val OnMetaBlueLight  = Color(0xFF001A41)
val OnMetaBlueDark   = Color(0xFF003580)
val OnMetaBlueDarkC  = Color(0xFFD4E3FF)

// Backgrounds — no pure white or black
val BackgroundLight  = Color(0xFFF0F0F0)
val SurfaceLight     = Color(0xFFE6E6E6)
val BackgroundDark   = Color(0xFF1A1A1A)   // primary app background on Portal
val SurfaceDark      = Color(0xFF2B2B2B)   // cards, surfaces
val ContentOnLight   = Color(0xFF1A1A1A)
val ContentOnDark    = Color(0xFFDADADA)   // body text in dark theme

// Secondary / neutral
val NeutralGrey      = Color(0xFF565F71)
val NeutralGreyDark  = Color(0xFFBEC6DC)
```

### Color scheme mappings

```kotlin
private val LightColorScheme = lightColorScheme(
    primary            = MetaBlue,        onPrimary           = OnMetaBlue,
    primaryContainer   = MetaBlueLight,   onPrimaryContainer  = OnMetaBlueLight,
    secondary          = NeutralGrey,     onSecondary         = OnMetaBlue,
    background         = BackgroundLight, surface             = SurfaceLight,
    onBackground       = ContentOnLight,  onSurface           = ContentOnLight,
)

// Portal always uses this scheme (darkTheme = true)
private val DarkColorScheme = darkColorScheme(
    primary            = MetaBlue,        onPrimary           = OnMetaBlue,
    primaryContainer   = MetaBlueDarkCont,onPrimaryContainer  = OnMetaBlueDarkC,
    secondary          = NeutralGreyDark, onSecondary         = OnMetaBlue,
    background         = BackgroundDark,  surface             = SurfaceDark,
    onBackground       = ContentOnDark,   onSurface           = ContentOnDark,
)
```

> **Key rule:** `primary = MetaBlue` and `onPrimary = OnMetaBlue` in BOTH schemes. Buttons are always Meta blue (`#0866FF`) with near-white (`#F0F0F0`) text — never the M3 tonal pastel approach.

---

## 3. Typography

### Font: Inter via XML downloadable fonts

Do **not** use the `ui-text-google-fonts` library. Use XML downloadable font resources instead — they require no extra dependency beyond the GMS fonts provider already on the device.

Create three files in `res/font/`:

**`res/font/inter.xml`**
```xml
<?xml version="1.0" encoding="utf-8"?>
<font-family xmlns:app="http://schemas.android.com/apk/res-auto"
    app:fontProviderAuthority="com.google.android.gms.fonts"
    app:fontProviderPackage="com.google.android.gms"
    app:fontProviderQuery="Inter"
    app:fontProviderCerts="@array/com_google_android_gms_fonts_certs">
</font-family>
```

**`res/font/inter_medium.xml`** — query `name=Inter&amp;weight=500`

**`res/font/inter_bold.xml`** — query `name=Inter&amp;weight=700`

You also need `res/values/font_certs.xml` containing the `com_google_android_gms_fonts_certs` array with Google's dev and prod certificate strings (standard boilerplate — copy from the Android downloadable fonts documentation).

### FontFamily declaration

```kotlin
private val InterFontFamily = FontFamily(
    Font(R.font.inter,        weight = FontWeight.Normal),
    Font(R.font.inter_medium, weight = FontWeight.Medium),
    Font(R.font.inter_bold,   weight = FontWeight.Bold),
)
```

Register all three weights so the system loads genuine glyphs instead of synthesising fake bold.

### Type scale

```kotlin
val Typography = Typography(
    headlineSmall = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold,   fontSize = 24.sp, lineHeight = 32.sp),
    titleMedium   = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Bold,   fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge     = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium,  fontSize = 18.sp, lineHeight = 28.sp),
    bodyMedium    = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium,  fontSize = 16.sp, lineHeight = 24.sp),
    bodySmall     = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium,  fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge    = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium,  fontSize = 16.sp, lineHeight = 24.sp),
    labelMedium   = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium,  fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall    = TextStyle(fontFamily = InterFontFamily, fontWeight = FontWeight.Medium,  fontSize = 14.sp, lineHeight = 20.sp),
)
```

**Rules:**
- Minimum font size: **14sp**. Never go smaller.
- Preferred body size: **18sp**.
- Use only Bold, Medium, or Normal weights — never Light or Thin (they are illegible on the device).
- Use weight to communicate hierarchy: Bold for headings, Medium for body and labels.

---

## 4. Buttons and interactive elements

### Hit targets
Every tappable element must have a minimum touch area of **52dp tall**. Apply with:

```kotlin
Modifier.heightIn(min = 52.dp)
```

Do not set a fixed `height` — use `heightIn(min = ...)` so the button can grow with its content but never shrinks below the minimum.

### Button appearance
Material3 `Button` uses a fully-rounded pill shape. Keep button text short enough that width clearly exceeds height, or the button will look circular. Use `heightIn(min = 52.dp)` rather than `height(52.dp)` to allow natural pill proportions.

Button text is rendered using `labelLarge` from the type scale (Medium weight, 16sp) — white on Meta blue. No overrides needed if the color scheme is set correctly.

### Spacing between buttons
Use `Arrangement.spacedBy(16.dp)` in Rows containing multiple buttons.

---

## 5. Layout

### Top reservation
Reserve **64dp at the top** of the screen. The Portal system overlay occupies this area. Do not place interactive content there.

```kotlin
Modifier.padding(top = 64.dp)
```

### Content padding and spacing
- Outer screen padding: **16dp** on all sides (below the 64dp top reservation).
- Spacing between sections: **16dp** minimum.
- Spacing between elements within a section: **8dp**.

### Orientation
Design layouts to work in **landscape orientation first**. Portal is a landscape-first device.

---

## 6. Dependencies (app/build.gradle.kts)

```kotlin
implementation(platform(libs.androidx.compose.bom))   // BOM 2026.02.01 or later
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.compose.ui)
implementation(libs.androidx.compose.ui.graphics)
implementation(libs.androidx.compose.ui.tooling.preview)
implementation(libs.androidx.activity.compose)
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
```

Do **not** add `ui-text-google-fonts` — the XML downloadable font approach covers Inter without it.

---

## 7. Checklist before shipping

- [ ] `dynamicColor = false` in `SampleAppTheme`
- [ ] `darkTheme = true` passed from `MainActivity`
- [ ] `primary = MetaBlue`, `onPrimary = OnMetaBlue` in **both** color schemes
- [ ] All three Inter font weights registered in `FontFamily`
- [ ] No font size below 14sp anywhere in the app
- [ ] No font weight of Light or Thin anywhere
- [ ] Every button/chip has `heightIn(min = 52.dp)`
- [ ] Top 64dp of screen is empty / non-interactive
- [ ] No pure `#000000` or `#FFFFFF` colors
