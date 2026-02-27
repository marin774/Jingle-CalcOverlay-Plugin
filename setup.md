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

Only do this setup if you don't use Toolscreen, or if you want a different overlay on OBS.

1. In Jingle, click "Copy Script Path" button.
   
   ![image](https://github.com/user-attachments/assets/0df671e0-d77a-4e77-96a6-b4e366baba08)

2. Open OBS. Navigate to "Tools" -> "Scripts".

   <img width="535" height="206" alt="image" src="https://github.com/user-attachments/assets/20f93845-9c7b-41a7-b44b-d220bd3b56ab" />
 
3. Click + in bottom left corner.

   <img width="763" height="517" alt="image" src="https://github.com/user-attachments/assets/35ff854b-a101-437a-a856-d753bc65a2ff" />

4. Paste Script Path you copied in step 1, then paste it in the bottom bar. Click "Open".

   <img width="943" height="508" alt="image" src="https://github.com/user-attachments/assets/818fe8f0-f5e6-453f-bdf6-c01019ac0357" />

5. Add a new OBS source. Click "+", then "Calc Overlay". That's it.

   <img width="356" height="539" alt="image" src="https://github.com/user-attachments/assets/cdf05656-75ba-4643-9c5c-c40a69a3c6a0" />

## Toolscreen Window Overlay

If you're using Toolscreen, you can use CalcOverlay's window overlay instead of Ninjabrain Bot's window overlay.

1. In Jingle, click "Copy Color Key" button.

   <img width="580" height="186" alt="image" src="https://github.com/user-attachments/assets/7b305b48-4ee3-4c6a-89c1-85e22210d3c1" />

2. Open Toolscreen's config, navigate to Window Overlays -> "Add New Window Overlay"

   <img width="428" height="225" alt="image" src="https://github.com/user-attachments/assets/02f7de69-2191-4a04-b330-3fd110c91121" />

3. Expand the new Window Overlay. Under "Select Window", select "[javaw.exe] Calc Overlay [TOOLSCREEN CAPTURE WINDOW]".

   <img width="644" height="220" alt="image" src="https://github.com/user-attachments/assets/6c148468-6f4b-485f-8cd9-ca47ade8db2d" />

4. Add a color key to remove the gray background.
   - Scroll down to "Color Keying".
   - Click "Enable Color Key".
   - Click "+ Add Color Key".
   - Click the box next to numbers and paste the Color Key in the bottom bar.
   - Move the slider to 0.02.

   <img width="543" height="422" alt="image" src="https://github.com/user-attachments/assets/4645d37c-13d2-489c-9369-664dd1746822" />

That's it. You can change the scale and move the Window Overlay to your liking.

## Configuring Calc Overlay
There is a lot of configuration you can do. I'll go over each setting:

### General

![image](https://github.com/user-attachments/assets/51819444-417c-412f-98ae-2bce7f9df82b)

- **Font** - customize the font, needs a pretty large size (44pt by default).
  > NOTE! <br>
  > If you can't find an installed font, make sure to install the font for \*all users\*!<br>
  > Right-click the font file -> Install for all users.
- **Overlay position** - positions the overlay within the image (image is always the same size, so this setting will anchor it to a corner)
- **Outline width** - controls the width of the outline around all text, set to 0 to disable outline.

---

### Eye Throws Overlay

<img width="568" height="446" alt="image" src="https://github.com/user-attachments/assets/bd25e98e-564f-4975-ab88-bf216ead68a7" />

Click "Preview Eye Throws Overlay". This will open a new window that displays a dummy eye throw measurement. You can see how your layout might look like in OBS.

- **Shown measurements** - number of displayed rows for measurements, between 1 and 5
- **Overworld coords** - same as the Ninjabrain Bot setting (chunk, 8-8 or 4-4)
- **Angle display** - whether to show angle and angle change (e.g. `35.53 (<- 8.4)`), just the angle (e.g. `35.53`), or just the angle change (e.g. `<- 8.4`)
- **Show info bar** - displays more info: high error, close stronghold (portal link), and angle + adjustments on the last measurement (this is estimated, will be fixed with Ninjabrain Bot v1.5.2+)
- **Show Overworld/Nether coords based on dimension** - displays ONLY overworld or nether coords, depending on the last dimension you F3+C'd in (even if they are both disabled in Column Settings)

#### Columns
- **Show column** - whether to show the column or not
- **Header** - what to show above the column: text, icon, or nothing
- **Move left** - moves the column 1 place left
- **Move right** - moves the column 1 place right

---

### All Advancements Overlay

![image](https://github.com/user-attachments/assets/39235014-4408-4b69-9c15-374bf65dd098)

**NOTE**: This overlay will show on OBS if your Ninjabrain Bot is in "All Advancements" mode (pictured below).
![image](https://github.com/user-attachments/assets/549a16a7-d974-4d92-a7d0-0851924fa1bc)

Click "Preview All Advancements Overlay". This will open a new window that displays a dummy All Advancements tab. You can see how your layout might look like in OBS.

#### Columns
- **Show column** - whether to show the column or not
- **Header** - what to show above the column: text or nothing
- **Move left** - moves the column 1 place left
- **Move right** - moves the column 1 place right

#### Rows
- **Show row** - whether to show the row or not
- **Move up** - moves the column 1 place up
- **Move left** - moves the column 1 place left
