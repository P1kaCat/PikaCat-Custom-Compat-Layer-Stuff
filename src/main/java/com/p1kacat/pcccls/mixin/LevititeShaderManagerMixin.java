package com.p1kacat.pcccls.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.p1kacat.pcccls.client.CompatRuntimeState;

@Mixin(targets = "dev.eriksonn.aeronautics.content.blocks.levitite.LevititeShaderManager", remap = false)
public abstract class LevititeShaderManagerMixin {
    @Inject(method = "needsLayers()Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void pcccls$disableGhostLayersWhenNeeded(CallbackInfoReturnable<Boolean> cir) {
        if (CompatRuntimeState.shouldFixLevititeShaderFlicker()) {
            cir.setReturnValue(false);
        }
    }
}
