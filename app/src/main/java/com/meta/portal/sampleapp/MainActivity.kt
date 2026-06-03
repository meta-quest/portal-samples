/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.portal.sampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meta.portal.sampleapp.ui.theme.SampleAppTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      SampleAppTheme(darkTheme = true) {
        Scaffold(
            topBar = {
              TopAppBar(
                  title = { Text(stringResource(R.string.title_ui_showcase)) },
                  colors =
                      TopAppBarDefaults.topAppBarColors(
                          containerColor = MaterialTheme.colorScheme.primaryContainer
                      ),
              )
            }
        ) { paddingValues ->
          ShowcaseScreen(modifier = Modifier.padding(paddingValues))
        }
      }
    }
  }
}

@Composable
fun ShowcaseScreen(modifier: Modifier = Modifier) {
  Column(
      modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    UiElementsSection()
    HorizontalDivider()
    PermissionsSection()
    HorizontalDivider()
    CameraSection()
    HorizontalDivider()
    AudioRecorderSection()
  }
}
