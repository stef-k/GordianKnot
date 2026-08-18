# Offline Password Generator Android

A minimal Android wrapper around the offline password generator.

## Security design

- No `INTERNET` permission.
- No external libraries or runtime dependencies.
- No analytics, accounts, storage, cookies, localStorage, or database.
- The HTML/JS generator is bundled inside the APK.
- `crypto.getRandomValues()` is used for password randomness.
- Rejection sampling avoids modulo bias.
- At least one character from every selected class is guaranteed.
- Clipboard access occurs only when the user presses Copy.
- Android backup is disabled.
- Cleartext network traffic is disabled.
- WebView file/content access is disabled.

## Build APK in Android Studio

1. Open this folder in Android Studio.
2. Let Android Studio install Android SDK 35 / Gradle dependencies if needed.
3. Use:
   Build > Build App Bundle(s) / APK(s) > Build APK(s)

The debug APK is typically created at:

`app/build/outputs/apk/debug/app-debug.apk`

For a long-term release APK, create and retain your own signing key so only you can publish upgrades under the same app identity.

## Command-line build

With Android SDK and Gradle available:

`gradle assembleDebug`

or use a generated Gradle wrapper:

`./gradlew assembleDebug`
