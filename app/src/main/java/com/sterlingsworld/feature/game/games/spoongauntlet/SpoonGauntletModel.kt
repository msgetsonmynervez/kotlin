package com.sterlingsworld.feature.game.games.spoongauntlet

import com.sterlingsworld.R

enum class SpoonGauntletScreen {
    TITLE,
    INTRO,
    HERO_SELECT,
    BOT_INTRO,
    BOT_SELECT,
    AGENDA,
    SCENE,
    FLARE_UP,
    RESULT,
}

enum class GauntletHero(
    val label: String,
    val title: String,
    val perk: String,
    val artRes: Int,
) {
    JANE(
        label = "Jane Doe",
        title = "The Detective",
        perk = "Bureaucracy Shield (-1 Spoon on admin tasks)",
        artRes = R.drawable.spoon_hero_jane,
    ),
    JOHN(
        label = "John Doe",
        title = "The Sentinel",
        perk = "Zen Endurance (lower flare-up chance)",
        artRes = R.drawable.spoon_hero_john,
    ),
}

enum class GauntletBot(
    val label: String,
    val perk: String,
    val welcome: String,
    val artRes: Int,
) {
    IBOT(
        label = "i-bot",
        perk = "-1 cost on all physical tasks",
        welcome = "i-bot: Optimization link established. We will conserve energy where we can.",
        artRes = R.drawable.spoon_bot_ibot,
    ),
    MSOFT(
        label = "M-S0ft",
        perk = "One Mandatory Update cost bypass",
        welcome = "M-S0ft: Welcome. Let's keep today's routine stable and low-drama.",
        artRes = R.drawable.spoon_bot_msoft,
    ),
    G00GL(
        label = "G00gl-",
        perk = "Start with +2 extra Spoons",
        welcome = "G00gl-: Forecast loaded. A steadier route gives you the best odds.",
        artRes = R.drawable.spoon_bot_g00gl,
    ),
}

enum class GauntletAgenda(
    val label: String,
    val modifier: Int,
    val subtitle: String,
) {
    HUSTLE("The Hustle", -2, "High risk"),
    EQUILIBRIUM("Equilibrium", 0, "Standard load"),
    SURVIVAL("Survival", 2, "Maximum caution"),
}

enum class GauntletSceneType {
    BUREAUCRACY,
    SOCIAL,
    PHYSICAL,
}

enum class GauntletChoiceTone {
    MARTYR,
    SAVAGE,
    BOT,
    NEUTRAL,
}

data class GauntletChoice(
    val text: String,
    val cost: Int,
    val karmaDelta: Int,
    val tone: GauntletChoiceTone,
)

data class GauntletScene(
    val title: String,
    val description: String,
    val subtitle: String,
    val type: GauntletSceneType,
    val artRes: Int?,
    val choices: List<GauntletChoice>,
    val botChoices: Map<GauntletBot, GauntletChoice>,
)

data class SpoonGauntletResult(
    val won: Boolean = false,
    val title: String = "",
    val message: String = "",
)

data class SpoonGauntletUiState(
    val screen: SpoonGauntletScreen = SpoonGauntletScreen.TITLE,
    val hero: GauntletHero? = null,
    val bot: GauntletBot? = null,
    val agenda: GauntletAgenda? = null,
    val currentSceneIndex: Int = 0,
    val spoons: Int = 0,
    val maxSpoons: Int = 0,
    val karma: Int = 50,
    val mSoftUsed: Boolean = false,
    val eventMessage: String? = null,
    val result: SpoonGauntletResult? = null,
)

val SPOON_GAUNTLET_SCENES = listOf(
    GauntletScene(
        title = "09:30 AM - The Pharmacy Loop",
        subtitle = "Medication pickup and queue friction",
        description = "The automated system loops. Penelope puts you back on hold to 'check the back.' The smooth corporate jazz is physically eroding your sanity. Your meds are hostage.",
        type = GauntletSceneType.BUREAUCRACY,
        artRes = R.drawable.spoon_scene_pharmacy,
        choices = listOf(
            GauntletChoice("Politely listen to 45 minutes of smooth jazz. (-2 Spoons)", 2, 20, GauntletChoiceTone.MARTYR),
            GauntletChoice("Fake a loud medical emergency to bypass the queue. (-0 Spoons)", 0, -20, GauntletChoiceTone.SAVAGE),
        ),
        botChoices = mapOf(
            GauntletBot.IBOT to GauntletChoice("[i-bot] Optimize call by screaming binary noise at high pitch. (-1 Spoon)", 1, 0, GauntletChoiceTone.BOT),
            GauntletBot.MSOFT to GauntletChoice("[M-S0ft] Force a Windows 95 update on Penelope's register. (-0 Spoons)", 0, -10, GauntletChoiceTone.BOT),
            GauntletBot.G00GL to GauntletChoice("[G00gl-] Blackmail Penelope by reading her incognito searches aloud. (-1 Spoon)", 1, -15, GauntletChoiceTone.BOT),
        ),
    ),
    GauntletScene(
        title = "11:00 AM - Owen's MLM Pitch",
        subtitle = "A pushy conversation burns energy",
        description = "Owen corners you in the clinic waiting room. He thrusts a bottle of 'Cloudy Magic Water' into your personal space. It smells like patchouli and financial ruin. He claims it cures nerve damage and bad credit.",
        type = GauntletSceneType.SOCIAL,
        artRes = R.drawable.spoon_scene_owen,
        choices = listOf(
            GauntletChoice("Buy \$40 of Cloudy Magic Water to make him leave. (-2 Spoons)", 2, 20, GauntletChoiceTone.MARTYR),
            GauntletChoice("Hiss like a feral cat until he slowly backs away. (-0 Spoons)", 0, -20, GauntletChoiceTone.SAVAGE),
        ),
        botChoices = mapOf(
            GauntletBot.IBOT to GauntletChoice("[i-bot] Calculate and loudly recite his exact odds of bankruptcy. (-1 Spoon)", 1, 0, GauntletChoiceTone.BOT),
            GauntletBot.MSOFT to GauntletChoice("[M-S0ft] Pretend your audio drivers crashed and stare blankly. (-0 Spoons)", 0, -10, GauntletChoiceTone.BOT),
            GauntletBot.G00GL to GauntletChoice("[G00gl-] Cast targeted ads for 'Cult Deprogramming' to his phone. (-1 Spoon)", 1, -15, GauntletChoiceTone.BOT),
        ),
    ),
    GauntletScene(
        title = "01:00 PM - Tammy's Kale Sermon",
        subtitle = "Wellness chatter you did not ask for",
        description = "Tragic Tammy blocks aisle 4. She insists your central nervous system lesions are just 'repressed veggie-energy' and tries to force-feed you raw, unwashed kale while explaining her new wellness podcast.",
        type = GauntletSceneType.SOCIAL,
        artRes = R.drawable.spoon_scene_tammy,
        choices = listOf(
            GauntletChoice("Accept the kale and nod agreeably at her 'vibes' theory. (-2 Spoons)", 2, 20, GauntletChoiceTone.MARTYR),
            GauntletChoice("Slowly bite into a stick of butter while maintaining eye contact. (-0 Spoons)", 0, -20, GauntletChoiceTone.SAVAGE),
        ),
        botChoices = mapOf(
            GauntletBot.IBOT to GauntletChoice("[i-bot] Physically categorize kale as 'biowaste' and discard it. (-1 Spoon)", 1, 0, GauntletChoiceTone.BOT),
            GauntletBot.MSOFT to GauntletChoice("[M-S0ft] Initiate 'Safe Mode' and play dead in the aisle. (-0 Spoons)", 0, -10, GauntletChoiceTone.BOT),
            GauntletBot.G00GL to GauntletChoice("[G00gl-] Show her WebMD results proving kale causes sadness. (-1 Spoon)", 1, -15, GauntletChoiceTone.BOT),
        ),
    ),
    GauntletScene(
        title = "02:30 PM - Parking Lot Standoff",
        subtitle = "Accessibility conflict in the lot",
        description = "Val is blocking the blue handicap spot with her massive SUV. 'You look fine to me, stop being lazy!' she shouts out the window, aggressively sipping a violently pink iced coffee.",
        type = GauntletSceneType.BUREAUCRACY,
        artRes = R.drawable.spoon_scene_parking,
        choices = listOf(
            GauntletChoice("Apologize for existing and park in the next zip code. (-3 Spoons)", 3, 20, GauntletChoiceTone.MARTYR),
            GauntletChoice("Tell Val her personality is a handicap and block her in. (-0 Spoons)", 0, -20, GauntletChoiceTone.SAVAGE),
        ),
        botChoices = mapOf(
            GauntletBot.IBOT to GauntletChoice("[i-bot] Deploy theoretical spike strip to optimize parking availability. (-1 Spoon)", 1, 0, GauntletChoiceTone.BOT),
            GauntletBot.MSOFT to GauntletChoice("[M-S0ft] Error 404: Val's empathy not found. Ignore and park anyway. (-0 Spoons)", 0, -10, GauntletChoiceTone.BOT),
            GauntletBot.G00GL to GauntletChoice("[G00gl-] Reroute Val's active GPS to the local landfill. (-1 Spoon)", 1, -15, GauntletChoiceTone.BOT),
        ),
    ),
    GauntletScene(
        title = "04:30 PM - The BBQ Heatwave",
        subtitle = "Social time in draining weather",
        description = "Cousin Greg insists you sit in a sagging nylon camping chair in the direct 98-degree sun to 'be social.' The heat waves are radiating off the pavement. You can actually feel your myelin melting.",
        type = GauntletSceneType.PHYSICAL,
        artRes = R.drawable.spoon_scene_bbq,
        choices = listOf(
            GauntletChoice("Sit in the chair and literally melt into the asphalt for the family. (-3 Spoons)", 3, 30, GauntletChoiceTone.MARTYR),
            GauntletChoice("Steal Greg's sunglasses and lock yourself in the bathroom. (-0 Spoons)", 0, -20, GauntletChoiceTone.SAVAGE),
        ),
        botChoices = mapOf(
            GauntletBot.IBOT to GauntletChoice("[i-bot] Spray Greg with the garden hose to regulate ambient temp. (-1 Spoon)", 1, 0, GauntletChoiceTone.BOT),
            GauntletBot.MSOFT to GauntletChoice("[M-S0ft] Ctrl+Alt+Delete Greg from your social circle and go inside. (-0 Spoons)", 0, -10, GauntletChoiceTone.BOT),
            GauntletBot.G00GL to GauntletChoice("[G00gl-] Cast a 10-hour loop of screaming on his TV as a distraction. (-1 Spoon)", 1, -15, GauntletChoiceTone.BOT),
        ),
    ),
    GauntletScene(
        title = "06:00 PM - Uncle Bob's Tablet",
        subtitle = "A family device lands on your plate",
        description = "Uncle Bob shoves a sticky tablet in your face. It's infested with malware, crypto miners, and bizarre pop-ups. 'Can you just fix the internet real quick?' he asks, wiping BBQ sauce on his shirt.",
        type = GauntletSceneType.SOCIAL,
        artRes = R.drawable.spoon_scene_unclebob,
        choices = listOf(
            GauntletChoice("Scrub the malware while he explains cryptocurrencies to you. (-2 Spoons)", 2, 20, GauntletChoiceTone.MARTYR),
            GauntletChoice("Snap the tablet over your knee. 'It was too far gone.' (-0 Spoons)", 0, -20, GauntletChoiceTone.SAVAGE),
        ),
        botChoices = mapOf(
            GauntletBot.IBOT to GauntletChoice("[i-bot] Microwave the device to cleanse the digital infection. (-1 Spoon)", 1, 0, GauntletChoiceTone.BOT),
            GauntletBot.MSOFT to GauntletChoice("[M-S0ft] Install BonziBuddy to establish absolute dominance. (-0 Spoons)", 0, -10, GauntletChoiceTone.BOT),
            GauntletBot.G00GL to GauntletChoice("[G00gl-] Forward his browser history directly to the family group chat. (-1 Spoon)", 1, -15, GauntletChoiceTone.BOT),
        ),
    ),
    GauntletScene(
        title = "07:30 PM - The HOA Zoom Call",
        subtitle = "Advocacy fatigue on video chat",
        description = "Margaret, the HOA president, is on a video call denying your accessibility ramp because the railing isn't 'community standard Desert Sand.' She takes a slow, agonizing sip of white wine.",
        type = GauntletSceneType.BUREAUCRACY,
        artRes = R.drawable.spoon_scene_hoa,
        choices = listOf(
            GauntletChoice("Agree to repaint the ramp and apologize to the board. (-2 Spoons)", 2, 20, GauntletChoiceTone.MARTYR),
            GauntletChoice("Hold a megaphone to the microphone and chant ADA regulations. (-0 Spoons)", 0, -20, GauntletChoiceTone.SAVAGE),
        ),
        botChoices = mapOf(
            GauntletBot.IBOT to GauntletChoice("[i-bot] DDoS the HOA servers. Ramp approved by default. (-1 Spoon)", 1, 0, GauntletChoiceTone.BOT),
            GauntletBot.MSOFT to GauntletChoice("[M-S0ft] Apply an unremovable potato filter to Margaret's camera. (-0 Spoons)", 0, -10, GauntletChoiceTone.BOT),
            GauntletBot.G00GL to GauntletChoice("[G00gl-] Share screen showing her unpaid property taxes. (-1 Spoon)", 1, -15, GauntletChoiceTone.BOT),
        ),
    ),
    GauntletScene(
        title = "08:30 PM - The 12-Page Text",
        subtitle = "Long messages after a full day",
        description = "Karen sends a 12-page text message wall detailing how crystal healing and essential oils will 'align your chakras.' The screen glare in the dark room is a physical assault on your exhausted eyes.",
        type = GauntletSceneType.SOCIAL,
        artRes = R.drawable.spoon_scene_texts,
        choices = listOf(
            GauntletChoice("Read it all and draft a heartfelt 'Thank you so much!' (-2 Spoons)", 2, 20, GauntletChoiceTone.MARTYR),
            GauntletChoice("Reply with 'K'. (-0 Spoons)", 0, -20, GauntletChoiceTone.SAVAGE),
        ),
        botChoices = mapOf(
            GauntletBot.IBOT to GauntletChoice("[i-bot] Auto-reply with the Wikipedia link to 'Pseudoscience'. (-1 Spoon)", 1, 0, GauntletChoiceTone.BOT),
            GauntletBot.MSOFT to GauntletChoice("[M-S0ft] Corrupt the text thread so her iPhone soft-bricks. (-0 Spoons)", 0, -10, GauntletChoiceTone.BOT),
            GauntletBot.G00GL to GauntletChoice("[G00gl-] Sign her email up for 500 essential oil spam newsletters. (-1 Spoon)", 1, -15, GauntletChoiceTone.BOT),
        ),
    ),
    GauntletScene(
        title = "09:30 PM - The 400-Piece Race Track",
        subtitle = "Parenting joy with a real cost",
        description = "Your son is bouncing with excitement, holding a box of 400 tiny, identical plastic race track pieces. The instructions are a blurred mess. Your hands are shaking violently.",
        type = GauntletSceneType.PHYSICAL,
        artRes = R.drawable.spoon_scene_racetrack,
        choices = listOf(
            GauntletChoice("Assemble it with shaking hands to be the ultimate hero. (-4 Spoons)", 4, 40, GauntletChoiceTone.MARTYR),
            GauntletChoice("Tell him race cars are bad for the environment and go to sleep. (-0 Spoons)", 0, -20, GauntletChoiceTone.SAVAGE),
        ),
        botChoices = mapOf(
            GauntletBot.IBOT to GauntletChoice("[i-bot] Superglue the pieces into a single abstract orb. Finished. (-1 Spoon)", 1, 0, GauntletChoiceTone.BOT),
            GauntletBot.MSOFT to GauntletChoice("[M-S0ft] Convince him the track is currently buffering and needs time. (-0 Spoons)", 0, -10, GauntletChoiceTone.BOT),
            GauntletBot.G00GL to GauntletChoice("[G00gl-] Order a fully assembled track via 30-minute drone delivery. (-1 Spoon)", 1, -15, GauntletChoiceTone.BOT),
        ),
    ),
    GauntletScene(
        title = "10:30 PM - The Pain Cave",
        subtitle = "Unhelpful advice from the sidelines",
        description = "Dave the trainer yells 'NO PAIN NO GAIN!' through the gym window as you walk by. He gestures to a rusted 50lb iron weight. He clearly has no idea what an autoimmune disease is.",
        type = GauntletSceneType.PHYSICAL,
        artRes = R.drawable.spoon_scene_paincave,
        choices = listOf(
            GauntletChoice("Try to lift the iron and immediately regret all life choices. (-2 Spoons)", 2, 20, GauntletChoiceTone.MARTYR),
            GauntletChoice("Throw a protein shaker at his head and walk out. (-0 Spoons)", 0, -20, GauntletChoiceTone.SAVAGE),
        ),
        botChoices = mapOf(
            GauntletBot.IBOT to GauntletChoice("[i-bot] Calculate Dave's VO2 max and insult his cardiovascular health. (-1 Spoon)", 1, 0, GauntletChoiceTone.BOT),
            GauntletBot.MSOFT to GauntletChoice("[M-S0ft] Blue screen your own body and collapse dramatically. (-0 Spoons)", 0, -10, GauntletChoiceTone.BOT),
            GauntletBot.G00GL to GauntletChoice("[G00gl-] Leave a devastating 1-star Yelp review from the floor. (-1 Spoon)", 1, -15, GauntletChoiceTone.BOT),
        ),
    ),
)
