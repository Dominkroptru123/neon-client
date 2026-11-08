package ruipokoim.neonclient.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import ruipokoim.neonclient.utils.render.color.Color;
import static ruipokoim.neonclient.utils.render.texture.Texture.RESET_ICON;

public class WReset {
    public static final int SIZE = 16;
    private static final int LINE = new Color(0, 0, 0).getRgb();
    private static final int FILL = new Color(20, 20, 20, 200).getArgb(200);
    private static final int HOVEREDLINE = new Color(10, 10, 10).getRgb();
    private static final int HOVEREDFILL = new Color(30, 30, 30, 200).getArgb(200);

    public static void render(DrawContext ctx, int x, int y, int mx, int my) {
        ctx.fill(x, y, x + SIZE, y + SIZE, isHovered(mx,my,x,y) ? HOVEREDLINE : LINE);
        ctx.fill(x + 1, y + 1, x + SIZE - 1, y + SIZE - 1, isHovered(mx,my,x,y) ? HOVEREDFILL : FILL);
        ctx.drawTexturedQuad(RESET_ICON, x + 3, y + 3, x + SIZE - 3, y + SIZE - 3, 0f, 1f, 0f, 1f);
    }
    public static boolean isHovered(double mx, double my, int x, int y) {
        return mx >= x && mx <= x + SIZE && my >= y && my <= y + SIZE;
    }
}