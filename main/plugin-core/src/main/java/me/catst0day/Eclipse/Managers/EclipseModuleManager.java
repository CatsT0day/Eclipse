package me.catst0day.Eclipse.Managers;

import me.catst0day.Eclipse.Eclipse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static me.catst0day.Eclipse.Utils.Util.log;

public class EclipseModuleManager {
    private final Eclipse plugin;
    private final File moduleFile;
    private FileConfiguration moduleConfig;
    private final Map<String, Boolean> modules;
    private boolean systemEnabled;

    public EclipseModuleManager(Eclipse plugin) {
        this.plugin = plugin;
        this.moduleFile = new File(plugin.getDataFolder(), "modules.yml");
        this.modules = new HashMap<>();
        this.systemEnabled = true;
        
        loadConfig();
        loadModules();
    }

    private void loadConfig() {
        if (!moduleFile.exists()) {
            plugin.saveResource("modules.yml", false);
        }
        moduleConfig = YamlConfiguration.loadConfiguration(moduleFile);
        systemEnabled = moduleConfig.getBoolean("enabled", true);
    }

    private void loadModules() {
        modules.clear();
        
        if (!moduleConfig.contains("modules")) {
            return;
        }

        var modulesSection = moduleConfig.getConfigurationSection("modules");
        if (modulesSection == null) return;

        for (String moduleName : modulesSection.getKeys(false)) {
            boolean enabled = modulesSection.getBoolean(moduleName, true);
            modules.put(moduleName.toLowerCase(), enabled);
        }
    }

    public void saveConfig() {
        try {
            moduleConfig.set("enabled", systemEnabled);
            
            var modulesSection = moduleConfig.getConfigurationSection("modules");
            if (modulesSection == null) {
                moduleConfig.createSection("modules");
                modulesSection = moduleConfig.getConfigurationSection("modules");
            }

            for (Map.Entry<String, Boolean> entry : modules.entrySet()) {
                modulesSection.set(entry.getKey(), entry.getValue());
            }

            moduleConfig.save(moduleFile);
        } catch (IOException e) {
            log("Failed to save module.yml: " + e.getMessage());
        }
    }

    public void reloadConfig() {
        loadConfig();
        loadModules();
    }

    public boolean isSystemEnabled() {
        return systemEnabled;
    }

    public void setSystemEnabled(boolean enabled) {
        this.systemEnabled = enabled;
    }

    public boolean isModuleEnabled(String moduleName) {
        if (!systemEnabled) return false;
        return !modules.getOrDefault(moduleName.toLowerCase(), false);
    }

    public void setModuleEnabled(String moduleName, boolean enabled) {
        modules.put(moduleName.toLowerCase(), enabled);
    }

    public Map<String, Boolean> getAllModules() {
        return new HashMap<>(modules);
    }
}
