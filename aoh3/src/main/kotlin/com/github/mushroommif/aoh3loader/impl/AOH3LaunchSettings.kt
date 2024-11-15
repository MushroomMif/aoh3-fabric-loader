package com.github.mushroommif.aoh3loader.impl

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.nio.file.Paths

data class AOH3LaunchSettings(
    var schemaVersion: Int = SCHEMA_VERSION,
    var jarPath: String = if (isOnMac()) locateMacGameJar() else "game.jar",
    var gameValuesPath: String = "game/gameValues/GameValues_Text.json",
    var gameEntrypoint: String = "aoh.kingdoms.history.mainGame.desktop.DesktopLauncher",
    var versionOverride: String = ""
) {
    companion object Serializer: JsonSerializer<AOH3LaunchSettings>, JsonDeserializer<AOH3LaunchSettings> {
        const val SCHEMA_VERSION = 1

        private fun isOnMac(): Boolean {
            return System.getProperty("os.name").lowercase().startsWith("mac")
        }

        private fun locateMacGameJar(): String {
            return try {
                val contentDir = Paths.get("Age of History 3.app/Contents/MacOS").toFile()
                contentDir.list().first {
                    it.startsWith("Age of History 3") && it.endsWith(".jar")
                }
            } catch (_: Exception) {
                "error"
            }
        }

        override fun serialize(
            src: AOH3LaunchSettings,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            val json = JsonObject()
            json.addProperty("__schema_version", src.schemaVersion)
            json.addProperty("jar_path", src.jarPath)
            json.addProperty("game_values_path", src.gameValuesPath)
            json.addProperty("game_entrypoint", src.gameEntrypoint)
            json.addProperty("version_override", src.versionOverride)
            return json
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): AOH3LaunchSettings {
            require(json is JsonObject)
            return AOH3LaunchSettings(
                json.get("__schema_version")?.asInt ?: 0,
                json.get("jar_path").asString,
                json.get("game_values_path").asString,
                json.get("game_entrypoint").asString,
                json.get("version_override").asString
            )
        }
    }
}
