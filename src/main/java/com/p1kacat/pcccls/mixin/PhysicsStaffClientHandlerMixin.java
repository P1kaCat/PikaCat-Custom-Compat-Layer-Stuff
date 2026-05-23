package com.p1kacat.pcccls.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.p1kacat.pcccls.client.CompatRuntimeState;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

@Mixin(targets = "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffClientHandler", remap = false)
public abstract class PhysicsStaffClientHandlerMixin {
    @Inject(
            method = "getStaffFocusPos(Lnet/minecraft/world/entity/player/Player;ZF)Lnet/minecraft/world/phys/Vec3;",
            at = @At("RETURN"),
            cancellable = true,
            remap = false)
    private static void pcccls$fixFocusPos(
            Player player,
            boolean rightHand,
            float partialTicks,
            CallbackInfoReturnable<Vec3> cir) {
        if (!CompatRuntimeState.shouldFixPhysicsStaffFocus()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.gameRenderer == null) {
            return;
        }

        Camera camera = minecraft.gameRenderer.getMainCamera();
        if (camera == null || !player.isLocalPlayer() || camera.isDetached()) {
            return;
        }

        Vector3f look = camera.getLookVector();
        Vector3f left = camera.getLeftVector();
        Vector3f up = camera.getUpVector();

        // Use a fully stable camera-space anchor under shader compat so angle-dependent
        // projection drift cannot reintroduce beam offsets.
        double sideOffset = rightHand ? -0.325D : 0.325D;
        Vec3 stableAnchor = camera.getPosition().add(
                look.x() * 0.95D + left.x() * sideOffset + up.x() * -0.12D,
                look.y() * 0.95D + left.y() * sideOffset + up.y() * -0.12D,
                look.z() * 0.95D + left.z() * sideOffset + up.z() * -0.12D);
        cir.setReturnValue(stableAnchor);
    }
}
