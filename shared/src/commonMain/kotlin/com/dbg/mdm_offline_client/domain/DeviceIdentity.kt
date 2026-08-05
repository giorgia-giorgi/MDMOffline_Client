package com.dbg.mdm_offline_client.domain

/** Friendly default name for registration (e.g. device model). */
expect fun defaultDeviceName(): String

/** Platform label sent in `/register` (`Android`, `Desktop`). */
expect fun platformLabel(): String

/** App version string used in registration. */
expect fun appVersionName(): String

/** Generate a new UUID string for stable device identity. */
expect fun newDeviceId(): String
