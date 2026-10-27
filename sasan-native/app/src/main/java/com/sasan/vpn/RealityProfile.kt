package com.sasan.vpn

import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.net.URLDecoder
import java.util.UUID

data class RealityProfile(
    val server: String, val port: Int, val uuid: String,
    val sni: String, val publicKey: String, val shortId: String,
    val fingerprint: String, val flow: String, val spiderX: String,
) {
    companion object {
        fun parse(input: String): RealityProfile {
            require(input.length < 4096) { "Link too long" }
            val url = URI(input.trim())
            require(url.scheme.equals("vless", ignoreCase = true)) { "Only VLESS supported" }
            val server = url.host ?: error("Missing server hostname")
            val port = url.port
            require(port in 1..65535) { "Invalid port" }
            val uuid = url.rawUserInfo ?: error("Missing UUID")
            require(UUID.fromString(uuid).toString().equals(uuid, true)) { "Invalid UUID" }
            val args = (url.rawQuery ?: "").split("&").filter { it.isNotBlank() }
                .associate { kv ->
                    val key = kv.substringBefore("=")
                    val value = URLDecoder.decode(kv.substringAfter("=", ""), "UTF-8")
                    key to value
                }
            require(args["security"] == "reality") { "Expected security=reality" }
            require(args.getOrDefault("type", "tcp") == "tcp") { "Only TCP supported" }
            val key = args["pbk"].orEmpty()
            require(key.matches(Regex("^[A-Za-z0-9_-]{43}$"))) { "Invalid REALITY public key" }
            val short = args["sid"].orEmpty()
            require(short.matches(Regex("^(?:[0-9a-fA-F]{2}){0,8}$"))) { "Invalid short ID" }
            val sni = args["sni"].orEmpty()
            require(sni.isNotBlank()) { "Missing SNI" }
            val fp = args.getOrDefault("fp", "chrome")
            require(fp in setOf("chrome", "firefox", "safari", "edge", "ios")) { "Unsupported fingerprint" }
            val flow = args["flow"].orEmpty()
            require(flow.isEmpty() || flow == "xtls-rprx-vision") { "Unsupported flow" }
            return RealityProfile(server, port, uuid, sni, key, short, fp, flow, args["spx"] ?: "/")
        }
    }

    fun config(): String {
        val tls = JSONObject().put("enabled", true).put("server_name", sni)
            .put("insecure", false)
            .put("utls", JSONObject().put("enabled", true).put("fingerprint", fingerprint))
            .put("reality", JSONObject().put("enabled", true).put("public_key", publicKey).put("short_id", shortId))
        val outbound = JSONObject().put("type", "vless").put("tag", "proxy")
            .put("server", server).put("server_port", port).put("uuid", uuid)
            .put("tls", tls)
        if (flow.isNotEmpty()) outbound.put("flow", flow)
        return JSONObject()
            .put("log", JSONObject().put("level", "warn"))
            .put("dns", JSONObject()
                .put("servers", JSONArray().put(JSONObject().put("tag", "dns-proxy").put("type", "udp")
                    .put("server", "1.1.1.1").put("server_port", 53).put("detour", "proxy")))
                .put("final", "dns-proxy").put("strategy", "prefer_ipv4"))
            .put("inbounds", JSONArray().put(JSONObject().put("type", "tun")
                .put("tag", "tun-in").put("address", JSONArray().put("172.18.0.1/30"))
                .put("mtu", 1400).put("auto_route", true).put("strict_route", true)
                .put("stack", "gvisor")))
            .put("outbounds", JSONArray().put(outbound).put(JSONObject().put("tag", "direct").put("type", "direct")))
            .put("route", JSONObject().put("auto_detect_interface", true)
                .put("rules", JSONArray().put(JSONObject().put("protocol", "dns").put("action", "hijack-dns")))
                .put("final", "proxy"))
            .toString()
    }
}
