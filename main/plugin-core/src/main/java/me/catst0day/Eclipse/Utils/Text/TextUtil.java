package me.catst0day.Eclipse.Utils.Text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class TextUtil {
    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&').hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.builder()
            .character('§').hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public static final TextUtil BLACK = new TextUtil("Black", '0', 0, 0, 0);
    public static final TextUtil DARK_BLUE = new TextUtil("Dark_Blue", '1', 0, 0, 170);
    public static final TextUtil DARK_GREEN = new TextUtil("Dark_Green", '2', 0, 170, 0);
    public static final TextUtil DARK_AQUA = new TextUtil("Dark_Aqua", '3', 0, 170, 170);
    public static final TextUtil DARK_RED = new TextUtil("Dark_Red", '4', 170, 0, 0);
    public static final TextUtil DARK_PURPLE = new TextUtil("Dark_Purple", '5', 170, 0, 170);
    public static final TextUtil GOLD = new TextUtil("Gold", '6', 255, 170, 0);
    public static final TextUtil GRAY = new TextUtil("Gray", '7', 170, 170, 170);
    public static final TextUtil DARK_GRAY = new TextUtil("Dark_Gray", '8', 85, 85, 85);
    public static final TextUtil BLUE = new TextUtil("Blue", '9', 85, 85, 255);
    public static final TextUtil GREEN = new TextUtil("Green", 'a', 85, 255, 85);
    public static final TextUtil AQUA = new TextUtil("Aqua", 'b', 85, 255, 255);
    public static final TextUtil RED = new TextUtil("Red", 'c', 255, 85, 85);
    public static final TextUtil LIGHT_PURPLE = new TextUtil("Light_Purple", 'd', 255, 85, 255);
    public static final TextUtil YELLOW = new TextUtil("Yellow", 'e', 255, 255, 85);
    public static final TextUtil WHITE = new TextUtil("White", 'f', 255, 255, 255);

    public static final TextUtil OBFUSCATED = new TextUtil("Obfuscated", 'k', false);
    public static final TextUtil BOLD = new TextUtil("Bold", 'l', false);
    public static final TextUtil STRIKETHROUGH = new TextUtil("Strikethrough", 'm', false);
    public static final TextUtil UNDERLINE = new TextUtil("Underline", 'n', false);
    public static final TextUtil ITALIC = new TextUtil("Italic", 'o', false);
    public static final TextUtil RESET = new TextUtil("Reset", 'r', false, true);

    private final char c;
    private final boolean color;
    private boolean isReset;
    private int redChannel = -1;
    private int greenChannel = -1;
    private int blueChannel = -1;
    private final String name;

    private TextUtil(String name, char c, int red, int green, int blue) {
        this.name = name;
        this.c = c;
        this.color = true;
        this.redChannel = red;
        this.greenChannel = green;
        this.blueChannel = blue;
    }

    private TextUtil(String name, char c, boolean color) {
        this.name = name;
        this.c = c;
        this.color = color;
    }

    private TextUtil(String name, char c, boolean color, boolean isReset) {
        this.name = name;
        this.c = c;
        this.color = color;
        this.isReset = isReset;
    }

    @Override
    public String toString() {
        return "§" + c;
    }

    public static @NotNull Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        if (input.contains("<") && (input.contains(">") || input.contains("/"))) {
            try {
                return MINI.deserialize(input);
            } catch (Exception ignored) {}
        }
        return translateToComponent(input);
    }

    public static List<Component> parse(List<String> lines) {
        return lines.stream().map(TextUtil::parse).collect(Collectors.toList());
    }

    public static String colorize(String text) {
        if (text == null || text.isEmpty()) return text;
        return toLegacy(parse(text));
    }

    public static List<String> colorize(List<String> lines) {
        return lines.stream().map(TextUtil::colorize).collect(Collectors.toList());
    }

    public static String toLegacy(Component component) {
        return SECTION_SERIALIZER.serialize(component);
    }

    public static String translateHexAndAlternateColorCodes(String text) {
        if (text == null || text.isEmpty()) return text;
        text = text.replace("<newline>", "\\n");
        String legacy = toLegacy(parse(text));
        legacy = legacy.replace("\\n", "\n");
        return legacy;
    }

    public static @NotNull Component toComponent(String text) {
        return parse(text);
    }

    public static String deColorize(String text) {
        if (text == null || text.isEmpty()) return text;
        String cleaned = text.replace("&", "§");
        Component component = SECTION_SERIALIZER.deserialize(cleaned);
        return AMPERSAND_SERIALIZER.serialize(component);
    }

    public static String stripColor(String text) {
        if (text == null) return null;
        return ChatColor.stripColor(translateHexAndAlternateColorCodes(text));
    }

    private static Component translateToComponent(String input) {
        if (input.contains("#") && !input.contains("&#")) {
            input = input.replaceAll("#([a-fA-F0-9]{6})", "&#$1");
        }
        return input.contains("§") ? SECTION_SERIALIZER.deserialize(input) : AMPERSAND_SERIALIZER.deserialize(input);
    }

    public String getName() { return name; }
    public char getChar() { return c; }
    public boolean isColor() { return color; }
    public boolean isReset() { return isReset; }
    public int getRed() { return redChannel; }
    public int getGreen() { return greenChannel; }
    public int getBlue() { return blueChannel; }
}
