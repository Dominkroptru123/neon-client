package ruipokoim.neonclient.gui.widgets;


import net.minecraft.client.gui.DrawContext;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.utils.misc.input.Keybind;
import ruipokoim.neonclient.utils.render.color.Color;

import static ruipokoim.neonclient.NeonMain.ModuleManager;

public class WModuleBind implements WWidget {
    private static final int ROW_H = 20;
    private static final int BOX = 16;
    private static final int PADDING = 6;
    private static final int LINE = new Color(0, 0, 0).getRgb();
    private static final int FILL = new Color(20, 20, 20, 200).getArgb(200);
    private static final int HOVEREDLINE = new Color(10, 10, 10).getRgb();
    private static final int HOVEREDFILL = new Color(30, 30, 30, 200).getArgb(200);
    private final Module module;

    private int lastBx, lastBy, bx, width;
    public WModuleBind(Module module) { this.module = module; }

    @Override public int getHeight() { return ROW_H; }

    @Override
    public void render(DrawContext ctx, int x, int y, int width, int mx, int my) {
        NeonMain.TEXT_RENDERER.begin(ctx, 1.0, false, false);
        NeonMain.TEXT_RENDERER.render("Bind", x + PADDING,
                y + (ROW_H - NeonMain.TEXT_RENDERER.getHeight(false)) / 2.0,
                new Color(200, 200, 200), false);
        NeonMain.TEXT_RENDERER.end();
        int bx = (int)(NeonMain.TEXT_RENDERER.getWidth("Bind", 4, false) + 3) + x + PADDING;
        int by = y + (ROW_H - BOX) / 2;
        lastBx = bx; lastBy = by;
        String text = ModuleManager.isBinding() ? "..." : module.keybind.toString();
        int textWidth = (int)(NeonMain.TEXT_RENDERER.getWidth(text, text.length(), false)) + 8;
        ctx.fill(bx, by, bx + textWidth + 1, by + BOX, isHovered(mx,my,bx,by,textWidth + 1, BOX) ? HOVEREDLINE : LINE);
        ctx.fill(bx + 1, by + 1, bx + textWidth, by + BOX - 1, isHovered(mx,my,bx,by,textWidth + 1, BOX) ? HOVEREDFILL : FILL);
        NeonMain.TEXT_RENDERER.begin(ctx, 1.0, false, false);
        NeonMain.TEXT_RENDERER.render(text, bx + 4,
                y + (ROW_H - NeonMain.TEXT_RENDERER.getHeight(false)) / 2.0,
                new Color(200, 200, 200), false);
        NeonMain.TEXT_RENDERER.end();
        this.width = textWidth;
        int rx = x + width - PADDING - WReset.SIZE;
        int ry = y + (ROW_H - WReset.SIZE) / 2;
        WReset.render(ctx, rx, ry, mx, my);
    }

    @Override
    public boolean onClick(double mx, double my, int button, int x, int y, int width) {
        int rx = x + width - PADDING - WReset.SIZE;
        int ry = y + (ROW_H - WReset.SIZE) / 2;
        if (WReset.isHovered(mx, my, rx, ry) && module.keybind.isSet()) {
            module.keybind.set(Keybind.none());
            ModuleManager.setModuleToBind(null);
            module.info("Removed bind.");
            return true;
        }
        if (mx >= lastBx && mx <= lastBx + this.width + 1 && my >= lastBy && my <= lastBy + BOX) {
            ModuleManager.setModuleToBind(module);
            return true;

        }
        return false;
    }
}