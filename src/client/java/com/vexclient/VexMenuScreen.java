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
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class VexMenuScreen extends Screen {
    private static final int PURPLE = 0xFF9B35FF;
    private static final int WHITE = 0xFFF4F0FF;
    private static final int MUTED = 0xFFB9A9C8;
    private static final Map<String, String> SETTINGS = new LinkedHashMap<>();
    private static final Map<String, List<String>> PAGES = new LinkedHashMap<>();
    private static boolean loaded;
    private String selectedPage = "Crystals";

    public VexMenuScreen() {
        super(Component.literal("VEX CLIENT"));
        definePages();
        loadSettings();
    }

    private static void definePages() {
        if (!PAGES.isEmpty()) return;
        PAGES.put("Crystals", List.of(
                "Crystal visuals", "Crystal size", "Crystal colour", "Crystal glow", "Crystal spin"
        ));
        PAGES.put("Anchors", List.of(
                "Anchor visuals", "Anchor colour", "Anchor glow", "Glowstone highlight", "Anchor charge style"
        ));
        PAGES.put("Pearls", List.of(
                "Pearl trail", "Pearl size", "Pearl colour", "Pearl particles"
        ));
        PAGES.put("Consumables", List.of(
                "Golden apple style", "Enchanted apple effect", "XP bottle style", "Potion bottle colour"
        ));
        PAGES.put("Equipment", List.of(
                "Sword style", "Sword size", "Sword colour", "Armour HUD", "Armour durability display"
        ));
        PAGES.put("Blocks", List.of(
                "Obsidian highlight", "Obsidian outline colour", "Glowstone style", "Block highlight"
        ));
        PAGES.put("General", List.of(
                "Custom item visuals", "Show item names", "Compact HUD", "Purple interface theme"
        ));
    }

    private static void loadSettings() {
        if (loaded) return;
        for (List<String> page : PAGES.values()) {
            for (String name : page) {
                SETTINGS.put(name, defaultValue(name));
            }
        }
        Path file = Minecraft.getInstance().gameDirectory.toPath().resolve("config/vexclient.properties");
        Properties properties = new Properties();
        if (Files.exists(file)) {
            try (InputStream in = Files.newInputStream(file)) {
                properties.load(in);
                for (String name : SETTINGS.keySet()) {
                    SETTINGS.put(name, properties.getProperty(name, SETTINGS.get(name)));
                }
            } catch (IOException ignored) { }
        }
        loaded = true;
    }

    private static String defaultValue(String name) {
        if (name.contains("colour") || name.contains("color")) return "Purple";
        if (name.contains("size")) return "100%";
        if (name.contains("charge")) return "Classic";
        if (name.contains("effect") || name.contains("particles") || name.contains("glow")
                || name.contains("trail") || name.contains("HUD") || name.contains("durability")
                || name.contains("highlight") || name.contains("outline") || name.contains("names")
                || name.contains("theme") || name.contains("visuals") || name.contains("style")
                || name.contains("spin") || name.contains("Compact")) return "ON";
        return "Default";
    }

    private static void saveSettings() {
        Path file = Minecraft.getInstance().gameDirectory.toPath().resolve("config/vexclient.properties");
        Properties properties = new Properties();
        SETTINGS.forEach(properties::setProperty);
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) {
                properties.store(out, "VEX CLIENT customisation settings");
            }
        } catch (IOException ignored) { }
    }

    private static String nextValue(String name, String current) {
        if (name.contains("colour") || name.contains("color")) {
            List<String> values = List.of("Purple", "Cyan", "Red", "Blue", "Green", "White");
            return values.get((values.indexOf(current) + 1 + values.size()) % values.size());
        }
        if (name.contains("size")) {
            List<String> values = List.of("75%", "90%", "100%", "110%", "125%", "150%");
            return values.get((values.indexOf(current) + 1 + values.size()) % values.size());
        }
        if (name.contains("charge")) {
            List<String> values = List.of("Classic", "Minimal", "Neon", "Clean");
            return values.get((values.indexOf(current) + 1 + values.size()) % values.size());
        }
        if (name.contains("style") || name.contains("effect")) {
            List<String> values = List.of("Default", "Clean", "Neon", "Classic");
            return values.get((values.indexOf(current) + 1 + values.size()) % values.size());
        }
        if (name.contains("visuals") || name.contains("glow") || name.contains("trail")
                || name.contains("particles") || name.contains("HUD") || name.contains("highlight")
                || name.contains("durability") || name.contains("outline") || name.contains("names")
                || name.contains("theme") || name.contains("spin") || name.contains("Compact")
                || name.contains("Custom")) {
            return "ON".equals(current) ? "OFF" : "ON";
        }
        return "Default".equals(current) ? "ON" : "Default";
    }

    @Override
    protected void init() {
        clearWidgets();
        int panelWidth = Math.min(620, this.width - 24);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(28, (this.height - 390) / 2);
        int tabGap = 4;
        int tabWidth = (panelWidth - 32 - tabGap * (PAGES.size() - 1)) / PAGES.size();
        int tabIndex = 0;
        for (String page : PAGES.keySet()) {
            int x = left + 16 + tabIndex * (tabWidth + tabGap);
            addRenderableWidget(Button.builder(Component.literal(page), button -> {
                selectedPage = page;
                rebuildWidgets();
            }).bounds(x, top + 76, tabWidth, 24).build());
            tabIndex++;
        }

        List<String> options = PAGES.get(selectedPage);
        int rowY = top + 112;
        for (String name : options) {
            String value = SETTINGS.get(name);
            addRenderableWidget(Button.builder(Component.literal(name + "   [" + value + "]"), button -> {
                SETTINGS.put(name, nextValue(name, SETTINGS.get(name)));
                saveSettings();
                rebuildWidgets();
            }).bounds(left + 18, rowY, panelWidth - 36, 32).build());
            rowY += 38;
        }
        addRenderableWidget(Button.builder(Component.literal("SAVE SETTINGS"), button -> saveSettings())
                .bounds(left + 18, top + 340, (panelWidth - 44) / 2, 28).build());
        addRenderableWidget(Button.builder(Component.literal("CLOSE  [RIGHT SHIFT]"), button -> onClose())
                .bounds(left + 26 + (panelWidth - 44) / 2, top + 340, (panelWidth - 44) / 2, 28).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xD9000000);
        int panelWidth = Math.min(620, this.width - 24);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(28, (this.height - 390) / 2);
        graphics.fill(left - 2, top - 2, left + panelWidth + 2, top + 382, PURPLE);
        graphics.fill(left, top, left + panelWidth, top + 378, 0xFF100B18);
        graphics.fill(left, top, left + panelWidth, top + 3, PURPLE);
        graphics.drawCenteredString(this.font, Component.literal("VEX CLIENT"), this.width / 2, top + 14, PURPLE);
        graphics.drawCenteredString(this.font, Component.literal("PVP CUSTOMISATION"), this.width / 2, top + 34, WHITE);
        graphics.drawCenteredString(this.font, Component.literal("Select a category, then click an option to cycle its value"), this.width / 2, top + 57, MUTED);
        graphics.drawCenteredString(this.font, Component.literal("Settings save locally in config/vexclient.properties"), this.width / 2, top + 365, MUTED);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        saveSettings();
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
