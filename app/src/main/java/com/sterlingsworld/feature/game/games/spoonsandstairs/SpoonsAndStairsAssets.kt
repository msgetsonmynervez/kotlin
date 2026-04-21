package com.sterlingsworld.feature.game.games.spoonsandstairs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.platform.LocalContext
import com.sterlingsworld.R

class SpoonsAndStairsAssets(
    val player: ImageBitmap,
    val obstacleToys: ImageBitmap,
    val obstacleLaundry: ImageBitmap,
    val obstacleStairs: ImageBitmap,
    val powerupWater: ImageBitmap,
    val powerupLightning: ImageBitmap,
    val backgroundRug: ImageBitmap,
    val backgroundRoad: ImageBitmap,
    val spoonIcon: ImageBitmap,
) {
    fun spriteFor(type: GameObjectType): ImageBitmap = when (type) {
        GameObjectType.TOY -> obstacleToys
        GameObjectType.LAUNDRY -> obstacleLaundry
        GameObjectType.STAIRS -> obstacleStairs
        GameObjectType.WATER -> powerupWater
        GameObjectType.LIGHTNING -> powerupLightning
    }
}

@Composable
fun rememberSpoonsAndStairsAssets(): SpoonsAndStairsAssets {
    val resources = LocalContext.current.resources
    return remember(resources) {
    SpoonsAndStairsAssets(
        player = ImageBitmap.imageResource(resources, R.drawable.player_ship),
        obstacleToys = ImageBitmap.imageResource(resources, R.drawable.obstacle_toys),
        obstacleLaundry = ImageBitmap.imageResource(resources, R.drawable.obstacle_laundry),
        obstacleStairs = ImageBitmap.imageResource(resources, R.drawable.obstacle_stairs),
        powerupWater = ImageBitmap.imageResource(resources, R.drawable.powerup_water),
        powerupLightning = ImageBitmap.imageResource(resources, R.drawable.powerup_lightning),
        backgroundRug = ImageBitmap.imageResource(resources, R.drawable.bg_rug),
        backgroundRoad = ImageBitmap.imageResource(resources, R.drawable.bg_spoons_and_stairs),
        spoonIcon = ImageBitmap.imageResource(resources, R.drawable.icon_spoon),
    )
}
}
