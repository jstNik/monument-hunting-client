package com.example.monument_hunting.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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

@Composable
fun HomePageSheetContent(
    serverData: Data<ServerData, ApiRequestException>,
    playerLocation: LatLng?
) {
    if(playerLocation != null){
        when (serverData.status) {
            Data.Status.Success -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "There is this riddle solve in this zone",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom=16.dp)
                    )
                    Text(
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et commodo ipsum." +
                        " Cras accumsan congue tellus, et elementum ipsum pellentesque sed." +
                        " Integer ipsum augue, lobortis ac placerat sed, molestie ut magna." +
                        " Nunc id nunc neque. Quisque gravida ante orci, nec iaculis lectus mollis sit amet." +
                        " Sed aliquam gravida ante sit amet vestibulum. Nullam nec luctus erat. Donec a suscipit sem." +
                        " Curabitur feugiat congue justo pellentesque interdum. Praesent convallis at mi eu bibendum." +
                        " Vestibulum condimentum tempus tincidunt. Nam tempor non ex nec faucibus." +
                        " Curabitur condimentum pellentesque enim, sit amet laoreet mauris semper at." +
                        " Nam quis scelerisque orci. Morbi vel ultrices urna.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Data.Status.Loading -> {

            }

            Data.Status.Error -> {

            }
        }
    } else {
        Text(
            "Position can not be obtained",
            style = MaterialTheme.typography.titleLarge
        )
    }
}