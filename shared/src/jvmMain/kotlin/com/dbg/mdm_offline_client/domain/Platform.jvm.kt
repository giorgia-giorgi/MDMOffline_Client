package com.dbg.mdm_offline_client.domain

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun defaultDeviceName(): String =
    System.getProperty("user.name")?.takeIf { it.isNotBlank() }?.let { "$it PC" }
        ?: "Desktop device"

actual fun platformLabel(): String = "Desktop"

actual fun appVersionName(): String = "1.0.0"

actual fun newDeviceId(): String = machineSerialNumber().orEmpty()

private fun machineSerialNumber(): String? {
    val os = System.getProperty("os.name").orEmpty().lowercase()
    val raw = when {
        os.contains("win") -> windowsBiosSerial()
        os.contains("mac") || os.contains("darwin") -> macSerial()
        else -> linuxSerial()
    } ?: return null
    val serial = raw.trim()
    if (serial.isEmpty()) return null
    if (serial.equals("To Be Filled By O.E.M.", ignoreCase = true)) return null
    if (serial.equals("None", ignoreCase = true)) return null
    if (serial.equals("Default string", ignoreCase = true)) return null
    return serial
}

private fun windowsBiosSerial(): String? = runCatching {
    val process = ProcessBuilder(
        "powershell",
        "-NoProfile",
        "-Command",
        "(Get-CimInstance -ClassName Win32_BIOS).SerialNumber",
    ).redirectErrorStream(true).start()
    val output = process.inputStream.bufferedReader().readText()
    process.waitFor()
    output.lineSequence().map { it.trim() }.firstOrNull { it.isNotEmpty() }
}.getOrNull()

private fun macSerial(): String? = runCatching {
    val process = ProcessBuilder(
        "ioreg",
        "-l",
    ).redirectErrorStream(true).start()
    val output = process.inputStream.bufferedReader().readText()
    process.waitFor()
    Regex(""""IOPlatformSerialNumber"\s*=\s*"([^"]+)"""")
        .find(output)
        ?.groupValues
        ?.getOrNull(1)
}.getOrNull()

private fun linuxSerial(): String? = runCatching {
    java.io.File("/sys/class/dmi/id/product_serial").takeIf { it.canRead() }?.readText()?.trim()
}.getOrNull()
