package com.myelin.game.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.luckypaws.platform.AndroidInterfaceImpl

class MyelinProtocolActivity : AndroidApplication() {
    private lateinit var platform: AndroidInterfaceImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        platform = AndroidInterfaceImpl(this)

        val configuration = AndroidApplicationConfiguration().apply {
            useImmersiveMode = true
        }

        initialize(NativeGameRegistry.create(intent.getStringExtra(NativeGameRegistry.EXTRA_GAME_ID), platform), configuration)
    }

    override fun onDestroy() {
        if (::platform.isInitialized) {
            platform.dispose()
        }
        super.onDestroy()
    }
    companion object {
        fun intent(context: Context, gameId: String? = null): Intent =
            Intent(context, MyelinProtocolActivity::class.java).apply {
                gameId?.let { putExtra(NativeGameRegistry.EXTRA_GAME_ID, it) }
            }
    }
}
