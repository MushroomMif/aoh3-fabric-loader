package com.github.mushroommif.aoh3loader.impl

data class AOH3LaunchSettings(
    val jarPath: String = defaultJarPath,
    val gameValuesPath: String = "game/gameValues/GameValues_Text.json",
    val gameEntrypoint: String = "aoc.kingdoms.lukasz.jakowski.desktop.DesktopLauncher",
    val versionOverride: String = ""
) {
    companion object {
        private val defaultJarPath = if (isOnMac()) "Age of History 3.app" else "aoh3.exe"
        private fun isOnMac(): Boolean = System.getProperty("os.name").lowercase().startsWith("mac")
    }
}
