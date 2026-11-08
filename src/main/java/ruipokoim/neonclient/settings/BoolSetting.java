package ruipokoim.neonclient.settings;

import java.util.List;
import java.util.function.Consumer;

public class BoolSetting extends Setting<Boolean> {
    private static final List<String> SUGGESTIONS = List.of("true", "false", "toggle");
    private BoolSetting(String name, String description, Boolean defaultValue, Consumer<Boolean> onChanged, boolean visible) {
        super(name, description, defaultValue, onChanged, visible);
    }
    @Override
    protected boolean isValueValid(Boolean value){
        return true;
    }
    public static class Builder extends SettingBuilder<Builder, Boolean, BoolSetting> {
        public Builder() {
            super(false);
        }
        @Override
        public BoolSetting build() {
            return new BoolSetting(name, description, defaultValue, onChanged, visible);
        }
    }
}