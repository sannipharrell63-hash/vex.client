package com.vexclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

public final class VexClient implements ClientModInitializer {
    private static boolean oWasDown;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long window = client.getWindow().getHandle();
            boolean focused = GLFW.glfwGetWindowAttrib(window, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
            boolean oDown = focused && GLFW.glfwGetKey(window, GLFW.GLFW_KEY_O) == GLFW.GLFW_PRESS;

            if (oDown && !oWasDown) {
                if (client.screen instanceof VexMenuScreen) {
                    client.setScreen(null);
                } else if (client.screen == null) {
                    client.setScreen(new VexMenuScreen());
                }
            }
            oWasDown = oDown;
        });
    }
}
