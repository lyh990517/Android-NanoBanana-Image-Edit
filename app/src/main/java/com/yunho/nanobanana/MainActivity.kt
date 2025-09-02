package com.yunho.nanobanana

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yunho.nanobanana.NanoBanana.Companion.rememberNanoBanana
import com.yunho.nanobanana.NanoBananaService.Companion.rememberNanoBananaService
import com.yunho.nanobanana.components.ApiKeySetting
import com.yunho.nanobanana.components.Generate
import com.yunho.nanobanana.components.PickedImages
import com.yunho.nanobanana.components.PickerTitle
import com.yunho.nanobanana.components.Prompt
import com.yunho.nanobanana.components.Reset
import com.yunho.nanobanana.components.ResultImage
import com.yunho.nanobanana.components.Save
import com.yunho.nanobanana.components.SelectImages
import com.yunho.nanobanana.components.StylePicker
import com.yunho.nanobanana.extension.saveBitmapToGallery
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                NanoBanana(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
fun NanoBanana(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val nanoBananaService = rememberNanoBananaService(context)
    val nanoBanana = rememberNanoBanana(nanoBananaService)
    val content by nanoBanana.content

    LaunchedEffect(Unit) {
        nanoBanana.launch()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top)
    ) {
        when (val state = content) {
            is NanoBanana.Content.Picker -> {
                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetMultipleContents()
                ) { uris ->
                    state.select(
                        context = context,
                        uris = uris
                    )
                }

                PickerTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                ApiKeySetting(
                    service = nanoBananaService,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                StylePicker(
                    content = state,
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth()
                )

                Prompt(
                    prompt = state.prompt,
                    modifier = Modifier.fillMaxWidth()
                )

                SelectImages(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(48.dp),
                    onClick = { galleryLauncher.launch("image/*") }
                )

                if (state.selectedBitmaps.isNotEmpty()) {
                    PickedImages(
                        selectedBitmaps = state.selectedBitmaps,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Generate(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(56.dp),
                        onClick = { scope.launch { state.request() } }
                    )
                }
            }

            NanoBanana.Content.Loading -> CircularProgressIndicator()
            is NanoBanana.Content.Result -> {
                ResultImage(
                    bitmap = state.result,
                    modifier = Modifier.fillMaxWidth()
                )

                Save(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(48.dp),
                    onClick = {
                        val result = context.saveBitmapToGallery(state.result)

                        Toast.makeText(
                            context,
                            if (result) "Image saved to gallery!" else "❌ Failed to save image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                Reset(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(56.dp),
                    onClick = state::reset
                )
            }

            is NanoBanana.Content.Error -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )

                Reset(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(56.dp),
                    onClick = state::reset
                )
            }
        }
    }
}
