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
    private static final int PURPLE = 0xFF9D25FF;
    private static final int WHITE = 0xFFF7F2FF;
    private static final int MUTED = 0xFFBBA9CC;
    private static final Map<String, String> VALUES = new LinkedHashMap<>();
    private static final Map<String, List<String>> PAGES = new LinkedHashMap<>();
    private static boolean loaded;
    private String selectedPage = "Combat";

    public VexMenuScreen() {
        super(Component.literal("VEX CLIENT"));
        definePages();
        loadSettings();
    }

    private static void definePages() {
        if (!PAGES.isEmpty()) return;
        PAGES.put("Combat", List.of("Crystal visuals", "Crystal size", "Crystal colour", "Anchor visuals", "Anchor charge style", "Sword style"));
        PAGES.put("Movement", List.of("Pearl trail", "Pearl size", "Pearl colour", "Keep sprint indicator"));
        PAGES.put("Render", List.of("Crystal glow", "Pearl particles", "Armour HUD", "Armour durability display"));
        PAGES.put("Utility", List.of("Golden apple style", "Enchanted apple effect", "XP bottle style", "Potion bottle colour", "Glowstone style"));
        PAGES.put("Misc", List.of("Obsidian highlight", "Obsidian outline colour", "Block highlight", "Show item names"));
        PAGES.put("Configs", List.of("Custom item visuals", "Compact HUD", "Purple interface theme", "Reset settings"));
    }

    private static void loadSettings() {
        if (loaded) return;
        for (List<String> page : PAGES.values()) for (String name : page) VALUES.put(name, defaultValue(name));
        Path file = Minecraft.getInstance().gameDirectory.toPath().resolve("config/vexclient.properties");
        Properties properties = new Properties();
        if (Files.exists(file)) {
            try (InputStream in = Files.newInputStream(file)) {
                properties.load(in);
                for (String name : VALUES.keySet()) VALUES.put(name, properties.getProperty(name, VALUES.get(name)));
            } catch (IOException ignored) { }
        }
        loaded = true;
    }

    private static String defaultValue(String name) {
        if (name.contains("colour") || name.contains("color") || name.contains("outline")) return "Purple";
        if (name.contains("size")) return "100%";
        if (name.contains("charge")) return "Classic";
        if (name.contains("style") || name.contains("effect")) return "Default";
        return "ON";
    }

    private static void saveSettings() {
        Path file = Minecraft.getInstance().gameDirectory.toPath().resolve("config/vexclient.properties");
        Properties properties = new Properties();
        VALUES.forEach(properties::setProperty);
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) { properties.store(out, "VEX CLIENT preferences"); }
        } catch (IOException ignored) { }
    }

    private static String nextValue(String name, String current) {
        if (name.equals("Reset settings")) {
            for (String key : VALUES.keySet()) VALUES.put(key, defaultValue(key));
            saveSettings();
            return "Done";
        }
        if (name.contains("colour") || name.contains("color") || name.contains("outline")) {
            List<String> options = List.of("Purple", "Cyan", "Red", "Blue", "Green", "White");
            return options.get((options.indexOf(current) + 1 + options.size()) % options.size());
        }
        if (name.contains("size")) {
            List<String> options = List.of("75%", "90%", "100%", "110%", "125%", "150%");
            return options.get((options.indexOf(current) + 1 + options.size()) % options.size());
        }
        if (name.contains("style") || name.contains("effect")) {
            List<String> options = List.of("Default", "Clean", "Neon", "Classic");
            return options.get((options.indexOf(current) + 1 + options.size()) % options.size());
        }
        return "ON".equals(current) ? "OFF" : "ON";
    }

    @Override
    protected void init() {
        clearWidgets();
        int panelWidth = Math.min(760, this.width - 24);
        int panelHeight = Math.min(430, this.height - 24);
        int left = (this.width - panelWidth) / 2;
        int top = (this.height - panelHeight) / 2;
        int sidebarWidth = Math.max(112, Math.min(150, panelWidth / 4));
        int contentX = left + sidebarWidth + 22;
        int contentWidth = panelWidth - sidebarWidth - 38;

        int tabY = top + 108;
        for (String page : PAGES.keySet()) {
            final String category = page;
            addRenderableWidget(Button.builder(Component.literal((selectedPage.equals(page) ? "◆  " : "◇  ") + page), button -> {
                selectedPage = category;
                rebuildWidgets();
            }).bounds(left + 12, tabY, sidebarWidth - 22, 28).build());
            tabY += 34;
        }

        int rowY = top + 108;
        for (String option : PAGES.get(selectedPage)) {
            addRenderableWidget(Button.builder(Component.literal(option + "     " + VALUES.get(option)), button -> {
                VALUES.put(option, nextValue(option, VALUES.get(option)));
                saveSettings();
                rebuildWidgets();
            }).bounds(contentX, rowY, contentWidth, 34).build());
            rowY += 42;
        }

        int footerY = top + panelHeight - 48;
        addRenderableWidget(Button.builder(Component.literal("SAVE CONFIG"), button -> saveSettings())
                .bounds(contentX, footerY, contentWidth / 2 - 5, 28).build());
        addRenderableWidget(Button.builder(Component.literal("CLOSE  [RIGHT SHIFT]"), button -> onClose())
                .bounds(contentX + contentWidth / 2 + 5, footerY, contentWidth / 2 - 5, 28).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xD9000000);
        int panelWidth = Math.min(760, width - 24);
        int panelHeight = Math.min(430, height - 24);
        int left = (width - panelWidth) / 2;
        int top = (height - panelHeight) / 2;
        int sidebarWidth = Math.max(112, Math.min(150, panelWidth / 4));

        graphics.fill(left - 2, top - 2, left + panelWidth + 2, top + panelHeight + 2, PURPLE);
        graphics.fill(left, top, left + panelWidth, top + panelHeight, 0xFF0B0812);
        graphics.fill(left, top, left + panelWidth, top + 3, PURPLE);
        graphics.fill(left + 8, top + 8, left + panelWidth - 8, top + 66, 0xFF15101E);
        graphics.fill(left + 8, top + 76, left + sidebarWidth, top + panelHeight - 12, 0xFF15101E);
        graphics.fill(left + sidebarWidth + 8, top + 76, left + panelWidth - 8, top + panelHeight - 12, 0xFF100C17);
        graphics.fill(left + sidebarWidth + 2, top + 76, left + sidebarWidth + 3, top + panelHeight - 12, 0xFF552078);

        graphics.drawString(font, Component.literal("VEX CLIENT"), left + 22, top + 17, PURPLE);
        graphics.drawString(font, Component.literal("CLICK GUI  /  " + selectedPage.toUpperCase()), left + 22, top + 39, WHITE);
        graphics.drawString(font, Component.literal("RIGHT SHIFT  •  TOGGLE MENU"), left + panelWidth - 190, top + 28, MUTED);
        graphics.drawString(font, Component.literal("CATEGORIES"), left + 18, top + 88, MUTED);
        graphics.drawString(font, Component.literal("MODULES & CUSTOMISATION"), left + sidebarWidth + 20, top + 88, MUTED);
        graphics.drawString(font, Component.literal("Click an option to change its value  •  Saved locally"), left + sidebarWidth + 20, top + panelHeight - 15, MUTED);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        saveSettings();
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
