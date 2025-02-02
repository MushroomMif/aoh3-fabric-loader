package com.github.mushroommif.aoh3loader.impl.patch

import net.fabricmc.loader.impl.game.patch.GamePatch
import net.fabricmc.loader.impl.launch.FabricLauncher
import net.fabricmc.loader.impl.launch.FabricLauncherBase
import net.fabricmc.loader.impl.util.log.Log
import net.fabricmc.loader.impl.util.log.LogCategory
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import java.util.function.Consumer
import java.util.function.Function

object BrandingPatch: GamePatch() {
    private const val GV_INJECTED_CLASS = "aoc.kingdoms.lukasz.jakowski.GameValues"
    private const val GV_INJECTED_METHOD = "init"
    private const val GV_INJECT_AFTER_SET = "text"

    private const val MM_INJECTED_CLASS = "aoc.kingdoms.lukasz.menus.MainMenu"
    private const val MM_INJECTED_METHOD = "<init>"
    private const val MM_INJECT_AT_FIELD_GET = "BUTTON_HEIGHT3"

    private const val LM_INJECTED_CLASS = "aoc.kingdoms.lukasz.menusInGame.InGame_LegaciesEmpty"
    private const val LM_INJECTED_METHOD = MM_INJECTED_METHOD
    private const val LM_INJECT_AT_FIELD_GET = MM_INJECT_AT_FIELD_GET

    private val internalName = this::class.java.name.replace(".", "/")

    override fun process(
        launcher: FabricLauncher,
        classSource: Function<String, ClassNode?>,
        classEmitter: Consumer<ClassNode>
    ) {
        // Adding " Fabric" to game version and calculating new x position for it in menu
        val gameValuesClass = classSource.apply(GV_INJECTED_CLASS)
            ?: return logFailure("Could not load class $GV_INJECTED_CLASS")
        val initMethod = findMethod(gameValuesClass) { method ->
            method.name == GV_INJECTED_METHOD
        } ?: return logFailure("Could not find $GV_INJECTED_METHOD method in $GV_INJECTED_CLASS")

        val insnIterator = initMethod.instructions.iterator()
        val setTextInsn = findInsn(initMethod, { insn ->
            insn is FieldInsnNode && insn.opcode == Opcodes.PUTSTATIC && insn.name == GV_INJECT_AFTER_SET
        }, false) ?: return logFailure("Could not find set of static field $GV_INJECT_AFTER_SET in $GV_INJECTED_METHOD")

        moveAfter(insnIterator, setTextInsn)
        insnIterator.add(MethodInsnNode(
            Opcodes.INVOKESTATIC, internalName, ::setCustomBranding.name, "()V", false
        ))

        // Fixing game version display to fully show "Fabric" text
        val mainMenuClass = classSource.apply(MM_INJECTED_CLASS)
            ?: return logFailure("Could not load class $MM_INJECTED_CLASS")
        patchGameVersionPosToCustom(mainMenuClass, MM_INJECTED_METHOD, MM_INJECT_AT_FIELD_GET)
            ?.let(::logFailure)

        val legaciesMenuClass = classSource.apply(LM_INJECTED_CLASS)
            ?: return logFailure("Could not load class $LM_INJECTED_CLASS")
        patchGameVersionPosToCustom(legaciesMenuClass, LM_INJECTED_METHOD, LM_INJECT_AT_FIELD_GET)
            ?.let(::logFailure)

        classEmitter.apply {
            accept(gameValuesClass)
            accept(mainMenuClass)
            accept(legaciesMenuClass)
        }
    }

    @JvmField
    var VERSION_TEXT_POS_X = 0

    @JvmStatic @Suppress("UNUSED")
    fun setCustomBranding() {
        try {
            val gameValuesClass = FabricLauncherBase.getClass(GV_INJECTED_CLASS)
            val gameTextField = gameValuesClass.getField(GV_INJECT_AFTER_SET)
            val gameText = gameTextField.get(null)
            val gameTextClass = gameText.javaClass

            val versionField = gameTextClass.getField("VERSION")
            val currentVersion = versionField.get(gameText) as String
            versionField.set(gameText, "$currentVersion Fabric")

            val cfgClass = FabricLauncherBase.getClass("aoh.kingdoms.history.mainGame.CFG")
            val buttonHeight3Field = cfgClass.getField("BUTTON_HEIGHT3")
            val buttonHeight3 = buttonHeight3Field.get(null) as Int
            VERSION_TEXT_POS_X = (buttonHeight3 * 2.3).toInt()
        } catch (e: Exception) {
            Log.warn(LogCategory.GAME_PATCH, "Failed to set custom branding", e)
        }
    }

    private fun patchGameVersionPosToCustom(injectedClass: ClassNode, injectedMethodName: String, patchGetFieldName: String): String? {
        val method = findMethod(injectedClass) { method ->
            method.name == injectedMethodName
        } ?: return "Could not find $injectedMethodName method in ${injectedClass.name}"

        val getButtonHeight3Insn = findInsn(method, { insn ->
            insn is FieldInsnNode && insn.opcode == Opcodes.GETSTATIC && insn.name == patchGetFieldName
        }, false) as? FieldInsnNode ?: return "Could not find $patchGetFieldName get in ${injectedClass.name} $injectedMethodName method"

        getButtonHeight3Insn.owner = internalName
        getButtonHeight3Insn.name = ::VERSION_TEXT_POS_X.name

        return null
    }

    private fun logFailure(text: String) {
        Log.warn(LogCategory.GAME_PATCH, "Failed to apply Branding patch: $text")
    }
}