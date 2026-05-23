package com.p1kacat.pcccls.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

import com.p1kacat.pcccls.PccclsConfig;
import com.p1kacat.pcccls.PccclsMod;

import net.neoforged.fml.ModList;

public final class CompatRuntimeState {
    private static final String IRIS_CONFIG_PATH = "config/iris.properties";
    private static final String KEY_ENABLE_SHADERS = "enableShaders";
    private static final String KEY_SHADER_PACK = "shaderPack";

    private static boolean vibrantCloudsShaderActive = false;
    private static boolean simpleCloudsLoaded = false;
    private static boolean oculusForSimpleCloudsLoaded = false;
    private static boolean aeronauticsLoaded = false;
    private static boolean simulatedLoaded = false;

    private static boolean warnedMissingIrisConfig = false;
    private static boolean warnedIrisReadError = false;

    private CompatRuntimeState() {
    }

    static void refresh(Path gameDirectory) {
        refreshLoadedMods();
        refreshShaderState(gameDirectory);
    }

    public static boolean isCompatActive() {
        return PccclsConfig.ENABLE_VIBRANT_CLOUDS_COMPAT.get() && vibrantCloudsShaderActive;
    }

    public static boolean isSimpleCloudsLoaded() {
        return simpleCloudsLoaded;
    }

    public static boolean isOculusForSimpleCloudsLoaded() {
        return oculusForSimpleCloudsLoaded;
    }

    public static boolean shouldFixPhysicsStaffFocus() {
        return isCompatActive()
                && PccclsConfig.FIX_PHYSICS_STAFF_FOCUS.get()
                && simulatedLoaded
                && isSimpleCloudsCompatSatisfied();
    }

    public static boolean shouldFixLevititeShaderFlicker() {
        return isCompatActive()
                && PccclsConfig.FIX_LEVITITE_SHADER_FLICKER.get()
                && aeronauticsLoaded
                && isSimpleCloudsCompatSatisfied();
    }

    private static boolean isSimpleCloudsCompatSatisfied() {
        if (!PccclsConfig.ENABLE_SIMPLE_CLOUDS_COMPAT.get()) {
            return true;
        }
        return simpleCloudsLoaded || oculusForSimpleCloudsLoaded;
    }

    private static void refreshLoadedMods() {
        ModList modList = ModList.get();
        simpleCloudsLoaded = modList.isLoaded("simpleclouds");
        oculusForSimpleCloudsLoaded = modList.isLoaded("oculus_for_simpleclouds");
        aeronauticsLoaded = modList.isLoaded("aeronautics");
        simulatedLoaded = modList.isLoaded("simulated");
    }

    private static void refreshShaderState(Path gameDirectory) {
        Path irisPropertiesPath = gameDirectory.resolve(IRIS_CONFIG_PATH);
        if (!Files.isRegularFile(irisPropertiesPath)) {
            vibrantCloudsShaderActive = false;
            if (!warnedMissingIrisConfig) {
                PccclsMod.LOGGER.debug("[pcccls] Iris config not found at {}", irisPropertiesPath);
                warnedMissingIrisConfig = true;
            }
            return;
        }

        Properties properties = new Properties();
        try (InputStream stream = Files.newInputStream(irisPropertiesPath)) {
            properties.load(stream);
        } catch (IOException e) {
            vibrantCloudsShaderActive = false;
            if (!warnedIrisReadError) {
                PccclsMod.LOGGER.warn("[pcccls] Failed to read {}", irisPropertiesPath, e);
                warnedIrisReadError = true;
            }
            return;
        }

        if (!Boolean.parseBoolean(properties.getProperty(KEY_ENABLE_SHADERS, "false"))) {
            vibrantCloudsShaderActive = false;
            return;
        }

        String shaderPack = properties.getProperty(KEY_SHADER_PACK, "").trim();
        if (shaderPack.isEmpty()) {
            vibrantCloudsShaderActive = false;
            return;
        }

        String expected = PccclsConfig.SHADER_NAME_MATCH.get().trim().toLowerCase(Locale.ROOT);
        if (expected.isEmpty()) {
            vibrantCloudsShaderActive = false;
            return;
        }

        vibrantCloudsShaderActive = shaderPack.toLowerCase(Locale.ROOT).contains(expected);
    }
}
