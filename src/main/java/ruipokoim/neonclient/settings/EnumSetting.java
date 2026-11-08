package ruipokoim.neonclient.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {
    private final T[] values;
    private final List<String> suggestions;
    protected boolean isValueValid(T value){
        return true;
    }
    public void setByName(String name) {
        for (T value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                set(value);
                return;
            }
        }
    }
    @SuppressWarnings("unchecked")
    public EnumSetting(String name, String description, T defaultValue, Consumer<T> onChanged, boolean visible) {
        super(name, description, defaultValue, onChanged, visible);

        values = (T[]) defaultValue.getDeclaringClass().getEnumConstants();
        suggestions = new ArrayList<>(values.length);
        for (T value : values) suggestions.add(value.toString());
    }
    public static class Builder<T extends Enum<?>> extends SettingBuilder<Builder<T>, T, EnumSetting<T>> {
        public Builder() {
            super(null);
        }
        @Override
        public EnumSetting<T> build() {
            return new EnumSetting<>(name, description, defaultValue, onChanged, visible);
        }
    }
}