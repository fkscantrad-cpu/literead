# LiteRead - Android Document Reader

## Overview
LiteRead is an ultra-lightweight Android document reader application. This is a **mobile Android app** that produces APK files for Android devices - it is not a web application and cannot run in a browser.

## Project Type
- **Platform**: Android (API 21+, Android 5.0+)
- **Language**: Kotlin 1.9.22
- **Build System**: Gradle with Kotlin DSL
- **Architecture**: MVVM (Model-View-ViewModel)

## Features
- Multi-format support: PDF, EPUB, TXT, MOBI
- 4 themes: Light, Dark, Sepia, AMOLED
- Bookmarks and reading history
- Text search
- Ultra-lightweight (~10MB APK)

## Project Structure
```
literead/
├── app/
│   ├── src/main/
│   │   ├── java/com/literead/    # Kotlin source code
│   │   │   ├── data/             # Database and models
│   │   │   ├── reader/           # Document readers
│   │   │   ├── theme/            # Theme management
│   │   │   ├── ui/               # Activities, fragments, adapters
│   │   │   └── utils/            # Utility classes
│   │   ├── res/                  # Android resources
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts          # App dependencies
│   └── proguard-rules.pro
├── build.gradle.kts              # Root build config
├── settings.gradle.kts
├── gradlew                       # Gradle wrapper
└── *.sh                          # Build scripts
```

## Building the App
This project requires Android Studio or the Android SDK to build:

1. **Android Studio (Recommended)**
   - Open the project in Android Studio
   - Sync Gradle
   - Run on emulator or connected device

2. **Command Line**
   ```bash
   ./gradlew assembleDebug
   # APK output: app/build/outputs/apk/debug/app-debug.apk
   ```

## Dependencies
- AndroidX Core, AppCompat, Lifecycle
- Room Database for local storage
- Material Design components
- Pdfium for PDF rendering
- Readium for EPUB support
- Koin for dependency injection
- Kotlin Coroutines and Serialization

## Replit Environment Notes
- Android SDK is not available in Replit
- The workflow displays project information
- Full building requires Android Studio locally
