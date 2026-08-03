package com.dbg.mdm_offline_client.api

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val deviceId: String,
    val deviceName: String,
    val platform: String,
    val appVersion: String,
)

@Serializable
data class RegisterResponse(
    val serverId: String,
    val accepted: Boolean,
    val message: String,
)

@Serializable
data class StatusDeviceDto(
    val id: String,
    val name: String,
    val platform: String,
    val registeredAt: Long,
)

@Serializable
data class StatusResponse(
    val running: Boolean = false,
    val lanAddress: String = "",
    val devices: List<StatusDeviceDto> = emptyList(),
    val onlineDeviceCount: Int = 0,
)
