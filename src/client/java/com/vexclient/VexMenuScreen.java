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
    private static final int PURPLE = 0xFF9B28FF;
    private static final int WHITE = 0xFFF8F2FF;
    private static final int MUTED = 0xFFB9A9C9;
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
        PAGES.put("Combat", List.of("Crystal type", "Crystal size", "Crystal colour", "Anchor type", "Anchor colour", "Anchor size", "Pearl type", "Pearl colour", "Pearl size", "Golden apple type", "Apple colour", "Apple size"));
        PAGES.put("Movement", List.of("Movement effects", "Pearl trail", "Pearl trail colour", "Sprint indicator"));
        PAGES.put("Render", List.of("Crystal glow", "Pearl particles", "Armour HUD", "Armour durability", "Enchantment glint"));
        PAGES.put("Utility", List.of("Sword style", "Obsidian highlight", "Glowstone style", "XP bottle style", "Potion bottle style"));
        PAGES.put("Misc", List.of("Armour colour", "Armour glow", "Block highlight", "Show item names"));
        PAGES.put("Configs", List.of("Save config", "Reset settings"));
        PAGES.put("General", List.of("GUI scale", "Menu key", "Interface effects", "Purple theme"));
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
        if (name.contains("colour") || name.contains("color")) return "Purple";
        if (name.contains("size")) return "100%";
        if (name.equals("GUI scale")) return "Normal";
        if (name.equals("Menu key")) return "O";
        if (name.contains("type") || name.contains("style")) return "Default";
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
        if (name.equals("Save config")) {
            saveSettings();
            return "Saved";
        }
        if (name.equals("Menu key")) return "O";
        if (name.equals("GUI scale")) {
            List<String> options = List.of("Small", "Normal", "Large");
            return options.get((options.indexOf(current) + 1 + options.size()) % options.size());
        }
        if (name.contains("colour") || name.contains("color")) {
            List<String> options = List.of("Purple", "Cyan", "Red", "Blue", "Green", "White");
            return options.get((options.indexOf(current) + 1 + options.size()) % options.size());
        }
        if (name.contains("size")) {
            List<String> options = List.of("75%", "90%", "100%", "110%", "125%", "150%");
            return options.get((options.indexOf(current) + 1 + options.size()) % options.size());
        }
        if (name.contains("type") || name.contains("style")) {
            List<String> options = List.of("Default", "Clean", "Neon", "Classic");
            return options.get((options.indexOf(current) + 1 + options.size()) % options.size());
        }
        return "ON".equals(current) ? "OFF" : "ON";
    }

    @Override
    protected void init() {
        clearWidgets();
        int panelWidth = Math.min(900, this.width - 24);
        int panelHeight = Math.min(500, this.height - 24);
        int left = (this.width - panelWidth) / 2;
        int top = (this.height - panelHeight) / 2;
        int sidebarWidth = Math.max(130, Math.min(170, panelWidth / 4));
        int contentX = left + sidebarWidth + 24;
        int contentWidth = panelWidth - sidebarWidth - 42;
        int tabY = top + 104;

        for (String page : PAGES.keySet()) {
            final String category = page;
            addRenderableWidget(Button.builder(Component.literal((selectedPage.equals(page) ? "◆  " : "◇  ") + page), button -> {
                selectedPage = category;
                rebuildWidgets();
            }).bounds(left + 14, tabY, sidebarWidth - 26, 29).build());
            tabY += 35;
        }

        List<String> options = PAGES.get(selectedPage);
        int columns = contentWidth > 510 ? 2 : 1;
        int gap = 12;
        int cardWidth = (contentWidth - gap * (columns - 1)) / columns;
        int cardTop = top + 112;
        int cardHeight = Math.max(74, Math.min(112, (panelHeight - 174) / 3));
        int innerRows = Math.max(1, (options.size() + columns - 1) / columns);

        for (int i = 0; i < options.size(); i++) {
            String option = options.get(i);
            int col = i / innerRows;
            int row = i % innerRows;
            int x = contentX + col * (cardWidth + gap);
            int y = cardTop + row * (cardHeight + 10);
            int buttonY = y + 38;
            addRenderableWidget(Button.builder(Component.literal(option + "     ‹ " + VALUES.get(option) + " ›"), button -> {
                VALUES.put(option, nextValue(option, VALUES.get(option)));
                saveSettings();
                rebuildWidgets();
            }).bounds(x + 10, buttonY, cardWidth - 20, 30).build());
        }

        int footerY = top + panelHeight - 42;
        addRenderableWidget(Button.builder(Component.literal("SAVE CONFIG"), button -> saveSettings())
                .bounds(contentX, footerY, contentWidth / 2 - 6, 28).build());
        addRenderableWidget(Button.builder(Component.literal("CLOSE  [O]"), button -> onClose())
                .bounds(contentX + contentWidth / 2 + 6, footerY, contentWidth / 2 - 6, 28).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xD9000000);
        int panelWidth = Math.min(900, width - 24);
        int panelHeight = Math.min(500, height - 24);
        int left = (width - panelWidth) / 2;
        int top = (height - panelHeight) / 2;
        int sidebarWidth = Math.max(130, Math.min(170, panelWidth / 4));
        int contentX = left + sidebarWidth + 24;
        int contentWidth = panelWidth - sidebarWidth - 42;

        graphics.fill(left - 2, top - 2, left + panelWidth + 2, top + panelHeight + 2, PURPLE);
        graphics.fill(left, top, left + panelWidth, top + panelHeight, 0xFF0B0812);
        graphics.fill(left, top, left + panelWidth, top + 4, PURPLE);
        graphics.fill(left + 8, top + 8, left + panelWidth - 8, top + 66, 0xFF15101F);
        graphics.fill(left + 8, top + 76, left + sidebarWidth, top + panelHeight - 10, 0xFF15101F);
        graphics.fill(left + sidebarWidth + 8, top + 76, left + panelWidth - 8, top + panelHeight - 10, 0xFF100C18);
        graphics.fill(left + sidebarWidth + 2, top + 76, left + sidebarWidth + 3, top + panelHeight - 10, 0xFF552078);

        graphics.drawString(font, Component.literal("♛  VEX"), left + 22, top + 17, PURPLE);
        graphics.drawString(font, Component.literal("CLIENT  •  " + selectedPage.toUpperCase()), left + 22, top + 39, WHITE);
        graphics.drawString(font, Component.literal("PRESS O TO TOGGLE MENU"), left + panelWidth - 190, top + 28, MUTED);
        graphics.drawString(font, Component.literal("CATEGORIES"), left + 18, top + 88, MUTED);
        graphics.drawString(font, Component.literal(selectedPage.toUpperCase() + "  /  CUSTOMISATION"), left + sidebarWidth + 20, top + 88, WHITE);

        List<String> options = PAGES.get(selectedPage);
        int columns = contentWidth > 510 ? 2 : 1;
        int gap = 12;
        int cardWidth = (contentWidth - gap * (columns - 1)) / columns;
        int cardTop = top + 112;
        int cardHeight = Math.max(74, Math.min(112, (panelHeight - 174) / 3));
        int rows = Math.max(1, (options.size() + columns - 1) / columns);
        for (int i = 0; i < options.size(); i++) {
            int col = i / rows;
            int row = i % rows;
            int x = contentX + col * (cardWidth + gap);
            int y = cardTop + row * (cardHeight + 10);
            graphics.fill(x, y, x + cardWidth, y + cardHeight, 0xFF1A1425);
            graphics.fill(x, y, x + cardWidth, y + 2, 0xFF5B2788);
            graphics.drawString(font, Component.literal("✦  " + options.get(i)), x + 12, y + 13, WHITE);
        }
        graphics.drawString(font, Component.literal("Settings save locally  •  VEX CLIENT v0.1.0"), contentX, top + panelHeight - 15, MUTED);
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
