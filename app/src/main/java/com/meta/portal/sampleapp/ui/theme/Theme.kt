/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.portal.sampleapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
    lightColorScheme(
        primary = MetaBlue,
        onPrimary = OnMetaBlue,
        primaryContainer = MetaBlueLight,
        onPrimaryContainer = OnMetaBlueLight,
        secondary = NeutralGrey,
        onSecondary = OnMetaBlue,
        background = BackgroundLight,
        surface = SurfaceLight,
        onBackground = ContentOnLight,
        onSurface = ContentOnLight,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = MetaBlue,
        onPrimary = OnMetaBlue,
        primaryContainer = MetaBlueDarkCont,
        onPrimaryContainer = OnMetaBlueDarkC,
        secondary = NeutralGreyDark,
        onSecondary = OnMetaBlue,
        background = BackgroundDark,
        surface = SurfaceDark,
        onBackground = ContentOnDark,
        onSurface = ContentOnDark,
    )

@Composable
fun SampleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disabled: dynamic color overrides all custom colors and appears over-saturated in MR headsets
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
  val colorScheme =
      when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
      }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
