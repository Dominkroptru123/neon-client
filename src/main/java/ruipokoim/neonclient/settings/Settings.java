package ruipokoim.neonclient.settings;

import ruipokoim.neonclient.utils.misc.ISerializable;

import java.util.ArrayList;
import java.util.List;
public class Settings implements ISerializable<Settings> {
    private SettingGroup defaultGroup;
    private boolean invalidate;
    public final List<SettingGroup> groups = new ArrayList<>(1);
    public void onActivated() {
        for (SettingGroup group : groups) {
            for (Setting<?> setting : group.settings) {
                setting.onActivated();
            }
        }
    }
    public Setting<?> get(String name) {
        for (SettingGroup sg : this.groups) {
            for (Setting<?> setting : sg.settings) {
                if (name.equalsIgnoreCase(setting.name)) return setting;
            }
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    public <T> Setting<T> get(String name, Class<T> tClass) {
        for (SettingGroup sg : this.groups) {
            for (Setting<?> setting : sg.settings) {
                Class<?> sClass = setting.getDefaultValue().getClass();
                if (name.equalsIgnoreCase(setting.name) && tClass.equals(sClass))
                    return (Setting<T>) setting;
            }
        }

        return null;
    }
    public void invalidate() {
        invalidate = true;
    }
    public SettingGroup getGroup(String name) {
        for (SettingGroup sg : this.groups) {
            if (sg.name.equals(name)) return sg;
        }
        return null;
    }
    public List<SettingGroup> getGroups(){
        return groups;
    }
    public int sizeGroups() {
        return groups.size();
    }
    public SettingGroup getDefaultGroup() {
        if (defaultGroup == null) defaultGroup = createGroup("General");
        return defaultGroup;
    }

    public SettingGroup createGroup(String name, boolean expanded) {
        SettingGroup group = new SettingGroup(name, expanded);
        groups.add(group);
        return group;
    }
    public SettingGroup createGroup(String name) {
        return createGroup(name, true);
    }
}