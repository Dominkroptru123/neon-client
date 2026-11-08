package ruipokoim.neonclient.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.settings.KeybindSetting;
import ruipokoim.neonclient.utils.render.color.Color;

public class WBind implements WWidget {
    private static final int ROW_H = 20;
    private static final int BOX = 16;
    private static final int PADDING = 6;
    private static final int LINE = new Color(0, 0, 0).getRgb();
    private static final int FILL = new Color(20, 20, 20, 200).getArgb(200);
    private static final int HOVEREDLINE = new Color(10, 10, 10).getRgb();
    private static final int HOVEREDFILL = new Color(30, 30, 30, 200).getArgb(200);
    private final KeybindSetting setting;
    private final double labelWidth;

    private int lastBx, lastBy, bx, width;
    public WBind(KeybindSetting setting, double labelWidth) {
        this.setting = setting;
        this.labelWidth = labelWidth;
        setting.widget = this;
    }

    @Override public int getHeight() { return ROW_H; }

    @Override
    public void render(DrawContext ctx, int x, int y, int width, int mx, int my) {
        NeonMain.TEXT_RENDERER.begin(ctx, 1.0, false, false);
        NeonMain.TEXT_RENDERER.render(setting.title,
                x + PADDING,
                y + (ROW_H - NeonMain.TEXT_RENDERER.getHeight(false)) / 2.0,
                new Color(200, 200, 200), false);
        NeonMain.TEXT_RENDERER.end();
        int bx = (int)(x + labelWidth + PADDING + 3);
        int by = y + (ROW_H - BOX) / 2;
        lastBx = bx; lastBy = by;
        String text = setting.listening ? "..." : setting.get().toString();
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
        if (WReset.isHovered(mx, my, rx, ry)) {
            setting.resetBind();
            return true;
        }
        if (mx >= lastBx && mx <= lastBx + this.width + 1 && my >= lastBy && my <= lastBy + BOX) {
            setting.listening = true;
        }
        return false;
    }
}
