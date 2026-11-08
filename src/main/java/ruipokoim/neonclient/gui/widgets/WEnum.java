package ruipokoim.neonclient.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.settings.EnumSetting;
import ruipokoim.neonclient.utils.render.color.Color;

public class WEnum implements WWidget {
    private static final int ROW_H   = 20;
    private static final int PADDING = 6;

    private final EnumSetting<?> setting;
    private final double labelWidth;

    public WEnum(EnumSetting<?> setting, double labelWidth) {
        this.setting    = setting;
        this.labelWidth = labelWidth;
    }

    @Override public int getHeight() { return ROW_H; }

    @Override
    public void render(DrawContext ctx, int x, int y, int width, int mx, int my) {
        NeonMain.TEXT_RENDERER.begin(ctx, 1.0, false, false);
        NeonMain.TEXT_RENDERER.render(setting.title, x + PADDING,
                y + (ROW_H - NeonMain.TEXT_RENDERER.getHeight(false)) / 2.0,
                new Color(200, 200, 200), false);

        String val = setting.get() != null ? setting.get().toString() : "?";
        NeonMain.TEXT_RENDERER.render(val, x + labelWidth + PADDING + 3,
                y + (ROW_H - NeonMain.TEXT_RENDERER.getHeight(false)) / 2.0,
                new Color(80, 255, 255), false);
        NeonMain.TEXT_RENDERER.end();

        WReset.render(ctx, x + width - PADDING - WReset.SIZE, y + (ROW_H - WReset.SIZE) / 2,mx,my);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean onClick(double mx, double my, int button, int x, int y, int width) {
        int rx = x + width - PADDING - WReset.SIZE;
        int ry = y + (ROW_H - WReset.SIZE) / 2;
        if (WReset.isHovered(mx, my, rx, ry)) {
            setting.setByName(setting.getDefaultValue().name());
            return true;
        }
        if (my >= y && my <= y + ROW_H) {
            EnumSetting es = setting;
            Enum<?> current = (Enum<?>) es.get();
            Enum<?>[] values = current.getDeclaringClass().getEnumConstants();
            int idx  = current.ordinal();
            int next = button == 1
                    ? (idx - 1 + values.length) % values.length
                    : (idx + 1) % values.length;
            es.set(values[next]);
            return true;
        }
        return false;
    }
}