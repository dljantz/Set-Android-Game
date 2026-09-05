package com.example.set.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RulesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Rules of SET",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "The Goal:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Find groups of 3 cards that form a valid 'SET'.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "The 4 Features of Every Card:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "1. Number: 1, 2, or 3\n" +
                            "2. Symbol: Oval, Squiggle, Diamond\n" +
                            "3. Shading: Solid, Striped, Open\n" +
                            "4. Color: Red, Green, Purple",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "The Golden Rule:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Three cards make a SET if and only if, for EACH of the four features individually, they are:\n\n" +
                            "  • ALL THE SAME, or\n" +
                            "  • ALL DIFFERENT.\n\n" +
                            "If 2 cards have the same attribute and the 3rd differs, it is NOT a set!",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Examples:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• 1 Red Solid Oval\n" +
                            "• 2 Red Solid Squiggles\n" +
                            "• 3 Red Solid Diamonds\n" +
                            "-> Numbers are all diff (1, 2, 3)\n" +
                            "-> Colors are all same (Red)\n" +
                            "-> Shadings are all same (Solid)\n" +
                            "-> Shapes are all diff (Oval, Squiggle, Diamond)\n" +
                            "=> VALID SET!",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Got It!")
            }
        }
    )
}
