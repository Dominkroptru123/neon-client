package ruipokoim.neonclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.mixininterface.IChatHud;
import ruipokoim.neonclient.settings.Settings;
import ruipokoim.neonclient.utils.Utils;
import ruipokoim.neonclient.utils.misc.ISerializable;
import ruipokoim.neonclient.utils.misc.input.Keybind;

import static ruipokoim.neonclient.NeonMain.Config;

public abstract class Module implements ISerializable {
    protected final MinecraftClient mc;
    public final String name;
    public final String title;
    public final String description;
    public final Settings settings = new Settings();
    public boolean active;
    public final Keybind keybind = Keybind.none();
    public boolean toggleOnBindRelease = false;
    public boolean chatFeedback = true;
    public final Category category;
    public Module(Category category, String name, String description) {
        this.mc = MinecraftClient.getInstance();
        this.category = category;
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.description = description;
    }
    public void toggle(){
        if(!active){
            active = true;
            ModuleManager.active.add(this);
            settings.onActivated();
            NeonMain.EVENT_BUS.subscribe(this);
            onActivate();
        }
        else{
            NeonMain.EVENT_BUS.unsubscribe(this);
            onDeactivate();
            active = false;
            ModuleManager.active.remove(this);
        }
    }
    public void onActivate(){}
    public void onDeactivate(){}
    public void enable() {
        if (!isActive()) toggle();
    }
    public void disable() {
        if (isActive()) toggle();
    }
    public boolean isActive(){
        return this.active;
    }
    public int getKeybind(){
        return keybind.getValue();
    }
    public void info(Text message) {
        if (mc.world == null) return;
        Text customMessage = Text.literal("[").formatted(Formatting.GRAY)
                .append(Text.literal("Neon").formatted(Formatting.BLUE))
                .append(Text.literal("] ").formatted(Formatting.GRAY))
                .append(Text.literal("[").formatted(Formatting.GRAY))
                .append(Text.literal(title).formatted(Formatting.AQUA))
                .append(Text.literal("] ").formatted(Formatting.GRAY))
                .append(message);
        mc.execute(() -> ((IChatHud) mc.inGameHud.getChatHud()).neon$add(customMessage, 0));
    }
    public void info(String message) {
        if (mc.world == null) return;
        Text customMessage = Text.literal("[").formatted(Formatting.GRAY)
                .append(Text.literal("Neon").formatted(Formatting.BLUE))
                .append(Text.literal("] ").formatted(Formatting.GRAY))
                .append(Text.literal("[").formatted(Formatting.GRAY))
                .append(Text.literal(title).formatted(Formatting.AQUA))
                .append(Text.literal("] ").formatted(Formatting.GRAY))
                .append(Text.literal(message).formatted(Formatting.GRAY));
        mc.execute(() -> ((IChatHud) mc.inGameHud.getChatHud()).neon$add(customMessage, 0));
    }
}
