package com.dbg.mdm_offline_client.domain

import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader


class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun isJvmPlatform(): Boolean = true

actual fun defaultDeviceName(): String =
    System.getProperty("user.name")?.takeIf { it.isNotBlank() }?.let { "$it PC" }
        ?: "Desktop device"

actual fun platformLabel(): String = "Desktop"

actual fun appVersionName(): String = "1.0.0"

actual fun newDeviceId(): String = machineSerialNumber().orEmpty()

private fun machineSerialNumber(): String? {
    val os = System.getProperty("os.name").orEmpty().lowercase()

    return when {
        os.contains("win") -> windowsMachineSerial()
        os.contains("mac") || os.contains("darwin") -> macSerial()
        else -> linuxSerial()
    }
}

private fun windowsMachineSerial(): String {
    var result = ""

    try {
        val file = File.createTempFile("realhowto", ".vbs")
        file.deleteOnExit()

        val fw = FileWriter(file)

        val vbs1 = ("Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                + "Set colItems = objWMIService.ExecQuery _ \n"
                + "   (\"Select * from Win32_Processor\") \n"
                + "For Each objItem in colItems \n"
                + "    Wscript.Echo objItem.ProcessorId \n"
                + "    exit for  ' do the first cpu only! \n"
                + "Next \n")

        fw.write(vbs1)

        fw.close()

        val p = Runtime.getRuntime().exec(
            "cscript //NoLogo " + file.path
        )

        val input = BufferedReader(
            InputStreamReader(p.inputStream)
        )

        var line: String?

        while ((input.readLine().also { line = it }) != null) {
            result += line
        }

        input.close()
    }

    catch (E: Exception) {
        System.err.println(
            "Windows CPU Exp : "
                    + E.message
        )
    }

    return result.trim { it <= ' ' }
}

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
