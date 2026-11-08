package ruipokoim.neonclient.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.settings.DoubleSetting;
import ruipokoim.neonclient.utils.render.color.Color;

public class WSliderDouble implements WWidget {
    private static final int ROW_H = 20;
    private static final int SLIDER_H = 6;
    private static final int PADDING = 6;
    private static final int TEXTBOX_W = 40;
    private static final int SLIDER_BG = new Color(40, 40, 50, 255).getArgb(255);
    private static final int SLIDER_FG = new Color(80, 255, 255, 180).getArgb(180);

    private final DoubleSetting setting;
    private final double labelWidth;
    private final WTextBox textBox;

    private final boolean noSlider;
    private boolean dragging;
    private int sliderX, sliderW;

    public WSliderDouble(DoubleSetting setting, double labelWidth) {
        this.setting = setting;
        this.labelWidth = labelWidth;
        this.textBox = new WTextBox(
                String.format("%." + setting.decimalPlaces + "f", setting.get()),
                input -> {
                    try {
                        double v = Double.parseDouble(input.trim());
                        if (v < setting.min || v > setting.max) return false;
                        setting.set(v);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
        );
        this.noSlider = setting.noSlider;
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
        int tbX = (int)(x + labelWidth + PADDING + 3);
        int tbY = y;
        textBox.render(ctx, tbX, tbY, TEXTBOX_W, mx, my);
        if (!textBox.isFocused()) {
            textBox.setText(String.format("%." + setting.decimalPlaces + "f", setting.get()));
        }
        if (!noSlider) {
            int sx  = tbX + TEXTBOX_W + PADDING;
            int endX = x + width - PADDING - WReset.SIZE - PADDING;
            int sw = endX - sx;
            int sy = y + (ROW_H - SLIDER_H) / 2;
            sliderX = sx; sliderW = sw;

            double t = Math.max(0, Math.min(1, (setting.get() - setting.sliderMin) / (setting.sliderMax - setting.sliderMin)));
            ctx.fill(sx, sy, sx + sw, sy + SLIDER_H, SLIDER_BG);
            ctx.fill(sx, sy, sx + (int)(sw * t), sy + SLIDER_H, SLIDER_FG);

            WReset.render(ctx, x + width - PADDING - WReset.SIZE, y + (ROW_H - WReset.SIZE) / 2,mx,my);
        }
    }

    @Override
    public boolean onClick(double mx, double my, int button, int x, int y, int width) {
        int tbX = (int)(x + labelWidth + PADDING + 3);
        if (textBox.onClick(mx, my, button, tbX, y, TEXTBOX_W)) return true;
        int rx = x + width - PADDING - WReset.SIZE;
        int ry = y + (ROW_H - WReset.SIZE) / 2;
        if (WReset.isHovered(mx, my, rx, ry)) { setting.set(setting.getDefaultValue()); return true; }
        if (!noSlider) {
            int sx = tbX + TEXTBOX_W + PADDING;
            int endX = x + width - PADDING - WReset.SIZE - PADDING;
            if (button == 0 && my >= y && my <= y + ROW_H && mx >= sx && mx <= endX) {
                dragging = true;
                updateValue(mx);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRelease(int button) {
        if (button == 0) dragging = false;
        textBox.onRelease(button);
    }

    @Override
    public void onMouseMove(double mx) {
        if (dragging) updateValue(mx);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return textBox.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char chr) {
        return textBox.charTyped(chr);
    }

    private void updateValue(double mx) {
        double t = Math.max(0, Math.min(1, (mx - sliderX) / sliderW));
        setting.set(setting.sliderMin + t * (setting.sliderMax - setting.sliderMin));
    }
}