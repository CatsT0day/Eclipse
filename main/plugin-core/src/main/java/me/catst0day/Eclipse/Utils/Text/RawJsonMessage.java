package me.catst0day.Eclipse.Utils.Text;

import me.catst0day.Eclipse.Entity.Player.EclipsePlr;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RawJsonMessage {

    private List<Component> parts = new ArrayList<>();
    private final Map<String, Object> temp = new HashMap<>();
    private String lastRawText = "";

    public RawJsonMessage() {}

    public RawJsonMessage clear() {
        parts = new ArrayList<>();
        temp.clear();
        lastRawText = "";
        return this;
    }

    public RawJsonMessage addText(String text) {
        if (text == null || text.isEmpty()) return this;
        if (!lastRawText.isEmpty()) build();
        this.lastRawText = text;
        return this;
    }

    public RawJsonMessage addItem(ItemStack item) {
        if (item == null) return this;
        temp.put("hover", item.asHoverEvent());
        return this;
    }

    public void addCommand(String cmd) {
        if (cmd == null || cmd.isEmpty()) return;
        temp.put("click", ClickEvent.runCommand(cmd.startsWith("/") ? cmd : "/" + cmd));
    }

    public RawJsonMessage addSuggestion(String suggestion) {
        if (suggestion == null || suggestion.isEmpty()) return this;
        temp.put("click", ClickEvent.suggestCommand(suggestion));
        return this;
    }

    public RawJsonMessage addUrl(String url) {
        if (url == null || url.isEmpty()) return this;
        temp.put("click", ClickEvent.openUrl(url.startsWith("http") ? url : "https:
        return this;
    }

    public RawJsonMessage addInsertion(String insertion) {
        if (insertion == null || insertion.isEmpty()) return this;
        temp.put("insertion", insertion);
        return this;
    }

    public RawJsonMessage addHover(List<String> hoverText) {
        if (hoverText == null || hoverText.isEmpty()) return this;
        return addHover(String.join("\n", hoverText));
    }

    public RawJsonMessage addHover(String hover) {
        if (hover == null || hover.isEmpty()) return this;
        temp.put("hover", HoverEvent.showText(TextUtil.parse(hover)));
        return this;
    }

    public RawJsonMessage addRM(RawJsonMessage other) {
        this.build();
        other.build();
        this.parts.addAll(other.parts);
        return this;
    }

    public void build() {
        if (lastRawText == null || lastRawText.isEmpty()) return;

        Component part = TextUtil.parse(lastRawText);

        if (temp.containsKey("click")) part = part.clickEvent((ClickEvent) temp.get("click"));
        if (temp.containsKey("hover")) {
            Object h = temp.get("hover");
            if (h instanceof HoverEvent) part = part.hoverEvent((HoverEvent<?>) h);
        }
        if (temp.containsKey("insertion")) part = part.insertion((String) temp.get("insertion"));

        parts.add(part);
        temp.clear();
        lastRawText = "";
    }

    public String combineClean() {
        if (parts.isEmpty()) return "";
        TextComponent.Builder builder = Component.text();
        parts.forEach(builder::append);
        return TextUtil.stripColor(TextUtil.toLegacy(builder.build()));
    }

    public Component getResult() {
        build();
        TextComponent.Builder root = Component.text();
        parts.forEach(root::append);
        return root.build();
    }

    public void show(EclipsePlr plr) {
        if (plr != null && plr.isOnline()) plr.getPlayer().sendMessage(getResult());
    }

    public void show(Player player) {
        if (player != null && player.isOnline()) player.sendMessage(getResult());
    }

    public void show(Collection<Player> players) {
        Component res = getResult();
        players.forEach(p -> {
            if (p.isOnline()) p.sendMessage(res);
        });
    }

    public void broadcast() {
        Bukkit.broadcast(getResult());
    }
}
