# SASAN VPN Xray — Android experimental build

This is an **independent client from Hiddify**. It is an auditable, noncommercial Android customization of the GPLv3 [v2rayNG project](https://github.com/2dust/v2rayNG), using the original [Xray-core](https://github.com/XTLS/Xray-core) networking implementation.

## Source and credits
- Upstream v2rayNG source pinned at: `0f961c54c8c9f85c5271778653340025d33772cc`
- Upstream AndroidLibXrayLite AAR release: `v26.10.21`
- AAR verified with SHA-256: `e56ec3b06485fe7b37af26948673951edf719c50d6bbc618b8539045952adde4`
- HEV TUN library built from v2rayNG's pinned native submodule
- Original v2rayNG GPLv3 license applies, source code remains available from the attributed original repository
- The parent repository also contains an unrelated Hiddify experiment, which this build does **not** compile or include

## Changes in SASAN build
- Android application ID: `com.sasanvpn.xray` (independent of upstream v2rayNG)
- Android launcher display name: `SASAN VPN`
- Simple new launcher vector icon
- Kotlin/JNI package and engine implementation **unchanged** to avoid breaking binary linkage
- No private Marzban subscription URLs, server IP addresses, Reality keys, UUIDs, passwords or profiles are included

## Build
See [`.github/workflows/sasan-xray.yml`](../.github/workflows/sasan-xray.yml). This workflow checks out the upstream project at a pinned commit, pulls verified native dependencies, applies minimal branding, runs Gradle unit tests, and assembles a Playstore **debug test** APK (not a production release). It includes open-source upstream dependencies and is not built from Hiddify.

## Import a private Marzban profile
After installation, paste or QR-import your own VLESS Reality TCP config **on your Android phone**, not into GitHub. Then test with a known-working client on the same phone, cellular carrier, and time. Do not publish connection logs containing sensitive URLs, UUIDs, Reality keys, subscription tokens, or passwords.

## Limitations
- The build passing only confirms compilation and unit tests; **real phone connectivity has not been verified**.
- Public GitHub Actions build artifacts are available to accounts with access and are test packages only, signed with Android's default debug signing key.
- The app cannot provide connectivity if no route to the international internet remains.
- For production: complete mobile data/Wi-Fi connection tests, review all inherited app behaviors, create and protect a stable signing key, check VPN leaks, and publish clear GPLv3 source attribution.
