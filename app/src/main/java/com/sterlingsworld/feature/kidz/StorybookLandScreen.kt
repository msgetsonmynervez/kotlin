package com.sterlingsworld.feature.kidz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sterlingsworld.R
import com.sterlingsworld.core.ui.components.BathroomMapButton
import com.sterlingsworld.core.ui.components.DashedCornerButton
import com.sterlingsworld.data.catalog.KidzCatalog

@Composable
fun StorybookLandScreen(
    onVideoSelected: (videoId: String) -> Unit = {},
    onBack: () -> Unit = {},
) {
    val videos = KidzCatalog.videos()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_storybook_land),
            contentDescription = "Adventures in Storybook Land",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize(),
        )
        DashedCornerButton(
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            onClick = onBack,
        )
        BathroomMapButton(Modifier.align(Alignment.TopEnd).padding(16.dp))

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            videos.forEach { activity ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color(0xFFCE93D8), RoundedCornerShape(12.dp))
                        .background(Color(0xEE1B1B2F))
                        .clickable { onVideoSelected(activity.video.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = activity.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
