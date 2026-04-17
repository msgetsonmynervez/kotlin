package com.sterlingsworld.data.catalog

import com.sterlingsworld.domain.model.Album
import com.sterlingsworld.domain.model.Track

/**
 * Authoritative Studio content registry.
 *
 * Asset paths are relative to the Android `assets/` directory root.
 * The five active albums total 135 tracks:
 *   - Sterling Main Library:       tracks 01–48  (48 tracks)
 *   - Dark Side of the Spoon:      tracks 49–63  (15 tracks)
 *   - Groove:                      tracks 64–96  (33 tracks)
 *   - Neural Garden:               ng01a/b–ng15a/b (30 tracks)
 *   - Standup:                     st01–st09 (9 tracks)
 */
object StudioCatalog {

    val albums: List<Album> by lazy {
        listOf(
            sterlingMainLibrary(),
            darkSideOfTheSpoon(),
            groove(),
            neuralGarden(),
            standup(),
        )
    }

    val allTracks: List<Track> by lazy { albums.flatMap { it.tracks } }

    fun albumById(id: String): Album? = albums.firstOrNull { it.id == id }

    // ── Sterling Main Library ─────────────────────────────────────────────────

    private fun sterlingMainLibrary(): Album {
        val tracks = (1..48).map { n ->
            val padded = n.toString().padStart(2, '0')
            Track(
                id = "track-$padded",
                title = "Music Track $padded",
                assetPath = "audio/music/music-track-$padded.mp3",
                trackNumber = n,
                albumId = "sterling-main",
            )
        }
        return Album(
            id = "sterling-main",
            title = "Sterling Main Library",
            artist = "Sterling Sound Team",
            description = "Current release-scoped offline music library for Studio playback.",
            tracks = tracks,
        )
    }

    // ── Dark Side of the Spoon ────────────────────────────────────────────────

    private fun darkSideOfTheSpoon(): Album {
        val titles = listOf(
            "Silver Spoon Overture", "Clinic After Midnight", "Velvet Waiting Room",
            "X-Ray Lullaby", "Paper Wristband", "Dim Hallway Echo",
            "Blue Fluorescent", "Mercury Cart", "Quiet Pager",
            "Soft Tile Rain", "Night Shift Carousel", "Moonlit Check-In",
            "Last Cup of Coffee", "Static Discharge", "Morning Spoonlight",
        )
        val tracks = titles.mapIndexed { index, title ->
            val n = 49 + index
            val padded = n.toString().padStart(2, '0')
            Track(
                id = "dark-side-$padded",
                title = title,
                assetPath = "audio/music/dark_side_of_the_spoon/music-track-$padded.mp3",
                trackNumber = n,
                albumId = "dark-side-of-the-spoon",
            )
        }
        return Album(
            id = "dark-side-of-the-spoon",
            title = "Dark Side of the Spoon",
            artist = "Sterling Sound Team",
            description = "Moody album with nocturnal clinic-pop textures, soft percussion, and a slightly surreal after-hours glow.",
            tracks = tracks,
        )
    }

    // ── Groove ────────────────────────────────────────────────────────────────

    private fun groove(): Album {
        val titles = listOf(
            "Open Floor", "Easy Motion", "Sidewalk Bounce", "Corner Brass",
            "Late Bus Shuffle", "Velvet Kick", "Pocket Rhythm", "After School Jam",
            "City Step", "Handclap Weather", "Warm Turntable", "Neon Two-Step",
            "Sunday Sneakers", "Scooter Parade", "Downtown Swing", "Bright Side Loop",
            "Backbeat Picnic", "Roller Disco Breeze", "Midday Strut", "Passing Parade",
            "Window Seat Funk", "Playground Horns", "Golden Side Street", "Lazy Snare",
            "Uptown Smile", "Skate Key", "Blue Sky Pocket", "Magnet Shoes",
            "Bounce Signal", "Lunch Break Groove", "Soft Amp Sunset", "Last Light Boogie",
            "Home Stretch",
        )
        val tracks = titles.mapIndexed { index, title ->
            val n = 64 + index
            val padded = n.toString().padStart(2, '0')
            // Tracks 66–82 live in a groove subdirectory in the RN source; mirror that here.
            val subdir = if (n in 66..82) "groove/" else ""
            Track(
                id = "groove-$padded",
                title = title,
                assetPath = "audio/music/${subdir}music-track-$padded.mp3",
                trackNumber = n,
                albumId = "groove",
            )
        }
        return Album(
            id = "groove",
            title = "Groove",
            artist = "Sterling Sound Team",
            description = "Rhythm-forward album built around bright movement, casual bounce, and easy daytime energy.",
            tracks = tracks,
        )
    }

    // ── Standup ───────────────────────────────────────────────────────────────

    private fun standup(): Album {
        val titles = listOf(
            "Bladder",
            "Doing well",
            "Fatigue",
            "Full time job",
            "Helpful",
            "Invisible",
            "Uncertainty",
            "Unhelpful",
            "You look great",
        )
        val tracks = titles.mapIndexed { index, title ->
            val padded = (index + 1).toString().padStart(2, '0')
            val filename = "$title.mp3"
            Track(
                id = "st$padded",
                title = title,
                assetPath = "audio/music/standup/$filename",
                trackNumber = index + 1,
                albumId = "standup",
            )
        }
        return Album(
            id = "standup",
            title = "Standup",
            artist = "Sterling",
            description = "A standup comedy album about the real, unfiltered experience of living with MS.",
            tracks = tracks,
        )
    }

    // ── Neural Garden ─────────────────────────────────────────────────────────

    private fun neuralGarden(): Album {
        val titlePairs = listOf(
            "Signal Seed" to "Signal Bloom",
            "Quiet Circuit" to "Quiet Canopy",
            "Glass Moss" to "Glass Rain",
            "Soft Relay" to "Soft Meadow",
            "Circuit Fern" to "Circuit Flower",
            "Drift Root" to "Drift Petal",
            "Lumen Soil" to "Lumen Grove",
            "Pulse Dew" to "Pulse Shade",
            "Velvet Branch" to "Velvet Light",
            "Mirror Leaf" to "Mirror Sky",
            "Still Current" to "Still Orchard",
            "Midnight Vine" to "Midnight Bloom",
            "Warm Static" to "Warm Field",
            "Hidden River" to "Hidden Lantern",
            "Afterglow Garden" to "Afterglow Sleep",
        )
        val tracks = titlePairs.flatMapIndexed { index, (titleA, titleB) ->
            val padded = (index + 1).toString().padStart(2, '0')
            listOf(
                Track(
                    id = "ng${padded}a",
                    title = titleA,
                    assetPath = "audio/music/neural_garden/ng${padded}a.mp3",
                    trackNumber = index * 2 + 1,
                    albumId = "neural-garden",
                ),
                Track(
                    id = "ng${padded}b",
                    title = titleB,
                    assetPath = "audio/music/neural_garden/ng${padded}b.mp3",
                    trackNumber = index * 2 + 2,
                    albumId = "neural-garden",
                ),
            )
        }
        return Album(
            id = "neural-garden",
            title = "Neural Garden",
            artist = "Sterling Sound Team",
            description = "Ambient album with soft synthetic textures and calm late-night garden energy.",
            tracks = tracks,
        )
    }
}
