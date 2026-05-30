package me.catst0day.Eclipse.kt.placeholders

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player




fun String.parsePlaceholders(player: OfflinePlayer?): String {
    return if (player != null) {
        PlaceholderAPI.setPlaceholders(player, this)
    } else {
        PlaceholderAPI.setPlaceholders(null, this)
    }
}


fun String.parsePlaceholdersSafe(player: OfflinePlayer?): String {
    return try {
        parsePlaceholders(player)
    } catch (e: Exception) {
        this
    }
}


fun String.parsePlaceholdersOrDefault(player: OfflinePlayer?, default: String): String {
    val result = parsePlaceholders(player)
    return if (result.isBlank()) default else result
}


fun String.parsePlaceholdersOrNull(player: OfflinePlayer?): String? {
    val result = parsePlaceholders(player)
    return if (result.isBlank()) null else result
}


fun String.containsPlaceholders(): Boolean {
    return PlaceholderAPI.containsPlaceholders(this)
}


fun String.getPlaceholderIdentifiers(): List<String> {
    val pattern = Regex("%([^%]+)%")
    return pattern.findAll(this).map { it.groupValues[1] }.toList()
}


fun Player.parsePlaceholders(text: String): String = text.parsePlaceholders(this)


fun Player.parsePlaceholdersSafe(text: String): String = text.parsePlaceholdersSafe(this)


fun Player.parsePlaceholdersOrDefault(text: String, default: String): String = 
    text.parsePlaceholdersOrDefault(this, default)


fun Player.parsePlaceholdersOrNull(text: String): String? = text.parsePlaceholdersOrNull(this)


fun List<String>.parsePlaceholders(player: OfflinePlayer?): List<String> {
    return map { it.parsePlaceholders(player) }
}


fun List<String>.parsePlaceholdersSafe(player: OfflinePlayer?): List<String> {
    return map { it.parsePlaceholdersSafe(player) }
}


object PlaceholderUtils {
    
    @JvmStatic
    @JvmOverloads
    fun parse(text: String, player: OfflinePlayer? = null): String {
        return text.parsePlaceholders(player)
    }
    
    @JvmStatic
    @JvmOverloads
    fun parseSafe(text: String, player: OfflinePlayer? = null): String {
        return text.parsePlaceholdersSafe(player)
    }
    
    @JvmStatic
    fun parseOrDefault(text: String, player: OfflinePlayer?, default: String): String {
        return text.parsePlaceholdersOrDefault(player, default)
    }
    
    @JvmStatic
    @JvmOverloads
    fun parseOrNull(text: String, player: OfflinePlayer? = null): String? {
        return text.parsePlaceholdersOrNull(player)
    }
    
    @JvmStatic
    fun containsPlaceholders(text: String): Boolean {
        return text.containsPlaceholders()
    }
    
    @JvmStatic
    fun getPlaceholderIdentifiers(text: String): List<String> {
        return text.getPlaceholderIdentifiers()
    }
    
    @JvmStatic
    @JvmOverloads
    fun parseList(texts: List<String>, player: OfflinePlayer? = null): List<String> {
        return texts.parsePlaceholders(player)
    }
    
    @JvmStatic
    @JvmOverloads
    fun parseListSafe(texts: List<String>, player: OfflinePlayer? = null): List<String> {
        return texts.parsePlaceholdersSafe(player)
    }
}
