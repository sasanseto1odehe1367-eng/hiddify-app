# Security review — SASAN VPN Native v0.1 (2026-10-27)

## Scope / findings
A first-party source-code review and deterministic APK manifest checks were performed. **No malware-free conclusion is possible yet**: the prebuilt native libbox .aar and the signed installation file have not undergone a full independent malware/security analysis. Google Play Protect actively classifies the debug APK as potentially harmful on the Huawei P Smart 2019. **Do not bypass the block or distribute the APK** while it is unresolved.

### First-party Android source findings
- Three requested permissions: INTERNET, ACCESS_NETWORK_STATE and FOREGROUND_SERVICE. Runtime VPN authorization is user-consented via VpnService.prepare().
- Manifest declares usesCleartextTraffic=false and allowBackup=false.
- VPN service is exported for Android system binding but protected by signature permission BIND_VPN_SERVICE. Always-on capability is explicitly disabled until recovery is implemented.
- The app locally encrypts the user-supplied VLESS Reality link with Android Keystore AES/GCM. Never commit or upload credentials/UUIDs/private profile URLs. User links are not shipped inside APK.
- No SMS, contacts, location, camera, microphone, accessibility, overlay or external storage permissions were found in first-party manifest (merged APK is enforced by audit script).
- Before hardening, Core crash/reset could advertise a false connection status or leave stale status. Now service returns START_NOT_STICKY and reports a connection error without exposing raw configuration details.
- The app **does not have a verified kill switch**. Traffic can leave the VPN on failure. Do not describe it as fail-closed, anonymous, or safe in a hostile environment.
- VLESS/REALITY routing, tunnel sockets, DNS behavior, telemetry and network changes still require on-device/instrumented testing. A successful compilation is not an end-to-end test.

### Native dependency supply-chain risk (OPEN)
- Binary: https://github.com/singbox-android/libbox/releases/download/1.14.0/libbox.aar
- Pinned SHA-256: `13fbeabcb4622250762afa1b137daaba861a4dc1875643d18621339912f32330`
- Source project: https://github.com/singbox-android/libbox, GPL-3.0. This is a third-party distribution, not a verified reproducible build from official SagerNet upstream.
- The checksum in CI assures bytes match the download **only**. It does not prove the binaries are safe. Before release, independently verify the complete transitive source/toolchain provenance, review embedded native objects and reproduce the binary from a trusted source, or use a verified official core release.
- Review GPL obligations, provide corresponding sources/attributions on distribution.

### Google Play Protect finding (OPEN)
- Device: Huawei P Smart 2019 POT-LX1, Android 9, EMUI 9.0.1.
- Actual warning: "Schädliche App blockiert" / "Diese App gefährdet möglicherweise dein Gerät oder deine Daten".
- This is a potentially harmful app classification, NOT merely the unknown-source sideload warning. Do not bypass/disable Play Protect.
- The exact detection category is **not provided** by the screenshot. Release/debug signature changes do not establish safety and cannot guarantee the warning will disappear.
- Submit an official appeal **only after** dependency provenance and actual APK security review are complete. Include the exact blocked APK checksum, package name, screenshots, and test evidence. Never submit private VPN links or signing keys.

### CI gates
- Downloaded AAR is checksum-verified against the pinned release.
- `audit_apk.py` checks first-party permissions, merged permissions, the protected VPN service and selected dangerous source APIs.
- Android lint, `apksigner verify`, package/minSdk/ABI metadata and output sha256 are checked in CI.
- These tests cannot replace malware analysis, release review, signed build reputation or device-level test.

## Release signing
Keep signing key private. See `RELEASE_SIGNING.md`. This project will not auto-create/upload a production signing key to a repository. A locally signed build would still require Play Protect review.
