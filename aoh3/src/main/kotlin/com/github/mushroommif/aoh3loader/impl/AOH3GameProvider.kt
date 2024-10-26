package com.github.mushroommif.aoh3loader.impl

import com.github.mushroommif.aoh3loader.impl.patch.EntrypointPatch
import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.metadata.ModDependency
import net.fabricmc.loader.impl.game.GameProvider
import net.fabricmc.loader.impl.game.GameProvider.BuiltinMod
import net.fabricmc.loader.impl.game.patch.GameTransformer
import net.fabricmc.loader.impl.launch.FabricLauncher
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata
import net.fabricmc.loader.impl.metadata.ModDependencyImpl
import net.fabricmc.loader.impl.util.Arguments
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
        return gameEntrypoint
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
        val mainClass = try {
            loader.loadClass(gameEntrypoint)
        } catch (_: ClassNotFoundException) {
            error("Specified in $launchSettingsPath game_entrypoint class does not exist")
        }

        val invoker = try {
            MethodHandles.lookup().findStatic(
                mainClass, "main", MethodType.methodType(
                    Void.TYPE, Array<String>::class.java
                )
            )
        } catch (_: NoSuchMethodException) {
            error("Specified in $launchSettingsPath game_entrypoint class does not have a \"main\" method")
        } catch (_: IllegalAccessException) {
            error("Specified in $launchSettingsPath game_entrypoint class does not have a static \"main\" method")
        }

        invoker.invokeExact(arrayOf<String>())
    }

    override fun getArguments(): Arguments {
        return arguments
    }

    override fun getLaunchArguments(sanitize: Boolean): Array<String> = emptyArray()

    companion object {
        private val launchSettingsPath = Paths.get("fabric_launch_settings.json")

        private fun loadLaunchSettings(): AOH3LaunchSettings {
            val launchSettingsFile = launchSettingsPath.toFile()
            val gson = GsonBuilder()
                .setPrettyPrinting()
                .registerTypeHierarchyAdapter(AOH3LaunchSettings::class.java, AOH3LaunchSettings.Serializer)
                .create()

            if (!launchSettingsFile.exists()) {
                val defaultSettings = AOH3LaunchSettings()
                launchSettingsFile.createNewFile()
                launchSettingsFile.writeText(
                    gson.toJson(defaultSettings)
                )
                return defaultSettings
            }

            return try {
                gson.fromJson(
                    launchSettingsFile.readText(), AOH3LaunchSettings::class.java
                )
            } catch (e: Exception) {
                throw Exception("Failed to read $launchSettingsPath. " +
                        "You can delete it and run the loader again, it will reset the file", e)
            }
        }

        val launchSettings = loadLaunchSettings()
        init {
            if (launchSettings.jarPath == "error") {
                error("The launcher failed to locate jar file of the game, so you need to specify it in $launchSettingsPath by yourself")
            }
        }

        private val gameJarPath = Paths.get(launchSettings.jarPath)
        private val gameEntrypoint = launchSettings.gameEntrypoint

        private fun loadGameVersion(): String {
            val versionOverride = launchSettings.versionOverride
            if (versionOverride.isNotBlank()) {
                return versionOverride
            }

            val gameValuesPath = launchSettings.gameValuesPath
            if (gameValuesPath.isBlank()) {
                error("game_values_path and version_override are both empty in $launchSettingsPath. " +
                        "At least one of them should be specified")
            }

            val gameValuesFile = Paths.get(gameValuesPath).toFile()
            if (!gameValuesFile.exists()) {
                error("Specified in $launchSettingsPath game_values file does not exist")
            }

            val gameValuesJson = gameValuesFile.readText()
            return try {
                gameValuesJson
                    .split("VERSION: \"")[1]
                    .split("\"")[0]
            } catch (e: Exception) {
                throw Exception("Failed to read game_values file, is it corrupted or is the format changed? " +
                        "If it is the second, you can use \"version_override\" in $launchSettingsPath for now", e)
            }
        }
    }
}