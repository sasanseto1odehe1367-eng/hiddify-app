#!/usr/bin/env python3
"""Noncommercial Android branding overlay for a pinned GPLv3 v2rayNG checkout.

Only display identity and Android application ID are changed. Existing package
names for Kotlin/JNI remain intact. No credentials are embedded.
"""
from pathlib import Path
import sys

def replace_once(path: Path, before: str, after: str) -> None:
    content = path.read_text(encoding="utf-8")
    count = content.count(before)
    if count != 1:
        raise RuntimeError(f"{path}: expected exactly 1 occurrence, found {count}")
    path.write_text(content.replace(before, after), encoding="utf-8")

def main() -> None:
    if len(sys.argv) != 2:
        raise SystemExit("Usage: rebrand.py PATH_TO_V2RAYNG_GIT_ROOT")
    root = Path(sys.argv[1]).resolve()
    module = root / "V2rayNG" / "app"
    if not module.is_dir():
        raise RuntimeError(f"Not a v2rayNG checkout: {root}")

    replace_once(
        module / "build.gradle.kts",
        'applicationId = "com.v2ray.ang"',
        'applicationId = "com.sasanvpn.xray"',
    )
    replace_once(
        module / "src/main/res/values/strings.xml",
        '<string name="app_name" translatable="false">v2rayNG</string>',
        '<string name="app_name" translatable="false">SASAN VPN</string>',
    )
    replace_once(
        module / "src/main/AndroidManifest.xml",
        'android:icon="@mipmap/ic_launcher"\n        android:label="@string/app_name"',
        'android:icon="@drawable/sasan_vpn_icon"\n        android:label="@string/app_name"',
    )
    # The original Kotlin package and JNI library identifiers intentionally
    # stay unchanged. Changing those breaks the upstream networking engine.
    icon = module / "src/main/res/drawable/sasan_vpn_icon.xml"
    icon.parent.mkdir(parents=True, exist_ok=True)
    icon.write_text("""<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp" android:height="108dp"
    android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#101C37"
        android:pathData="M0,0 L108,0 L108,108 L0,108 Z" />
    <path android:fillColor="#19D9C7"
        android:pathData="M54,11 L88,25 L88,52 C88,75 74,92 54,100 C34,92 20,75 20,52 L20,25 Z" />
    <path android:fillColor="#112643"
        android:pathData="M54,19 L80,30 L80,52 C80,69 70,82 54,91 C38,82 28,69 28,52 L28,30 Z" />
    <path android:fillColor="#FFFFFF"
        android:pathData="M60,28 L39,57 L53,57 L47,82 L72,47 L57,47 Z" />
</vector>
""", encoding="utf-8")
    print("SASAN VPN branding applied; original GPLv3 sources remain credited.")

if __name__ == "__main__":
    main()
