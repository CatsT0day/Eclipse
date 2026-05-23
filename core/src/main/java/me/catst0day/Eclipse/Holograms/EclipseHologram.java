package me.catst0day.Eclipse.Holograms;

import me.catst0day.Eclipse.Holograms.Processor.EclipseHoloProcessor;
import me.catst0day.Eclipse.Holograms.Processor.EclipseHoloProcessorDisplay;
import me.catst0day.Eclipse.Holograms.Processor.EclipseHoloProcessorArmorStand;
import me.catst0day.Eclipse.Holograms.Settings.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class EclipseHologram {
    private final String name;
    private final UUID uuid;
    private Location location;
    private List<String> lines;
    private boolean clickable;
    private String clickCommand;
    private double clickCost;
    private boolean showParticles;
    private boolean enabled;
    private String permission;
    

    private final EclipseHoloSettings settings;
    private final EclipseHoloTextSettings textSettings;
    private final EclipseHoloIconSettings iconSettings;
    private final EclipseHoloAnimSettings animationSettings;
    private final EclipseHoloBoardSettings boardSettings;
    private final EclipseHoloPageSettings pageSettings;
    private EclipseHoloProcessor processor;
    private final Map<UUID, Integer> playerPages;
    
    public enum FollowType {
        FIXED, VERTICAL, HORIZONTAL, CENTER
    }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT
    }
    
    public EclipseHologram(String name, Location location, List<String> lines) {
        this.name = sanitizeName(name);
        this.uuid = UUID.randomUUID();
        this.location = location.clone();
        this.lines = new ArrayList<>(lines);
        this.clickable = false;
        this.clickCommand = "";
        this.clickCost = 0.0;
        this.showParticles = true;
        this.enabled = true;
        this.permission = null;
        this.settings = new EclipseHoloSettings();
        this.textSettings = new EclipseHoloTextSettings();
        this.iconSettings = new EclipseHoloIconSettings();
        this.animationSettings = new EclipseHoloAnimSettings();
        this.boardSettings = new EclipseHoloBoardSettings();
        this.pageSettings = new EclipseHoloPageSettings();
        initializeProcessor();
        this.playerPages = new HashMap<>();
    }
    
    private void initializeProcessor() {
        boolean useTextDisplay = isTextDisplayAvailable();
        if (useTextDisplay) {
            this.processor = new EclipseHoloProcessorDisplay(this);
        } else {
            this.processor = new EclipseHoloProcessorArmorStand(this);
        }
    }
    
    private boolean isTextDisplayAvailable() {
        try {
            String version = org.bukkit.Bukkit.getBukkitVersion();
            String[] parts = version.split("-");
            String mainVersion = parts[0];
            String[] versionNumbers = mainVersion.split("\\.");
            
            if (versionNumbers.length >= 2) {
                int major = Integer.parseInt(versionNumbers[0]);
                int minor = Integer.parseInt(versionNumbers[1]);
                
                if (major > 1) return true;
                if (major == 1 && minor >= 19) return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    
    private String sanitizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "hologram_" + UUID.randomUUID().toString().substring(0, 8);
        }
        return name.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
    
    public String getName() {
        return name;
    }
    
    public UUID getUniqueId() {
        return uuid;
    }
    
    public Location getLocation() {
        return location.clone();
    }
    
    public void setLocation(Location location) {
        this.location = location.clone();
    }
    
    public List<String> getLines() {
        return new ArrayList<>(lines);
    }
    
    public List<String> getLinesForPage(int page) {
        List<String> allLines = new ArrayList<>();
        List<List<String>> pages = parsePages();
        
        if (page < 0 || page >= pages.size()) {
            return allLines;
        }
        
        return new ArrayList<>(pages.get(page));
    }
    
    public int getPageCount() {
        return parsePages().size();
    }
    
    private List<List<String>> parsePages() {
        List<List<String>> pages = new ArrayList<>();
        List<String> currentPage = new ArrayList<>();
        
        for (String line : lines) {
            if (line.trim().equalsIgnoreCase("!nextpage!")) {
                if (!currentPage.isEmpty()) {
                    pages.add(new ArrayList<>(currentPage));
                    currentPage.clear();
                }
            } else {
                currentPage.add(line);
            }
        }
        
        if (!currentPage.isEmpty()) {
            pages.add(currentPage);
        }
        
        if (pages.isEmpty()) {
            pages.add(new ArrayList<>());
        }
        
        return pages;
    }
    
    public void setLines(List<String> lines) {
        this.lines = new ArrayList<>(lines);
    }
    
    public void addLine(String line) {
        this.lines.add(sanitizeLine(line));
    }
    
    public void removeLine(int index) {
        if (index >= 0 && index < lines.size()) {
            lines.remove(index);
        }
    }
    
    public void setLine(int index, String line) {
        if (index >= 0 && index < lines.size()) {
            lines.set(index, sanitizeLine(line));
        }
    }
    
    private String sanitizeLine(String line) {
        if (line == null) return "";
        return line.replace("\n", " ").replace("\r", "");
    }
    
    public int getViewDistance() {
        return settings.getVisibilityRange();
    }
    
    public void setViewDistance(int viewDistance) {
        settings.setVisibilityRange(Math.max(1, Math.min(128, viewDistance)));
    }
    
    public boolean isAlwaysVisible() {
        return !settings.isRequiresPermission();
    }
    
    public void setAlwaysVisible(boolean alwaysVisible) {
        settings.setRequiresPermission(!alwaysVisible);
    }
    
    public int getUpdateInterval() {
        return settings.getUpdateIntervalTicks();
    }
    
    public void setUpdateInterval(int updateInterval) {
        settings.setUpdateIntervalTicks(Math.max(1, updateInterval));
    }
    
    public boolean isClickable() {
        return clickable;
    }
    
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }
    
    public String getClickCommand() {
        return clickCommand;
    }
    
    public void setClickCommand(String clickCommand) {
        this.clickCommand = sanitizeCommand(clickCommand);
    }
    
    public double getClickCost() {
        return clickCost;
    }
    
    public void setClickCost(double clickCost) {
        this.clickCost = Math.max(0, clickCost);
    }
    
    private String sanitizeCommand(String command) {
        if (command == null) return "";
        return command.trim();
    }
    
    public boolean isShowParticles() {
        return showParticles;
    }
    
    public void setShowParticles(boolean showParticles) {
        this.showParticles = showParticles;
    }
    
    public int getFadeInTicks() {
        return animationSettings.getFadeInTicks();
    }
    
    public void setFadeInTicks(int fadeInTicks) {
        animationSettings.setFadeInTicks(Math.max(0, fadeInTicks));
        animationSettings.setFadeInEnabled(fadeInTicks > 0);
    }
    
    public int getFadeOutTicks() {
        return animationSettings.getFadeOutTicks();
    }
    
    public void setFadeOutTicks(int fadeOutTicks) {
        animationSettings.setFadeOutTicks(Math.max(0, fadeOutTicks));
        animationSettings.setFadeOutEnabled(fadeOutTicks > 0);
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public void setPermission(String permission) {
        this.permission = permission != null && !permission.trim().isEmpty() ? permission.trim() : null;
        settings.setRequiresPermission(this.permission != null);
    }
    
    public int getUpdateRange() {
        return settings.getUpdateRange();
    }
    
    public void setUpdateRange(int updateRange) {
        settings.setUpdateRange(Math.max(1, Math.min(128, updateRange)));
    }
    
    public boolean isLineOfSight() {
        return settings.isCheckLineOfSight();
    }
    
    public void setLineOfSight(boolean lineOfSight) {
        settings.setCheckLineOfSight(lineOfSight);
    }
    
    public FollowType getFollowType() {
        Billboard billboard = settings.getBillboard();
        return switch (billboard) {
            case FIXED -> FollowType.FIXED;
            case VERTICAL -> FollowType.VERTICAL;
            case HORIZONTAL -> FollowType.HORIZONTAL;
            case CENTER -> FollowType.CENTER;
            default -> FollowType.FIXED;
        };
    }
    
    public void setFollowType(FollowType followType) {
        switch (followType) {
            case FIXED: settings.setBillboard(Billboard.FIXED); break;
            case VERTICAL: settings.setBillboard(Billboard.VERTICAL); break;
            case HORIZONTAL: settings.setBillboard(Billboard.HORIZONTAL); break;
            case CENTER: settings.setBillboard(Billboard.CENTER); break;
        }
    }
    
    public boolean isDoubleSided() {
        return textSettings.isDoubleSided();
    }
    
    public void setDoubleSided(boolean doubleSided) {
        textSettings.setDoubleSided(doubleSided);
    }
    
    public TextAlignment getTextAlignment() {
        return TextAlignment.valueOf(textSettings.getTextAlignment().name());
    }
    
    public void setTextAlignment(TextAlignment textAlignment) {
        textSettings.setTextAlignment(me.catst0day.Eclipse.Holograms.Settings.TextAlignment.valueOf(textAlignment.name()));
    }
    
    public boolean isTextShadow() {
        return textSettings.isShadowed();
    }
    
    public void setTextShadow(boolean textShadow) {
        textSettings.setShadowed(textShadow);
    }
    
    public int getTextAlpha() {
        return textSettings.getTextAlpha();
    }
    
    public void setTextAlpha(int textAlpha) {
        textSettings.setTextAlpha(Math.max(0, Math.min(255, textAlpha)));
    }
    
    public int getTextWidth() {
        return textSettings.getLineWidth();
    }
    
    public void setTextWidth(int textWidth) {
        textSettings.setLineWidth(Math.max(1, textWidth));
    }
    
    public int getTextFillerWidth() {
        return textSettings.getFillerAmount();
    }
    
    public void setTextFillerWidth(int textFillerWidth) {
        textSettings.setFillerAmount(Math.max(0, textFillerWidth));
    }
    
    public boolean isTextSeeThrough() {
        return textSettings.isSeeThrough();
    }
    
    public void setTextSeeThrough(boolean textSeeThrough) {
        textSettings.setSeeThrough(textSeeThrough);
    }
    
    public int getLightLevel() {
        return settings.getBlockLevel();
    }
    
    public void setLightLevel(int lightLevel) {
        settings.setBlockLevel(Math.max(-1, Math.min(15, lightLevel)));
    }
    
    public String getBackgroundColor() {
        return textSettings.getBackgroundColor();
    }
    
    public void setBackgroundColor(String backgroundColor) {
        if (isValidHexColor(backgroundColor)) {
            textSettings.setBackgroundColor(backgroundColor);
        }
    }
    
    private boolean isValidHexColor(String color) {
        if (color == null) return false;
        Pattern pattern = Pattern.compile("^#[0-9A-Fa-f]{6}$");
        return pattern.matcher(color).matches();
    }
    
    public int getBackgroundAlpha() {
        return textSettings.getBackgroundAlpha();
    }
    
    public void setBackgroundAlpha(int backgroundAlpha) {
        textSettings.setBackgroundAlpha(Math.max(0, Math.min(255, backgroundAlpha)));
    }
    
    public double getScale() {
        return settings.getScale().getX();
    }
    
    public void setScale(double scale) {
        double clampedScale = Math.max(0.1, Math.min(5.0, scale));
        settings.setScale(clampedScale, clampedScale, clampedScale);
    }
    
    public double getYawOffset() {
        return settings.getYaw();
    }
    
    public void setYawOffset(double yawOffset) {
        settings.setYaw(yawOffset);
    }
    
    public double getPitchOffset() {
        return settings.getPitch();
    }
    
    public void setPitchOffset(double pitchOffset) {
        settings.setPitch(pitchOffset);
    }
    
    public boolean isBoardEnabled() {
        return boardSettings.isEnabled();
    }
    
    public void setBoardEnabled(boolean boardEnabled) {
        boardSettings.setEnabled(boardEnabled);
    }
    
    public Material getBoardMaterial() {
        return boardSettings.getMaterial();
    }
    
    public void setBoardMaterial(Material boardMaterial) {
        if (boardMaterial != null && boardMaterial.isBlock()) {
            boardSettings.setMaterial(boardMaterial);
        }
    }
    
    public double getBoardScale() {
        return boardSettings.getScale().getX();
    }
    
    public void setBoardScale(double boardScale) {
        boardSettings.setScale(new Vector(boardScale, boardScale, boardScale));
    }
    
    public double getBoardYawOffset() {
        return boardSettings.getDirection().getY();
    }
    
    public void setBoardYawOffset(double boardYawOffset) {
        Vector dir = boardSettings.getDirection();
        dir.setY(boardYawOffset);
        boardSettings.setDirection(dir);
    }
    
    public double getBoardPitchOffset() {
        return boardSettings.getDirection().getX();
    }
    
    public void setBoardPitchOffset(double boardPitchOffset) {
        Vector dir = boardSettings.getDirection();
        dir.setX(boardPitchOffset);
        boardSettings.setDirection(dir);
    }
    
    public double getBoardThickness() {
        return boardSettings.getThickness();
    }
    
    public void setBoardThickness(double boardThickness) {
        boardSettings.setThickness(Math.max(0.01, Math.min(2.0, boardThickness)));
    }
    
    public double getIconScale() {
        return iconSettings.getScale().getX();
    }
    
    public void setIconScale(double iconScale) {
        iconSettings.setScale(iconScale);
    }
    
    public double getIconYawOffset() {
        return iconSettings.getYaw();
    }
    
    public void setIconYawOffset(double iconYawOffset) {
        iconSettings.setYaw((int) iconYawOffset);
    }
    
    public double getIconPitchOffset() {
        return iconSettings.getPitch();
    }
    
    public void setIconPitchOffset(double iconPitchOffset) {
        iconSettings.setPitch((int) iconPitchOffset);
    }
    
    public int getPlayerPage(Player player) {
        return playerPages.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void setPlayerPage(Player player, int page) {
        int maxPage = getPageCount() - 1;
        int clampedPage = Math.max(0, Math.min(maxPage, page));
        playerPages.put(player.getUniqueId(), clampedPage);
    }
    
    public void nextPage(Player player) {
        setPlayerPage(player, getPlayerPage(player) + 1);
    }
    
    public void prevPage(Player player) {
        setPlayerPage(player, getPlayerPage(player) - 1);
    }
    
    public void removePlayerPage(Player player) {
        playerPages.remove(player.getUniqueId());
    }
    
    public boolean isVisibleTo(Player player) {
        if (!enabled) return false;
        if (permission != null && !player.hasPermission(permission)) return false;
        if (isAlwaysVisible()) return true;
        if (!player.getWorld().equals(location.getWorld())) return false;
        return player.getLocation().distance(location) <= getViewDistance();
    }
    
    public boolean shouldUpdateFor(Player player) {
        if (!enabled) return false;
        if (!player.getWorld().equals(location.getWorld())) return false;
        return player.getLocation().distance(location) <= getUpdateRange();
    }
    
    public String parseLine(String line, Player player) {
        if (line == null) return "";
        
        String parsed = line;
        me.catst0day.Eclipse.Utils.Util.log("DEBUG parseLine: Original line: '" + parsed + "'");
        parsed = parseCText(parsed, player);
        me.catst0day.Eclipse.Utils.Util.log("DEBUG parseLine: After parseCText: '" + parsed + "'");
        parsed = parseEconomyPlaceholders(parsed, player);
        me.catst0day.Eclipse.Utils.Util.log("DEBUG parseLine: After economy placeholders: '" + parsed + "'");
        parsed = parseIcons(parsed, player);
        me.catst0day.Eclipse.Utils.Util.log("DEBUG parseLine: After parseIcons: '" + parsed + "'");
        if (getTextFillerWidth() > 0 && parsed.contains("%filler%")) {
            parsed = parsed.replace("%filler%", generateFiller(parsed, getTextFillerWidth()));
            me.catst0day.Eclipse.Utils.Util.log("DEBUG parseLine: After filler: '" + parsed + "'");
        }
        
        return parsed;
    }
    
    private String parseCText(String line, Player player) {
        Pattern pattern = Pattern.compile("<T>(.*?)</T>(?:<C>(.*?)</C>)?(?:<H>(.*?)</H>)?");
        Matcher matcher = pattern.matcher(line);
        
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String text = matcher.group(1) != null ? matcher.group(1) : "";
            String command = matcher.group(2) != null ? matcher.group(2) : "";
            String hover = matcher.group(3) != null ? matcher.group(3) : "";

            matcher.appendReplacement(result, text);
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    private String parseIcons(String line, Player player) {
        return line.replaceAll("(ICON|SICON):[^\\s]+", "[ICON]");
    }
    
    private String parseEconomyPlaceholders(String line, Player player) {
        if (player == null) return line;
        
        me.catst0day.Eclipse.Eclipse plugin = me.catst0day.Eclipse.Eclipse.getI();
        if (plugin == null || plugin.getEconomyManager() == null) return line;
        
        me.catst0day.Eclipse.Managers.EclipseEconomyManager economy = plugin.getEconomyManager();
        java.util.UUID playerUuid = player.getUniqueId();
        
        String parsed = line;
        parsed = parsed.replace("{balance}", economy.formatAmount(economy.getBalance(playerUuid)));
        parsed = parsed.replace("{currency}", economy.getSettings().getCurrencyName());
        parsed = parsed.replace("{currency_symbol}", economy.getSettings().getCurrencySymbol());
        parsed = parsed.replace("{player}", player.getName());
        parsed = parsed.replace("{online}", String.valueOf(org.bukkit.Bukkit.getOnlinePlayers().size()));
        double totalBalance = 0;
        for (org.bukkit.entity.Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
            totalBalance += economy.getBalance(onlinePlayer.getUniqueId());
        }
        parsed = parsed.replace("{server_balance}", economy.formatAmount(totalBalance));
        
        // Top balance placeholders (top_balance_1, top_balance_2, etc.)
        java.util.Map<java.util.UUID, Double> balances = new java.util.HashMap<>();
        for (org.bukkit.entity.Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
            balances.put(onlinePlayer.getUniqueId(), economy.getBalance(onlinePlayer.getUniqueId()));
        }
        
        // Sort by balance descending
        java.util.List<java.util.Map.Entry<java.util.UUID, Double>> sorted = balances.entrySet().stream()
                .sorted(java.util.Map.Entry.<java.util.UUID, Double>comparingByValue().reversed())
                .toList();
        
        for (int i = 0; i < Math.min(10, sorted.size()); i++) {
            java.util.Map.Entry<java.util.UUID, Double> entry = sorted.get(i);
            java.util.UUID uuid = entry.getKey();
            double balance = entry.getValue();
            org.bukkit.OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(uuid);
            String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";
            
            parsed = parsed.replace("{top_balance_" + (i + 1) + "_name}", playerName);
            parsed = parsed.replace("{top_balance_" + (i + 1) + "}", economy.formatAmount(balance));
        }
        
        return parsed;
    }
    
    private String generateFiller(String line, int targetWidth) {
        int currentWidth = line.replace("%filler%", "").length();
        int spacesNeeded = Math.max(0, targetWidth - currentWidth);
        return " ".repeat(spacesNeeded);
    }
    public EclipseHoloSettings getSettings() {
        return settings;
    }
    
    public EclipseHoloTextSettings getTextSettings() {
        return textSettings;
    }
    
    public EclipseHoloIconSettings getIconSettings() {
        return iconSettings;
    }
    
    public EclipseHoloAnimSettings getAnimationSettings() {
        return animationSettings;
    }
    
    public EclipseHoloBoardSettings getBoardSettings() {
        return boardSettings;
    }
    
    public EclipseHoloPageSettings getPageSettings() {
        return pageSettings;
    }
}
