package io.dala.pandanow.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun <T> SelectionCard(
    title: String,
    options: List<Pair<T, String>>,
    currentSelection: T,
    onOptionSelected: (T) -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .padding(end = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            options.forEach { (option, description) ->
                Text(
                    text = description,
                    color = if (option == currentSelection) MaterialTheme.colorScheme.primary else Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onOptionSelected(option)
                            onDismiss()
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}