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
   
   ![image](https://github.com/user-attachments/assets/0df671e0-d77a-4e77-96a6-b4e366baba08)

2. Open OBS. Navigate to "Tools" -> "Scripts".

   ![image](https://kappa.lol/CJ3qxg)
 
3. Click + in bottom left corner.

   ![image](https://kappa.lol/PRCN_5)

4. Paste Script Path you copied in step 1, then paste it in the bottom bar. Click "Open".

   ![image](https://kappa.lol/HqYBOx)

5. Add a new OBS source. Click "+", then "Calc Overlay". That's it.

   ![image](https://kappa.lol/wsZdal)


## Configuring Calc Overlay
There is a lot of configuration you can do. I'll go over each setting:

### General

![image](https://github.com/user-attachments/assets/b566b614-b00e-455c-b65e-13944aef6f9a)

- **Font** - customize the font, needs a pretty large size (44pt by default).
  > NOTE! <br>
  > If you can't find an installed font, make sure to install the font for \*all users\*!<br>
  > Right-click the font file -> Install for all users.
- **Overlay position** - positions the overlay within the image (image is always the same size, so this setting will anchor it to a corner)

---

### Eye Throws Overlay

![image](https://github.com/user-attachments/assets/0427a2ee-0e0a-4bdd-a950-1fca1dd66c67)

Click "Preview Eye Throws Overlay". This will open a new window that displays a dummy eye throw measurement. You can see how your layout might look like in OBS.

- **Shown measurements** - number of displayed rows for measurements, between 1 and 5
- **Overworld coords** - same as the Ninjabrain Bot setting (chunk, 8-8 or 4-4)
- **Show angle direction** - whether to show what direction you need to go (shown next to Angle, e.g. `(<- 14.2)`)
- **Show Overworld/Nether coords based on dimension** - displays ONLY overworld or nether coords, depending on the last dimension you F3+C'd in (even if they are both disabled in Column Settings)

#### Columns
- **Show column** - whether to show the column or not
- **Header** - what to show above the column: text, icon, or nothing
- **Move left** - moves the column 1 place left
- **Move down** - moves the column 1 place right

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
- **Move down** - moves the column 1 place right

#### Rows
- **Show row** - whether to show the row or not
- **Move up** - moves the column 1 place up
- **Move left** - moves the column 1 place left
