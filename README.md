This is a Kotlin Multiplatform project for the **MDM Offline** mobile client (Compose Multiplatform), companion to the LAN-only desktop MDM console.

* [/iosApp](./iosApp/iosApp) contains an iOS application entry point.
* [/shared](./shared/src) holds shared UI, networking, i18n, and settings (`commonMain` + platform sources).
* [/androidApp](./androidApp) is the primary Android application (required for v1).

### Product

**MDM Offline** keeps device management on your local network — no cloud accounts. The PC console runs an embedded HTTP server; this app discovers it, registers the device, and shows connection status.

### Requirements to connect

1. Run the **MDM Offline desktop console** on a PC on the same Wi‑Fi / LAN.
2. Install and open this client on the phone/tablet.
3. Ports used:
   - **HTTP** `9876` — `GET /status`, `POST /register`, `POST /update_info` (runtime checks `/status` on launch; discovers only if needed; `/update_info` every 10 min with the same fallback)
   - **UDP discovery** `9877` — broadcast `MDM_DISCOVER`, reply `MDM_SERVER|<ipv4>|<httpPort>`

Cleartext HTTP on the LAN is expected (no TLS in v1).

### Running the apps

Use the run configurations in your IDE toolbar, or:

- Android app: `./gradlew :androidApp:assembleDebug`
- Desktop (JVM) shell of the shared UI: `./gradlew :desktopApp:run`
- iOS app: open [/iosApp](./iosApp) in Xcode and run from there (UDP discovery is not implemented on iOS in v1 — use manual IP entry)

### Running tests

- Android host tests: `./gradlew :shared:testAndroidHostTest`
- Desktop tests: `./gradlew :shared:jvmTest`
- Common protocol/i18n tests: `./gradlew :shared:jvmTest`

### v1 scope

Onboarding, LAN discovery, register, home status, manual server entry, EN/IT strings, local persistence of `deviceId` / tutorial / last server URL.

Out of scope: lock/wipe/policies/commands, auth, dark theme, Device Owner APIs.

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html).
