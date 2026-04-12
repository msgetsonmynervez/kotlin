package com.sterlingsworld.feature.map

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sterlingsworld.core.navigation.Screen
import com.sterlingsworld.core.ui.theme.Background
import com.sterlingsworld.core.ui.theme.Primary
import com.sterlingsworld.core.ui.theme.Secondary
import com.sterlingsworld.core.ui.theme.Surface
import com.sterlingsworld.core.ui.theme.SurfaceStrong
import com.sterlingsworld.core.ui.theme.TextMuted
import com.sterlingsworld.core.ui.theme.TextPrimary
import com.sterlingsworld.core.util.rememberAssetBitmap

@Composable
fun MapScreen(onNavigateToZone: (route: String) -> Unit) {
    val context = LocalContext.current
    val parkMapBitmap = rememberAssetBitmap("images/map/park_map.png")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Park map — real bundled image when available
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceStrong, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (parkMapBitmap != null) {
                Image(
                    bitmap = parkMapBitmap,
                    contentDescription = "Sterling Park map",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(
                            parkMapBitmap.width.toFloat() / parkMapBitmap.height.toFloat()
                        ),
                    contentScale = ContentScale.FillWidth,
                )
            } else {
                Text(
                    text = "Sterling Park Map",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(48.dp),
                )
            }
        }

        Text(
            text = "Zones",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )

        val zones = listOf(
            "Arcade" to Screen.Arcade.route,
            "Cinema" to Screen.Cinema.route,
            "Studio" to Screen.Studio.route,
            "Kidz"   to Screen.Kidz.route,
        )
        zones.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                pair.forEach { (label, route) ->
                    Button(
                        onClick = { onNavigateToZone(route) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Surface),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(label, color = Primary)
                    }
                }
                if (pair.size == 1) Box(modifier = Modifier.weight(1f))
            }
        }

        OutlinedButton(
            onClick = {
                val uri = Uri.parse("geo:0,0?q=restroom+near+me")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Find Nearby Restrooms", color = Secondary)
        }
    }
}
