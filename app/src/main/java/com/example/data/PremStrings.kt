package com.example.data

import com.example.model.AppLanguage

object PremStrings {

    fun get(key: String, lang: AppLanguage): String {
        val entry = strings[key] ?: return key
        return if (lang == AppLanguage.BN) entry.first else entry.second
    }

    // Pair of (Bangla, English)
    private val strings = mapOf(
        // App Identity
        "app_name" to Pair("প্রেম", "PREM"),
        "tagline" to Pair("ভালোবাসার গল্প শুরু হোক PREM দিয়ে ❤️", "Let your love story begin with PREM ❤️"),
        "subtitle" to Pair("নতুন মানুষের সাথে পরিচিত হোন, খুঁজে নিন আপনার মনের মানুষ।", "Meet new people and find your special someone."),
        "dating_not_marriage" to Pair("ভালোবাসা ও বন্ধুত্বের প্ল্যাটফর্ম (বিয়ে নয়)", "Love, Friendship & Dating (Not Marriage)"),

        // Auth
        "create_account" to Pair("নতুন অ্যাকাউন্ট তৈরি করুন", "Create Account"),
        "login" to Pair("লগইন করুন", "Login"),
        "login_with_google" to Pair("গুগল দিয়ে প্রবেশ করুন", "Sign in with Google"),
        "phone_login" to Pair("মোবাইল নম্বর দিয়ে প্রবেশ", "Continue with Mobile"),
        "email_login" to Pair("ইমেইল দিয়ে প্রবেশ", "Continue with Email"),
        "enter_phone" to Pair("আপনার ১১ ডিজিটের মোবাইল নম্বর লিখুন", "Enter 11-digit mobile number"),
        "enter_email" to Pair("আপনার ইমেইল ঠিকানা দিন", "Enter your email address"),
        "enter_password" to Pair("পাসওয়ার্ড দিন (কমপক্ষে ৬ অক্ষর)", "Enter password (min 6 characters)"),
        "enter_otp" to Pair("৪ ডিজিটের OTP কোড লিখুন", "Enter 4-digit OTP code"),
        "verify_otp" to Pair("OTP যাচাই করুন", "Verify OTP"),
        "resend_otp" to Pair("পুনরায় কোড পাঠান", "Resend Code"),
        "otp_sent_to" to Pair("কোড পাঠানো হয়েছে:", "OTP sent to:"),
        "demo_otp_hint" to Pair("ডেমো OTP: 1234", "Demo OTP: 1234"),
        "age_warning" to Pair("আমি নিশ্চিত করছি আমার বয়স ১৮ বছরের বেশি", "I confirm that I am at least 18 years old"),
        "must_be_18" to Pair("PREM শুধুমাত্র ১৮+ বয়সীদের জন্য", "PREM is strictly for 18+ individuals"),
        "terms_accept" to Pair("আমি PREM এর শর্তাবলী ও গোপনীয়তা নীতি মেনে নিচ্ছি", "I agree to PREM terms and privacy policy"),
        "logout" to Pair("লগআউট", "Logout"),

        // Navigation
        "nav_discover" to Pair("আবিষ্কার", "Discover"),
        "nav_likes" to Pair("পছন্দ", "Likes"),
        "nav_matches" to Pair("ম্যাচ", "Matches"),
        "nav_messages" to Pair("বার্তা", "Messages"),
        "nav_profile" to Pair("প্রোফাইল", "Profile"),

        // Profile Setup
        "complete_profile" to Pair("আপনার প্রোফাইল সাজান", "Complete Your Profile"),
        "full_name" to Pair("পুরো নাম", "Full Name"),
        "age" to Pair("বয়স", "Age"),
        "gender" to Pair("লিঙ্গ", "Gender"),
        "gender_male" to Pair("পুরুষ", "Male"),
        "gender_female" to Pair("নারী", "Female"),
        "gender_other" to Pair("অন্যান্য", "Other"),
        "division" to Pair("বিভাগ", "Division"),
        "district" to Pair("জেলা", "District"),
        "city" to Pair("শহর / এলাকা", "City / Area"),
        "education" to Pair("শিক্ষাগত যোগ্যতা", "Education"),
        "profession" to Pair("পেশা / কর্মক্ষেত্র", "Profession"),
        "relationship_status" to Pair("সম্পর্কের বর্তমান অবস্থা", "Relationship Status"),
        "status_single" to Pair("অবিবাহিত / সিঙ্গেল", "Single"),
        "status_divorced" to Pair("ডিভোর্সড", "Divorced"),
        "status_separated" to Pair("আলাদা থাকছেন", "Separated"),
        "interests" to Pair("পছন্দের বিষয়সমূহ (ট্যাগ নির্বাচন করুন)", "Interests (Select tags)"),
        "hobbies" to Pair("শখ ও আগ্রহ", "Hobbies"),
        "bio" to Pair("আমার সম্পর্কে (বায়ো)", "About Me (Bio)"),
        "partner_pref" to Pair("যেমন সঙ্গিনী/সঙ্গী খুঁজছেন", "Partner Preferences"),
        "save_profile" to Pair("প্রোফাইল সংরক্ষণ করুন", "Save Profile"),
        "edit_profile" to Pair("প্রোফাইল সম্পাদনা", "Edit Profile"),
        "profile_photo" to Pair("প্রোফাইল ছবি", "Profile Photo"),
        "add_photos" to Pair("আরও ছবি যোগ করুন", "Add Photos"),
        "change_avatar" to Pair("ছবি পরিবর্তন", "Change Photo"),

        // Discover
        "find_someone" to Pair("কাছের মানুষ খুঁজুন", "Find Someone Nearby"),
        "filter" to Pair("ফিল্টার", "Filters"),
        "filter_all" to Pair("সকল", "All"),
        "active_now" to Pair("এখন সক্রিয়", "Active Now"),
        "recently_active" to Pair("সম্প্রতি সক্রিয়", "Recently Active"),
        "verified_badge" to Pair("ভেরিফাইড প্রোফাইল", "Verified Profile"),
        "years_old" to Pair("বছর", "yrs"),
        "like" to Pair("লাইক", "Like"),
        "pass" to Pair("পাস", "Pass"),
        "super_like" to Pair("সুপার লাইক", "Super Like"),
        "no_more_profiles" to Pair("আপাতত নতুন কোনো প্রোফাইল নেই! পরে আবার দেখুন।", "No more profiles available right now! Check back soon."),
        "refresh" to Pair("নতুন করে খুঁজুন", "Refresh Profiles"),

        // Matching
        "match_title" to Pair("অভিনন্দন! মিল হয়ে গেছে! ❤️", "It's a Match! ❤️"),
        "match_desc" to Pair("আপনারা একে অপরকে পছন্দ করেছেন। এখনই বার্তা পাঠানো শুরু করুন!", "You both liked each other! Start your conversation now."),
        "say_hello" to Pair("হাই বলুন", "Say Hello"),
        "keep_browsing" to Pair("অন্যদের দেখুন", "Keep Browsing"),
        "matches_count" to Pair("মোট ম্যাচ", "Total Matches"),
        "no_matches_yet" to Pair("এখনো কোনো ম্যাচ হয়নি। আরও মানুষকে লাইক দিন!", "No matches yet. Like more profiles to match!"),

        // Likes Tab
        "likes_received" to Pair("যাঁরা পছন্দ করেছেন", "Liked You"),
        "likes_sent" to Pair("আপনি যাদের পছন্দ করেছেন", "Sent Likes"),
        "like_back" to Pair("পছন্দ ফিরিয়ে দিন", "Like Back"),
        "upgrade_see_likes" to Pair("PREM গোল্ড নিয়ে দেখুন কে কে আপনাকে লাইক করেছে", "Upgrade to PREM Gold to see everyone who liked you"),

        // Chat & Messaging
        "type_message" to Pair("বার্তা লিখুন...", "Type a message..."),
        "online" to Pair("অনলাইন", "Online"),
        "offline" to Pair("অফলাইন", "Offline"),
        "last_seen" to Pair("সর্বশেষ সক্রিয়:", "Last seen:"),
        "typing" to Pair("লিখছেন...", "Typing..."),
        "send_photo" to Pair("ছবি শেয়ার", "Share Photo"),
        "safety_tip" to Pair("নিরাপত্তা টিপ: ব্যক্তিগত আর্থিক তথ্য কখনো শেয়ার করবেন না।", "Safety Tip: Never share bank or financial information."),
        "no_messages_yet" to Pair("বার্তা পাঠিয়ে আলাপ শুরু করুন!", "Send a message to break the ice!"),
        "block_user" to Pair("ব্যবহারকারী ব্লক করুন", "Block User"),
        "report_user" to Pair("রিপোর্ট করুন", "Report Account"),
        "user_blocked_msg" to Pair("ব্যবহারকারীকে ব্লক করা হয়েছে।", "User has been blocked."),
        "report_submitted" to Pair("রিপোর্ট সফলভাবে জমা হয়েছে। অ্যাডমিন টিম খতিয়ে দেখবে।", "Report submitted. Admin moderation team will review it."),

        // Safety & Privacy
        "safety_center" to Pair("নিরাপত্তা ও গোপনীয়তা", "Safety & Privacy Center"),
        "hide_profile" to Pair("প্রোফাইল লুকান (ইনকগনিটো মোড)", "Hide Profile (Incognito Mode)"),
        "hide_profile_desc" to Pair("আবিষ্কার তালিকায় আপনার প্রোফাইল সাময়িকভাবে প্রদর্শিত হবে না।", "Your profile won't be shown to new people in Discover."),
        "blocked_accounts" to Pair("ব্লক করা তালিকা", "Blocked Accounts"),
        "unblock" to Pair("আনব্লক", "Unblock"),
        "community_guidelines" to Pair("কমিউনিটি নির্দেশিকা", "Community Guidelines"),
        "phone_privacy_note" to Pair("আপনার ফোন নম্বর বা ব্যক্তিগত তথ্য সবসময় সুরক্ষিত থাকবে।", "Your phone number and sensitive details are never shown publicly."),

        // Admin
        "admin_panel" to Pair("অ্যাডমিন ড্যাশবোর্ড", "Admin Dashboard"),
        "admin_stats" to Pair("অ্যাপ পরিসংখ্যান", "App Statistics"),
        "total_users" to Pair("মোট নিবন্ধিত", "Total Users"),
        "total_matches" to Pair("মোট ম্যাচ সংখ্যা", "Total Matches"),
        "pending_reports" to Pair("অমীমাংসিত রিপোর্ট", "Pending Reports"),
        "registered_users" to Pair("ব্যবহারকারী তালিকা", "User Management"),
        "verify_account" to Pair("ভেরিফাই করুন", "Verify"),
        "unverify_account" to Pair("ভেরিফিকেশন প্রত্যাহার", "Unverify"),
        "ban_account" to Pair("ব্যান / স্থগিত করুন", "Ban Account"),
        "unban_account" to Pair("ব্যান মুক্ত করুন", "Unban"),
        "dismiss" to Pair("বাতিল", "Dismiss"),
        "resolve" to Pair("সমাধান", "Resolve"),

        // Premium Membership
        "prem_premium" to Pair("PREM প্রিমিয়াম মেম্বারশিপ", "PREM Premium Membership"),
        "upgrade_now" to Pair("সুবিধাসমূহ দেখুন", "View Benefits"),
        "unlimited_likes" to Pair("সীমাহীন লাইক পাঠানোর সুযোগ", "Unlimited Likes"),
        "see_who_liked" to Pair("কে আপনাকে লাইক করেছে সরাসরি দেখা", "See Who Liked You"),
        "profile_boost" to Pair("১০ গুণ বেশি মানুষের কাছে প্রোফাইল পৌঁছানো", "10x Profile Boost in Discover"),
        "gold_badge" to Pair("বিশেষ গোল্ড ব্যাজ", "Exclusive Gold Badge"),
        "advanced_filters" to Pair("উন্নত ফিল্টার সুবিধা", "Advanced Lifestyle Filters"),
        "free_tier_note" to Pair("PREM এর মূল ডেটিং ফিচার সবসময় সম্পূর্ণ বিনামূল্যে ব্যবহারযোগ্য।", "Core dating & chat features on PREM are always 100% free."),

        // Dialogs & Actions
        "cancel" to Pair("বাতিল", "Cancel"),
        "confirm" to Pair("নিশ্চিত", "Confirm"),
        "delete" to Pair("মুছে ফেলুন", "Delete"),
        "delete_profile" to Pair("প্রোফাইল মুছে ফেলুন", "Delete Account"),
        "delete_confirm_msg" to Pair("আপনি কি নিশ্চিত আপনার অ্যাকাউন্ট মুছে ফেলতে চান? এটি আর ফিরিয়ে আনা যাবে না।", "Are you sure you want to permanently delete your account?"),
        "language_toggle" to Pair("English", "বাংলা"),
        "switch_lang" to Pair("English এ দেখুন", "বাংলায় দেখুন"),
        "success" to Pair("সফল হয়েছে", "Success"),
        "error" to Pair("সমস্যা হয়েছে", "Error"),
        "try_again" to Pair("আবার চেষ্টা করুন", "Try Again")
    )
}
