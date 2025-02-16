[🇺🇸 English](https://github.com/MushroomMif/aoh3-fabric-loader/blob/master/README.md)
-----
AOH3 Fabric Loader
===========
Это версия загрузчика Fabric, изначально созданного для модифицирования Minecraft,
изменённая для поддержки игры Age of History 3. Сам загрузчик работает хорошо
и выглядит стабильно, но вся экосистема вокруг него всё ещё в бете, поэтому
ожидайте баги и недоработки во время её использования.

# Как это использовать:
> [!WARNING]
> Загрузчик Fabric не работает с бета-версией игры,
> он работает только со стабильной версией
1. Скачайте jar-файл загрузчика из [последнего релиза](https://github.com/MushroomMif/aoh3-fabric-loader/releases/latest)
и переместите его в папку игры
2. Скачайте [архив с необходимыми библиотеками](https://disk.yandex.ru/d/lNFkjFRI_5MzUQ)
   и разархивируйте его содержимое тоже в папку игры
3. Если вы используйте Windows:<br/>
   Создайте файл `start.bat` в папке игры и напишите там
   `"jre/bin/java.exe" -cp "./*" net.fabricmc.loader.impl.launch.knot.KnotClient`
   <br/><br/>
   Если вы используете Mac:<br/>
   - Скачайте и установите [JRE 8](https://adoptium.net/temurin/releases/?os=mac&package=jre&version=8&arch=any)
   - Создайте файл `start.sh` в папке игры и напишите туда следующее
   ```shell
   #!/bin/sh
   java -cp "./*" net.fabricmc.loader.impl.launch.knot.KnotClient
   ```
4. Откройте созданный файл, он запустит игру вместе с Fabric загрузчиком
5. Теперь вы можете добавлять моды в папку `mods` (мы рекомендуем сразу поставить
   [AOH3 Fabric Api](https://github.com/MushroomMif/aoh3-fabric-api),
   так как он нужен для работы почти всех других модов) и настроить файл
   `fabric_launch_settings.json` если это необходимо. Наслаждайтесь!

# Как создать мод для этого загрузчика:
Взгляните [на репозиторий с примером AOH3 Fabric мода](https://github.com/MushroomMif/example-aoh3-fabric-mod).
В нём есть хорошая инструкция, рассказывающая о процессе установки и настройки
проекта вашего мода.