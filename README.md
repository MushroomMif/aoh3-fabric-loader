AOH3 Fabric Loader
===========
This is a modified for working with Age of History 3 game, version of 
Fabric Loader originally created for Minecraft modding.
For now, it is very experimental and can be unstable, but you can
already try it. Known issues:
- Logging system of the game is not working properly

# How to use it:
> [!WARNING]  
> The loader was only tested on the Windows at the moment and likely 
> won't work on Mac

- Go to "Actions" page of this repository, open a last action and
download its artifacts
- Unzip downloaded archive into the game directory
- Download
[an archive with required libraries](https://disk.yandex.ru/d/lNFkjFRI_5MzUQ)
and also unzip it to the game directory
- Create a `start.bat` file in the game directory and write
`"jre/bin/java.exe" -cp "./*" net.fabricmc.loader.impl.launch.knot.KnotClient` 
there
- Open the created file, it will launch the game with the loader
- Now you can put mods into `mods` directory and configure
`fabric_launch_settings.json` if needed
- Enjoy!

Later I will write how to create a mod for this loader and will provide an 
example mod. Remember that the loader is in very active development right
now so don't expect a lot from it