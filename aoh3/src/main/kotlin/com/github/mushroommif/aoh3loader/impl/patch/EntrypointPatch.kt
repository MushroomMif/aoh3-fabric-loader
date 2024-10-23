package com.github.mushroommif.aoh3loader.impl.patch

import net.fabricmc.loader.impl.game.patch.GamePatch
import net.fabricmc.loader.impl.launch.FabricLauncher
import org.objectweb.asm.tree.ClassNode
import java.util.function.Consumer
import java.util.function.Function

object EntrypointPatch: GamePatch() {
    override fun process(
        launcher: FabricLauncher,
        classSource: Function<String, ClassNode>,
        classEmitter: Consumer<ClassNode>
    ) {

    }
}