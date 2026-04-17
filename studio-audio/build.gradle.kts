plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName.set("studio-audio")
    dynamicDelivery {
        deliveryType.set("install-time")
    }
}
