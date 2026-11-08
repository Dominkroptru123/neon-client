package ruipokoim.neonclient.settings;

import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.utils.Utils;

import java.util.function.Consumer;

public abstract class Setting<T> {
    public final String name, title, description;
    public final boolean visible;
    private final Consumer<T> onChanged;
    public Module module;
    protected T value;
    protected final T defaultValue;
    public Setting(String name, String description, T defaultValue, Consumer<T> onChanged, boolean visible) {
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.description = description;
        this.defaultValue = defaultValue;
        this.onChanged = onChanged;
        this.visible = visible;
        resetVal();
    }
    public T get() {
        return value;
    }
    public boolean set(T value) {
        if (!isValueValid(value)) return false;
        this.value = value;
        onChanged();
        return true;
    }
    protected abstract boolean isValueValid(T value);
    protected void resetVal(){
        value = defaultValue;
    }
    public T getDefaultValue() {
        return defaultValue;
    }
    public void onChanged() {
        if (onChanged != null) onChanged.accept(value);
    }
    public boolean isVisible() {
        return visible == true;
    }
    public void onActivated() {}
    @SuppressWarnings("unchecked")
    public abstract static class SettingBuilder<B, V, S> {
        protected String name = "undefined", description = "";
        protected V defaultValue;
        protected boolean visible = true;
        protected Consumer<V> onChanged;
        protected Consumer<Setting<V>> onModuleActivated;
        protected SettingBuilder(V defaultValue) {
            this.defaultValue = defaultValue;
        }
        public B name(String name) {
            this.name = name;
            return (B) this;
        }
        public B description(String description) {
            this.description = description;
            return (B) this;
        }
        public B defaultValue(V defaultValue) {
            this.defaultValue = defaultValue;
            return (B) this;
        }
        public B visible(boolean visible) {
            this.visible = visible;
            return (B) this;
        }
        public B onChanged(Consumer<V> onChanged) {
            this.onChanged = onChanged;
            return (B) this;
        }
        public B onModuleActivated(Consumer<Setting<V>> onModuleActivated) {
            this.onModuleActivated = onModuleActivated;
            return (B) this;
        }
        public abstract S build();
    }
}