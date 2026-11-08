package ruipokoim.neonclient.renderer.fonts;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import ruipokoim.neonclient.utils.render.color.Color;

import static ruipokoim.neonclient.NeonMain.mc;

public class VanillaTextRenderer implements TextRenderer {
    public static final VanillaTextRenderer INSTANCE = new VanillaTextRenderer();

    @Override
    public void init(Identifier fontFile, int size) {

    }

    private final net.minecraft.client.font.TextRenderer mcTextRenderer;
    private DrawContext currentContext;
    private double alpha = 1.0;
    private double scale = 1.0;
    private boolean building = false;

    public VanillaTextRenderer() {
        this.mcTextRenderer = mc.textRenderer;
    }

    @Override
    public void setAlpha(double a) {
        this.alpha = a;
    }

    @Override
    public void begin(DrawContext context, double scale, boolean scaleOnly, boolean big) {
        this.currentContext = context;
        this.scale = scale;
        this.building = true;
        context.getMatrices().pushMatrix();
        context.getMatrices().scale((float) scale, (float) scale);
    }

    @Override
    public double getWidth(String text, int length, boolean shadow) {
        if (text == null || text.isEmpty()) return 0;
        String sub = text.length() > length ? text.substring(0, length) : text;
        return mcTextRenderer.getWidth(sub) * scale;
    }

    @Override
    public double getHeight(boolean shadow) {
        return mcTextRenderer.fontHeight * scale;
    }

    @Override
    public double render(String text, double x, double y, Color color, boolean shadow) {
        if (currentContext == null || !building) return 0;
        int alphaInt = (int) (this.alpha * color.a);
        int argb = color.getArgb(alphaInt);
        float scaledX = (float) (x / scale);
        float scaledY = (float) (y / scale);

        currentContext.drawText(mcTextRenderer, text, (int) scaledX, (int) scaledY, argb, shadow);

        return getWidth(text, false);
    }

    @Override
    public boolean isBuilding() {
        return building;
    }

    @Override
    public void end() {
        if (currentContext != null && building) {
            currentContext.getMatrices().popMatrix();
            currentContext = null;
        }
        this.building = false;
        this.alpha = 1.0;
    }
}