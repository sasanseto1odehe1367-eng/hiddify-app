#!/usr/bin/env python3
"""Fail closed on surprising Android permissions/manifest changes.

This is a structural check, NOT an antivirus or binary supply-chain audit.
Usage: python3 audit_apk.py /path/app-debug.apk /path/android-sdk/aapt
"""
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

apk, aapt = map(Path, sys.argv[1:])
manifest = Path(__file__).resolve().parents[1] / "app/src/main/AndroidManifest.xml"
ns = "{http://schemas.android.com/apk/res/android}"
doc = ET.parse(manifest)
root = doc.getroot()
allowed = {
    "android.permission.INTERNET",
    "android.permission.ACCESS_NETWORK_STATE",
    "android.permission.FOREGROUND_SERVICE",
}
declared = {n.attrib[ns + "name"] for n in root.findall("uses-permission")}
assert declared == allowed, f"Unexpected source permissions: {declared ^ allowed}"
app = root.find("application")
assert app is not None
assert app.attrib[ns + "allowBackup"] == "false"
assert app.attrib[ns + "usesCleartextTraffic"] == "false"
service = app.find("service")
assert service is not None
assert service.attrib.get(ns + "exported") == "true"
assert service.attrib.get(ns + "permission") == "android.permission.BIND_VPN_SERVICE"
meta = service.find("meta-data")
assert meta is not None
assert meta.attrib.get(ns + "name") == "android.net.VpnService.SUPPORTS_ALWAYS_ON"
assert meta.attrib.get(ns + "value") == "false"
for comp in app.findall("receiver") + app.findall("provider"):
    assert comp.attrib.get(ns + "exported") != "true", "Unexpected exported component"
output = subprocess.check_output([str(aapt), "dump", "permissions", str(apk)], text=True)
actual = set(re.findall(r"name='([^']+)'", output))
unexpected = {x for x in actual - allowed
    if not x.startswith("com.sasanvpn.nativeclient.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION")}
assert not unexpected, f"Unexpected permissions in merged APK: {sorted(unexpected)}"
bad_source = [
    r"dalvik\.system\.DexClassLoader", r"java\.lang\.Runtime\.getRuntime\(\)\.exec",
    r"SmsManager", r"READ_CONTACTS", r"READ_SMS", r"READ_CALL_LOG",
    r"AccessibilityService", r"SYSTEM_ALERT_WINDOW",
]
src = Path(__file__).resolve().parents[1] / "app/src/main/java"
code = "\n".join(f.read_text(encoding="utf-8") for f in src.rglob("*.kt"))
for term in bad_source:
    assert not re.search(term, code), f"Review suspicious source pattern: {term}"
print("PASS: minimal Android permissions and protected VpnService manifest.")
print("PASS: no listed high-risk API references in first-party Kotlin source.")
print("LIMITATION: third-party native .aar has NOT been malware-audited by this script.")
