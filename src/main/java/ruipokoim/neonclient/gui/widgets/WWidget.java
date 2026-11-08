package ruipokoim.neonclient.gui.widgets;

import net.minecraft.client.gui.DrawContext;

public interface WWidget {
    int getHeight();
    void render(DrawContext ctx, int x, int y, int width, int mouseX, int mouseY);
    default boolean isHovered(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    default boolean onClick(double mx, double my, int button, int x, int y, int width) { return false; }
    default void onRelease(int button) {}
    default void onMouseMove(double mx) {}
}