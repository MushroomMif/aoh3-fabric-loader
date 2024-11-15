package com.github.mushroommif.aoh3loader.impl.patch

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.impl.game.patch.GamePatch
import net.fabricmc.loader.impl.launch.FabricLauncher
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import java.util.function.Consumer
import java.util.function.Function

object EntrypointPatch: GamePatch() {
    private const val INJECTED_CLASS = "aoh.kingdoms.history.mainGame.AA_Game"
    private const val INJECTED_METHOD = "create"
    private const val INJECT_AFTER_CALL = "initLoadInterface"
    private val internalName = this::class.java.name.replace(".", "/")

    override fun process(
        launcher: FabricLauncher,
        classSource: Function<String, ClassNode?>,
        classEmitter: Consumer<ClassNode>
    ) {
        val gameClass = classSource.apply(INJECTED_CLASS)
        requireNotNull(gameClass) { "Could not load game class $INJECTED_CLASS" }

        val createMethod = findMethod(gameClass) { method ->
            method.name == INJECTED_METHOD
        }
        requireNotNull(createMethod) { "Could not find \"$INJECTED_METHOD\" method in $INJECTED_CLASS!" }

        val insnIterator = createMethod.instructions.iterator()
        val loadFileInterfaceInsn = findInsn(createMethod, { insn ->
            insn is MethodInsnNode && insn.name == INJECT_AFTER_CALL
        }, false)
        requireNotNull(loadFileInterfaceInsn) { "Could not find call of \"$INJECT_AFTER_CALL\" method in $INJECTED_METHOD!" }

        moveAfter(insnIterator, loadFileInterfaceInsn)

        insnIterator.add(MethodInsnNode(
            Opcodes.INVOKESTATIC, internalName, "invokeModEntrypoints", "()V", false
        ))

        classEmitter.accept(gameClass)
    }


    @JvmStatic @Suppress("UNUSED")
    fun invokeModEntrypoints() {
        FabricLoader.getInstance().invokeEntrypoints("main", ModInitializer::class.java, ModInitializer::onInitialize)
    }
}