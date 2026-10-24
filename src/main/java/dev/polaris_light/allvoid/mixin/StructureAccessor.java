package dev.polaris_light.allvoid.mixin;

import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Structure.class)
interface StructureAccessor {
    @Invoker("getLowestY")
    static int allvoid$invokeGetLowestY(Structure.GenerationContext context, int width, int depth) {
        throw new AssertionError();
    }
}
