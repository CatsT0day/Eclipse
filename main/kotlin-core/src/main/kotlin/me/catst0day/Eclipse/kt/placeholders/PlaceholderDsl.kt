package me.catst0day.Eclipse.kt.placeholders

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin

/**
 * DSL for registering custom placeholders with PlaceholderAPI
 * Uses Kotlin features that Java doesn't have:
 * - Lambda with receiver
 * - Extension functions
 * - Named parameters
 * - Type-safe builders
 */

/**
 * Represents a custom placeholder with its identifier and resolver function
 */
data class CustomPlaceholder(
    val identifier: String,
    val description: String = "",
    val resolver: (OfflinePlayer?, String) -> String
)

/**
 * Builder class for creating placeholder expansions using DSL
 */
class PlaceholderExpansionBuilder(private val plugin: Plugin) {
    private val placeholders = mutableListOf<CustomPlaceholder>()
    private val placeholderMap = mutableMapOf<String, CustomPlaceholder>()
    private var author: String = plugin.name
    private var version: String = plugin.description.version
    private var identifier: String = plugin.name.lowercase()

    /**
     * Set the expansion identifier
     */
    fun identifier(id: String) {
        this.identifier = id
    }

    /**
     * Set the expansion author
     */
    fun author(author: String) {
        this.author = author
    }

    /**
     * Set the expansion version
     */
    fun version(version: String) {
        this.version = version
    }

    /**
     * Add a placeholder using DSL
     */
    fun placeholder(
        name: String,
        description: String = "",
        resolver: (OfflinePlayer?, String) -> String
    ) {
        placeholders.add(CustomPlaceholder(name, description, resolver))
    }

    /**
     * Add a simple placeholder that doesn't use parameters
     */
    fun simplePlaceholder(
        name: String,
        description: String = "",
        resolver: (OfflinePlayer?) -> String
    ) {
        placeholder(name, description) { player, _ -> resolver(player) }
    }

    /**
     * Add multiple placeholders at once
     */
    fun placeholders(block: PlaceholderListBuilder.() -> Unit) {
        PlaceholderListBuilder().apply(block).placeholders.forEach {
            placeholders.add(it)
        }
    }

    /**
     * Internal helper method to isolate resolution logic from the anonymous object's scope.
     * This eliminates compiler ambiguity issues with PlaceholderAPI internals.
     */
    private fun resolvePlaceholder(player: OfflinePlayer?, params: String): String? {
        for (ph in placeholders) {
            if (params.startsWith(ph.identifier, ignoreCase = false)) {
                val paramString = if (params.length > ph.identifier.length) {
                    params.substring(ph.identifier.length + 1)
                } else {
                    ""
                }
                return ph.resolver(player, paramString)
            }
        }

        for (ph in placeholders) {
            if (ph.identifier == params) {
                return ph.resolver(player, "")
            }
        }

        return null
    }

    /**
     * Build and register the expansion
     */
    fun buildAndRegister(): PlaceholderExpansion {
        val expansion = object : PlaceholderExpansion() {
            override fun getIdentifier(): String = this@PlaceholderExpansionBuilder.identifier
            override fun getAuthor(): String = this@PlaceholderExpansionBuilder.author
            override fun getVersion(): String = this@PlaceholderExpansionBuilder.version
            override fun canRegister(): Boolean = true

            override fun onRequest(player: OfflinePlayer?, params: String): String? {
                return this@PlaceholderExpansionBuilder.resolvePlaceholder(player, params)
            }
        }

        expansion.register()
        return expansion
    }
}

/**
 * Builder for adding multiple placeholders
 */
class PlaceholderListBuilder {
    internal val placeholders = mutableListOf<CustomPlaceholder>()

    fun placeholder(
        name: String,
        description: String = "",
        resolver: (OfflinePlayer?, String) -> String
    ) {
        placeholders.add(CustomPlaceholder(name, description, resolver))
    }

    fun simplePlaceholder(
        name: String,
        description: String = "",
        resolver: (OfflinePlayer?) -> String
    ) {
        placeholder(name, description) { player, _ -> resolver(player) }
    }
}

/**
 * Main DSL function to register placeholders
 */
fun registerPlaceholders(
    plugin: Plugin,
    block: PlaceholderExpansionBuilder.() -> Unit
): PlaceholderExpansion {
    return PlaceholderExpansionBuilder(plugin).apply(block).buildAndRegister()
}

/**
 * Java-friendly API for registering placeholders
 */
object PlaceholderRegistrar {

    @JvmStatic
    @JvmOverloads
    fun register(
        plugin: Plugin,
        identifier: String = plugin.name.lowercase(),
        author: String = plugin.name,
        version: String = plugin.description.version,
        placeholders: List<CustomPlaceholder>
    ): PlaceholderExpansion {
        val builder = PlaceholderExpansionBuilder(plugin)
        builder.identifier(identifier)
        builder.author(author)
        builder.version(version)

        placeholders.forEach { placeholder ->
            builder.placeholder(placeholder.identifier, placeholder.description, placeholder.resolver)
        }

        return builder.buildAndRegister()
    }

    @JvmStatic
    fun createPlaceholder(
        identifier: String,
        description: String,
        resolver: PlaceholderResolver
    ): CustomPlaceholder {
        return CustomPlaceholder(identifier, description) { player, params ->
            resolver.resolve(player, params)
        }
    }

    @JvmStatic
    fun createSimplePlaceholder(
        identifier: String,
        description: String,
        resolver: SimplePlaceholderResolver
    ): CustomPlaceholder {
        return CustomPlaceholder(identifier, description) { player, _ ->
            resolver.resolve(player)
        }
    }
}

/**
 * Functional interfaces for Java compatibility
 */
fun interface PlaceholderResolver {
    fun resolve(player: OfflinePlayer?, params: String): String
}

fun interface SimplePlaceholderResolver {
    fun resolve(player: OfflinePlayer?): String
}