package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EclipseChatManager {
    private final FileConfiguration chatConfig;
    private final Map<String, List<String>> languageFilters;
    private final Map<UUID, ChatMode> playerChatModes;
    private final Map<UUID, Long> mutedPlayers;
    private final Map<UUID, String> nicknames;
    private final int localChatRadius;
    private final boolean enabled;

    public enum ChatMode {
        GLOBAL,
        LOCAL,
        STAFF
    }

    public EclipseChatManager(Eclipse plugin) {
        this.chatConfig = plugin.getConfig();
        this.languageFilters = new HashMap<>();
        this.playerChatModes = new ConcurrentHashMap<>();
        this.mutedPlayers = new ConcurrentHashMap<>();
        this.nicknames = new ConcurrentHashMap<>();
        this.localChatRadius = chatConfig.getInt("chat.localChatRadius", 100);
        this.enabled = chatConfig.getBoolean("chat.enabled", true);
        
        loadLanguageFilters();
    }

    private void loadLanguageFilters() {
        if (!chatConfig.contains("chat.languageFilters")) {
            return;
        }

        var filtersSection = chatConfig.getConfigurationSection("chat.languageFilters");
        if (filtersSection == null) return;

        for (String lang : filtersSection.getKeys(false)) {
            var langSection = filtersSection.getConfigurationSection(lang);
            if (langSection == null) continue;

            if (!langSection.getBoolean("enabled", true)) continue;

            List<String> blockedWords = langSection.getStringList("blockedWords");
            languageFilters.put(lang.toUpperCase(), blockedWords);
        }
    }

    public String filterMessage(String message, String language) {
        if (!enabled) return message;

        String langKey = language.toUpperCase();
        List<String> blockedWords = languageFilters.get(langKey);
        
        if (blockedWords == null || blockedWords.isEmpty()) {
            return message;
        }

        String filteredMessage = message;
        String replacement = chatConfig.getString("chat.languageFilters." + langKey + ".replacement", "***");
        boolean caseSensitive = chatConfig.getBoolean("chat.languageFilters." + langKey + ".caseSensitive", false);

        for (String word : blockedWords) {
            if (caseSensitive) {
                filteredMessage = filteredMessage.replace(word, replacement);
            } else {
                String lowerMessage = filteredMessage.toLowerCase();
                String lowerWord = word.toLowerCase();
                if (lowerMessage.contains(lowerWord)) {
                    filteredMessage = filteredMessage.replaceAll("(?i)" + java.util.regex.Pattern.quote(word), replacement);
                }
            }
        }

        return filteredMessage;
    }

    public boolean isMessageBlocked(String message, String language) {
        if (!enabled) return false;

        String langKey = language.toUpperCase();
        List<String> blockedWords = languageFilters.get(langKey);
        
        if (blockedWords == null || blockedWords.isEmpty()) {
            return false;
        }

        String lowerMessage = message.toLowerCase();
        for (String word : blockedWords) {
            if (lowerMessage.contains(word.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public void setPlayerChatMode(UUID playerId, ChatMode mode) {
        playerChatModes.put(playerId, mode);
    }

    public ChatMode getPlayerChatMode(UUID playerId) {
        return playerChatModes.getOrDefault(playerId, ChatMode.GLOBAL);
    }

    public void mutePlayer(UUID playerId, long durationSeconds) {
        mutedPlayers.put(playerId, System.currentTimeMillis() + (durationSeconds * 1000));
    }

    public void unmutePlayer(UUID playerId) {
        mutedPlayers.remove(playerId);
    }

    public boolean isPlayerMuted(UUID playerId) {
        Long muteEndTime = mutedPlayers.get(playerId);
        if (muteEndTime == null) return false;
        
        if (System.currentTimeMillis() > muteEndTime) {
            mutedPlayers.remove(playerId);
            return false;
        }
        
        return true;
    }

    public long getRemainingMuteTime(UUID playerId) {
        Long muteEndTime = mutedPlayers.get(playerId);
        if (muteEndTime == null) return 0;
        
        long remaining = muteEndTime - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    public String getChatFormat(ChatMode mode) {
        String path = "chat.chatFormats." + mode.name().toLowerCase();
        return chatConfig.getString(path, "&f%player%&7: &f%message%");
    }

    public int getLocalChatRadius() {
        return localChatRadius;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean shouldAutoMute() {
        return chatConfig.getBoolean("moderation.autoMute", false);
    }

    public int getMuteDuration() {
        return chatConfig.getInt("moderation.muteDuration", 300);
    }

    public boolean shouldWarnPlayer() {
        return chatConfig.getBoolean("moderation.warnPlayer", true);
    }

    public String getWarnMessage() {
        return chatConfig.getString("moderation.warnMessage", "&cВаше сообщение содержит запрещенные слова!");
    }

    public boolean shouldLogBlockedMessages() {
        return chatConfig.getBoolean("moderation.logBlockedMessages", true);
    }

    public void reloadConfig() {
        languageFilters.clear();
        loadLanguageFilters();
    }

    public void removePlayer(UUID playerId) {
        playerChatModes.remove(playerId);
        mutedPlayers.remove(playerId);
        nicknames.remove(playerId);
    }

    public void setNickname(UUID playerId, String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            nicknames.remove(playerId);
        } else {
            nicknames.put(playerId, nickname);
        }
    }

    public String getNickname(UUID playerId) {
        return nicknames.get(playerId);
    }

    public String getDisplayName(UUID playerId, String defaultName) {
        String nickname = nicknames.get(playerId);
        return nickname != null ? nickname : defaultName;
    }

    public boolean hasNickname(UUID playerId) {
        return nicknames.containsKey(playerId);
    }
}
