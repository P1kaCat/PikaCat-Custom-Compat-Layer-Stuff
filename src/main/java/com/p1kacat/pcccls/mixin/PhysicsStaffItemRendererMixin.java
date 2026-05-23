package com.p1kacat.pcccls.mixin;

import java.lang.reflect.Method;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.p1kacat.pcccls.client.CompatRuntimeState;

import net.minecraft.client.resources.model.BakedModel;

@Mixin(targets = "dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItemRenderer", remap = false)
public abstract class PhysicsStaffItemRendererMixin {
    private static Method renderMethod;
    private static Method renderSolidGlowingMethod;
    private static Method renderGlowingMethod;

    @Redirect(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lcom/simibubi/create/foundation/item/render/CustomRenderedItemModel;Lcom/simibubi/create/foundation/item/render/PartialItemModelRenderer;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/item/render/PartialItemModelRenderer;renderSolidGlowing(Lnet/minecraft/client/resources/model/BakedModel;I)V",
                    ordinal = 1),
            remap = false)
    private void pcccls$renderInnerBeamCompat(
            @Coerce Object renderer,
            BakedModel model,
            int packedLight) {
        invokeRenderer(renderer, "renderSolidGlowing", model, packedLight);
        if (CompatRuntimeState.shouldFixPhysicsStaffFocus()) {
            invokeRenderer(renderer, "render", model, packedLight);
        }
    }

    @Redirect(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lcom/simibubi/create/foundation/item/render/CustomRenderedItemModel;Lcom/simibubi/create/foundation/item/render/PartialItemModelRenderer;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/item/render/PartialItemModelRenderer;renderGlowing(Lnet/minecraft/client/resources/model/BakedModel;I)V",
                    ordinal = 1),
            remap = false)
    private void pcccls$renderOuterBeamCompat(
            @Coerce Object renderer,
            BakedModel model,
            int packedLight) {
        invokeRenderer(renderer, "renderGlowing", model, packedLight);
        if (CompatRuntimeState.shouldFixPhysicsStaffFocus()) {
            invokeRenderer(renderer, "renderSolidGlowing", model, packedLight);
        }
    }

    private static void invokeRenderer(Object renderer, String methodName, BakedModel model, int packedLight) {
        Method method = resolveRendererMethod(renderer, methodName);
        if (method == null) {
            return;
        }

        try {
            method.invoke(renderer, model, packedLight);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static Method resolveRendererMethod(Object renderer, String methodName) {
        try {
            return switch (methodName) {
                case "render" -> {
                    if (renderMethod == null) {
                        renderMethod = renderer.getClass().getMethod("render", BakedModel.class, int.class);
                    }
                    yield renderMethod;
                }
                case "renderSolidGlowing" -> {
                    if (renderSolidGlowingMethod == null) {
                        renderSolidGlowingMethod =
                                renderer.getClass().getMethod("renderSolidGlowing", BakedModel.class, int.class);
                    }
                    yield renderSolidGlowingMethod;
                }
                case "renderGlowing" -> {
                    if (renderGlowingMethod == null) {
                        renderGlowingMethod = renderer.getClass().getMethod("renderGlowing", BakedModel.class, int.class);
                    }
                    yield renderGlowingMethod;
                }
                default -> null;
            };
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}
