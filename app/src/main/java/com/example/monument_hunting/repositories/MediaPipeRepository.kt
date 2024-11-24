package com.example.monument_hunting.repositories

import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MediaPipeRepository @Inject constructor(
    private val imageClassifier: ImageClassifier
) {

    suspend fun classifyImage(bitmap: Bitmap): String? {
        val image = BitmapImageBuilder(bitmap).build()
        val result = withContext(Dispatchers.Default){
            imageClassifier.classify(image)
        }
        return result
            .classificationResult()
            .classifications()
            .firstOrNull()
            ?.categories()
            ?.firstOrNull()
            ?.categoryName()
    }

}