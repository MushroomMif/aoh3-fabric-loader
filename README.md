[🇷🇺 Русский](https://github.com/MushroomMif/aoh3-fabric-loader/blob/master/README_RU.md)

> [!CAUTION]
> This project is dead. I am not interested in this project or in the AOH3 game anymore.
> Most likely this project will break with new AOH3 updates, if it is not already broken.
> If you want, you can fork it and update by yourself.
> Perhaps after some time I will be interested in this project again, but I don't think it will happen soon.
-----
AOH3 Fabric Loader
===========
This is a version of Fabric Loader, originally created for Minecraft modding,
modified for working with Age of History 3 game.
The loader is fully working now and looks stable, but the whole ecosystem around
it is still in beta so expect some bugs and flaws while using it.

# How to use it:
> [!WARNING]
> Fabric loader does not work with beta version of the game,
> it works only with the stable version
1. Download loader's jar file from its [latest release](https://github.com/MushroomMif/aoh3-fabric-loader/releases/latest)
and move it to the game directory
2. Download
[an archive with required libraries](https://disk.yandex.ru/d/lNFkjFRI_5MzUQ)
and unzip it also to the game directory
3. If you are using Windows:<br/>
Create a `start.bat` file in the game directory and write
`"jre/bin/java.exe" -cp "./*" net.fabricmc.loader.impl.launch.knot.KnotClient` there
<br/><br/>
If you are using Mac:<br/>
   - Download and install [JRE 8](https://adoptium.net/temurin/releases/?os=mac&package=jre&version=8&arch=any)
   - Create a `start.sh` file in the game directory and write the following there
   ```shell
   #!/bin/sh
   java -cp "./*" net.fabricmc.loader.impl.launch.knot.KnotClient
   ```
4. Open the created file, it will launch the game with the loader
5. Now you can put mods into `mods` directory (we recommend you to install
   [AOH3 Fabric Api](https://github.com/MushroomMif/aoh3-fabric-api) right away 
   as almost all mods need this for working properly) and configure
   `fabric_launch_settings.json` if needed. Enjoy!

# How to develop a mod for it:
Have a look at [Example AOH3 Fabric mod repository](https://github.com/MushroomMif/example-aoh3-fabric-mod).
It has a great instruction telling how to set up your mod's project.
