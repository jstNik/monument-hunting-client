package com.example.monument_hunting.ui.composables

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monument_hunting.BuildConfig.APPLICATION_ID
import com.example.monument_hunting.domain.Riddle
import com.example.monument_hunting.utils.Data
import com.example.monument_hunting.view_models.CameraViewModel
import com.example.monument_hunting.view_models.HomePageViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CameraScreen(
    riddle: Riddle?,
    location: LatLng?,
    onSuccess: () -> Unit,
    onFailure: (error: String?) -> Unit
){

    val context = LocalContext.current
    val cameraViewModel = viewModel<CameraViewModel>()
    val processingImage by cameraViewModel.processingImage.collectAsStateWithLifecycle()

    var permissionsGranted by remember{
        mutableStateOf(
            context.checkSelfPermission(CAMERA) == PERMISSION_GRANTED
        )
    }
    val uri = FileProvider.getUriForFile(
        context, "$APPLICATION_ID.provider", context.createImageFile()
    )

    val requestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionsGranted = isGranted
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        cameraViewModel.classifyImage(if (success) uri else null)
    }

    if (permissionsGranted)
        when(processingImage.status) {
            Data.Status.Success -> {
                if(processingImage.data == true) {
                    if(riddle == null)
                        onFailure("Sorry i can't find the riddle :(")
                    else if(SphericalUtil.computeDistanceBetween(location, riddle.monument.position) > 100)
                        onFailure("You are too far!")
                    else {
                        onSuccess()
                        cameraViewModel.resetState()
                    }
                }
                else
                    LaunchedEffect(Unit) {
                        cameraLauncher.launch(uri)
                    }
            }
            Data.Status.Loading -> {
                CircularProgressIndicator()
            }
            Data.Status.Error -> {
                LaunchedEffect(Unit) {
                    cameraLauncher.launch(uri)
                }
            }
        }
    else
        LaunchedEffect(Unit) {
            requestLauncher.launch(CAMERA)
        }
}


fun Context.createImageFile(): File{
    val imageFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
    return image
}