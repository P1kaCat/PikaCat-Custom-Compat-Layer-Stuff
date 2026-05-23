package com.p1kacat.pcccls;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(PccclsMod.MODID)
public final class PccclsMod {
    public static final String MODID = "pcccls";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PccclsMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, PccclsConfig.SPEC);
    }
}
