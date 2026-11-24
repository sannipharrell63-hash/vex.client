package com.vexclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class VexClient implements ClientModInitializer {
    private static boolean rightShiftWasDown;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long window = client.getWindow().getWindow();
            boolean focused = GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
            boolean rightShiftDown = focused && GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

            // Edge-trigger the key so holding Right Shift opens the menu only once.
            if (rightShiftDown && !rightShiftWasDown) {
                if (client.screen instanceof VexMenuScreen) {
                    client.setScreen(null);
                } else if (client.screen == null) {
                    client.setScreen(new VexMenuScreen());
                }
            }
            rightShiftWasDown = rightShiftDown;
        });
    }
}
