/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.portal.sampleapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun PermissionsSection() {
  val context = LocalContext.current
  fun isGranted(p: String) =
      ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED

  Text(
      stringResource(R.string.section_permission_requests),
      style = MaterialTheme.typography.headlineSmall,
  )

  var cameraGranted by remember { mutableStateOf(isGranted(Manifest.permission.CAMERA)) }
  val cameraLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        cameraGranted = it
      }

  var locationGranted by remember {
    mutableStateOf(isGranted(Manifest.permission.ACCESS_FINE_LOCATION))
  }
  val locationLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        locationGranted = it
      }

  var contactsGranted by remember { mutableStateOf(isGranted(Manifest.permission.READ_CONTACTS)) }
  val contactsLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        contactsGranted = it
      }

  var audioGranted by remember { mutableStateOf(isGranted(Manifest.permission.RECORD_AUDIO)) }
  val audioLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        audioGranted = it
      }

  var allGranted by remember {
    mutableStateOf(
        isGranted(Manifest.permission.CAMERA) &&
            isGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
            isGranted(Manifest.permission.READ_CONTACTS) &&
            isGranted(Manifest.permission.RECORD_AUDIO)
    )
  }
  val multiLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
          results ->
        cameraGranted = results[Manifest.permission.CAMERA] ?: cameraGranted
        locationGranted = results[Manifest.permission.ACCESS_FINE_LOCATION] ?: locationGranted
        contactsGranted = results[Manifest.permission.READ_CONTACTS] ?: contactsGranted
        audioGranted = results[Manifest.permission.RECORD_AUDIO] ?: audioGranted
        allGranted = cameraGranted && locationGranted && contactsGranted && audioGranted
      }

  PermissionRow(stringResource(R.string.permission_camera), cameraGranted) {
    cameraLauncher.launch(Manifest.permission.CAMERA)
  }
  PermissionRow(stringResource(R.string.permission_location), locationGranted) {
    locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
  }
  PermissionRow(stringResource(R.string.permission_contacts), contactsGranted) {
    contactsLauncher.launch(Manifest.permission.READ_CONTACTS)
  }
  PermissionRow(stringResource(R.string.permission_audio), audioGranted) {
    audioLauncher.launch(Manifest.permission.RECORD_AUDIO)
  }
  PermissionRow(stringResource(R.string.permission_all), allGranted) {
    multiLauncher.launch(
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
        )
    )
  }
}

@Composable
fun PermissionRow(name: String, granted: Boolean, onRequest: () -> Unit) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(name, style = MaterialTheme.typography.bodyLarge)
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
          color =
              if (granted) MaterialTheme.colorScheme.tertiaryContainer
              else MaterialTheme.colorScheme.errorContainer,
          shape = MaterialTheme.shapes.small,
      ) {
        Text(
            text = stringResource(if (granted) R.string.status_granted else R.string.status_denied),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color =
                if (granted) MaterialTheme.colorScheme.onTertiaryContainer
                else MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.labelSmall,
        )
      }
      Button(onClick = onRequest, modifier = Modifier.heightIn(min = 52.dp)) {
        Text(stringResource(R.string.btn_request))
      }
    }
  }
}
