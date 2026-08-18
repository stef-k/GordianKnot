# Gordian Knot

Gordian Knot is a small offline Android password generator. It creates passwords
entirely on the device and has no Internet permission, analytics, accounts, or
password storage.

> This independent project is not affiliated with or endorsed by Google or any
> password-manager vendor.

## Highlights

- Cryptographically secure randomness from Web Crypto
- Rejection sampling to avoid modulo bias
- At least one character from every selected character group
- Password lengths from 6 to 64 characters, with warnings for legacy lengths
- System, light, and dark themes
- No third-party runtime libraries
- No network, database, cookies, local storage, or Android backup
- Sensitive clipboard metadata on supported Android versions
- Screenshot and Recent Apps preview protection in release builds

## Privacy and security

Passwords are generated in the bundled page and are never written to storage or
sent over a network. The Android host has no `INTERNET` permission and blocks
WebView navigation, file access, content access, cookies, and web storage.

Copying necessarily places a password on Android's system clipboard. Gordian
Knot marks that clip as sensitive, but the operating system, keyboards, or other
software may still be able to observe clipboard contents. Save copied passwords
promptly in a trusted password manager.

The app depends on the Android System WebView supplied and updated by the device.

Release builds block screenshots and Recent Apps previews. Debug builds allow
screenshots so maintainers can capture documentation and test the interface.

## Build

Prerequisites:

- JDK 17
- Android SDK 36

Android Studio can open the repository directly. For a command-line debug build:

```shell
./gradlew lint assembleDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat lint assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

Run the dependency-free generator tests with Node.js 18 or newer:

```shell
node --test tests/generator.test.mjs
```

## Release signing

Debug APKs are automatically signed with a development key. For long-term use,
create and securely retain your own release signing key. Never commit keystores,
key passwords, or a populated `local.properties` file.

## Screenshot

A real device or emulator screenshot can be captured from a debug build. Release
builds intentionally block screenshot capture with Android's secure-window flag.

## License

[MIT](LICENSE)
