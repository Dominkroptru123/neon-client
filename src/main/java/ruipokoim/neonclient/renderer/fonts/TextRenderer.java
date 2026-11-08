package ruipokoim.neonclient.renderer.fonts;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import ruipokoim.neonclient.utils.render.color.Color;

public interface TextRenderer {
    static TextRenderer get() {
        return CustomTextRenderer.INSTANCE;
    }
    void init(Identifier fontFile, int size);
    void setAlpha(double a);

    void begin(DrawContext context, double scale, boolean scaleOnly, boolean big);

    default void begin(DrawContext context, double scale) {
        begin(context, scale, false, false);
    }

    default void begin(DrawContext context) {
        begin(context, 1, false, false);
    }

    double getWidth(String text, int length, boolean shadow);

    default double getWidth(String text, boolean shadow) {
        return getWidth(text, text.length(), shadow);
    }

    default double getWidth(String text) {
        return getWidth(text, text.length(), false);
    }

    double getHeight(boolean shadow);

    default double getHeight() {
        return getHeight(false);
    }

    double render(String text, double x, double y, Color color, boolean shadow);

    default double render(String text, double x, double y, Color color) {
        return render(text, x, y, color, false);
    }

    boolean isBuilding();

    void end();
}