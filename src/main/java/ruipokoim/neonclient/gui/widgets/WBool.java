package ruipokoim.neonclient.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.settings.BoolSetting;
import ruipokoim.neonclient.utils.render.color.Color;

public class WBool implements WWidget {
    private static final int ROW_H = 20;
    private static final int PADDING = 6;
    private static final int BOX = 16;
    private static final int BOOL_ON = new Color(80, 255, 255, 255).getArgb(255);
    private static final int LINE = new Color(0, 0, 0).getRgb();
    private static final int FILL = new Color(20, 20, 20, 200).getArgb(200);
    private static final int HOVEREDLINE = new Color(10, 10, 10).getRgb();
    private static final int HOVEREDFILL = new Color(30, 30, 30, 200).getArgb(200);
    private final BoolSetting setting;
    private final double labelWidth;

    private double animProgress;

    public WBool(BoolSetting setting, double labelWidth) {
        this.setting = setting;
        this.labelWidth = labelWidth;
        this.animProgress = setting.get() ? 1 : 0;
    }

    @Override
    public int getHeight() {
        return ROW_H;
    }
    @Override
    public void render(DrawContext ctx, int x, int y, int width, int mx, int my) {
        double speed = 0.15;
        if (Boolean.TRUE.equals(setting.get())) {
            animProgress += speed;
        } else {
            animProgress -= speed;
        }
        if (animProgress > 1) animProgress = 1;
        else if (animProgress < 0) animProgress = 0;
        NeonMain.TEXT_RENDERER.begin(ctx, 1.0, false, false);
        NeonMain.TEXT_RENDERER.render(setting.title,
                x + PADDING,
                y + (ROW_H - NeonMain.TEXT_RENDERER.getHeight(false)) / 2.0,
                new Color(200, 200, 200), false);
        NeonMain.TEXT_RENDERER.end();
        int bx = (int)(x + labelWidth + PADDING + 3);
        int by = y + (ROW_H - BOX) / 2;
        ctx.fill(bx, by, bx + BOX, by + BOX, isHovered(mx,my,bx,by, BOX, BOX) ? HOVEREDLINE : LINE);
        ctx.fill(bx + 1, by + 1, bx + BOX - 1, by + BOX - 1, isHovered(mx,my,bx,by, BOX, BOX) ? HOVEREDFILL : FILL);
        if (animProgress > 0) {
            double size = 8 * animProgress;
            double offset = (BOX - size) / 2.0;
            ctx.fill(
                    (int) (bx + offset),
                    (int) (by + offset),
                    (int) (bx + BOX - offset),
                    (int) (by + BOX - offset),
                    BOOL_ON
            );
        }
        int rx = x + width - PADDING - WReset.SIZE;
        int ry = y + (ROW_H - WReset.SIZE) / 2;
        WReset.render(ctx, rx, ry, mx, my);
    }
    @Override
    public boolean onClick(double mx, double my, int button, int x, int y, int width) {
        int rx = x + width - PADDING - WReset.SIZE;
        int ry = y + (ROW_H - WReset.SIZE) / 2;
        if (WReset.isHovered(mx, my, rx, ry)) {
            setting.set(setting.getDefaultValue());
            return true;
        }
        int bx = (int)(x + labelWidth + PADDING + 3);
        int by = y + (ROW_H - BOX) / 2;
        if (button == 0 && mx >= bx && mx <= bx + BOX && my >= by && my <= by + BOX) {
            setting.set(!Boolean.TRUE.equals(setting.get()));
            return true;
        }
        return false;
    }
}