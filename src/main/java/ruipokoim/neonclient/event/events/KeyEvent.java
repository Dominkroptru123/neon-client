package ruipokoim.neonclient.event.events;

import net.minecraft.client.input.KeyInput;
import ruipokoim.neonclient.event.Cancellable;
import ruipokoim.neonclient.utils.misc.input.KeyAction;

public class KeyEvent extends Cancellable {
    private static final KeyEvent INSTANCE = new KeyEvent();
    public KeyInput input;
    public KeyAction action;
    public static KeyEvent get(KeyInput input, KeyAction action) {
        INSTANCE.setCancelled(false);
        INSTANCE.input = input;
        INSTANCE.action = action;
        return INSTANCE;
    }
    public int key() {
        return INSTANCE.input.key();
    }
    public int modifiers() {
        return INSTANCE.input.modifiers();
    }
}