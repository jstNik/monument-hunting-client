package com.example.monument_hunting.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.monument_hunting.R
import com.example.monument_hunting.domain.Riddle
import com.google.android.gms.maps.model.LatLng

@Composable
fun HomePageSheetContent(
    riddle: Riddle?,
    playerLocation: LatLng?,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            .fillMaxWidth()
    ) {
        if (playerLocation != null) {
            Text(
                riddle?.title ?: "No riddles found in your zone",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                riddle?.body ?: "Try to go into a different area",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Image(
                painterResource(R.drawable.florence_skyline),
                null,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
            IconButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painterResource(R.drawable.photo_camera),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        "You think you found it?!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Text(
                "Position can not be obtained",
                style = MaterialTheme.typography.titleLarge
            )
            Image(
                painterResource(R.drawable.florence_skyline),
                null,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }

    }
}