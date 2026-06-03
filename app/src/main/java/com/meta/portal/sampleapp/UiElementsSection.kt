/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.portal.sampleapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiElementsSection() {
  Text(stringResource(R.string.section_ui_elements), style = MaterialTheme.typography.headlineSmall)

  var count by remember { mutableIntStateOf(0) }
  Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
    Button(onClick = { count++ }, modifier = Modifier.heightIn(min = 52.dp)) {
      Text(stringResource(R.string.btn_click_me))
    }
    OutlinedButton(onClick = { count = 0 }, modifier = Modifier.heightIn(min = 52.dp)) {
      Text(stringResource(R.string.btn_reset))
    }
  }
  Text(stringResource(R.string.clicked_times, count), style = MaterialTheme.typography.bodyMedium)

  var text by remember { mutableStateOf("") }
  OutlinedTextField(
      value = text,
      onValueChange = { text = it },
      label = { Text(stringResource(R.string.label_enter_text)) },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
  )

  var sliderValue by remember { mutableFloatStateOf(0.5f) }
  Text(
      stringResource(R.string.slider_value, (sliderValue * 100).toInt()),
      style = MaterialTheme.typography.bodyMedium,
  )
  Slider(value = sliderValue, onValueChange = { sliderValue = it })

  var checked by remember { mutableStateOf(false) }
  Row(verticalAlignment = Alignment.CenterVertically) {
    Checkbox(checked = checked, onCheckedChange = { checked = it })
    Text(stringResource(if (checked) R.string.checkbox_checked else R.string.checkbox_unchecked))
  }

  var switched by remember { mutableStateOf(false) }
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Switch(checked = switched, onCheckedChange = { switched = it })
    Text(stringResource(if (switched) R.string.switch_on else R.string.switch_off))
  }

  val radioOptions = listOf("Option A", "Option B", "Option C")
  var selectedOption by remember { mutableStateOf(radioOptions[0]) }
  Text(stringResource(R.string.label_radio_buttons), style = MaterialTheme.typography.bodyMedium)
  Column(modifier = Modifier.selectableGroup()) {
    radioOptions.forEach { option ->
      Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selectedOption == option, onClick = { selectedOption = option })
        Text(option)
      }
    }
  }
  Text(
      stringResource(R.string.selected_option, selectedOption),
      style = MaterialTheme.typography.bodySmall,
  )

  Card(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(stringResource(R.string.card_title), style = MaterialTheme.typography.titleMedium)
      Text(stringResource(R.string.card_body), style = MaterialTheme.typography.bodyMedium)
    }
  }

  val dropdownItems = listOf("Red", "Green", "Blue", "Yellow")
  var expanded by remember { mutableStateOf(false) }
  var selectedItem by remember { mutableStateOf(dropdownItems[0]) }
  ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
    OutlinedTextField(
        value = selectedItem,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(R.string.label_favorite_color)) },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
    )
    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      dropdownItems.forEach { item ->
        DropdownMenuItem(
            text = { Text(item) },
            onClick = {
              selectedItem = item
              expanded = false
            },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        )
      }
    }
  }
}
