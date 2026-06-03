/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.portal.sampleapp

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.io.File

private enum class AudioState {
  IDLE,
  RECORDING,
  RECORDED,
  PLAYING,
}

@Composable
fun AudioRecorderSection() {
  val context = LocalContext.current
  var audioState by remember { mutableStateOf(AudioState.IDLE) }
  val outputFile = remember { File(context.cacheDir, "audio_recording.3gp") }
  var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
  var player by remember { mutableStateOf<MediaPlayer?>(null) }
  var permissionDenied by remember { mutableStateOf(false) }

  DisposableEffect(Unit) {
    onDispose {
      runCatching { recorder?.stop() }
      recorder?.release()
      player?.release()
    }
  }

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(
        stringResource(R.string.section_audio_recorder),
        style = MaterialTheme.typography.headlineSmall,
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
          text =
              stringResource(
                  when (audioState) {
                    AudioState.IDLE -> R.string.audio_status_idle
                    AudioState.RECORDING -> R.string.audio_status_recording
                    AudioState.RECORDED -> R.string.audio_status_recorded
                    AudioState.PLAYING -> R.string.audio_status_playing
                  }
              ),
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          style = MaterialTheme.typography.bodyMedium,
      )
    }

    if (permissionDenied) {
      Text(
          stringResource(R.string.error_audio_permission),
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
      )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
      Button(
          onClick = {
            when (audioState) {
              AudioState.IDLE,
              AudioState.RECORDED -> {
                if (
                    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) !=
                        PackageManager.PERMISSION_GRANTED
                ) {
                  permissionDenied = true
                  return@Button
                }
                permissionDenied = false
                recorder =
                    (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context)
                        else @Suppress("DEPRECATION") MediaRecorder())
                        .apply {
                          setAudioSource(MediaRecorder.AudioSource.MIC)
                          setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                          setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                          setOutputFile(outputFile.absolutePath)
                          prepare()
                          start()
                        }
                audioState = AudioState.RECORDING
              }
              AudioState.RECORDING -> {
                try {
                  recorder?.stop()
                  audioState = AudioState.RECORDED
                } catch (e: RuntimeException) {
                  audioState = AudioState.IDLE
                } finally {
                  recorder?.release()
                  recorder = null
                }
              }
              AudioState.PLAYING -> {}
            }
          },
          enabled = audioState != AudioState.PLAYING,
          modifier = Modifier.heightIn(min = 52.dp),
      ) {
        Text(
            stringResource(
                if (audioState == AudioState.RECORDING) R.string.btn_stop else R.string.btn_record
            )
        )
      }

      Button(
          onClick = {
            when (audioState) {
              AudioState.RECORDED -> {
                player =
                    MediaPlayer().apply {
                      setDataSource(outputFile.absolutePath)
                      prepare()
                      start()
                      setOnCompletionListener {
                        release()
                        player = null
                        audioState = AudioState.RECORDED
                      }
                    }
                audioState = AudioState.PLAYING
              }
              AudioState.PLAYING -> {
                player?.stop()
                player?.release()
                player = null
                audioState = AudioState.RECORDED
              }
              else -> {}
            }
          },
          enabled = audioState == AudioState.RECORDED || audioState == AudioState.PLAYING,
          modifier = Modifier.heightIn(min = 52.dp),
      ) {
        Text(
            stringResource(
                if (audioState == AudioState.PLAYING) R.string.btn_stop else R.string.btn_play
            )
        )
      }

      OutlinedButton(
          onClick = {
            runCatching { recorder?.stop() }
            recorder?.release()
            recorder = null
            player?.stop()
            player?.release()
            player = null
            outputFile.delete()
            audioState = AudioState.IDLE
            permissionDenied = false
          },
          enabled = audioState != AudioState.IDLE,
          modifier = Modifier.heightIn(min = 52.dp),
      ) {
        Text(stringResource(R.string.btn_reset))
      }
    }
  }
}
