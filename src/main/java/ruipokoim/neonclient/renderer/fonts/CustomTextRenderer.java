package ruipokoim.neonclient.renderer.fonts;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import ruipokoim.neonclient.utils.render.color.Color;

public class CustomTextRenderer implements TextRenderer {
    public static final CustomTextRenderer INSTANCE = new CustomTextRenderer();
    private final FontAdapter adapter = new FontAdapter();
    private DrawContext context;
    private boolean initialized = false;
    public void setup() {
        if (!initialized) {
            init(Identifier.of("neon-client", "fonts/outfit.ttf"), 11);
            initialized = true;
        }
    }
    @Override
    public void init(Identifier fontFile, int size) {
        adapter.load(fontFile, (float) size);
    }
    @Override
    public void begin(DrawContext context, double scale, boolean scaleOnly, boolean big) {
        this.context = context;
        context.getMatrices().pushMatrix();
        float gs = adapter.getGuiScale();
        context.getMatrices().scale(1f / gs, 1f / gs);
    }
    @Override
    public double render(String text, double x, double y, Color color, boolean shadow) {
        if (!adapter.isLoaded()) return x;
        float gs = adapter.getGuiScale();
        float baselineY = (float)(y * gs) + adapter.getFontAscent() * gs;
        float currX = (float)(x * gs);
        for (char c : text.toCharArray()) {
            FontAdapter.Glyph g = adapter.getGlyph(c);
            if (g == null) continue;

            if (g.width() > 0 && g.height() > 0) {
                float drawX = currX + g.xOffset() * gs;
                float drawY = baselineY + g.yOffset() * gs;
                float u1 = g.u() / 1024f;
                float u2 = (g.u() + g.width()) / 1024f;
                float v1 = g.v() / 1024f;
                float v2 = (g.v() + g.height()) / 1024f;
                if (shadow) {
                    context.drawTexturedQuad(
                            adapter.getTextureId(),
                            (int)(drawX + 1), (int)(drawY + 1),
                            (int)(drawX + 1 + g.width()), (int)(drawY + 1 + g.height()),
                            u1, u2, v1, v2
                    );
                }
                context.drawTexturedQuad(
                        adapter.getTextureId(),
                        (int) drawX, (int) drawY,
                        (int)(drawX + g.width()), (int)(drawY + g.height()),
                        u1, u2, v1, v2
                );
            }

            currX += g.advance() * gs;
        }

        return currX / gs;
    }

    @Override
    public void end() {
        context.getMatrices().popMatrix();
    }

    @Override
    public double getWidth(String text, int length, boolean shadow) {
        double w = 0;
        for (int i = 0; i < Math.min(length, text.length()); i++) {
            FontAdapter.Glyph g = adapter.getGlyph(text.charAt(i));
            if (g != null) w += g.advance();
        }
        return w;
    }

    @Override public double getHeight(boolean shadow) { return adapter.getFontHeight(); }
    @Override public void setAlpha(double a) {}
    @Override public boolean isBuilding() { return true; }
}