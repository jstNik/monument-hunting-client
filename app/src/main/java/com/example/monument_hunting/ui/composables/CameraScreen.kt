package com.example.monument_hunting.ui.composables

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monument_hunting.domain.Monument
import com.example.monument_hunting.utils.Data
import com.example.monument_hunting.view_models.CameraViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.util.concurrent.Executor

@Composable
fun CameraScreen(
    monument: Monument?,
    location: LatLng?,
    onSuccess: (success: String) -> Unit,
    onFailure: (error: String?) -> Unit
){

    val context = LocalContext.current
    val cameraViewModel = viewModel<CameraViewModel>()
    val processingImage by cameraViewModel.processingImage.collectAsStateWithLifecycle()

    var permissionsGranted by remember {
        mutableStateOf(
            context.checkSelfPermission(CAMERA) == PERMISSION_GRANTED
        )
    }
    var cameraLaunched by rememberSaveable {
        mutableStateOf(false)
    }

    val requestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionsGranted = isGranted
    }

    if (permissionsGranted)
        when(processingImage.status) {
            Data.Status.Success -> {
                if(processingImage.data == true) {
                    onSuccess("Congrats, You found the monument!")
                    cameraViewModel.resetState()
                }
                else
                    if (!cameraLaunched) {
                        CameraContent(
                            onPhotoCaptured = { bitmap ->
                                if(monument == null)
                                    onFailure("Sorry can't find the monument")
                                else if(SphericalUtil.computeDistanceBetween(location, monument.position) > 100)
                                    onFailure("You are too far away!")
                                else
                                    cameraViewModel.classifyImage(bitmap, monument)
                            },
                            onError = { error ->
                                onFailure(error)
                                cameraViewModel.resetState()
                            }
                        )
                    }
            }
            Data.Status.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            Data.Status.Error -> {
                onFailure(processingImage.error)
                cameraViewModel.resetState()
            }
        }
    else
        LaunchedEffect(Unit) {
            requestLauncher.launch(CAMERA)
        }



}


@Composable
private fun CameraContent(
    onPhotoCaptured: (Bitmap) -> Unit,
    onError: (String) -> Unit
) {

    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val cameraController: LifecycleCameraController = remember { LifecycleCameraController(context) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { capturePhoto(context, cameraController, onPhotoCaptured, onError) },
                modifier = Modifier
                    .padding(bottom=16.dp)
                    .size(80.dp)
            ){
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = "Camera capture icon",
                    modifier = Modifier
                        .size(80.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->

        val bckground = MaterialTheme.colorScheme.background

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        setBackgroundColor(bckground.toArgb())
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        previewView.controller = cameraController
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                }
            )
        }
    }
}

private fun capturePhoto(
    context: Context,
    cameraController: LifecycleCameraController,
    onPhotoCaptured: (Bitmap) -> Unit,
    onError: (String) -> Unit
) {
    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

    cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            val correctedBitmap: Bitmap = image
                .toBitmap()
                .rotateBitmap(image.imageInfo.rotationDegrees)
            onPhotoCaptured(correctedBitmap)
            image.close()
        }

        override fun onError(exception: ImageCaptureException) {
            onError.invoke(exception.message ?: "")
        }
    })
}

fun Bitmap.rotateBitmap(rotationDegrees: Int): Bitmap {
    val matrix = Matrix().apply {
        postRotate(-rotationDegrees.toFloat())
        postScale(-1f, -1f)
    }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}