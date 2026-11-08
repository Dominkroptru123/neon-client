package ruipokoim.neonclient.config;

import com.google.gson.*;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.GameJoinedEvent;
import ruipokoim.neonclient.event.events.GameLeftEvent;
import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.settings.*;
import ruipokoim.neonclient.utils.misc.input.Keybind;

import java.io.*;

import static ruipokoim.neonclient.NeonMain.*;

public class Config {
    private static final String CONFIG_NAME = "default.json";
    private static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    private JsonObject json = new JsonObject();
    public static boolean isLoading = false;

    public Config() {
        NeonMain.EVENT_BUS.subscribe(this);

        // FIX: The Shutdown Hook ensures the config saves even if the game is closed directly (ALT+F4 or window 'X')
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveAll();
            } catch (Exception ignored) {
                // Ignore errors during JVM shutdown
            }
        }));
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        // Left empty as per your comment to prevent overwriting modules enabled in the main menu
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        saveAll();
    }

    public void load() {
        File configFile = getFile();
        if (configFile == null) return;

        // Use BufferedReader just like your example for safer/cleaner reads
        try (FileReader fr = new FileReader(configFile);
             BufferedReader br = new BufferedReader(fr)) {

            JsonElement element = JsonParser.parseReader(br);
            if (element != null && element.isJsonObject()) {
                this.json = element.getAsJsonObject();
            }
        } catch (Exception e) {
            LOG.error("Failed to parse config json", e);
            this.json = new JsonObject(); // Fallback to avoid null pointers
        }

        // --- MODULE LOADING LOGIC STAYS EXACTLY THE SAME ---
        for (Module module : ModuleManager.get()) {
            JsonObject moduleObject = json.getAsJsonObject(module.name);
            if (moduleObject == null) continue;

            // 1. Load keybind first
            JsonObject keybindObj = moduleObject.getAsJsonObject("moduleKeybind");
            if (keybindObj != null) {
                loadKeybind(module.keybind, keybindObj);
            }

            // 2. Load settings FIRST so they exist in memory
            JsonObject settingsObject = moduleObject.getAsJsonObject("settings");
            if (settingsObject != null) {
                for (SettingGroup group : module.settings.getGroups()) {
                    for (Setting<?> setting : group.getSettings()) {
                        JsonElement settingElement = settingsObject.get(setting.name);
                        if (settingElement != null && !settingElement.isJsonNull()) {
                            applySetting(setting, settingElement);
                        }
                    }
                }
            }

            // 3. Enable/disable AFTER settings are loaded
            boolean active = moduleObject.has("active") && moduleObject.get("active").getAsBoolean();
            if (active) {
                module.enable();
            } else {
                module.disable();
            }
        }

        LOG.info("Config loaded");
    }

    public void saveAll() {
        File configFile = getFile();
        if (configFile == null) return;

        for (Module module : ModuleManager.get()) {
            updateModuleJson(module);
        }

        writeConfig(configFile);
    }

    public void saveModule(Module module) {
        File configFile = getFile();
        if (configFile == null) return;

        updateModuleJson(module);
        writeConfig(configFile);
    }

    // --- MODULE SAVING LOGIC STAYS EXACTLY THE SAME ---

    private void updateModuleJson(Module module) {
        JsonObject moduleObject = getModuleObject(module);

        // Active
        moduleObject.addProperty("active", module.active);

        // Module keybind
        moduleObject.add("moduleKeybind", serializeKeybind(module.keybind));

        // Settings
        JsonObject settingsObject = new JsonObject();

        for (SettingGroup group : module.settings.getGroups()) {
            for (Setting<?> setting : group.getSettings()) {
                Object val = setting.get();

                if (setting instanceof BoolSetting) {
                    settingsObject.addProperty(setting.name, (Boolean) val);
                } else if (setting instanceof IntSetting || setting instanceof DoubleSetting) {
                    settingsObject.addProperty(setting.name, (Number) val);
                } else if (setting instanceof EnumSetting<?>) {
                    settingsObject.addProperty(setting.name, ((Enum<?>) val).name());
                } else if (setting instanceof KeybindSetting) {
                    settingsObject.add(setting.name, serializeKeybind((Keybind) val));
                }
            }
        }

        moduleObject.add("settings", settingsObject);
    }

    private void applySetting(Setting<?> setting, JsonElement element) {
        try {
            if (setting instanceof BoolSetting s && element.isJsonPrimitive()) {
                s.set(element.getAsBoolean());
            } else if (setting instanceof IntSetting s && element.isJsonPrimitive()) {
                s.set(element.getAsInt());
            } else if (setting instanceof DoubleSetting s && element.isJsonPrimitive()) {
                s.set(element.getAsDouble());
            } else if (setting instanceof EnumSetting<?> s && element.isJsonPrimitive()) {
                s.setByName(element.getAsString());
            } else if (setting instanceof KeybindSetting s && element.isJsonObject()) {
                loadKeybind(s.get(), element.getAsJsonObject());
            }
        } catch (Exception e) {
            LOG.error("Failed to apply setting: " + setting.name, e);
        }
    }

    private void loadKeybind(Keybind keybind, JsonObject obj) {
        if (obj.has("isKey") && obj.has("value")) {
            int modifiers = obj.has("modifiers") ? obj.get("modifiers").getAsInt() : 0;
            keybind.set(
                    obj.get("isKey").getAsBoolean(),
                    obj.get("value").getAsInt(),
                    modifiers
            );
        }
    }

    private JsonObject serializeKeybind(Keybind bind) {
        JsonObject obj = new JsonObject();
        obj.addProperty("isKey", bind.isKey());
        obj.addProperty("value", bind.getValue());
        obj.addProperty("modifiers", 0);
        return obj;
    }

    private JsonObject getModuleObject(Module module) {
        if (json.has(module.name) && json.get(module.name).isJsonObject()) {
            return json.getAsJsonObject(module.name);
        }
        JsonObject obj = new JsonObject();
        json.add(module.name, obj);
        return obj;
    }

    // --- UTILITIES MOVED FROM EXAMPLE ---

    private void writeConfig(File file) {
        // Use BufferedWriter just like your example for safer/cleaner writes
        try (FileWriter fw = new FileWriter(file);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(GSON.toJson(json));
            LOG.info("Config saved");
        } catch (IOException e) {
            LOG.error("Failed to write config file", e);
        }
    }

    private File getFile() {
        File file = new File(FOLDER, CONFIG_NAME);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
                // Write empty json skeleton on creation
                try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write("{}");
                }
            }
            return file;
        } catch (IOException e) {
            LOG.error("Could not validate config file", e);
            return null;
        }
    }
}