package ruipokoim.neonclient.modules;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.*;
import ruipokoim.neonclient.modules.modules.*;
import ruipokoim.neonclient.utils.misc.input.Input;
import ruipokoim.neonclient.utils.misc.input.KeyAction;
import ruipokoim.neonclient.utils.misc.input.Keybind;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static ruipokoim.neonclient.NeonMain.mc;
import static ruipokoim.neonclient.event.EventHandler.Priority.HIGH;
import static ruipokoim.neonclient.event.EventHandler.Priority.HIGHEST;

public class ModuleManager {
    private static final List<Category> CATEGORIES = new ArrayList<>();
    private static final List<Module> modules = new ArrayList<>();
    private static final Map<Category, List<Module>> groups = new Reference2ReferenceOpenHashMap<>();
    public static List<Module> active = new ArrayList<>();
    private Module moduleToBind;
    private boolean awaitingKeyRelease;
    public ModuleManager() {
        this.init();
        NeonMain.EVENT_BUS.subscribe(this);
    }
    public void init(){
        add(new CrystalOptimizer());
        add(new HoverTotem());
        add(new StorageESP());
        add(new AutoCrystal());
        add(new DoubleAnchor());
        add(new AnchorMacro());
        add(new SafeAnchor());
        add(new NoJumpDelay());
        add(new Fullbright());
        add(new ESP());
        add(new ShaderESP());
    }
    public static List<Module> get(){
        return modules;
    }
    public static <T extends Module> T getModule(Class<T> klass) {
        for (Module module : modules) {
            if (module.getClass() == klass) {
                return klass.cast(module);
            }
        }
        return null;
    }
    public void sortModules() {
        for (List<Module> modules : groups.values()) {
            modules.sort(Comparator.comparing(o -> o.title));
        }
    }
    public void add(Module module){
        this.modules.add(module);
        getGroup(module.category).add(module);
    }
    public static void registerCategory(Category category){
        CATEGORIES.add(category);
    }
    public static List<Category> getCategories(){
        return CATEGORIES;
    }
    public static List<Module> getGroup(Category category) {
        return groups.computeIfAbsent(category, category1 -> new ArrayList<>());
    }
    public void setModuleToBind(Module moduleToBind) {
        this.moduleToBind = moduleToBind;
    }
    public void awaitKeyRelease() {
        this.awaitingKeyRelease = true;
    }
    public boolean isBinding() {
        return moduleToBind != null;
    }
    @EventHandler(priority = HIGHEST)
    private void onKeyBinding(KeyEvent event) {
        if (event.action == KeyAction.Release && onBinding(true, event.key(), event.modifiers())) event.cancel();
    }
    @EventHandler(priority = HIGHEST)
    private void onButtonBinding(MouseClickEvent event) {
        if (event.action == KeyAction.Release && onBinding(false, event.button(), 0)) event.cancel();
    }
    private boolean onBinding(boolean isKey, int value, int modifiers) {
        if (!isBinding()) return false;
        if (awaitingKeyRelease) {
            if (!isKey || (value != GLFW.GLFW_KEY_ENTER && value != GLFW.GLFW_KEY_KP_ENTER)) return false;
            awaitingKeyRelease = false;
            return false;
        }
        if (moduleToBind.keybind.canBindTo(isKey, value, modifiers)) {
            moduleToBind.keybind.set(isKey, value, modifiers);
            moduleToBind.info(Text.literal("Bound to ").formatted(Formatting.GRAY).append(Text.literal(moduleToBind.keybind.toString()).formatted(Formatting.WHITE)));
        }
        else if (value == GLFW.GLFW_KEY_ESCAPE) {
            moduleToBind.keybind.set(Keybind.none());
            moduleToBind.info("Removed bind.");
        }
        else return false;
        NeonMain.EVENT_BUS.post(ModuleBindChangedEvent.get(moduleToBind));
        moduleToBind = null;
        return true;
    }
    @EventHandler(priority = HIGH)
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Repeat) return;
        onAction(true, event.key(), event.modifiers(), event.action == KeyAction.Press);
    }
    @EventHandler(priority = HIGH)
    private void onMouseClick(MouseClickEvent event) {
        if (event.action == KeyAction.Repeat) return;
        onAction(false, event.button(), 0, event.action == KeyAction.Press);
    }
    private void onAction(boolean isKey, int value, int modifiers, boolean isPress) {
        if (mc.currentScreen != null || Input.isKeyPressed(GLFW.GLFW_KEY_F3)) return;
        for (Module module : modules) {
            if (module.keybind.matches(isKey, value, modifiers) && (isPress || (module.toggleOnBindRelease && module.isActive()))) {
                module.toggle();
            }
        }
    }
    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        synchronized (active) {
            for (Module module : modules) {
                if (module.isActive()) {
                    NeonMain.EVENT_BUS.subscribe(module);
                    module.onActivate();
                }
            }
        }
    }
    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        synchronized (active) {
            for (Module module : modules) {
                if (module.isActive()) {
                    NeonMain.EVENT_BUS.unsubscribe(module);
                    module.onDeactivate();
                }
            }
        }
    }
    public void disableAll() {
        synchronized (active) {
            for (Module module : modules) {
                module.disable();
            }
        }
    }
}