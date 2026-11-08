package ruipokoim.neonclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.event.events.MouseClickEvent;
import ruipokoim.neonclient.event.events.MouseScrollEvent;
import ruipokoim.neonclient.utils.misc.input.Input;
import ruipokoim.neonclient.utils.misc.input.KeyAction;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow
    public abstract double getScaledX(Window window);

    @Shadow
    public abstract double getScaledY(Window window);

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, MouseInput input, int action, CallbackInfo ci) {
        Input.setButtonState(input.button(), action != GLFW_RELEASE);
        Click click = new Click(getScaledX(client.getWindow()), getScaledY(client.getWindow()), input);
        NeonMain.EVENT_BUS.post(MouseClickEvent.get(click, KeyAction.get(action)));
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
        NeonMain.EVENT_BUS.post(MouseScrollEvent.get(vertical));
    }
}