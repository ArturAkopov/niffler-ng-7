package guru.qa.niffler.condition;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public enum Color {
    yellow("rgba(255, 183, 3, 1)"),
    green("rgba(53, 173, 123, 1)"),
    orange("rgba(251, 133, 0, 1)"),
    blue("rgba(41, 65, 204, 1)");

    public final String rgba;

    private static final Map<String, Color> rgbaToColor = new HashMap<>();

    static {
        for (Color color : values()) {
            rgbaToColor.put(color.rgba, color);
        }
    }

    @NotNull
    public static Color fromRgba(String rgba) {
        Color color = rgbaToColor.get(rgba);
        if (color == null) {
            throw new IllegalArgumentException("Unknown color: " + rgba);
        }
        return color;
    }
}
