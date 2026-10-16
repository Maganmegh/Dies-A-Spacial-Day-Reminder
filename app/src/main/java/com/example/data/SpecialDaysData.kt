package com.example.data

import com.example.model.SpecialDayItem

object SpecialDaysData {
    val items: List<SpecialDayItem> = listOf(
        // National Days & Observances
        SpecialDayItem(
            id = "nat_republic",
            nameEn = "Republic Day",
            nameHi = "गणतंत्र दिवस",
            day = 26,
            month = 1,
            categoryEn = "National",
            categoryHi = "राष्ट्रीय दिवस",
            descriptionEn = "Commemorates the enactment of the Constitution of India in 1950.",
            descriptionHi = "1950 में भारत के संविधान के लागू होने का पावन राष्ट्रीय पर्व।"
        ),
        SpecialDayItem(
            id = "nat_independence",
            nameEn = "Independence Day",
            nameHi = "स्वतंत्रता दिवस",
            day = 15,
            month = 8,
            categoryEn = "National",
            categoryHi = "राष्ट्रीय दिवस",
            descriptionEn = "Celebration of India's independence from British rule in 1947.",
            descriptionHi = "1947 में भारत की स्वतंत्रता की स्मृति में राष्ट्रीय उत्सव।"
        ),
        SpecialDayItem(
            id = "nat_gandhi",
            nameEn = "Gandhi Jayanti",
            nameHi = "गांधी जयंती",
            day = 2,
            month = 10,
            categoryEn = "National",
            categoryHi = "राष्ट्रीय दिवस",
            descriptionEn = "Birth anniversary of Mahatma Gandhi, celebrated as International Day of Non-Violence.",
            descriptionHi = "राष्ट्रपिता महात्मा गांधी की जयंती व अंतरराष्ट्रीय अहिंसा दिवस।"
        ),
        SpecialDayItem(
            id = "nat_constitution",
            nameEn = "Constitution Day",
            nameHi = "संविधान दिवस",
            day = 26,
            month = 11,
            categoryEn = "National",
            categoryHi = "राष्ट्रीय दिवस",
            descriptionEn = "Commemorates the adoption of the Constitution of India in 1949.",
            descriptionHi = "भारत के संविधान को अपनाने की स्मृति में मनाया जाने वाला दिवस।"
        ),
        SpecialDayItem(
            id = "nat_youth",
            nameEn = "National Youth Day",
            nameHi = "राष्ट्रीय युवा दिवस",
            day = 12,
            month = 1,
            categoryEn = "National",
            categoryHi = "राष्ट्रीय दिवस",
            descriptionEn = "Birth anniversary of Swami Vivekananda.",
            descriptionHi = "स्वामी विवेकानंद की जयंती पर युवाओं को समर्पित पर्व।"
        ),

        // Major Festivals & Cultural Celebrations
        SpecialDayItem(
            id = "fest_new_year",
            nameEn = "New Year's Day",
            nameHi = "नव वर्ष दिवस",
            day = 1,
            month = 1,
            categoryEn = "Festival",
            categoryHi = "त्योहार",
            descriptionEn = "First day of the Gregorian calendar, celebrated worldwide.",
            descriptionHi = "ग्रेगोरियन कैलेंडर का प्रथम दिन व नई शुरुआत का उत्सव।"
        ),
        SpecialDayItem(
            id = "fest_makar_sankranti",
            nameEn = "Makar Sankranti / Pongal",
            nameHi = "मकर संक्रांति / पोंगल",
            day = 14,
            month = 1,
            categoryEn = "Festival",
            categoryHi = "त्योहार",
            descriptionEn = "Solar transition festival celebrating the harvest across India.",
            descriptionHi = "सूर्य के मकर राशि में प्रवेश और फसल आगमन का पावन पर्व।"
        ),
        SpecialDayItem(
            id = "fest_holi",
            nameEn = "Holi (Festival of Colors)",
            nameHi = "होली (रंगों का त्योहार)",
            day = 25,
            month = 3,
            categoryEn = "Festival",
            categoryHi = "त्योहार",
            descriptionEn = "Joyous celebration of spring, love, and victory of good over evil.",
            descriptionHi = "वसंत आगमन, रंगों, प्रेम और बुराई पर अच्छाई की विजय का उत्सव।"
        ),
        SpecialDayItem(
            id = "fest_rakhi",
            nameEn = "Raksha Bandhan",
            nameHi = "रक्षाबंधन",
            day = 19,
            month = 8,
            categoryEn = "Festival",
            categoryHi = "त्योहार",
            descriptionEn = "Celebration of sibling affection and lifelong protection.",
            descriptionHi = "भाई-बहन के पवित्र प्रेम और रक्षा के वचन का पावन त्योहार।"
        ),
        SpecialDayItem(
            id = "fest_teachers",
            nameEn = "Teachers' Day",
            nameHi = "शिक्षक दिवस",
            day = 5,
            month = 9,
            categoryEn = "Festival",
            categoryHi = "त्योहार",
            descriptionEn = "Honoring teachers on Dr. Sarvepalli Radhakrishnan's birth anniversary.",
            descriptionHi = "डॉ. सर्वपल्ली राधाकृष्णन की जयंती पर गुरुजनों के प्रति सम्मान।"
        ),
        SpecialDayItem(
            id = "fest_children",
            nameEn = "Children's Day",
            nameHi = "बाल दिवस",
            day = 14,
            month = 11,
            categoryEn = "Festival",
            categoryHi = "त्योहार",
            descriptionEn = "Pandit Jawaharlal Nehru's birthday celebrated for the joy and rights of children.",
            descriptionHi = "पंडित जवाहरलाल नेहरू की जयंती पर बच्चों की खुशियों का दिन।"
        ),
        SpecialDayItem(
            id = "fest_diwali",
            nameEn = "Diwali (Deepavali)",
            nameHi = "दीपावली",
            day = 1,
            month = 11,
            categoryEn = "Festival",
            categoryHi = "त्योहार",
            descriptionEn = "Festival of lights symbolizing light over darkness and hope over despair.",
            descriptionHi = "दीपों का महापर्व, अंधकार पर प्रकाश और अज्ञान पर ज्ञान की विजय।"
        ),
        SpecialDayItem(
            id = "fest_christmas",
            nameEn = "Christmas",
            nameHi = "क्रिसमस",
            day = 25,
            month = 12,
            categoryEn = "Festival",
            categoryHi = "त्योहार",
            descriptionEn = "Celebration of joy, goodwill, and gift giving around the world.",
            descriptionHi = "खुशियों, शांति और सौहार्द का विश्वव्यापी पर्व।"
        ),

        // International Days
        SpecialDayItem(
            id = "int_women",
            nameEn = "International Women's Day",
            nameHi = "अंतरराष्ट्रीय महिला दिवस",
            day = 8,
            month = 3,
            categoryEn = "International",
            categoryHi = "अंतरराष्ट्रीय दिवस",
            descriptionEn = "Celebrating social, economic, cultural, and political achievements of women.",
            descriptionHi = "महिलाओं की उपलब्धियों, शक्ति और अधिकारों के सम्मान का दिन।"
        ),
        SpecialDayItem(
            id = "int_earth",
            nameEn = "Earth Day",
            nameHi = "पृथ्वी दिवस",
            day = 22,
            month = 4,
            categoryEn = "International",
            categoryHi = "अंतरराष्ट्रीय दिवस",
            descriptionEn = "Global environmental awareness and protection of mother nature.",
            descriptionHi = "पर्यावरण संरक्षण और धरती माता के प्रति हमारी जिम्मेदारी का दिन।"
        ),
        SpecialDayItem(
            id = "int_yoga",
            nameEn = "International Yoga Day",
            nameHi = "अंतरराष्ट्रीय योग दिवस",
            day = 21,
            month = 6,
            categoryEn = "International",
            categoryHi = "अंतरराष्ट्रीय दिवस",
            descriptionEn = "Promoting holistic health, mindfulness, and inner peace worldwide.",
            descriptionHi = "शारीरिक व मानसिक स्वास्थ्य, शांति और योग के प्रसार का वैश्विक दिन।"
        ),
        SpecialDayItem(
            id = "int_friendship",
            nameEn = "Friendship Day",
            nameHi = "मित्रता दिवस",
            day = 3,
            month = 8,
            categoryEn = "International",
            categoryHi = "अंतरराष्ट्रीय दिवस",
            descriptionEn = "A day dedicated to celebrating trusted bonds and lifelong companions.",
            descriptionHi = "सच्चे मित्रों और आत्मीय रिश्तों को समर्पित खूबसूरत दिन।"
        ),
        SpecialDayItem(
            id = "int_parents",
            nameEn = "Global Parents Day",
            nameHi = "वैश्विक माता-पिता दिवस",
            day = 1,
            month = 6,
            categoryEn = "International",
            categoryHi = "अंतरराष्ट्रीय दिवस",
            descriptionEn = "Honoring parents for their selfless dedication and unconditional love.",
            descriptionHi = "माता-पिता के निःस्वार्थ समर्पण और असीम प्रेम के प्रति कृतज्ञता।"
        )
    )
}
