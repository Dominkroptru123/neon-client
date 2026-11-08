package ruipokoim.neonclient.utils;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.lang3.StringUtils;
import ruipokoim.neonclient.gui.ClickGUI;
import ruipokoim.neonclient.utils.world.WorldUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.lwjgl.glfw.GLFW.*;
import static ruipokoim.neonclient.NeonMain.mc;

public class Utils {
    public static List<WorldChunk> chunks(){
        return WorldUtils.getLoadedChunks();
    }
    public static List<BlockEntity> blockEntities(){
        return WorldUtils.getLoadedBlockEntities();
    }
    public static String nameToTitle(String name) {
        return Arrays.stream(name.split("-")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }
    public static String getButtonName(int button) {
        return switch (button) {
            case -1 -> "Unknown";
            case 0 -> "Mouse Left";
            case 1 -> "Mouse Right";
            case 2 -> "Mouse Middle";
            default -> "Mouse " + button;
        };
    }
    public static String getKeyName(int key) {
        return switch (key) {
            case GLFW_KEY_UNKNOWN -> "Unknown";
            case GLFW_KEY_ESCAPE -> "Esc";
            case GLFW_KEY_GRAVE_ACCENT -> "Grave Accent";
            case GLFW_KEY_WORLD_1 -> "World 1";
            case GLFW_KEY_WORLD_2 -> "World 2";
            case GLFW_KEY_PRINT_SCREEN -> "Print Screen";
            case GLFW_KEY_PAUSE -> "Pause";
            case GLFW_KEY_INSERT -> "Insert";
            case GLFW_KEY_DELETE -> "Delete";
            case GLFW_KEY_HOME -> "Home";
            case GLFW_KEY_PAGE_UP -> "Page Up";
            case GLFW_KEY_PAGE_DOWN -> "Page Down";
            case GLFW_KEY_END -> "End";
            case GLFW_KEY_TAB -> "Tab";
            case GLFW_KEY_LEFT_CONTROL -> "Left Control";
            case GLFW_KEY_RIGHT_CONTROL -> "Right Control";
            case GLFW_KEY_LEFT_ALT -> "Left Alt";
            case GLFW_KEY_RIGHT_ALT -> "Right Alt";
            case GLFW_KEY_LEFT_SHIFT -> "Left Shift";
            case GLFW_KEY_RIGHT_SHIFT -> "Right Shift";
            case GLFW_KEY_UP -> "Arrow Up";
            case GLFW_KEY_DOWN -> "Arrow Down";
            case GLFW_KEY_LEFT -> "Arrow Left";
            case GLFW_KEY_RIGHT -> "Arrow Right";
            case GLFW_KEY_APOSTROPHE -> "Apostrophe";
            case GLFW_KEY_BACKSPACE -> "Backspace";
            case GLFW_KEY_CAPS_LOCK -> "Caps Lock";
            case GLFW_KEY_MENU -> "Menu";
            case GLFW_KEY_LEFT_SUPER -> "Left Super";
            case GLFW_KEY_RIGHT_SUPER -> "Right Super";
            case GLFW_KEY_ENTER -> "Enter";
            case GLFW_KEY_KP_ENTER -> "Numpad Enter";
            case GLFW_KEY_NUM_LOCK -> "Num Lock";
            case GLFW_KEY_SCROLL_LOCK -> "Scroll Lock";
            case GLFW_KEY_SPACE -> "Space";
            case GLFW_KEY_F1 -> "F1";
            case GLFW_KEY_F2 -> "F2";
            case GLFW_KEY_F3 -> "F3";
            case GLFW_KEY_F4 -> "F4";
            case GLFW_KEY_F5 -> "F5";
            case GLFW_KEY_F6 -> "F6";
            case GLFW_KEY_F7 -> "F7";
            case GLFW_KEY_F8 -> "F8";
            case GLFW_KEY_F9 -> "F9";
            case GLFW_KEY_F10 -> "F10";
            case GLFW_KEY_F11 -> "F11";
            case GLFW_KEY_F12 -> "F12";
            case GLFW_KEY_F13 -> "F13";
            case GLFW_KEY_F14 -> "F14";
            case GLFW_KEY_F15 -> "F15";
            case GLFW_KEY_F16 -> "F16";
            case GLFW_KEY_F17 -> "F17";
            case GLFW_KEY_F18 -> "F18";
            case GLFW_KEY_F19 -> "F19";
            case GLFW_KEY_F20 -> "F20";
            case GLFW_KEY_F21 -> "F21";
            case GLFW_KEY_F22 -> "F22";
            case GLFW_KEY_F23 -> "F23";
            case GLFW_KEY_F24 -> "F24";
            case GLFW_KEY_F25 -> "F25";
            default -> {
                String keyName = glfwGetKeyName(key, 0);
                yield keyName == null ? "Unknown" : StringUtils.capitalize(keyName);
            }
        };
    }
    public static boolean canUpdate() {
        return mc != null && mc.world != null && mc.player != null;
    }
    public static boolean canOpenGui() {
        if (canUpdate()) return mc.currentScreen == null;
        return mc.currentScreen instanceof TitleScreen || mc.currentScreen instanceof MultiplayerScreen || mc.currentScreen instanceof SelectWorldScreen;
    }
    public static boolean canCloseGui() {
        return mc.currentScreen instanceof ClickGUI;
    }
}
