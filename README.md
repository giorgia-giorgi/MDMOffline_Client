# MDM Offline: Device client

## The platform

**MDM Offline** is a lightweight, private mobile device management system that stays entirely on your local network. There is no cloud account, and management data does not leave your office, home, or team network. Devices do not need to be enrolled as managed devices, and no managed profiles are required.

It is made of two applications that work together:

- **Desktop console**: the control room on a PC
- **Device client** (this project): the companion app on each phone or tablet you want to oversee

Put the PC and the devices on the same Wi-Fi or office network, keep the console running, and enrolled devices appear on the PC. From there you can see which devices are online, inspect their status, and keep them under local watch.

The product is designed for small fleets: an office, a household, or a shared set of phones and tablets where tighter control matters and a public cloud MDM is more than you want.

The interface is available in English and Italian.

## This project

This repository is the **device client**: the app you install on each phone or tablet that should be listed on the PC console.

On first launch it explains what the platform is for and that management stays on the LAN. After that it finds the desktop console on your network, registers this device, and shows whether you are connected and listed on the PC.

The client can keep running in the background, so you can leave the app and the device still appears online in the console. You can also pause discovery when you do not want this device to check in.

The Android app is the primary client. A desktop companion is included for development and for using the same experience on a Windows machine.

To enroll a device, keep the MDM Offline desktop console running on a PC on the same network, then open this app.
