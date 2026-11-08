package ruipokoim.neonclient.mixin;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ruipokoim.neonclient.modules.ModuleManager;
import ruipokoim.neonclient.modules.modules.SafeAnchor;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
    @Inject(method = "tick", at = @At("TAIL"))
    private void isPressed(CallbackInfo ci) {
        if(ModuleManager.getModule(SafeAnchor.class).doSneak()){
            playerInput = new PlayerInput(
                    playerInput.forward(),
                    playerInput.backward(),
                    playerInput.left(),
                    playerInput.right(),
                    playerInput.jump(),
                    true,
                    playerInput.sprint()
            );
        }
    }
}