package com.example.monument_hunting.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.monument_hunting.domain.Riddle
import com.example.monument_hunting.domain.ServerData
import com.example.monument_hunting.exceptions.ApiRequestException
import com.example.monument_hunting.ui.theme.MonumentHuntingTheme
import com.example.monument_hunting.utils.Data
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil

@Composable
fun HomePageSheetContent(
    riddle: Riddle?,
    playerLocation: LatLng?,
) {
    if (playerLocation != null) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                riddle?.name ?: "No riddles found in your zone",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                riddle?.body ?: "Try to go into a different area",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(
                modifier = Modifier.heightIn(min = 200.dp)
            )
        }
    } else {
        Text(
            "Position can not be obtained",
            style = MaterialTheme.typography.titleLarge
        )
    }
}