# Calc Overlay Setup

## Installation

Download the latest version from the [Releases](https://github.com/marin774/Jingle-CalcOverlay-Plugin/releases) page. Drag and drop it into your Jingle plugins folder, and restart Jingle.

## Enable API in Ninjabrain Bot
1. Open Ninjabrain Bot
2. Click on the settings wheel in the top right.

   ![image](https://github.com/user-attachments/assets/f7f819f1-3186-4b00-bcd4-3b67201e9db6)
3. Go to "Advanced" tab, then enable "Enable API (starts HTTP server)".

   ![image](https://github.com/user-attachments/assets/7e81f5cb-9c30-46f1-950c-82d31c10cca9)


## Enable CalcOverlay plugin
Once you've installed the plugin and restarted Jingle, enable the plugin and configure it:
1. Open the "Plugins" tab in Jingle.
2. Enable overlay by clicking on the "Enable overlay" checkbox.

## Setup OBS Script & Overlay
1. In Jingle, click "Copy Script Path" button.
2. Open OBS. Navigate to "Tools" -> "Scripts".

   ![image](https://kappa.lol/CJ3qxg)
 
3. Click + in bottom left corner.

   ![image](https://kappa.lol/PRCN_5)

4. Paste Script Path you copied in step 1, then paste it in the bottom bar. Click "Open".

   ![image](https://kappa.lol/HqYBOx)

5. Add a new OBS source. Click "+", then "Calc Overlay". That's it.

   ![image](https://kappa.lol/wsZdal)


## Configuring Calc Overlay
After you enabled the overlay, click "Preview Overlay" in the Overlay Settings section. This will open a new window that displays a dummy measurement. You can see how your layout might look like in OBS.

![example](https://github.com/user-attachments/assets/d4ab8839-2fcc-40ef-a6d1-2b6f9aa9257c)


### Buttons
- **Preview Overlay** - opens the overlay for testing different settings, described above

### General settings
- **Shown measurements** - number of displayed rows for measurements, between 1 and 5
- **Overlay position** - positions the overlay within the image (image is always the same size, so this setting will anchor it to a corner)
- **Overworld coords** - same as the Ninjabrain Bot setting
- **Font** - customize the font, needs a pretty large size (44pt by default).
  > NOTE! <br>
  > If you can't find an installed font, make sure to install the font for \*all users\*!<br>
  > Right-click the font file -> Install for all users.
- **Show angle direction** - whether to show what direction you need to go (shown next to Angle)
- **Show coords based on dimension** - displays ONLY overworld or nether coords, depending on the last dimension you F3+C'd in

### Column settings
- **Show column** - whether to show the column or not
- **Header** - what to show above the column: text, icon, or nothing
- **Move up** - moves the column 1 up (visually to the left)
- **Move down** - moves the column 1 down (visually to the right)
