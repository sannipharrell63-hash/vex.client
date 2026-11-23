package com.vexclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class VexClient implements ClientModInitializer {
    private static final KeyMapping.Category VEX_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("vexclient", "main")
    );
    private static KeyMapping openMenu;

    @Override
    public void onInitializeClient() {
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.vexclient.open",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                VEX_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.consumeClick()) {
                Minecraft.getInstance().setScreen(new VexMenuScreen());
            }
        });
    }
}
