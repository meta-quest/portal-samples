/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.portal.sampleapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

@Composable
fun CameraSection() {
  val context = LocalContext.current
  var showPreview by remember { mutableStateOf(false) }
  var permissionDenied by remember { mutableStateOf(false) }
  var cameraError by remember { mutableStateOf<String?>(null) }
  var availableCameras by remember {
    mutableStateOf<List<Pair<String, CameraSelector>>>(emptyList())
  }
  var selectedCamera by remember { mutableStateOf<Pair<String, CameraSelector>?>(null) }

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(
        stringResource(R.string.section_camera_stream),
        style = MaterialTheme.typography.headlineSmall,
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Button(
          onClick = {
            if (
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
            ) {
              showPreview = !showPreview
              if (!showPreview) {
                permissionDenied = false
                cameraError = null
                availableCameras = emptyList()
                selectedCamera = null
              }
            } else {
              permissionDenied = true
            }
          },
          modifier = Modifier.heightIn(min = 52.dp),
      ) {
        Text(
            stringResource(if (showPreview) R.string.btn_close_camera else R.string.btn_open_camera)
        )
      }

      availableCameras.forEach { option ->
        FilterChip(
            selected = selectedCamera?.first == option.first,
            onClick = { selectedCamera = option },
            label = { Text(option.first) },
            modifier = Modifier.heightIn(min = 52.dp),
        )
      }
    }

    if (permissionDenied) {
      Text(
          stringResource(R.string.error_camera_permission),
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
      )
    }

    if (cameraError != null) {
      Text(
          cameraError!!,
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
      )
    }

    if (showPreview) {
      CameraViewfinder(
          modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f).clip(MaterialTheme.shapes.medium),
          selectedSelector = selectedCamera?.second,
          onCamerasEnumerated = { cameras ->
            availableCameras = cameras
            if (selectedCamera == null) selectedCamera = cameras.firstOrNull()
          },
          onError = {
            cameraError = it
            showPreview = false
          },
      )
    }
  }
}

@Composable
fun CameraViewfinder(
    modifier: Modifier = Modifier,
    selectedSelector: CameraSelector? = null,
    onCamerasEnumerated: (List<Pair<String, CameraSelector>>) -> Unit = {},
    onError: (String) -> Unit = {},
) {
  val context = LocalContext.current
  val lifecycleOwner = context as LifecycleOwner
  // Plain arrays avoid triggering recomposition when written from async callbacks
  val providerRef = remember { arrayOfNulls<ProcessCameraProvider>(1) }
  val pvRef = remember { arrayOfNulls<PreviewView>(1) }

  DisposableEffect(Unit) { onDispose { providerRef[0]?.unbindAll() } }

  // Rebind whenever the user picks a different camera chip
  LaunchedEffect(selectedSelector) {
    val provider = providerRef[0] ?: return@LaunchedEffect
    val pv = pvRef[0] ?: return@LaunchedEffect
    val selector = selectedSelector ?: return@LaunchedEffect
    val preview = Preview.Builder().build().also { it.setSurfaceProvider(pv.surfaceProvider) }
    try {
      provider.unbindAll()
      provider.bindToLifecycle(lifecycleOwner, selector, preview)
    } catch (e: Exception) {
      onError(e.localizedMessage ?: context.getString(R.string.error_camera_generic))
    }
  }

  AndroidView(
      modifier = modifier,
      factory = { ctx ->
        PreviewView(ctx)
            .apply {
              implementationMode = PreviewView.ImplementationMode.COMPATIBLE
              scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            .also { pv ->
              pvRef[0] = pv
              val future = ProcessCameraProvider.getInstance(ctx)
              future.addListener(
                  {
                    val provider = future.get()
                    providerRef[0] = provider

                    val cameras =
                        provider.availableCameraInfos
                            .mapIndexedNotNull { i, info ->
                              runCatching {
                                    val label =
                                        when (info.lensFacing) {
                                          CameraSelector.LENS_FACING_BACK ->
                                              ctx.getString(R.string.camera_back)
                                          CameraSelector.LENS_FACING_FRONT ->
                                              ctx.getString(R.string.camera_front)
                                          else -> ctx.getString(R.string.camera_other, i + 1)
                                        }
                                    label to
                                        CameraSelector.Builder()
                                            .requireLensFacing(info.lensFacing)
                                            .build()
                                  }
                                  .getOrNull()
                            }
                            .distinctBy { it.first }

                    if (cameras.isEmpty()) {
                      onError(ctx.getString(R.string.error_no_camera))
                      return@addListener
                    }
                    onCamerasEnumerated(cameras)
                  },
                  ContextCompat.getMainExecutor(ctx),
              )
            }
      },
  )
}
