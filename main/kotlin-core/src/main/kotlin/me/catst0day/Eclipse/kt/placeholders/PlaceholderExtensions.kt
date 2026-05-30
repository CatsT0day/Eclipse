package me.catst0day.Eclipse.kt.placeholders

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

/**
 * Extension functions for PlaceholderAPI with Kotlin-specific features
 */

/**
 * Parse placeholders in a string for a player
 * @return String with all placeholders replaced, or original if PlaceholderAPI is not available
 */
fun String.parsePlaceholders(player: OfflinePlayer?): String {
    return if (player != null) {
        PlaceholderAPI.setPlaceholders(player, this)
    } else {
        PlaceholderAPI.setPlaceholders(null, this)
    }
}

/**
 * Parse placeholders with null safety - returns original string if parsing fails
 */
fun String.parsePlaceholdersSafe(player: OfflinePlayer?): String {
    return try {
        parsePlaceholders(player)
    } catch (e: Exception) {
        this
    }
}

/**
 * Parse placeholders with a default value if the result is empty
 */
fun String.parsePlaceholdersOrDefault(player: OfflinePlayer?, default: String): String {
    val result = parsePlaceholders(player)
    return if (result.isBlank()) default else result
}

/**
 * Parse placeholders and return null if the result is empty
 */
fun String.parsePlaceholdersOrNull(player: OfflinePlayer?): String? {
    val result = parsePlaceholders(player)
    return if (result.isBlank()) null else result
}

/**
 * Check if a string contains any PlaceholderAPI placeholders
 */
fun String.containsPlaceholders(): Boolean {
    return PlaceholderAPI.containsPlaceholders(this)
}

/**
 * Get all placeholder identifiers in a string using regex
 */
fun String.getPlaceholderIdentifiers(): List<String> {
    val pattern = Regex("%([^%]+)%")
    return pattern.findAll(this).map { it.groupValues[1] }.toList()
}

/**
 * Extension function for Player to parse placeholders
 */
fun Player.parsePlaceholders(text: String): String = text.parsePlaceholders(this)

/**
 * Extension function for Player to parse placeholders safely
 */
fun Player.parsePlaceholdersSafe(text: String): String = text.parsePlaceholdersSafe(this)

/**
 * Extension function for Player to parse placeholders with default
 */
fun Player.parsePlaceholdersOrDefault(text: String, default: String): String = 
    text.parsePlaceholdersOrDefault(this, default)

/**
 * Extension function for Player to parse placeholders or return null
 */
fun Player.parsePlaceholdersOrNull(text: String): String? = text.parsePlaceholdersOrNull(this)

/**
 * Parse placeholders in a list of strings
 */
fun List<String>.parsePlaceholders(player: OfflinePlayer?): List<String> {
    return map { it.parsePlaceholders(player) }
}

/**
 * Parse placeholders in a list of strings safely
 */
fun List<String>.parsePlaceholdersSafe(player: OfflinePlayer?): List<String> {
    return map { it.parsePlaceholdersSafe(player) }
}

/**
 * Java-friendly static methods for accessing placeholder utilities
 */
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
