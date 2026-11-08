package ruipokoim.neonclient.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.utils.render.color.Color;

public class WGroup implements WWidget {
    private static final int HEIGHT = 15;
    private static final int PADDING = 6;

    private final String name;

    public WGroup(String name) { this.name = name; }

    @Override public int getHeight() { return HEIGHT; }

    @Override
    public void render(DrawContext ctx, int x, int y, int width, int mx, int my) {
        int centerY = y + HEIGHT / 2;
        double tw   = NeonMain.TEXT_RENDERER.getWidth(name, name.length(), false);
        double tx   = x + (width - tw) / 2.0;
        double ty   = y + (HEIGHT - NeonMain.TEXT_RENDERER.getHeight(false)) / 2.0;

        ctx.fill(x + PADDING, centerY, (int)(tx - PADDING), centerY + 1, 0xFFFFFFFF);
        ctx.fill((int)(tx + tw + PADDING), centerY, x + width - PADDING, centerY + 1, 0xFFFFFFFF);

        NeonMain.TEXT_RENDERER.begin(ctx, 1.0, false, false);
        NeonMain.TEXT_RENDERER.render(name, tx, ty, new Color(255, 255, 255), false);
        NeonMain.TEXT_RENDERER.end();
    }
}