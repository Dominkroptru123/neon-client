package ruipokoim.neonclient.gui;

import net.minecraft.client.gui.DrawContext;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.modules.Category;
import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.modules.ModuleManager;
import ruipokoim.neonclient.utils.render.color.Color;

import java.util.List;
import java.util.function.Consumer;

public class CategoryWindow {
    private int x;
    private int y;
    private int width;
    private int height;
    private Category category;
    private boolean dragging;
    private double dragOffsetX, dragOffsetY;
    private int lastMouseX;
    private int lastMouseY;

    private final Consumer<Module> onSettingsOpen;

    private static final int BG_COLOR = new Color(0, 0, 0, 0).getArgb(0);
    private static final int HEADER_COLOR = new Color(0, 200, 255).getRgb();
    private static final int ACCENT_COLOR = new Color(0, 200, 255).getRgb();
    private static final int MODULE_ON_COLOR = new Color(50, 50, 50).getRgb();
    private static final int MODULE_OFF_COLOR = new Color(45, 45, 32, 80).getArgb(80);
    private static final int HEADER_HEIGHT = 17;
    private static final int MODULE_HEIGHT = 13;
    private static final int PADDING = 4;

    public CategoryWindow(int x, int y, int width, int height, Category category, Consumer<Module> onSettingsOpen) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.category = category;
        this.onSettingsOpen = onSettingsOpen;
        int maxWidth = (int) NeonMain.TEXT_RENDERER.getWidth(category.name, category.name.length(), false);
        for (Module module : ModuleManager.getGroup(category)) {
            int w = (int) NeonMain.TEXT_RENDERER.getWidth(module.title, module.title.length(), false);
            if (w > maxWidth) maxWidth = w;
        }
        this.width = maxWidth + (PADDING * 2) + 6;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        mouseDragged(mouseX, mouseY);
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        List<Module> modules = ModuleManager.getGroup(category);
        int totalHeight = HEADER_HEIGHT + modules.size() * MODULE_HEIGHT;
        context.fill(x, y, x + width, y + totalHeight, BG_COLOR);
        context.fill(x, y, x + width, y + HEADER_HEIGHT, HEADER_COLOR);
        context.fill(x, y, x + 2, y + HEADER_HEIGHT, ACCENT_COLOR);
        NeonMain.TEXT_RENDERER.begin(context, 1.0, false, false);
        NeonMain.TEXT_RENDERER.render(
                category.name,
                x + 5,
                y + (HEADER_HEIGHT - NeonMain.TEXT_RENDERER.getHeight(false)) / 2.0,
                new Color(80, 255, 255),
                false
        );
        NeonMain.TEXT_RENDERER.end();
        int moduleY = y + HEADER_HEIGHT;
        for (Module module : modules) {
            boolean enabled = module.isActive();
            int rowColor = enabled ? MODULE_ON_COLOR : MODULE_OFF_COLOR;
            context.fill(x, moduleY, x + width, moduleY + MODULE_HEIGHT, rowColor);
            if (enabled) {
                context.fill(x, moduleY, x + 1, moduleY + MODULE_HEIGHT, ACCENT_COLOR);
            }
            NeonMain.TEXT_RENDERER.begin(context, 1.0, false, false);
            NeonMain.TEXT_RENDERER.render(
                    module.title,
                    x + 5,
                    moduleY + (MODULE_HEIGHT - NeonMain.TEXT_RENDERER.getHeight(false)) / 2.0,
                    enabled ? new Color(80, 255, 255) : new Color(180, 180, 180),
                    false
            );
            NeonMain.TEXT_RENDERER.end();
            moduleY += MODULE_HEIGHT;
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        // Header drag — only on left click
        if (button == 0 && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + HEADER_HEIGHT) {
            dragging = true;
            dragOffsetX = mouseX - x;
            dragOffsetY = mouseY - y;
            return;
        }

        List<Module> modules = ModuleManager.getGroup(category);
        int moduleY = y + HEADER_HEIGHT;
        for (Module module : modules) {
            if (mouseX >= x && mouseX <= x + width
                    && mouseY >= moduleY && mouseY <= moduleY + MODULE_HEIGHT) {
                if (button == 0) {
                    // Left click → toggle module
                    module.toggle();
                } else if (button == 1) {
                    // Right click → open settings screen
                    if (onSettingsOpen != null) onSettingsOpen.accept(module);
                }
                return;
            }
            moduleY += MODULE_HEIGHT;
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
    }

    public void mouseDragged(double mouseX, double mouseY) {
        if (!dragging) return;
        x = (int)(mouseX - dragOffsetX);
        y = (int)(mouseY - dragOffsetY);
    }

    public int getX()      { return x; }
    public int getY()      { return y; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }
}