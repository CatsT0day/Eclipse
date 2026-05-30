package me.catst0day.Eclipse.kt.placeholders

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin




data class CustomPlaceholder(
    val identifier: String,
    val description: String = "",
    val resolver: (OfflinePlayer?, String) -> String
)


class PlaceholderExpansionBuilder(private val plugin: Plugin) {
    private val placeholders = mutableListOf<CustomPlaceholder>()
    private val placeholderMap = mutableMapOf<String, CustomPlaceholder>()
    private var author: String = plugin.name
    private var version: String = plugin.description.version
    private var identifier: String = plugin.name.lowercase()

    
    fun identifier(id: String) {
        this.identifier = id
    }

    
    fun author(author: String) {
        this.author = author
    }

    
    fun version(version: String) {
        this.version = version
    }

    
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

    
    fun placeholders(block: PlaceholderListBuilder.() -> Unit) {
        PlaceholderListBuilder().apply(block).placeholders.forEach {
            placeholders.add(it)
        }
    }

    
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


fun registerPlaceholders(
    plugin: Plugin,
    block: PlaceholderExpansionBuilder.() -> Unit
): PlaceholderExpansion {
    return PlaceholderExpansionBuilder(plugin).apply(block).buildAndRegister()
}


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


fun interface PlaceholderResolver {
    fun resolve(player: OfflinePlayer?, params: String): String
}

fun interface SimplePlaceholderResolver {
    fun resolve(player: OfflinePlayer?): String
}