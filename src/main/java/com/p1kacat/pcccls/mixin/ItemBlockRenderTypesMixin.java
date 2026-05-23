package com.p1kacat.pcccls.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.p1kacat.pcccls.client.CompatRuntimeState;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesMixin {
    private static final ChunkRenderTypeSet PCCCLS_SOLID_SET = ChunkRenderTypeSet.of(RenderType.SOLID);

    @Inject(
            method = "getRenderLayers(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/neoforged/neoforge/client/ChunkRenderTypeSet;",
            at = @At("HEAD"),
            cancellable = true)
    private static void pcccls$forceSolidRenderLayers(
            BlockState state,
            CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        if (CompatRuntimeState.shouldFixLevititeShaderFlicker() && isAeronauticsLevitite(state)) {
            cir.setReturnValue(PCCCLS_SOLID_SET);
        }
    }

    @Inject(
            method = "getChunkRenderType(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/RenderType;",
            at = @At("HEAD"),
            cancellable = true)
    private static void pcccls$forceSolidChunkRenderType(
            BlockState state,
            CallbackInfoReturnable<RenderType> cir) {
        if (CompatRuntimeState.shouldFixLevititeShaderFlicker() && isAeronauticsLevitite(state)) {
            cir.setReturnValue(RenderType.SOLID);
        }
    }

    @Inject(
            method = "getMovingBlockRenderType(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/RenderType;",
            at = @At("HEAD"),
            cancellable = true)
    private static void pcccls$forceSolidMovingRenderType(
            BlockState state,
            CallbackInfoReturnable<RenderType> cir) {
        if (CompatRuntimeState.shouldFixLevititeShaderFlicker() && isAeronauticsLevitite(state)) {
            cir.setReturnValue(RenderType.SOLID);
        }
    }

    private static boolean isAeronauticsLevitite(BlockState state) {
        Block block = state.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null || !id.getPath().contains("levitite")) {
            return false;
        }

        String namespace = id.getNamespace();
        return "aeronautics".equals(namespace) || "aeronautics_dyeable_components".equals(namespace);
    }
}
