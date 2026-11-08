package ruipokoim.neonclient.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.text.Text;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.gui.widgets.*;
import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.settings.*;
import ruipokoim.neonclient.utils.render.color.Color;

import java.util.ArrayList;
import java.util.List;

public class ModuleSettingsScreen extends Screen {
    private static final int BG_COLOR = new Color(20, 20, 25, 100).getArgb(100);
    private static final int BACKDROP = new Color(0, 0, 0, 0).getArgb(0);
    private static final int PANEL_WIDTH = 300;

    private final Module module;
    private final Screen parent;
    private final List<WWidget> widgets = new ArrayList<>();

    public ModuleSettingsScreen(Module module, Screen parent) {
        super(Text.empty());
        this.module = module;
        this.parent = parent;
    }

    @Override
    protected void init() {
        widgets.clear();
        widgets.add(new WHeader(module.title, module.name));

        for (var group : module.settings.getGroups()) {
            widgets.add(new WGroup(group.name));
            double maxLabelW = 0;
            for (var s : group.getSettings()) {
                if (s.isVisible())
                    maxLabelW = Math.max(maxLabelW, NeonMain.TEXT_RENDERER.getWidth(s.name, s.name.length(), false));
            }
            for (var setting : group.getSettings()) {
                if (!setting.isVisible()) continue;
                if (setting instanceof BoolSetting bs) widgets.add(new WBool(bs, maxLabelW));
                else if (setting instanceof DoubleSetting ds) widgets.add(new WSliderDouble(ds, maxLabelW));
                else if (setting instanceof IntSetting is) widgets.add(new WSliderInt(is, maxLabelW));
                else if (setting instanceof EnumSetting<?> es) widgets.add(new WEnum(es, maxLabelW));
                else if (setting instanceof KeybindSetting ks) widgets.add(new WBind(ks, maxLabelW));
            }
        }

        widgets.add(new WGroup("Bind"));
        widgets.add(new WModuleBind(module));
    }

    private int panelHeight() {
        return widgets.stream().mapToInt(WWidget::getHeight).sum() + 3;
    }
    private int panelX() { return (NeonMain.mc.getWindow().getScaledWidth()  - PANEL_WIDTH) / 2; }
    private int panelY() { return (NeonMain.mc.getWindow().getScaledHeight() - panelHeight()) / 2; }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, NeonMain.mc.getWindow().getScaledWidth(), NeonMain.mc.getWindow().getScaledHeight(), BACKDROP);
        int px = panelX(), py = panelY();
        ctx.fill(px, py, px + PANEL_WIDTH, py + panelHeight(), BG_COLOR);
        int y = py;
        for (var w : widgets) {
            w.render(ctx, px, y, PANEL_WIDTH, mouseX, mouseY);
            y += w.getHeight();
        }
    }
    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int px = panelX(), y = panelY();
        for (var w : widgets) {
            if (w.onClick(click.x(), click.y(), click.button(), px, y, PANEL_WIDTH)) return true;
            y += w.getHeight();
        }
        return super.mouseClicked(click, doubled);
    }
    @Override
    public boolean mouseReleased(Click click) {
        widgets.forEach(w -> w.onRelease(click.button()));
        return super.mouseReleased(new Click(click.x(), click.y(), new MouseInput(click.button(), click.modifiers())));
    }
    @Override
    public void mouseMoved(double mx, double my) {
        widgets.forEach(w -> w.onMouseMove(mx));
    }
    @Override
    public boolean keyPressed(KeyInput input){
        for(var w : widgets){
            if(w instanceof WTextBox tb && tb.keyPressed(input.getKeycode(), input.scancode(), input.modifiers())) return true;
            if(w instanceof WSliderInt si && si.keyPressed(input.getKeycode(), input.scancode(), input.modifiers())) return true;
            if(w instanceof WSliderDouble si && si.keyPressed(input.getKeycode(), input.scancode(), input.modifiers())) return true;
        }
        return super.keyPressed(input);
    }
    @Override
    public boolean charTyped(CharInput chr) {
        for (var w : widgets) {
            if (w instanceof WTextBox tb && tb.charTyped(chr.asString().charAt(0))) return true;
            if (w instanceof WSliderInt si && si.charTyped(chr.asString().charAt(0))) return true;
            if (w instanceof WSliderDouble si && si.charTyped(chr.asString().charAt(0))) return true;
        }
        return super.charTyped(chr);
    }
    @Override public boolean shouldPause() { return false; }
    @Override public void close() { NeonMain.mc.setScreen(parent); }
}