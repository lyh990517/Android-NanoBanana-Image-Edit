package com.yunho.nanobanana

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.yunho.nanobanana.NanoBanana.Content.Picker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow

class NanoBanana(
    private val nanoBananaService: NanoBananaService,
) : Channel<NanoBanana.Request> by Channel() {
    val content = mutableStateOf<Content>(Picker(queue = this))

    suspend fun launch() {
        consumeAsFlow().collect { request ->
            content.value = Content.Loading

            val result = nanoBananaService.editImage(
                prompt = request.prompt,
                bitmaps = request.selectedBitmaps
            )

            content.value = result?.let {
                Content.Result(
                    result = it,
                    content = content,
                    queue = this
                )
            } ?: Content.Error(
                message = "Failed to edit image",
                content = content,
                queue = this
            )
        }
    }

    data class Request(
        val prompt: String,
        val selectedBitmaps: List<Bitmap>
    )

    sealed interface Content {

        interface Reset {
            val content: MutableState<Content>
            val queue: Channel<Request>

            fun reset()
        }

        data class Picker(
            val queue: Channel<Request>
        ) : Content {
            val selectedBitmaps = mutableStateListOf<Bitmap>()
            val pagerState = PagerState { PROMPTS.size }

            fun getImage(index: Int) = IMAGES[index]
            val prompt get() = PROMPTS[pagerState.currentPage]

            suspend fun request() {
                queue.send(
                    element = Request(
                        prompt = PROMPTS[pagerState.currentPage],
                        selectedBitmaps = selectedBitmaps
                    )
                )
            }

            fun select(
                context: Context,
                uris: List<Uri>
            ) {
                val bitmaps = uris.mapNotNull { uri ->
                    try {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                    } catch (e: Exception) {
                        Log.e("ImagePicker", "Error converting URI to Bitmap", e)
                        null
                    }
                }

                selectedBitmaps.addAll(bitmaps)
            }

            companion object {
                private val PROMPTS = listOf(
                    "Transform the photo as if it were taken in Korea in the 1900s, the chosun dynasty",
                    "Create a hyper-realistic photo of a collectible figure placed on a computer desk,  with its packaging box visible in the background.",
                    "Create an image of the person from image 1 and the person from image 2 playing rock-paper-scissors. The person from image 1 shows 'rock' and is the winner, cheering excitedly. The person from image 2 shows 'scissors' and is the loser, looking very disappointed.",
                    "two people are shopping in costco (3d)"
                )

                private val IMAGES = listOf(
                    R.drawable.chosen,
                    R.drawable.figure,
                    R.drawable.rsp,
                    R.drawable.costco
                )
            }
        }

        data object Loading : Content
        data class Result(
            val result: Bitmap,
            override val content: MutableState<Content>,
            override val queue: Channel<Request>
        ) : Content, Reset {
            override fun reset() {
                content.value = Picker(queue = queue)
            }
        }

        data class Error(
            val message: String,
            override val content: MutableState<Content>,
            override val queue: Channel<Request>
        ) : Content, Reset {
            override fun reset() {
                content.value = Picker(queue = queue)
            }
        }
    }

    companion object {
        @Composable
        fun rememberNanoBanana(
            nanoBananaService: NanoBananaService
        ): NanoBanana = remember { NanoBanana(nanoBananaService = nanoBananaService) }
    }
}
