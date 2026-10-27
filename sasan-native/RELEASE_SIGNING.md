# Signing a release without exposing your private key

**Do not disable Play Protect. Do not distribute a release until independent APK/libbox review passes.**

The current GitHub workflow assembles a **debug-only** APK. Production signing is intentionally not automated without an owner-controlled signing key.

1. Use Android Studio on your trusted local computer and open `sasan-native/`. Install Android SDK/API 35 and JDK 17.
2. On your own device generate a long-lived private release keystore using the Android Studio **Build > Generate Signed Bundle / APK** wizard. Use a strong unique password and back up the .jks file and passwords securely **outside GitHub and chats**.
3. Choose **APK**, **release**, and enable the APK signature schemes recommended by the wizard. Build with release code (not the debug artifact).
4. Verify with `apksigner verify --verbose --print-certs path/to/app-release.apk` and note the SHA-256 certificate fingerprint.
5. Conduct a complete security assessment, review native library provenance, and test signing/upgrades on Android 9 before sharing. A release APK does not avoid Play Protect scanning.
6. File an appeal with Google using the exact blocked APK sample only once there's credible evidence of a false positive. See `PLAY_PROTECT_APPEAL.md`.

The development APK is signed using an ephemeral CI debug key. Its identity may change between runs; it must not be treated as a stable application release.
