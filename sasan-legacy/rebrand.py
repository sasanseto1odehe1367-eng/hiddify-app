#!/usr/bin/env python3
from pathlib import Path
import sys

def edit(path, old, new):
    p=Path(path); data=p.read_text()
    if data.count(old)!=1: raise RuntimeError(f"{p}: expected one match, got {data.count(old)}")
    p.write_text(data.replace(old,new))

root=Path(sys.argv[1])/"V2rayNG"/"app"
edit(root/"build.gradle.kts",'applicationId = "com.v2ray.ang"','applicationId = "com.sasanvpn.xraylegacy"')
edit(root/"src/main/res/values/strings.xml",'<string name="app_name" translatable="false">v2rayNG</string>','<string name="app_name" translatable="false">SASAN VPN Legacy</string>')
edit(root/"src/main/java/com/v2ray/ang/util/Utils.kt",'fun isXray(): Boolean = (ANG_PACKAGE.startsWith("com.v2ray.ang"))','fun isXray(): Boolean = true')
# Keep native class names and JNI bindings unchanged; align explicit broadcasts to new ID.
for filename in ["BROADCAST_ACTION_SERVICE","BROADCAST_ACTION_ACTIVITY","BROADCAST_ACTION_WIDGET_CLICK"]:
    pass
cfg=root/"src/main/java/com/v2ray/ang/AppConfig.kt"
contents=cfg.read_text()
contents=contents.replace('"com.v2ray.ang.action.service"','"$ANG_PACKAGE.action.service"').replace('"com.v2ray.ang.action.activity"','"$ANG_PACKAGE.action.activity"').replace('"com.v2ray.ang.action.widget.click"','"$ANG_PACKAGE.action.widget.click"')
cfg.write_text(contents)
manifest=root/"src/main/AndroidManifest.xml"
data=manifest.read_text()
data=data.replace('com.v2ray.ang.action.widget.click','${applicationId}.action.widget.click').replace('com.v2ray.ang.action.activity','${applicationId}.action.activity')
manifest.write_text(data)
edit(Path(sys.argv[1])/"V2rayNG"/"gradle"/"libs.versions.toml", 'module = "com.github.tbruyelle:rxpermissions"', 'module = "com.github.tbruyelle:RxPermissions"')
print("Applied SASAN VPN legacy branding, Xray mode, and historical RxPermissions artifact correction.")
