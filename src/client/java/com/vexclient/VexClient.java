package com.vexclient;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public final class VexClient implements ClientModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.vexclient.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KeyMapping.Category.MISC
        ));

        LOGGER.info("[VEX CLIENT] Loaded successfully. Press O to open the menu.");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                if (client.screen instanceof VexMenuScreen) {
                    client.setScreen(null);
                    LOGGER.info("[VEX CLIENT] Menu closed.");
                } else {
                    client.setScreen(new VexMenuScreen());
                    LOGGER.info("[VEX CLIENT] Menu opened.");
                }
            }
        });
    }
}
