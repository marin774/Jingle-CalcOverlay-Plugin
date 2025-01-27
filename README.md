# Jingle Calc Overlay Plugin

A Jingle plugin that makes a nicer Ninjabrain Bot OBS overlay. Overlays are customizable in settings.

![image](https://github.com/user-attachments/assets/5e522f9b-ef3b-46dc-bafa-728589848235)

## Installation

Download the latest version from the [Releases](https://github.com/marin774/Jingle-CalcOverlay-Plugin/releases) page. Drag and drop it into your Jingle plugins folder, and restart Jingle.

## Setup

1. Open Ninjabrain Bot
2. Click on the settings wheel in the top right.

   ![QiIkhogds3](https://github.com/user-attachments/assets/f7f819f1-3186-4b00-bcd4-3b67201e9db6)
3. Go to "Advanced" tab, then enable "Enable API (starts HTTP server)".

   ![tpPZRB54mE](https://github.com/user-attachments/assets/7e81f5cb-9c30-46f1-950c-82d31c10cca9)

Once you've installed the plugin and restarted Jingle, enable the plugin and configure it:
1. Open the "Plugins" tab in Jingle.
2. Click on "Open Config" next to Calc Overlay.
3. Enable overlay by clicking on the "Enable overlay" checkbox.
4. Add an Image Source to your OBS with the provided path (click "Copy File Path" to copy the image file path to your clipboard).

## Configuration
After you enabled the overlay, click "Open test overlay" in the Overlay Settings section. This will open a new window which displays a dummy measurement. You can use this window to instantly see how your overlay might look like in OBS. 

![image](https://github.com/user-attachments/assets/e6e67be4-dabf-48d2-b0a5-ecc728484adc)

### Buttons
- **Open test overlay** - opens the overlay for testing different settings, described above
- **Update OBS overlay** - if you have an active measurement in Ninjabrain Bot, use this button to update the overlay image (you don't have to click it, settings are saved as you edit them)

### General settings
- **Shown measurements** - number of displayed rows for measurements, between 1 and 5
- **Overlay position** - positions the overlay within the image (image is always the same size, so this setting will anchor it to a corner)
- **Overworld coords** - same as the Ninjabrain Bot setting
- **Show angle direction** - whether to show what direction you need to go (shown next to Angle)
- **Show coords based on dimension** - displays ONLY overworld or nether coords, depending on the last dimension you F3+C'd in

### Column settings
- **Show column** - whether to show the column or not
- **Show icon** - if the column is visible, whether to show the icon above the column or not
- **Move up** - moves the column 1 up
- **Move down** - moves the column 1 down
