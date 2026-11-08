package ruipokoim.neonclient.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.event.events.KeyEvent;
import ruipokoim.neonclient.utils.misc.input.KeyAction;

@Mixin({Keyboard.class})
public class KeyboardMixin {
    @Inject(method = {"onKey"}, at = {@At("HEAD")}, cancellable = true)
    private void onPress(long window, int action, KeyInput input, CallbackInfo ci) {
        if (input.key() == GLFW.GLFW_KEY_UNKNOWN) return;
        NeonMain.EVENT_BUS.post(KeyEvent.get(new KeyInput(input.key(), input.scancode(), input.modifiers()), KeyAction.get(action)));
    }
}