package com.example.data

import com.example.model.WishMessage

object WishesData {
    val messages: List<WishMessage> = listOf(
        // Birthday - Heartfelt & Warm
        WishMessage(
            id = "bday_1",
            categoryEn = "Birthday",
            categoryHi = "जन्मदिन",
            textEn = "Happy Birthday, {name}! 🎂✨ May your day be loaded with happiness, endless laughter, and memories you'll treasure forever.",
            textHi = "जन्मदिन की ढेरों शुभकामनाएँ, {name}! 🎂✨ ईश्वर आपके जीवन में खुशियों, उत्तम स्वास्थ्य और सफलता की कभी कमी न होने दे।"
        ),
        WishMessage(
            id = "bday_2",
            categoryEn = "Birthday",
            categoryHi = "जन्मदिन",
            textEn = "Wishing the happiest of birthdays to an extraordinary soul! Have an unforgettable year ahead, {name}! 🌟🎉",
            textHi = "आप जैसे नेक और प्यारे इंसान को जन्मदिन की बहुत-बहुत बधाई! यह नया साल आपके हर सपने को साकार करे, {name}! 🌟🎉"
        ),
        WishMessage(
            id = "bday_3",
            categoryEn = "Birthday",
            categoryHi = "जन्मदिन",
            textEn = "Another lap around the sun! 🚀 Sending you love, good energy, and heaps of celebration today, {name}!",
            textHi = "सूरज का एक और खूबसूरत चक्कर पूरा हुआ! 🚀 आज का दिन आपके लिए ढेर सारी खुशियाँ और उत्सव लेकर आए, {name}!"
        ),

        // Friends
        WishMessage(
            id = "friend_1",
            categoryEn = "Friends",
            categoryHi = "मित्र",
            textEn = "Happy Birthday to my partner in crime and truest friend, {name}! Cheers to more wild adventures and late night talks! 🥂🔥",
            textHi = "यार {name}, जन्मदिन की हार्दिक बधाई! तेरी दोस्ती मेरी ज़िंदगी का सबसे खूबसूरत तोहफा है। हमेशा ऐसे ही मुस्कुराते रहो! 🥂🔥"
        ),
        WishMessage(
            id = "friend_2",
            categoryEn = "Friends",
            categoryHi = "मित्र",
            textEn = "To someone who knows all my secrets and still hangs out with me: Happy Birthday, {name}! Party hard today! 🥳🍕",
            textHi = "हर मुश्किल में साथ निभाने वाले मेरे सबसे अजीज दोस्त {name} को जन्मदिन की अनंत शुभकामनाएँ! आज पार्टी तो बनती है! 🥳🍕"
        ),

        // Funny
        WishMessage(
            id = "funny_1",
            categoryEn = "Funny",
            categoryHi = "मजेदार",
            textEn = "Happy Birthday, {name}! Don't count the candles, just enjoy the glow! (And make sure you don't set off the fire alarm!) 🕯️😜",
            textHi = "जन्मदिन मुबारक {name}! उम्र मत गिनो, बस केक खाओ! मोमबत्तियाँ इतनी हो गई हैं कि दमकल बुलाना न पड़ जाए! 🕯️😜"
        ),
        WishMessage(
            id = "funny_2",
            categoryEn = "Funny",
            categoryHi = "मजेदार",
            textEn = "Happy Birthday, {name}! You're not getting older, you're just leveling up into a classic vintage edition! 🎮🕹️",
            textHi = "हैप्पी बर्थडे {name}! तुम बूढ़े नहीं हो रहे, बल्कि एक विंटेज क्लासिक एडिशन में अपग्रेड हो रहे हो! 🎮🕹️"
        ),

        // Emotional & Deep
        WishMessage(
            id = "emo_1",
            categoryEn = "Emotional",
            categoryHi = "भावनात्मक",
            textEn = "{name}, your presence brings warmth and light to everyone around you. Thank you for being who you are. Happy Birthday! 💖",
            textHi = "{name}, आपकी मौजूदगी हम सभी के जीवन में रोशनी और सुकून भर देती है। हमेशा हमारे साथ रहने के लिए धन्यवाद। जन्मदिन मुबारक! 💖"
        ),
        WishMessage(
            id = "emo_2",
            categoryEn = "Emotional",
            categoryHi = "भावनात्मक",
            textEn = "Some people make the world a gentler, kinder place simply by existing in it. You are one of them, {name}. Happy Birthday! 🌸",
            textHi = "कुछ लोग सिर्फ अपनी मौजूदगी से इस दुनिया को और खूबसूरत बना देते हैं, आप उन्हीं में से एक हैं {name}। जन्मदिन की दिली दुआएं! 🌸"
        ),

        // Family & Parents
        WishMessage(
            id = "fam_1",
            categoryEn = "Family",
            categoryHi = "परिवार",
            textEn = "Happy Birthday to the pillar of our family, {name}! Your wisdom, guidance, and endless love mean everything to us. 🏡❤️",
            textHi = "हमारे परिवार की शक्ति और प्रेरणा {name} को जन्मदिन की हार्दिक शुभकामनाएँ! आपका प्यार और मार्गदर्शन अनमोल है। 🏡❤️"
        ),
        WishMessage(
            id = "fam_2",
            categoryEn = "Family",
            categoryHi = "परिवार",
            textEn = "Blessed to have you in my life, {name}. Wishing you radiant health, peace of mind, and joy today and always! 🙏✨",
            textHi = "आपके स्नेह की छाया में जीवन सदैव महकता रहे। ईश्वर आपको दीर्घायु और निरोगी काया प्रदान करे, {name}! 🙏✨"
        ),

        // Anniversary
        WishMessage(
            id = "anni_1",
            categoryEn = "Anniversary",
            categoryHi = "सालगिरह",
            textEn = "Happy Anniversary, {name}! 🥂 May your beautiful bond of love, companionship, and mutual respect grow stronger each passing year.",
            textHi = "शादी की सालगिरह की कोटि-कोटि बधाई, {name}! 🥂 प्रेम, विश्वास और सम्मान की यह सुंदर डोर हर साल और भी मजबूत हो।"
        ),
        WishMessage(
            id = "anni_2",
            categoryEn = "Anniversary",
            categoryHi = "सालगिरह",
            textEn = "Wishing an extraordinary couple a magnificent anniversary! Here's to love, laughter, and your happily ever after! 💑💐",
            textHi = "एक आदर्श और सदाबहार जोड़ी को वैवाहिक वर्षगांठ की ढेरों शुभकामनाएँ! सदा मुस्कुराते रहें और साथ निभाते रहें! 💑💐"
        ),

        // Formal & Professional
        WishMessage(
            id = "formal_1",
            categoryEn = "Formal",
            categoryHi = "औपचारिक",
            textEn = "Wishing you a very Happy Birthday, {name}! May the coming year bring you continued success, good health, and prosperous endeavors. 👔📈",
            textHi = "जन्मदिन के शुभ अवसर पर हार्दिक बधाई, {name}! आगामी वर्ष आपके लिए नई उपलब्धियाँ, उत्तम स्वास्थ्य और अपार सफलता लेकर आए। 👔📈"
        ),
        WishMessage(
            id = "formal_2",
            categoryEn = "Formal",
            categoryHi = "औपचारिक",
            textEn = "Happy Birthday, {name}! It is a privilege working with you. Wishing you peace, prosperity, and great triumphs ahead. 🤝💼",
            textHi = "जन्मदिन की बहुत-बहुत शुभकामनाएँ, {name}! आपके साथ कार्य करना एक प्रेरणादायक अनुभव है। आपके उज्ज्वल भविष्य की कामना करता हूँ। 🤝💼"
        ),

        // Festivals
        WishMessage(
            id = "fest_1",
            categoryEn = "Festival",
            categoryHi = "त्योहार",
            textEn = "Wishing you and your family a joyous celebration, {name}! May this auspicious occasion bring peace, harmony, and prosperity into your home! 🪔✨",
            textHi = "इस पावन पर्व पर आपको और आपके पूरे परिवार को हार्दिक मंगलकामनाएँ, {name}! आपका घर-आँगन खुशियों और समृद्धि से महकता रहे! 🪔✨"
        )
    )

    fun getCategoriesEn(): List<String> = listOf("All", "Birthday", "Friends", "Family", "Anniversary", "Emotional", "Funny", "Formal", "Festival")
    fun getCategoriesHi(): List<String> = listOf("सभी", "जन्मदिन", "मित्र", "परिवार", "सालगिरह", "भावनात्मक", "मजेदार", "औपचारिक", "त्योहार")
}
