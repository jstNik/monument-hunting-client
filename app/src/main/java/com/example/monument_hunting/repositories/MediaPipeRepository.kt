package com.example.monument_hunting.repositories

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.UiContext
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MediaPipeRepository @Inject constructor(
    private val context: Context,
    private val imageClassifier: ImageClassifier
) {




    suspend fun classifyImage(uri: Uri) {
        val image = BitmapImageBuilder(context, uri).build()
        val result = withContext(Dispatchers.Default){
            imageClassifier.classify(image)
        }
        var results = ""
        result.classificationResult().classifications().forEachIndexed{ i, classification ->
            results += "Classification #$i\n"
            classification.categories().forEachIndexed { k, category ->
                results += "\tCategory #$k\n"
                results += "\t\tCategory name: ${category.categoryName()}\n"
                results += "\t\tDisplay name: ${category.displayName()}\n"
                results += "\t\tScore: ${category.score()}\n"
            }
        }
        Log.d("MediaPipe Results", results)
    }

}