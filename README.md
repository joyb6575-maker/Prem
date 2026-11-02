# PREM (প্রেম) — Dating & Social Connection App ❤️

> **ভালোবাসার গল্প শুরু হোক PREM দিয়ে**  
> *নতুন মানুষের সাথে পরিচিত হোন, খুঁজে নিন আপনার মনের মানুষ। (ভালোবাসা ও বন্ধুত্বের প্ল্যাটফর্ম — বিয়ে নয়)*

PREM is a modern, mobile-first dating and relationship application tailored specifically for young adults in Bangladesh. Built with **Kotlin**, **Jetpack Compose (Material 3)**, and **Google Cloud Firestore**, it provides a seamless, safe, and culturally relevant platform to find romance and meaningful connections.

---

## 🌟 মূল বৈশিষ্ট্যসমূহ (Key Features)

### 1. 🇧🇩 দ্বিভাষিক ইন্টারফেস (Bilingual Interface)
- সম্পূর্ণ অ্যাপটিতে যেকোনো সময় **বাংলা (Bangla)** এবং **English** ভাষার মধ্যে রূপান্তর করার সুবিধা।
- প্রতিটি বাটন, ফর্ম, ডায়ালগ এবং মেসেজ উভয় ভাষায় উপলব্ধ।

### 2. 🔐 নিরাপদ লগইন ও নিবন্ধন (Authentication & 18+ Security)
- **Google Sign-In**: Jetpack Credential Manager এবং Firebase Auth এর সমন্বয়ে সহজ গুগল লগইন।
- **মোবাইল নম্বর ও OTP**: বাংলাদেশের ১১ ডিজিটের যেকোনো মোবাইল নম্বর দিয়ে প্রবেশ ও OTP যাচাইকরণ।
- **ইমেইল ও পাসওয়ার্ড**: বিকল্প নিরাপদ লগইন ব্যবস্থা।
- **১৮+ বয়স যাচাই**: ১৮ বছরের নিচের ব্যবহারকারীদের প্রবেশ সম্পূর্ণরূপে নিষিদ্ধ।

### 3. 👤 বিস্তারিত ব্যবহারকারী প্রোফাইল (User Profiles)
- প্রোফাইল ছবি ও আকর্ষণীয় অবতার।
- বাংলাদেশের ৮টি বিভাগ (ঢাকা, চট্টগ্রাম, সিলেট, রাজশাহী, খুলনা, বরিশাল, রংপুর, ময়মনসিংহ) ও সংশ্লিষ্ট জেলা নির্বাচন।
- শিক্ষাগত যোগ্যতা, পেশা, বর্তমান সম্পর্কের অবস্থা (সিঙ্গেল, ডিভোর্সড ইত্যাদি)।
- ব্যক্তিগত পছন্দের ট্যাগ (*☕ আড্ডা ও চা, 📚 বাংলা সাহিত্য, ✈️ সাজেক ও কক্সবাজার ভ্রমণ, 🍲 বিরিয়ানি, 🎸 গান ও গিটার, 📸 ফটোগ্রাফি, 💻 কোডিং*)।
- বিস্তারিত বায়ো (Bio) এবং কেমন সঙ্গিনী/সঙ্গী খুঁজছেন তার বিবরণ।

### 4. 💖 আবিষ্কার ও ম্যাচিং সিস্টেম (Discover & Matching)
- কার্ডের মাধ্যমে অন্যান্য ব্যবহারকারীদের প্রোফাইল পরিদর্শন।
- বয়স, লিঙ্গ এবং বিভাগ অনুযায়ী ফিল্টার করার সুবিধা।
- **পাস (❌)**, **সুপার লাইক (⭐)** এবং **লাইক (❤️)** অ্যাকশন।
- **মিউচুয়াল ম্যাচ**: দুজনে একে অপরকে পছন্দ করলে আকর্ষণীয় অ্যানিমেশন সহ *"It's a Match! / মিল হয়ে গেছে! ❤️"* পপআপ।

### 5. 💬 রিয়েল-টাইম চ্যাট ও বার্তালাপ (Messaging)
- শুধুমাত্র ম্যাচ হওয়া ব্যবহারকারীদের মধ্যে ওয়ান-টু-ওয়ান ব্যক্তিগত চ্যাট।
- টেক্সট মেসেজ ও ছবি আদান-প্রদান।
- অনলাইন/অফলাইন স্ট্যাটাস এবং সর্বশেষ সক্রিয় থাকার সময়।
- সংবেদনশীল তথ্য ও আর্থিক লেনদেন রোধে বিশেষ নিরাপত্তা টিপস।

### 6. 🛡️ নিরাপত্তা ও গোপনীয়তা (Safety & Privacy)
- **ইনকগনিটো মোড (লুকানো প্রোফাইল)**: সাময়িকভাবে নতুন কারও কাছে প্রোফাইল প্রদর্শন বন্ধ রাখা।
- **অ্যাকাউন্ট ব্লক ও রিপোর্ট**: অসদাচরণ বা ভুয়া অ্যাকাউন্ট তাৎক্ষণিক ব্লক এবং অভিযোগ দায়েরের সুবিধা।
- **কমিউনিটি নির্দেশিকা**: পারস্পরিক শ্রদ্ধাশীল পরিবেশ বজায় রাখার নিয়মাবলী।
- মোবাইল নম্বর বা ব্যক্তিগত স্পর্শকাতর তথ্য কখনো প্রকাশ্যে প্রদর্শিত হয় না।

### 7. 👑 অ্যাডমিন ড্যাশবোর্ড (Admin Moderation Panel)
- নিবন্ধিত সকল ব্যবহারকারীর তালিকা ও অনুসন্ধান।
- ব্লু/ব্লু ভেরিফাইড টিক চিহ্ন প্রদান বা প্রত্যাহার।
- সন্দেহভাজন বা ফেক অ্যাকাউন্ট ব্যান/স্থগিত করা।
- ব্যবহারকারীদের জমা দেওয়া রিপোর্টের পর্যালোচনা ও সমাধান।
- মোট ব্যবহারকারী, ম্যাচ ও রিপোর্টের লাইভ পরিসংখ্যান।

---

## 🛠️ প্রযুক্তিগত কাঠামো (Tech Stack)

| কম্পোনেন্ট | প্রযুক্তি |
|---|---|
| **Language** | Kotlin 2.2 |
| **UI Framework** | Jetpack Compose (Material Design 3) |
| **Backend & Database** | Firebase Firestore (Real-time collections & subcollections) |
| **Authentication** | Firebase Auth + Android Jetpack Credential Manager (GoogleId) |
| **Image Loading** | Coil Compose |
| **Architecture** | MVVM / Clean Architecture with StateFlow & Coroutines |
| **Minimum SDK** | Android 7.0 (API Level 24) |
| **Target SDK** | Android 15 / 16 (API Level 36) |

---

## 🚀 GitHub থেকে ডাউনলোড ও চালানোর নিয়ম (How to Run from GitHub)

### ১. প্রজেক্ট ক্লোন বা ডাউনলোড করুন (Clone / Download)
টার্মিনালে কমান্ড চালান:
```bash
git clone https://github.com/YOUR_USERNAME/prem-dating-app.git
```
অথবা GitHub পেজের সবুজ **`Code`** বাটনে ক্লিক করে **`Download ZIP`** এ ক্লিক করুন এবং ফাইলটি আনজিপ করুন।

### ২. অ্যান্ড্রয়েড স্টুডিওতে ওপেন করুন (Open in Android Studio)
1. **Android Studio** (Ladybug, Meerkat বা সর্বশেষ সংস্করণ) চালু করুন।
2. **File** -> **Open** এ ক্লিক করে ক্লোন বা আনজিপ করা `prem-dating-app` ফোল্ডারটি সিলেক্ট করুন।
3. Gradle Sync স্বয়ংক্রিয়ভাবে শুরু হবে। প্রথমবার প্রয়োজনীয় ডিপেন্ডেন্সি ডাউনলোড হতে ২-৩ মিনিট সময় লাগতে পারে।

### ৩. অ্যাপ রান করুন (Run App)
1. আপনার পিসির Android Emulator চালু করুন অথবা USB ক্যাবল দিয়ে একটি Android মোবাইল কানেক্ট করুন (Developer Options -> USB Debugging অন থাকতে হবে)।
2. Android Studio-এর উপরের সবুজ **Run (▶)** বাটনে ক্লিক করুন অথবা কীবোর্ডে `Shift + F10` চাপুন।

---

## 📁 প্রজেক্ট ফাইল স্ট্রাকচার (Project Structure)

```
prem-dating-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/
│   │   │   ├── MainActivity.kt                      # Main Activity & Navigation
│   │   │   ├── model/PremModels.kt                  # Data models (UserProfile, Match, Chat)
│   │   │   ├── data/BangladeshiData.kt              # Divisions, districts & seed profiles
│   │   │   ├── data/PremStrings.kt                  # Bilingual (Bangla/English) dictionary
│   │   │   ├── data/PremRepository.kt               # Firestore & Auth Repository
│   │   │   ├── ui/components/PremComponents.kt     # App Bar, Match Dialog, Badges
│   │   │   ├── ui/screens/WelcomeAuthScreen.kt      # Google, Phone OTP, Email Login
│   │   │   ├── ui/screens/ProfileSetupScreen.kt     # Profile Creation & 18+ validation
│   │   │   ├── ui/screens/DiscoverScreen.kt         # Dating Cards Deck & Filters
│   │   │   ├── ui/screens/LikesScreen.kt            # Received & Sent Likes
│   │   │   ├── ui/screens/MatchesScreen.kt          # Matches & Conversations List
│   │   │   ├── ui/screens/ChatScreen.kt             # Real-time Chat
│   │   │   ├── ui/screens/ProfileDashboardScreen.kt # Dashboard, Incognito, Settings
│   │   │   ├── ui/screens/AdminScreen.kt            # Admin Moderation Panel
│   │   │   └── ui/screens/SafetyAndPremiumScreens.kt# Safety Center & Guidelines
│   │   ├── res/                                     # Drawables, layout, strings, icons
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── firestore.rules                                  # Secure zero-trust database rules
├── firebase-blueprint.json                          # Database schema blueprint
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🔒 গোপনীয়তা ও কপিরাইট
- এটি ১৮ বছরের ঊর্ধ্বের প্রাপ্তবয়স্কদের জন্য তৈরি সুস্থ সামাজিক যোগাযোগ ও ডেটিং প্ল্যাটফর্ম।
- ব্যক্তিগত গোপনীয়তা ও সাইবার নিরাপত্তা সম্পূর্ণ অগ্রাধিকার প্রাপ্ত।
