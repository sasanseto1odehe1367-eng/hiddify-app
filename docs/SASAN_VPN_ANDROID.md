# SASAN VPN — Android family build (prototype)

This is a noncommercial Android customization of [Hiddify](https://github.com/hiddify/hiddify-app), built for family use. It uses Hiddify's existing sing-box-based tunnel engine and supports imports of VLESS/Reality profiles through its standard profile importer.

## Changes in this branch
- Android display name: **SASAN VPN**
- Android application ID: `com.sasanvpn.family`, so it can coexist with the official Hiddify app.
- No VPN credentials, Reality keys, server IP addresses, or subscription tokens are bundled.

## Build and test
The original upstream Flutter build requirements apply (see `pubspec.yaml`, `Makefile` and the existing GitHub Actions workflow). The app has **not yet been compiled or tested on an Android phone**. A new application ID means this app cannot be used as an in-place update to the official Hiddify app.

To test a build, import a private Marzban VLESS/Reality profile or subscription **on the device after installation**. Do not commit or publish it. Test the VPN permission dialogue, DNS leak handling, IPv4/IPv6 behavior, battery optimization settings, calls over the VPN, reconnect behavior, and both Wi-Fi and mobile data.

**Important:** No app can restore international connectivity when every route out of the country is down. Better availability requires independent reachable paths and multiple servers; reconnect logic alone is insufficient.

## Licensing
This repository must remain a public fork under the [Hiddify Extended GPLv3 license](../LICENSE.md). Give credit to Hiddify and follow its release and noncommercial restrictions. All releases must be created using GitHub Actions. Do not reuse Hiddify branding for app-store publication.

## Next engineering tasks
- Replace launcher icons and splash images (current images are inherited from upstream).
- Audit Android app links and OAuth/analytics/reporting integrations for the new application ID.
- Run a clean Android build with the project's native core dependencies and review CI output.
- Add opt-in diagnostics that redact URLs, addresses, UUIDs and tokens.
- Add an optional secondary subscription and carefully tested failover.
