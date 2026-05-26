package me.catst0day.Eclipse.Utils.Schedulers;

import org.bukkit.plugin.Plugin;

public interface EclipseTask {
    boolean isCancelled();
    void cancel();
    Plugin getPlugin();
}