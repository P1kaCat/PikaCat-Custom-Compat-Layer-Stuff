package com.p1kacat.pcccls.client;

import com.p1kacat.pcccls.PccclsConfig;
import com.p1kacat.pcccls.PccclsMod;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = PccclsMod.MODID, dist = Dist.CLIENT)
public final class PccclsModClient {
    private int tickAccumulator = 0;
    private boolean runtimeStateInitialized = false;
    private boolean announcedReady = false;

    public PccclsModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }

        tickAccumulator++;
        int interval = Math.max(20, PccclsConfig.CHECK_INTERVAL_TICKS.get());
        if (!runtimeStateInitialized || tickAccumulator >= interval) {
            tickAccumulator = 0;
            runtimeStateInitialized = true;
            CompatRuntimeState.refresh(minecraft.gameDirectory.toPath());
        }

        if (!announcedReady && CompatRuntimeState.isCompatActive()) {
            PccclsMod.LOGGER.info(
                    "[pcccls] Runtime shader compat active (simpleclouds={}, oculus_for_simpleclouds={}).",
                    CompatRuntimeState.isSimpleCloudsLoaded(),
                    CompatRuntimeState.isOculusForSimpleCloudsLoaded());
            announcedReady = true;
        }
    }
}
