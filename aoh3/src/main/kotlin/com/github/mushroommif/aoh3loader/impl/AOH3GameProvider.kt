package com.github.mushroommif.aoh3loader.impl

import com.github.mushroommif.aoh3loader.impl.patch.EntrypointPatch
import net.fabricmc.loader.api.metadata.ModDependency
import net.fabricmc.loader.impl.game.GameProvider
import net.fabricmc.loader.impl.game.GameProvider.BuiltinMod
import net.fabricmc.loader.impl.game.patch.GameTransformer
import net.fabricmc.loader.impl.launch.FabricLauncher
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata
import net.fabricmc.loader.impl.metadata.ModDependencyImpl
import net.fabricmc.loader.impl.util.Arguments
import net.fabricmc.loader.impl.util.SystemProperties
import java.io.File
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class AOH3GameProvider: GameProvider {
    private val arguments = Arguments() // there is no arguments passed to the game on launch
    private val gameVersion: String = loadGameVersion()
    private val transformer = GameTransformer(EntrypointPatch)

    override fun getGameId(): String = "aoh3"

    override fun getGameName(): String = "Age of History 3"

    override fun getRawGameVersion(): String {
        return gameVersion
    }

    override fun getNormalizedGameVersion(): String {
        return gameVersion
    }

    override fun getBuiltinMods(): Collection<BuiltinMod> {
        return listOf(BuiltinMod(
            listOf(gameJarPath), BuiltinModMetadata.Builder(gameId, normalizedGameVersion)
                .setName(gameName)
                .addDependency(
                    ModDependencyImpl(
                        ModDependency.Kind.DEPENDS,
                        "java", listOf(">=8")
                    )
                )
                .build()
        ))
    }

    override fun getEntrypoint(): String {
        return GAME_ENTRYPOINT
    }

    override fun getLaunchDirectory(): Path = Paths.get(".")

    override fun isObfuscated(): Boolean = false

    override fun requiresUrlClassLoader(): Boolean = false

    override fun isEnabled(): Boolean = true

    override fun locateGame(launcher: FabricLauncher, args: Array<out String>): Boolean {
        if (!Files.exists(gameJarPath)) {
            return false
        }

        return true
    }

    override fun initialize(launcher: FabricLauncher) {
        transformer.locateEntrypoints(launcher, listOf(gameJarPath))
    }

    override fun getEntrypointTransformer(): GameTransformer {
        return transformer
    }

    override fun unlockClassPath(launcher: FabricLauncher) {
        launcher.addToClassPath(gameJarPath)
    }

    override fun launch(loader: ClassLoader) {
        val mainClass = loader.loadClass(GAME_ENTRYPOINT)
        val invoker = MethodHandles.lookup().findStatic(
            mainClass, "main", MethodType.methodType(
                Void.TYPE, Array<String>::class.java
            )
        )

        invoker.invokeExact(arrayOf<String>())
    }

    override fun getArguments(): Arguments {
        return arguments
    }

    override fun getLaunchArguments(sanitize: Boolean): Array<String> = emptyArray()

    companion object {
        private const val GAME_ENTRYPOINT = "aoc.kingdoms.lukasz.jakowski.desktop.DesktopLauncher"
        private val gameValuesPath = Paths.get("game/gameValues/GameValues_Text.json").toAbsolutePath()
        private val gameJarPath = Paths.get(
            System.getProperty(SystemProperties.GAME_JAR_PATH) ?: "aoh3.exe"
        )

        private fun loadGameVersion(): String {
            val predefinedVersion = System.getProperty(SystemProperties.GAME_VERSION)
            if (predefinedVersion != null) return predefinedVersion

            val gameValuesFile = File(gameValuesPath.toUri())
            val gameValuesJson = gameValuesFile.readText()

            return gameValuesJson
                .split("VERSION: \"")[1]
                .split("\"")[0]
        }
    }
}