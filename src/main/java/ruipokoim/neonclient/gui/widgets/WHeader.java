package ruipokoim.neonclient.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.utils.render.color.Color;

public class WHeader implements WWidget {
    private static final int HEIGHT      = 17;
    private static final int HEADER_COLOR = new Color(80, 255, 255, 255).getArgb(255);

    private final String title;
    private final String name;

    public WHeader(String title, String name) {
        this.title = title;
        this.name  = name;
    }

    @Override public int getHeight() { return HEIGHT; }

    @Override
    public void render(DrawContext ctx, int x, int y, int width, int mx, int my) {
        ctx.fill(x, y, x + width, y + HEIGHT, HEADER_COLOR);
        ctx.fill(x, y, x + 2, y + HEIGHT, HEADER_COLOR);

        double tw = NeonMain.TEXT_RENDERER.getWidth(name, name.length(), false);
        NeonMain.TEXT_RENDERER.begin(ctx, 1.0, false, false);
        NeonMain.TEXT_RENDERER.render(title,
                x + (width - tw) / 2.0,
                y + (HEIGHT - NeonMain.TEXT_RENDERER.getHeight(false)) / 2.0,
                new Color(80, 255, 255), false);
        NeonMain.TEXT_RENDERER.end();
    }
}