# Play Protect appeal package — DRAFT ONLY, NOT SUBMITTED

Official appeal process: https://developers.google.com/android/play-protect/warning-dev-guidance

**Do not submit as "false positive" yet**: third-party native binary and end-to-end functionality have not been independently validated. This report and the current screenshots are suitable to start an internal review.

Data to provide to Google only after security verification:
- Android package: `com.sasanvpn.nativeclient`
- App: SASAN VPN Native
- APK version: `0.1.0-test`
- Blocked artifact run: https://github.com/sasanseto1odehe1367-eng/hiddify-app/actions/runs/40430774110
- GitHub source branch: `feature/sasan-native-android`
- Device: Huawei P Smart 2019 POT-LX1, Android 9 / EMUI 9.0.1
- Alert (German): `Schädliche App blockiert`, `Diese App gefährdet möglicherweise dein Gerät oder deine Daten`
- Exact SHA-256 of blocked APK: **TO BE CONFIRMED FROM APK** (do not confuse with ZIP or libbox AAR)
- Signing certificate SHA-256: **TO BE CONFIRMED**
- Dependency report, reviewed source commit, malware/SDK scan result: **PENDING**
- Screenshot of Play Protect alert: user has the screenshot; attach without personal identifiers.
- No server usernames/UUIDs/config links/secrets needed.

Proposed English appeal once an independent security review supports it:

Subject: Request for review of Google Play Protect classification of SASAN VPN Native

Hello Google Play Protect review team,

I am the developer of a small Android VPN prototype (package com.sasanvpn.nativeclient). The attached APK triggered the "Harmful app blocked" warning when I attempted to install it on a Huawei P Smart 2019 (Android 9). I would like to request a review of the classification.

The app uses the standard Android VpnService interface after user approval. Its first-party manifest requests INTERNET, ACCESS_NETWORK_STATE and FOREGROUND_SERVICE. The user-supplied server profile is encrypted locally using Android Keystore; the prototype does not request contacts, SMS, microphone, camera, or accessibility permissions.

I am providing the exact APK SHA-256 and signing certificate fingerprint, the source repository and reviewed dependency/provenance details so you can investigate. I am not claiming this app has already been approved or is free of security issues; please advise if additional information or remediation is required.

Thank you.
