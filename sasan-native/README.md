# SASAN VPN Native — experimental independent Android client

This application uses its own Kotlin Android UI, encrypted profile storage, VLESS REALITY link parser, Android VpnService, and a new package identity. **It is not a reskin of Hiddify or v2rayNG.**

Only the cryptographic/network transport library comes from sing-box (libbox v1.14.0), which is externally maintained. The project targets Android 9 (API 28) on Huawei P Smart 2019 and supports API 26+.

The app has not yet passed CI, native runtime tests, or a real-device connectivity test. It is **experimental**, not production ready. Do not distribute as a reliable service and keep an established working VPN installed.

Never commit Marzban subscriptions, VLESS UUIDs, or REALITY keys. Enter profiles only on-device. For release, audit Android foreground-service behavior, secure storage, leaks, fail-closed operation and license notices. The GPL3 licensed sing-box dependency requires proper notices and compliance. No claim of circumvention during a complete internet shutdown is possible.
