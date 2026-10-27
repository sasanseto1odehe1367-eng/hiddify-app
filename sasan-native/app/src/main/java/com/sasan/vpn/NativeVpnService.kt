package com.sasan.vpn

import android.app.*
import android.content.Intent
import android.net.VpnService
import android.os.*
import io.nekohasekai.libbox.*
import java.net.NetworkInterface

class NativeVpnService : VpnService(), PlatformInterface, CommandServerHandler {
    companion object {
        const val START = "com.sasanvpn.nativeclient.START"
        const val STOP = "com.sasanvpn.nativeclient.STOP"
    }
    private var engine: CommandServer? = null
    private var tun: ParcelFileDescriptor? = null
    private var initialized = false

    private fun state(text: String) {
        getSharedPreferences("state", MODE_PRIVATE).edit().putString("status", text).apply()
    }
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel("vpn", "SASAN VPN", NotificationManager.IMPORTANCE_LOW))
        }
    }
    private fun foreground() {
        val notice = android.app.Notification.Builder(this, "vpn")
            .setContentTitle("SASAN VPN").setContentText("VPN session running")
            .setSmallIcon(android.R.drawable.ic_lock_lock).build()
        startForeground(21, notice)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            STOP -> { shutdown(); stopSelf(); return START_NOT_STICKY }
            START -> {
                foreground()
                Thread {
                    try {
                        if (!initialized) {
                            Libbox.setup(SetupOptions().apply {
                                basePath = filesDir.absolutePath
                                workingPath = cacheDir.absolutePath
                                tempPath = cacheDir.absolutePath
                                fixAndroidStack = true
                            })
                            initialized = true
                        }
                        val profile = RealityProfile.parse(SecretStore.read(this) ?: error("Missing profile"))
                        val server = CommandServer(this, this)
                        engine = server
                        server.start()
                        val config = profile.config()
                        Libbox.checkConfig(config)
                        server.startOrReloadService(config, OverrideOptions())
                        state("Tunnel running; verify actual websites")
                    } catch (e: Exception) {
                        state("Error: " + e.javaClass.simpleName + ": " + (e.message ?: "unknown").take(160))
                        shutdown()
                        stopSelf()
                    }
                }.start()
                return START_STICKY
            }
        }
        return START_NOT_STICKY
    }
    private fun shutdown() {
        try { engine?.closeService() } catch (_: Exception) {}
        try { engine?.close() } catch (_: Exception) {}
        engine = null
        try { tun?.close() } catch (_: Exception) {}
        tun = null
        state("Disconnected")
    }
    override fun onDestroy() { shutdown(); super.onDestroy() }
    override fun onRevoke() { shutdown(); stopSelf(); super.onRevoke() }
    override fun openTun(options: TunOptions): Int {
        val b = Builder().setSession("SASAN VPN").setMtu(1400)
        val v4 = options.inet4Address
        while (v4.hasNext()) { val p = v4.next(); b.addAddress(p.address(), p.prefix()) }
        val v6 = options.inet6Address
        while (v6.hasNext()) { val p = v6.next(); b.addAddress(p.address(), p.prefix()) }
        b.addRoute("0.0.0.0", 0)
        b.addDnsServer("1.1.1.1")
        b.addDisallowedApplication(packageName)
        tun = b.establish() ?: error("Android VPN consent missing")
        return tun!!.fd
    }
    override fun autoDetectInterfaceControl(fd: Int) { if (!protect(fd)) error("Cannot protect socket") }
    override fun useProcFS() = Build.VERSION.SDK_INT < 29
    override fun usePlatformAutoDetectInterfaceControl() = true
    override fun underNetworkExtension() = false
    override fun includeAllNetworks() = false
    override fun findConnectionOwner(ipProtocol: Int, sourceAddress: String, sourcePort: Int, destinationAddress: String, destinationPort: Int): ConnectionOwner? = null
    override fun readWIFIState(): WIFIState? = null
    override fun getInterfaces(): NetworkInterfaceIterator? = null
    override fun startDefaultInterfaceMonitor(listener: InterfaceUpdateListener?) {}
    override fun closeDefaultInterfaceMonitor(listener: InterfaceUpdateListener?) {}
    override fun clearDNSCache() {}
    override fun sendNotification(notification: io.nekohasekai.libbox.Notification) {}
    override fun cancelNotification(identifier: String, typeID: Int) {}
    override fun registerMyInterface(name: String?) {}
    override fun usePlatformBridge(): Boolean = false
    override fun createBridge(options: BridgeOptions?): BridgeSession = error("Platform bridge unsupported")
    override fun usePlatformShell(): Boolean = false
    override fun checkPlatformShell() { error("Platform shell unsupported") }
    override fun openShellSession(
        user: PlatformUser?,
        command: String?,
        environ: StringIterator?,
        term: String?,
        rows: Int,
        cols: Int
    ): ShellSession = error("Platform shell unsupported")
    override fun lookupUser(username: String?): PlatformUser = error("No user lookup")
    override fun lookupSFTPServer(): String = error("SFTP unsupported")
    override fun readSystemSSHHostKey(): String = error("SSH unsupported")
    override fun tailscaleHostname(): String = error("Tailscale unsupported")
    override fun startNeighborMonitor(listener: NeighborUpdateListener?) {}
    override fun closeNeighborMonitor(listener: NeighborUpdateListener?) {}
    override fun localDNSTransport(): LocalDNSTransport? = null
    override fun serviceReload() {}
    override fun serviceStop() { state("Service stopped") }
    override fun getSystemProxyStatus(): SystemProxyStatus? = null
    override fun setSystemProxyEnabled(enabled: Boolean) {}
    override fun triggerNativeCrash() { error("Native crash disabled") }
    override fun connectSSHAgent(): Int = -1
    override fun writeDebugMessage(message: String?) {}
}
