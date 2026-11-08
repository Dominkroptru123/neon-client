package ruipokoim.neonclient.settings;

import ruipokoim.neonclient.utils.misc.ISerializable;

import java.util.ArrayList;
import java.util.List;

public class SettingGroup implements ISerializable<SettingGroup> {
    public final String name;
    public boolean sectionExpanded;
    final List<Setting<?>> settings = new ArrayList<>(1);
    SettingGroup(String name, boolean sectionExpanded) {
        this.name = name;
        this.sectionExpanded = sectionExpanded;
    }
    public Setting<?> get(String name) {
        for (Setting<?> setting : this.settings) {
            if (setting.name.equals(name)) return setting;
        }
        return null;
    }
    public <T extends Setting<?>> T add(T setting) {
        settings.add(setting);
        return setting;
    }
    public Setting<?> getByIndex(int index) {
        return settings.get(index);
    }
    public List<Setting<?>> getSettings(){
        return settings;
    }
}