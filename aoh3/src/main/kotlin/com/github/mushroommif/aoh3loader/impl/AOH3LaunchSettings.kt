package com.github.mushroommif.aoh3loader.impl

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

data class AOH3LaunchSettings(
    val jarPath: String = defaultJarPath,
    val gameValuesPath: String = "game/gameValues/GameValues_Text.json",
    val gameEntrypoint: String = "aoc.kingdoms.lukasz.jakowski.desktop.DesktopLauncher",
    val versionOverride: String = ""
) {
    companion object Serializer: JsonSerializer<AOH3LaunchSettings>, JsonDeserializer<AOH3LaunchSettings> {
        private val defaultJarPath = if (isOnMac()) "Age of History 3.app" else "aoh3.exe"
        private fun isOnMac(): Boolean = System.getProperty("os.name").lowercase().startsWith("mac")

        override fun serialize(
            src: AOH3LaunchSettings,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            val json = JsonObject()
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
                json.get("jar_path").asString,
                json.get("game_values_path").asString,
                json.get("game_entrypoint").asString,
                json.get("version_override").asString
            )
        }
    }
}
