package me.catst0day.Eclipse.Bossbar;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class EclipseBossBarHideEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private EclipseBossBar bossBar = null;

    public EclipseBossBarHideEvent(EclipseBossBar bossBar) {
        this.bossBar = bossBar;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    @Override
    public final void setCancelled(final boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public final boolean isCancelled() {
        return cancel;
    }

    public EclipseBossBar getBossBar() {
        return bossBar;
    }

    public void setBossBar(EclipseBossBar bossBar) {
        this.bossBar = bossBar;
    }
}