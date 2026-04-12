# Keep Room entities and DAOs
-keep class com.sterlingsworld.data.progress.** { *; }

# Keep DataStore serialized classes
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep BuildConfig
-keep class com.sterlingsworld.BuildConfig { *; }
