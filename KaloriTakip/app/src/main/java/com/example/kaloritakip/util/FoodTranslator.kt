package com.example.kaloritakip.util

object FoodTranslator {
    private val englishToTurkish = mapOf(
        "egg" to "yumurta",
        "chicken" to "tavuk",
        "beef" to "et",
        "milk" to "süt",
        "apple" to "elma",
        "banana" to "muz",
        "orange" to "portakal",
        "bread" to "ekmek",
        "rice" to "pirinç",
        "cheese" to "peynir",
        "yogurt" to "yoğurt",
        "potato" to "patates",
        "tomato" to "domates",
        "carrot" to "havuç",
        "onion" to "soğan",
        "garlic" to "sarımsak",
        "fish" to "balık",
        "shrimp" to "karides",
        "pasta" to "makarna",
        "cereal" to "tahıl",
        "water" to "su",
        "coffee" to "kahve",
        "tea" to "çay",
        "juice" to "meyve suyu",
        "salad" to "salata",
        "soup" to "çorba",
        "grilled chicken" to "ızgara tavuk",
        "chicken breast" to "tavuk göğsü",
        "fried chicken" to "kızarmış tavuk",
        "chicken soup" to "tavuk çorbası",
        "chicken curry" to "tavuklu köri",
        "baked chicken" to "fırında tavuk",
        "chicken salad" to "tavuklu salata",
        "chicken sandwich" to "tavuklu sandviç",
        "chicken wrap" to "tavuk dürüm",
        "chicken nuggets" to "tavuk nugget",
        "steak" to "biftek",
        "beef stew" to "etli yahni",
        "beef burger" to "etli burger",
        "meatballs" to "köfte",
        "beef kebab" to "et şiş",
        "beef stir fry" to "etli sote",
        "fish fillet" to "balık fileto",
        "grilled fish" to "ızgara balık",
        "fish and chips" to "balık ve patates kızartması",
        "seafood pasta" to "deniz ürünlü makarna",
        "salmon" to "somon",
        "tuna" to "ton balığı",
        "seafood platter" to "deniz ürünleri tabağı",
        "vegetable soup" to "sebze çorbası",
        "tomato soup" to "domates çorbası",
        "lentil soup" to "mercimek çorbası",
        "mushroom soup" to "mantar çorbası",
        "caesar salad" to "sezar salatası",
        "greek salad" to "yunan salatası",
        "tuna salad" to "ton balıklı salata",
        "spaghetti bolognese" to "bolonya soslu spagetti",
        "mac and cheese" to "peynirli makarna",
        "lasagna" to "lazanya",
        "pasta carbonara" to "karbonara",
        "pasta with meatballs" to "köfteli makarna",
        "pizza" to "pizza",
        "pepperoni pizza" to "sucuklu pizza",
        "vegetarian pizza" to "vejetaryen pizza",
        "cheese pizza" to "peynirli pizza",
        "hawaiian pizza" to "hawaii pizza",
        "kebab" to "kebap",
        "döner" to "döner",
        "baklava" to "baklava",
        "börek" to "börek",
        "pide" to "pide",
        "gözleme" to "gözleme",
        "mantı" to "mantı",
        "köfte" to "köfte",
        "dolma" to "dolma",
        "sarma" to "sarma",
        "imam bayıldı" to "imam bayıldı",
        "karnıyarık" to "karnıyarık",
        "ice cream" to "dondurma",
        "chocolate" to "çikolata",
        "cake" to "pasta",
        "cookie" to "kurabiye",
        "pudding" to "puding",
        "french fries" to "patates kızartması",
        "chips" to "cips",
        "popcorn" to "patlamış mısır",
        "pancake" to "krep",
        "waffle" to "waffle",
        "soda" to "gazoz",
        "cola" to "kola",
        "lemonade" to "limonata",
        "milkshake" to "milkshake",
        "smoothie" to "smoothie",
        "beer" to "bira",
        "wine" to "şarap",
        "energy drink" to "enerji içeceği",
        "hot chocolate" to "sıcak çikolata",
        "omelette" to "omlet",
        "scrambled eggs" to "menemen",
        "boiled egg" to "haşlanmış yumurta",
        "fried egg" to "kızarmış yumurta",
        "toast" to "tost",
        "cereal with milk" to "sütlü gevrek",
        "pancakes" to "pankek",
        "croissant" to "kruvasan",
        "bagel" to "simit"
    )

    private val turkishToEnglish = englishToTurkish.entries.associate { (k, v) -> v to k }

    fun toTurkish(englishWord: String?): String {
        if (englishWord.isNullOrBlank()) {
            return ""
        }
        val exactMatch = englishToTurkish[englishWord.lowercase()]
        if (exactMatch != null) return exactMatch

        val words = englishWord.split(" ")
        if (words.size > 1) {
            val translatedWords = words.map { word ->
                englishToTurkish[word.lowercase()] ?: word
            }
            return translatedWords.joinToString(" ")
        }

        return englishWord
    }


    fun toEnglish(turkishWord: String): String {
        return turkishToEnglish[turkishWord.lowercase()] ?: turkishWord
    }
}