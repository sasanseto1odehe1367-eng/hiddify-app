param([Parameter(Mandatory=$true)][string]$ApkPath)
$ErrorActionPreference = "Stop"
$apk = (Resolve-Path -LiteralPath $ApkPath).Path
$cmd = Get-Command adb.exe -ErrorAction SilentlyContinue
$adb = if ($cmd) { $cmd.Source } else { "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" }
if (-not (Test-Path $adb)) {
  Write-Host "Official Android Platform Tools required: https://developer.android.com/tools/releases/platform-tools"
  exit 2
}
Write-Host "Device connection (must say device, not unauthorized):"
& $adb devices -l
Write-Host "Android version:"
& $adb shell getprop ro.build.version.release
Write-Host "Android API level:"
& $adb shell getprop ro.build.version.sdk
Write-Host "CPU ABI:"
& $adb shell getprop ro.product.cpu.abilist
Write-Host "Free storage:"
& $adb shell df -h /data
Write-Host "APK SHA-256:"
Get-FileHash -Algorithm SHA256 -LiteralPath $apk | Select-Object -ExpandProperty Hash
Write-Host "Attempting install; identify exact INSTALL_FAILED_... line:"
& $adb install -r $apk
Write-Host "Share only Android/API/ABI and INSTALL_FAILED error, never VPN configs or secrets."
