package com.vexclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public final class VexMenuScreen extends Screen {
    private static final int PURPLE = 0xFF9B35FF;
    private static final int WHITE = 0xFFF4F0FF;
    private static final Map<String, Boolean> MODULES = new LinkedHashMap<>();
    private static boolean loaded;

    public VexMenuScreen() {
        super(Component.literal("VEX CLIENT"));
        loadSettings();
    }

    private static void loadSettings() {
        if (loaded) return;
        MODULES.put("Crystal visuals", true);
        MODULES.put("Respawn anchor visuals", true);
        MODULES.put("Ender pearl trail", false);
        MODULES.put("Golden apple style", true);
        MODULES.put("Sword style", false);
        MODULES.put("Armour HUD", true);
        MODULES.put("Obsidian highlight", false);
        MODULES.put("Glowstone highlight", false);
        MODULES.put("XP bottle style", false);
        Path file = Minecraft.getInstance().gameDirectory.toPath().resolve("config/vexclient.properties");
        Properties properties = new Properties();
        if (Files.exists(file)) {
            try (InputStream in = Files.newInputStream(file)) {
                properties.load(in);
                for (String name : MODULES.keySet()) {
                    MODULES.put(name, Boolean.parseBoolean(properties.getProperty(name, MODULES.get(name).toString())));
                }
            } catch (IOException ignored) { }
        }
        loaded = true;
    }

    private static void saveSettings() {
        Path file = Minecraft.getInstance().gameDirectory.toPath().resolve("config/vexclient.properties");
        Properties properties = new Properties();
        MODULES.forEach((name, enabled) -> properties.setProperty(name, enabled.toString()));
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) {
                properties.store(out, "VEX CLIENT preferences");
            }
        } catch (IOException ignored) { }
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(520, this.width - 32);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(48, (this.height - 330) / 2);
        int buttonWidth = (panelWidth - 48) / 2;
        int index = 0;
        for (String name : MODULES.keySet()) {
            final String module = name;
            int column = index % 2;
            int row = index / 2;
            int x = left + 16 + column * (buttonWidth + 16);
            int y = top + 76 + row * 34;
            addRenderableWidget(Button.builder(toggleLabel(module), button -> {
                MODULES.put(module, !MODULES.get(module));
                saveSettings();
                rebuildWidgets();
            }).bounds(x, y, buttonWidth, 28).build());
            index++;
        }
        addRenderableWidget(Button.builder(Component.literal("CLOSE MENU"), button -> onClose())
                .bounds(left + 16, top + 76 + 5 * 34, panelWidth - 32, 30).build());
    }

    private static Component toggleLabel(String module) {
        return Component.literal((MODULES.get(module) ? "[ON]  " : "[OFF]  ") + module);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xC9000000);
        int panelWidth = Math.min(520, this.width - 32);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(48, (this.height - 330) / 2);
        graphics.fill(left - 2, top - 2, left + panelWidth + 2, top + 314, PURPLE);
        graphics.fill(left, top, left + panelWidth, top + 310, 0xFF100B18);
        graphics.fill(left, top, left + panelWidth, top + 3, PURPLE);
        graphics.drawCenteredString(this.font, Component.literal("VEX CLIENT"), this.width / 2, top + 16, PURPLE);
        graphics.drawCenteredString(this.font, Component.literal("PVP CUSTOMISATION MENU"), this.width / 2, top + 38, WHITE);
        graphics.drawCenteredString(this.font, Component.literal("Visual preference switches — settings save locally"), this.width / 2, top + 58, 0xFFB9A9C8);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
