package com.sasan.vpn

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/** Original simple Android interface. No Hiddify or v2rayNG UI code. */
class MainActivity : Activity() {
    private lateinit var input: EditText
    private lateinit var status: TextView
    private val requestVpn = 802

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 56, 36, 32)
        }
        val title = TextView(this).apply { text = "SASAN VPN"; textSize = 30f }
        status = TextView(this).apply { textSize = 16f }
        input = EditText(this).apply {
            hint = "Paste private vless:// ... Reality link here"
            minLines = 3; maxLines = 6
            setSingleLine(false)
        }
        val import = Button(this).apply { text = "Save VLESS / REALITY" }
        val start = Button(this).apply { text = "Connect" }
        val stop = Button(this).apply { text = "Disconnect" }
        val reload = Button(this).apply { text = "Refresh status" }
        val forget = Button(this).apply { text = "Delete saved profile" }
        listOf(title, status, input, import, start, stop, reload, forget).forEach {
            panel.addView(it, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }
        setContentView(panel)
        import.setOnClickListener {
            try {
                val raw = input.text.toString().trim()
                RealityProfile.parse(raw)
                SecretStore.save(this, raw)
                input.text.clear()
                Toast.makeText(this, "Profile encrypted on this phone", Toast.LENGTH_SHORT).show()
                refresh()
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid profile: " + (e.message ?: "format"), Toast.LENGTH_LONG).show()
            }
        }
        start.setOnClickListener {
            try {
                RealityProfile.parse(SecretStore.read(this) ?: error("Import a profile first"))
                val consent = VpnService.prepare(this)
                if (consent != null) startActivityForResult(consent, requestVpn) else begin()
            } catch (e: Exception) {
                Toast.makeText(this, e.message ?: "No profile", Toast.LENGTH_LONG).show()
            }
        }
        stop.setOnClickListener {
            startService(Intent(this, NativeVpnService::class.java).setAction(NativeVpnService.STOP))
            refresh()
        }
        reload.setOnClickListener { refresh() }
        forget.setOnClickListener {
            startService(Intent(this, NativeVpnService::class.java).setAction(NativeVpnService.STOP))
            SecretStore.clear(this); refresh()
        }
        refresh()
    }
    override fun onResume() { super.onResume(); if (::status.isInitialized) refresh() }
    private fun refresh() {
        val prefs = getSharedPreferences("state", MODE_PRIVATE)
        val saved = runCatching { SecretStore.read(this) }.getOrNull()
        val summary = saved?.let {
            runCatching { RealityProfile.parse(it).let { p -> p.server + ":" + p.port } }.getOrNull()
        } ?: "No profile"
        val state = prefs.getString("status", "Disconnected") ?: "Disconnected"
        status.text = "Profile: " + summary + "\nState: " + state + "\nTunnel started does not guarantee internet access."
    }
    private fun begin() {
        val intent = Intent(this, NativeVpnService::class.java).setAction(NativeVpnService.START)
        if (android.os.Build.VERSION.SDK_INT >= 26) startForegroundService(intent) else startService(intent)
        refresh()
    }
    @Deprecated("Legacy result handling for Android 9 compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestVpn && resultCode == RESULT_OK) begin()
    }
}
