package ruipokoim.neonclient.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.utils.render.color.Color;

import java.util.function.Function;

public class WTextBox implements WWidget {
    private static final int ROW_H = 20;
    private static final int PADDING = 2;
    private static final int BOX_H = 13;
    private static final int BORDER = new Color(0, 0, 0, 255).getArgb(255);
    private static final int BG = new Color(18, 18, 20, 255).getArgb(255);
    private static final int BG_FOCUS = new Color(18, 18, 20, 255).getArgb(255);
    private static final int ACCENT = new Color(80, 255, 255, 255).getArgb(255);
    private static final int CURSOR_BLINK_MS = 500;

    private String text;
    private String pending;
    private final Function<String, Boolean> validator;

    private boolean focused = false;
    private int cursor = 0;
    private int lastX, lastY, lastWidth;

    public WTextBox(String text, Function<String, Boolean> validator) {
        this.text = text;
        this.pending = text;
        this.validator = validator;
        this.cursor = text.length();
    }

    @Override public int getHeight() { return ROW_H; }

    @Override
    public void render(DrawContext ctx, int x, int y, int width, int mouseX, int mouseY) {
        lastX = x + 1; lastY = y; lastWidth = width;
        int bx = x + 1;
        int by = y + (ROW_H - BOX_H) / 2;
        int bw = width - PADDING;
        ctx.fill(bx - 1, by - 1, bx + bw + 1, by + BOX_H + 1, BORDER);
        ctx.fill(bx, by, bx + bw, by + BOX_H, focused ? BG_FOCUS : BG);
        String display = pending;
        double textH = NeonMain.TEXT_RENDERER.getHeight(false);
        double textY = by + (BOX_H - textH) / 2.0;
        String beforeCursor = display.substring(0, Math.min(cursor, display.length()));
        double cursorPx = NeonMain.TEXT_RENDERER.getWidth(beforeCursor, beforeCursor.length(), false);
        double maxTextW = bw - PADDING * 2;
        double scrollX = Math.max(0, cursorPx - maxTextW);
        ctx.enableScissor(bx + PADDING, by, bx + bw - PADDING, by + BOX_H);
        NeonMain.TEXT_RENDERER.begin(ctx, 1.0, false, false);
        NeonMain.TEXT_RENDERER.render(display,
                bx + PADDING - scrollX,
                textY,
                new Color(220, 220, 220), false);
        NeonMain.TEXT_RENDERER.end();
        if (focused) {
            long elapsed = System.currentTimeMillis() % (CURSOR_BLINK_MS * 2);
            if (elapsed < CURSOR_BLINK_MS) {
                int cx = (int)(bx + PADDING + cursorPx - scrollX);
                ctx.fill(cx, by + 2, cx + 1, by + BOX_H - 2, ACCENT);
            }
        }
        ctx.disableScissor();
    }

    @Override
    public boolean onClick(double mx, double my, int button, int x, int y, int width) {
        int bx = x + PADDING;
        int by = y + (ROW_H - BOX_H) / 2;
        int bw = width - PADDING * 2;
        boolean inside = mx >= bx && mx <= bx + bw && my >= by && my <= by + BOX_H;
        if (inside && button == 0) {
            if (!focused) {
                focused = true;
                pending = text;
                cursor  = text.length();
            } else {
                cursor = pickCursorAt(mx, bx);
            }
            return true;
        }
        if (focused) {
            focused = false;
            pending = text;
        }
        return false;
    }
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) return false;

        switch (keyCode) {
            case InputUtil.GLFW_KEY_ENTER, InputUtil.GLFW_KEY_KP_ENTER -> {
                if (validator.apply(pending)) {
                    text = pending;
                } else {
                    pending = text;
                }
                focused = false;
                cursor  = text.length();
                return true;
            }
            case InputUtil.GLFW_KEY_ESCAPE -> {
                pending = text;
                focused = false;
                return true;
            }
            case InputUtil.GLFW_KEY_BACKSPACE -> {
                if (cursor > 0 && !pending.isEmpty()) {
                    pending = pending.substring(0, cursor - 1) + pending.substring(cursor);
                    cursor--;
                }
                return true;
            }
            case InputUtil.GLFW_KEY_DELETE -> {
                if (cursor < pending.length()) {
                    pending = pending.substring(0, cursor) + pending.substring(cursor + 1);
                }
                return true;
            }
            case InputUtil.GLFW_KEY_LEFT -> {
                if (cursor > 0) cursor--;
                return true;
            }
            case InputUtil.GLFW_KEY_RIGHT -> {
                if (cursor < pending.length()) cursor++;
                return true;
            }
            case InputUtil.GLFW_KEY_HOME -> { cursor = 0; return true; }
            case InputUtil.GLFW_KEY_END  -> { cursor = pending.length(); return true; }
        }
        return false;
    }
    public boolean charTyped(char chr) {
        if (!focused) return false;
        pending = pending.substring(0, cursor) + chr + pending.substring(cursor);
        cursor++;
        return true;
    }

    public boolean isFocused() { return focused; }
    public void setText(String text) {
        if (!focused) {
            this.text = text;
            this.pending = text;
        }
    }
    private int pickCursorAt(double mouseX, int boxLeft) {
        String beforeCursor = pending.substring(0, Math.min(cursor, pending.length()));
        double cursorPx = NeonMain.TEXT_RENDERER.getWidth(beforeCursor, beforeCursor.length(), false);
        double maxTextW = lastWidth - PADDING * 4;
        double scrollX  = Math.max(0, cursorPx - maxTextW);

        double relX = mouseX - (boxLeft + PADDING) + scrollX;
        int best = 0;
        double bestDist = Double.MAX_VALUE;
        for (int i = 0; i <= pending.length(); i++) {
            String sub = pending.substring(0, i);
            double px = NeonMain.TEXT_RENDERER.getWidth(sub, sub.length(), false);
            double dist = Math.abs(px - relX);
            if (dist < bestDist) { bestDist = dist; best = i; }
        }
        return best;
    }
}