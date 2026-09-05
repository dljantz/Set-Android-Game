package com.example.set.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.set.model.GameMode

@Composable
fun SettingsDialog(
    gameMode: GameMode,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    showSetCount: Boolean,
    autoDealIfNoSets: Boolean,
    colorblindMode: Boolean,
    onModeSelected: (GameMode) -> Unit,
    onSoundToggled: (Boolean) -> Unit,
    onHapticsToggled: (Boolean) -> Unit,
    onShowSetCountToggled: (Boolean) -> Unit,
    onAutoDealToggled: (Boolean) -> Unit,
    onColorblindToggled: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Game Settings", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Game Mode",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                GameMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (mode == gameMode),
                            onClick = { onModeSelected(mode) }
                        )
                        Column(modifier = Modifier.padding(start = 6.dp)) {
                            Text(text = mode.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(text = mode.description, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Gameplay & Assists",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                SettingSwitchRow(
                    label = "Show Board Sets Count",
                    caption = "Displays how many valid Sets currently exist on the board",
                    checked = showSetCount,
                    onCheckedChange = onShowSetCountToggled
                )

                SettingSwitchRow(
                    label = "Auto-Deal when No Sets",
                    caption = "Automatically adds 3 cards when no sets are possible",
                    checked = autoDealIfNoSets,
                    onCheckedChange = onAutoDealToggled
                )

                SettingSwitchRow(
                    label = "Colorblind / High-Contrast",
                    caption = "Uses Orange, Teal, and Indigo for high color contrast",
                    checked = colorblindMode,
                    onCheckedChange = onColorblindToggled
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Feedback",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                SettingSwitchRow(
                    label = "Sound Effects",
                    caption = "Audio synthesizer tones for matches, taps, and errors",
                    checked = soundEnabled,
                    onCheckedChange = onSoundToggled
                )

                SettingSwitchRow(
                    label = "Haptic Vibration",
                    caption = "Tactile feedback on card selection and set completion",
                    checked = hapticsEnabled,
                    onCheckedChange = onHapticsToggled
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun SettingSwitchRow(
    label: String,
    caption: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(text = caption, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
