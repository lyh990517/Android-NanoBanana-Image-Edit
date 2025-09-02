package com.yunho.nanobanana.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.yunho.nanobanana.NanoBanana
import kotlin.math.absoluteValue

private const val RATIO = 300f / 420f
private const val SCALE = 0.8F
private val defaultMargin = 40.dp
private val staticPageSpacing = 16.dp

@Composable
fun StylePicker(
    content: NanoBanana.Content.Picker,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            val pagerState = content.pagerState

            Text(
                text = "Choose Style",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 20.dp, bottom = 12.dp)
            )

            @SuppressLint("UnusedBoxWithConstraintsScope")
            BoxWithConstraints(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .padding(top = 10.dp)
                    .weight(weight = 1f, false),
                contentAlignment = Alignment.Center
            ) {
                val maxWidth = maxWidth
                val itemWidth = min(maxWidth.minus(defaultMargin * 2), maxHeight.times(RATIO))
                val pageSpacing = -itemWidth.times((1f - SCALE).div(2f)) + staticPageSpacing

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = maxWidth.minus(itemWidth).div(2)),
                    pageSpacing = pageSpacing
                ) { page ->
                    Image(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = lerp(
                                    start = 1f,
                                    stop = SCALE,
                                    fraction = pagerState
                                        .getOffsetDistanceInPages(page)
                                        .absoluteValue
                                )

                                scaleY = scaleX
                            }
                            .width(maxWidth)
                            .aspectRatio(RATIO, true)
                            .clip(shape = RoundedCornerShape(60.dp)),
                        painter = painterResource(content.getImage(page)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
