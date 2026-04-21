package com.example.spoonsandstairs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource

/**
 * Container class that holds preloaded bitmaps for all sprites and
 * background images used by the game.  Keeping assets in a single
 * structure makes it easy to access them during rendering without
 * repeatedly decoding resources on every frame.
 */
class Assets(
    val player: ImageBitmap,
    val obstacleToys: ImageBitmap,
    val obstacleLaundry: ImageBitmap,
    val obstacleStairs: ImageBitmap,
    val powerupWater: ImageBitmap,
    val powerupLightning: ImageBitmap,
    val backgroundRug: ImageBitmap,
    val backgroundRoad: ImageBitmap,
    val spoonIcon: ImageBitmap
) {
    /**
     * Return the correct bitmap for a given [GameObjectType].  Hazards map
     * to their respective obstacle sprites and power ups map to their
     * respective pick up icons.
     */
    fun spriteFor(type: GameObjectType): ImageBitmap = when (type) {
        GameObjectType.TOY -> obstacleToys
        GameObjectType.LAUNDRY -> obstacleLaundry
        GameObjectType.STAIRS -> obstacleStairs
        GameObjectType.WATER -> powerupWater
        GameObjectType.LIGHTNING -> powerupLightning
    }
}

/**
 * Load and remember all image assets on the first composition.  Using
 * [remember] ensures that bitmaps are cached across recompositions rather
 * than being recreated on every frame.  This helper should be called
 * from within a composable before drawing the game screen.
 */
@Composable
fun rememberGameAssets(): Assets {
    return remember {
        Assets(
            player = ImageBitmap.imageResource(R.drawable.player_ship),
            obstacleToys = ImageBitmap.imageResource(R.drawable.obstacle_toys),
            obstacleLaundry = ImageBitmap.imageResource(R.drawable.obstacle_laundry),
            obstacleStairs = ImageBitmap.imageResource(R.drawable.obstacle_stairs),
            powerupWater = ImageBitmap.imageResource(R.drawable.powerup_water),
            powerupLightning = ImageBitmap.imageResource(R.drawable.powerup_lightning),
            backgroundRug = ImageBitmap.imageResource(R.drawable.bg_rug),
            backgroundRoad = ImageBitmap.imageResource(R.drawable.bg_road),
            spoonIcon = ImageBitmap.imageResource(R.drawable.icon_spoon)
        )
    }
}