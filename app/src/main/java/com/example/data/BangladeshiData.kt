package com.example.data

import com.example.model.UserProfile

object BangladeshiData {

    val divisions = listOf(
        "Dhaka",
        "Chattogram",
        "Sylhet",
        "Rajshahi",
        "Khulna",
        "Barisal",
        "Rangpur",
        "Mymensingh"
    )

    val divisionDistricts = mapOf(
        "Dhaka" to listOf("Dhaka City", "Gazipur", "Narayanganj", "Tangail", "Narsingdi", "Faridpur"),
        "Chattogram" to listOf("Chattogram City", "Cox's Bazar", "Cumilla", "Noakhali", "Feni", "Brahmanbaria"),
        "Sylhet" to listOf("Sylhet City", "Moulvibazar", "Habiganj", "Sunamganj"),
        "Rajshahi" to listOf("Rajshahi City", "Bogura", "Pabna", "Sirajganj", "Naogaon"),
        "Khulna" to listOf("Khulna City", "Jashore", "Kushtia", "Satkhira", "Bagerhat"),
        "Barisal" to listOf("Barisal City", "Bhola", "Patuakhali", "Pirojpur"),
        "Rangpur" to listOf("Rangpur City", "Dinajpur", "Kurigram", "Gaibandha"),
        "Mymensingh" to listOf("Mymensingh City", "Jamalpur", "Netrokona", "Sherpur")
    )

    val popularInterests = listOf(
        "☕ Adda & Cha",
        "📚 Bangla Literature",
        "🎸 Music & Guitar",
        "✈️ Traveling Sajek & Cox's Bazar",
        "🍲 Biryani & Street Food",
        "📸 Photography",
        "🎨 Art & Painting",
        "💻 Coding & Tech",
        "🏏 Cricket Lover",
        "🎬 World Cinema",
        "🌿 Gardening & Nature",
        "💪 Fitness & Yoga",
        "🎤 Singing Rabindra Sangeet",
        "☕ Coffee Dates",
        "🐈 Cat Lover"
    )

    val popularHobbies = listOf(
        "Playing Acoustic Guitar",
        "Reading Humayun Ahmed & Rabindranath",
        "Rooftop Gardening",
        "Cooking Traditional Bengali Dishes",
        "Night Rides across Dhaka",
        "Street & Portrait Photography",
        "Binge-watching Romantic Dramas",
        "Solo Backpacking in Sylhet"
    )

    // Curated high-quality seed profiles for the dating app
    val seedProfiles: List<UserProfile> = listOf(
        UserProfile(
            uid = "seed_user_1",
            fullName = "Ananya Roy",
            email = "ananya.roy@example.com",
            phoneNumber = "+8801711223344",
            age = 23,
            gender = "Female",
            division = "Dhaka",
            district = "Dhaka City",
            city = "Dhanmondi",
            education = "BSc in Architecture, BUET",
            profession = "Junior Architect",
            relationshipStatus = "Single",
            interests = listOf("☕ Adda & Cha", "🎨 Art & Painting", "📚 Bangla Literature", "📸 Photography"),
            hobbies = listOf("Watercolour sketching", "Reading poetry", "Visiting historical cafes"),
            bio = "ধানমন্ডি লেকে বিকেলের হাঁটা আর চায়ের কাপে জমজমাট আড্ডা পছন্দ করি। জীবনটাকে সহজ ও সুন্দর দৃষ্টিতে দেখতে চাই।",
            partnerPreferences = "Looking for someone open-minded, creative, who loves long conversations and tea.",
            profilePhotoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&q=80",
            photos = listOf(
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&q=80",
                "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&q=80"
            ),
            isVerified = true,
            isPremium = true,
            isOnline = true
        ),
        UserProfile(
            uid = "seed_user_2",
            fullName = "Tanvir Ahmed",
            email = "tanvir.ahmed@example.com",
            phoneNumber = "+8801811556677",
            age = 26,
            gender = "Male",
            division = "Dhaka",
            district = "Dhaka City",
            city = "Gulshan-2",
            education = "BSc in CSE, North South University",
            profession = "Senior Software Engineer",
            relationshipStatus = "Single",
            interests = listOf("💻 Coding & Tech", "✈️ Traveling Sajek & Cox's Bazar", "🎸 Music & Guitar", "☕ Coffee Dates"),
            hobbies = listOf("Playing indie rock on guitar", "Exploring remote hill tracts", "Weekend gaming"),
            bio = "দিনশেষে ভালো মনের একজন বন্ধু ও প্রিয় মানুষ দরকার, যার সাথে মন খুলে হাসা যায়। সাজেকের মেঘ দেখতে ভীষণ ভালোবাসি।",
            partnerPreferences = "Seeking someone ambitious, kind, who loves spontaneous travel trips and good music.",
            profilePhotoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&q=80",
            photos = listOf(
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&q=80",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&q=80"
            ),
            isVerified = true,
            isPremium = false,
            isOnline = true
        ),
        UserProfile(
            uid = "seed_user_3",
            fullName = "Nusrat Jahan Meem",
            email = "nusrat.meem@example.com",
            phoneNumber = "+8801911889900",
            age = 24,
            gender = "Female",
            division = "Chattogram",
            district = "Chattogram City",
            city = "Panchlaish",
            education = "MBBS, Chittagong Medical College",
            profession = "Doctor / Medical Officer",
            relationshipStatus = "Single",
            interests = listOf("📚 Bangla Literature", "🍲 Biryani & Street Food", "🌿 Gardening & Nature", "🐈 Cat Lover"),
            hobbies = listOf("Baking brownies", "Listening to soft acoustics", "Rescuing street cats"),
            bio = "পেশায় ডাক্তার, তবে মনের দিক থেকে একজন রোমান্টিক স্বপ্নবাজ। সমুদ্রের গর্জন শুনতে ভালোবাসেন এমন কাউকে খুঁজছি।",
            partnerPreferences = "Someone honest, empathetic, and respectful with a good sense of humor.",
            profilePhotoUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=500&q=80",
            photos = listOf(
                "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=500&q=80",
                "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&q=80"
            ),
            isVerified = true,
            isPremium = true,
            isOnline = false
        ),
        UserProfile(
            uid = "seed_user_4",
            fullName = "Farhan Kabir",
            email = "farhan.kabir@example.com",
            phoneNumber = "+8801611332211",
            age = 27,
            gender = "Male",
            division = "Sylhet",
            district = "Sylhet City",
            city = "Zindabazar",
            education = "BBA in Marketing, Shahjalal University",
            profession = "Digital Marketing Strategist",
            relationshipStatus = "Single",
            interests = listOf("☕ Adda & Cha", "📸 Photography", "🏏 Cricket Lover", "🎬 World Cinema"),
            hobbies = listOf("Landscape photography in tea gardens", "Watching Premier League", "Coffee brewing"),
            bio = "সিলেটের চায়ের বাগান আর বৃষ্টি আমার খুব প্রিয়। সম্পর্কের ভিত্তি বন্ধুত্ব আর বিশ্বাস। নতুন বন্ধুদের আমন্ত্রণ।",
            partnerPreferences = "Looking for a true partner in crime who enjoys art, literature, and quiet rainy evenings.",
            profilePhotoUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=500&q=80",
            photos = listOf(
                "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=500&q=80"
            ),
            isVerified = false,
            isPremium = false,
            isOnline = true
        ),
        UserProfile(
            uid = "seed_user_5",
            fullName = "Sabrina Tasnim",
            email = "sabrina.tasnim@example.com",
            phoneNumber = "+8801511445566",
            age = 22,
            gender = "Female",
            division = "Dhaka",
            district = "Dhaka City",
            city = "Uttara",
            education = "BFA in Fashion Design, Shanto-Mariam",
            profession = "Fashion Designer & Stylist",
            relationshipStatus = "Single",
            interests = listOf("🎨 Art & Painting", "✈️ Traveling Sajek & Cox's Bazar", "☕ Adda & Cha", "💪 Fitness & Yoga"),
            hobbies = listOf("Clothing styling", "Yoga at sunrise", "Exploring vintage bookstores"),
            bio = "রং আর কাপড়ের ডিজাইন নিয়ে কাজ করি। হাসিখুশি প্রাণবন্ত মানুষ আমার প্রিয়। ডেটিং মানেই দুজনের সুন্দর বোঝাপড়া।",
            partnerPreferences = "Looking for someone confident, respectful, and stylish with whom I can share endless laughs.",
            profilePhotoUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&q=80",
            photos = listOf(
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&q=80"
            ),
            isVerified = true,
            isPremium = true,
            isOnline = true
        )
    )
}
