package ruipokoim.neonclient.settings;

import org.lwjgl.glfw.GLFW;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.KeyEvent;
import ruipokoim.neonclient.event.events.MouseClickEvent;
import ruipokoim.neonclient.gui.widgets.WBind;
import ruipokoim.neonclient.modules.ModuleManager;
import ruipokoim.neonclient.utils.misc.input.KeyAction;
import ruipokoim.neonclient.utils.misc.input.Keybind;

import java.util.function.Consumer;

import static ruipokoim.neonclient.event.EventHandler.Priority.HIGH;
import static ruipokoim.neonclient.event.EventHandler.Priority.HIGHEST;

public class KeybindSetting extends Setting<Keybind> {
    private final Runnable actionStart;
    private final Runnable actionEnd;
    public WBind widget;
    public boolean listening;
    @Override
    protected boolean isValueValid(Keybind value) {
        return true;
    }
    private KeybindSetting(String name, String description, Keybind defaultValue, Consumer<Keybind> onChanged, boolean visible, Runnable actionStart, Runnable actionEnd) {
        super(name, description, defaultValue, onChanged, visible);
        this.actionStart = actionStart;
        this.actionEnd = actionEnd;
        NeonMain.EVENT_BUS.subscribe(this);
    }
    @EventHandler(priority = HIGHEST)
    private void onKeyBinding(KeyEvent event) {
        if (event.action == KeyAction.Press && event.key() == GLFW.GLFW_KEY_ESCAPE && onClear()) event.cancel();
        else if (event.action == KeyAction.Release && onAction(true, event.key(), event.modifiers())) event.cancel();
    }
    @EventHandler(priority = HIGHEST)
    private void onMouseClickBinding(MouseClickEvent event) {
        if (event.action == KeyAction.Press && onAction(false, event.button(), 0)) event.cancel();
    }
    @EventHandler(priority = HIGH)
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Release && get().matches(event.input) && (module == null || module.isActive()) && actionEnd != null) {
            actionEnd.run();
        }
        else if (event.action == KeyAction.Press && get().matches(event.input) && (module == null || module.isActive()) && actionStart != null) {
            actionStart.run();
        }
    }
    @EventHandler(priority = HIGH)
    private void onMouseClick(MouseClickEvent event) {
        if (event.action == KeyAction.Release && get().matches(event.input) && (module == null || module.isActive()) && actionEnd != null) {
            actionEnd.run();
        }
        else if (event.action == KeyAction.Press && get().matches(event.input) && (module == null || module.isActive()) && actionStart != null) {
            actionStart.run();
        }
    }
    public boolean onClear() {
        if (listening) {
            get().reset();
            reset();
            return true;
        }
        return false;
    }
    public boolean onAction(boolean isKey, int value, int modifiers) {
        if (listening && get().canBindTo(isKey, value, modifiers)) {
            get().set(isKey, value, modifiers);
            reset();
            return true;
        }
        return false;
    }
    public void resetBind() {
        get().set(Keybind.none());
        reset();
    }
    public void reset() {
        listening = false;
        if (NeonMain.ModuleManager.isBinding()) {
            NeonMain.ModuleManager.setModuleToBind(null);
        }
    }
    public static class Builder extends SettingBuilder<Builder, Keybind, KeybindSetting> {
        private Runnable actionStart;
        private Runnable actionEnd;
        public Builder() {
            super(Keybind.none());
        }
        public Builder actionStart(Runnable actionStart) {
            this.actionStart = actionStart;
            return this;
        }
        public Builder actionEnd(Runnable actionEnd) {
            this.actionEnd = actionEnd;
            return this;
        }
        @Override
        public KeybindSetting build() {
            return new KeybindSetting(name, description, defaultValue, onChanged, visible, actionStart, actionEnd);
        }
    }
}