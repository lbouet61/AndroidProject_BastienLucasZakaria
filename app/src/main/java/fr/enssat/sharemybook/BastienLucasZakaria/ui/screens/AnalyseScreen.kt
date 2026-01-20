package fr.enssat.sharemybook.BastienLucasZakaria.ui.screens

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

/**
 * A composable screen that displays a camera preview to scan for barcodes (specifically ISBNs).
 * It requests camera permission and, once granted, shows a live feed from the back camera.
 * An overlay text prompts the user to scan a barcode.
 * When a barcode is detected, it triggers the `onIsbnFound` callback with the barcode's raw value.
 *
 * This screen handles the entire camera setup and lifecycle using CameraX, including:
 * - Requesting camera permission.
 * - Binding the camera lifecycle to the composable's lifecycle.
 * - Setting up a `PreviewView` to display the camera feed.
 * - Configuring an `ImageAnalysis` use case to process frames for barcode detection using ML Kit.
 *
 * If camera permission is not granted, it displays a text message indicating that the
 * permission is required.
 *
 * @param onIsbnFound A callback function that is invoked when a barcode is successfully
 *                    detected. It receives the barcode's string value as an argument.
 */
@Composable
fun AnalyseScreen(onIsbnFound: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }

    // Demandeur de permission simplifié
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (hasCameraPermission) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                            processImageProxy(imageProxy, onIsbnFound)
                        }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner, cameraSelector, preview, imageAnalysis
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraX", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
            Text("Scannez un code barre", Modifier.align(Alignment.Center))
        }
    } else {
        Text("Permission caméra requise")
    }
}

/**
 * Processes an [ImageProxy] from the camera to detect barcodes using ML Kit.
 *
 * This function is designed to be used as an analyzer for CameraX's `ImageAnalysis` use case.
 * It converts the `ImageProxy` to an `InputImage` suitable for ML Kit. It then uses the
 * `BarcodeScanning` client to process the image.
 *
 * If a barcode is successfully detected, it extracts the raw value and invokes the `onIsbnFound`
 * callback with that value. It only processes the first barcode found.
 *
 * The `ImageProxy` is closed in all cases (success, failure, or no barcode found) to release
 * the image buffer and allow the camera to capture the next frame.
 *
 * @param imageProxy The image from the camera to be analyzed.
 * @param onIsbnFound A callback function that is invoked with the ISBN string when a barcode is successfully detected.
 */// Fonction d'analyse ML Kit
@androidx.annotation.OptIn(ExperimentalGetImage::class)
fun processImageProxy(imageProxy: ImageProxy, onIsbnFound: (String) -> Unit) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    // On cherche juste le premier code qui ressemble à un ISBN
                    barcode.rawValue?.let { value ->
                        onIsbnFound(value)
                    }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    } else {
        imageProxy.close()
    }
}