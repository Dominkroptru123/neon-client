package ruipokoim.neonclient.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ruipokoim.neonclient.modules.Category;
import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.modules.ModuleManager;
import ruipokoim.neonclient.utils.misc.input.KeyAction;
import ruipokoim.neonclient.utils.render.color.Color;

import java.util.ArrayList;
import java.util.List;

import static ruipokoim.neonclient.NeonMain.mc;

public class ClickGUI extends Screen {
    public List<CategoryWindow> windows;
    private static final int BACKDROP_COLOR = new Color(0, 0, 0, 0).getArgb(0);

    public ClickGUI() {
        super(Text.empty());
        this.windows = new ArrayList<>();
        int n = 4;
        for (Category category : ModuleManager.getCategories()) {
            CategoryWindow window = new CategoryWindow(n, 50, 0, 30, category, this::openModuleSettings);
            windows.add(window);
            n += window.getWidth() + 3;
        }
    }
    private void openModuleSettings(Module module) {
        mc.setScreen(new ModuleSettingsScreen(module, this));
    }
    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        drawContext.fill(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), BACKDROP_COLOR);
        super.render(drawContext, mouseX, mouseY, delta);
        for (CategoryWindow window : this.windows) {
            window.render(drawContext, mouseX, mouseY, delta);
        }
    }
    @Override
    public boolean mouseClicked(Click click, boolean doubled){
        for (CategoryWindow window : windows) {
            window.mouseClicked(click.x(), click.y(), click.button());
        }
        return super.mouseClicked(click, doubled);
    }
    @Override
    public boolean mouseReleased(Click click){
        for (CategoryWindow window : windows) {
            window.mouseReleased(click.x(), click.y(), click.button());
        }
        return super.mouseReleased(click);
    }
    @Override
    public void removed() {
        super.removed();
    }
    @Override
    public boolean shouldPause() {
        return false;
    }
}