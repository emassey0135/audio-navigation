# Audio Navigation

This is a Minecraft mod to add audio navigation features, mostly to make it easier for blind players to play the game. It is intended to complement, not replace, the [Minecraft Access](https://github.com/minecraft-access/minecraft-access) mod. Some features in this mod are inspired by [Microsoft Soundscape](https://github.com/microsoft/soundscape).

## Dependencies

This mod runs on either Fabric or NeoForge, and depends on the following mods:

* Fabric API (Fabric only)
* Fabric Language Kotlin (Fabric only)
* Kotlin for Forge (NeoForge only)
* Architectury API
* Fzzy Config

Also, it uses a [native library](https://github.com/emassey0135/audio-navigation-tts) for speech synthesis, which is downloaded automatically on first launch. The library is compiled for Windows, MacOS, and Linux, for both x86_64 and aarch64. Create an issue if you use an unsupported operating system or architecture and I will try to add it.

## Things to Know

This mod must be installed on both the client and server side, so if you are playing on a server, you must install it there as well as on your client. Also, points of interest for trees and other features will not be created unless you generate the world with this mod installed. If you travel into ungenerated chunks, it will save points of interest there, but once a chunk is generated it is too late to generate these POIs. Points of interest are stored in poi.db inside your .minecraft folder, or in the Minecraft server root if you are playing on a dedicated server. Deleting this file will delete all POIs.

## Current Features

* Points of interest will be announced as you pass, according to the configured radius, vertical distance limit, and maximum number of announcements. You will hear the announcement from the direction of the point of interest, and a sound will be played before speaking the announcement based on the type of the POI.
* When a world is generated, or when you travel into ungenerated chunks or trigger chunk generation in some other way, points of interest are created for features such as trees, bee hives, ice spikes, ore vanes, etc. Structures like villages are coming soon.
* You can create landmarks by pressing F6 to open the menu and pressing the "Add landmark" button. They are saved as points of interest and announced as you pass.
* To delete a landmark, oepn the menu with F6, press "Landmarks", find the landmark you want to delete, and press the "Delete" button.
* You can start an audio beacon on a landmark by opening the list of landmarks, finding the landmark you want, and pressing "Start beacon". This will start a continuous sound coming from the direction of the landmark that you can follow to find it. The sound will change depending on if you're facing it, facing away from it, or facing in the opposite direction.
* To stop an audio beacon, open the menu with F6 and press "Stop beacon".
* The landmark list is filtered by radius. To increase the radius in which landmarks are shown, press enter on the current radius.
* This mod is extremely configurable. To open the settings, press F6 to open the menu, and choose "Settings". From there, the configuration screen should be pretty intuitive to use.
* This mod can use multiple speech synthesizers. eSpeak NG is always included. On Windows, SAPI 5 is also supported, and on MacOS the mod can use AVSpeechSynthesizer to use all Apple TTS voices as well as third-party voices exposed to the system TTS.
* Since the list of voices is extremely long if all voices are included, it is filtered by synthesizer and language. When you open the synthesizers or languages list in speech settings, you can select one or multiple options, which will change how the voice list is filtered. By default all speech synthesizers are selected, but the current Minecraft language is the only selected language.
* The beacon sounds in this mod are taken from Soundscape, so if you are familiar with Soundscape's beacon names, they are the same sounds. If not, you can start a beacon and change the sound while the beacon is running, and the sound will change when you change the setting.
