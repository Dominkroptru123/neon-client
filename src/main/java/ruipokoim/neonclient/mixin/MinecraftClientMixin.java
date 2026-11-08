package ruipokoim.neonclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.event.events.GameLeftEvent;
import ruipokoim.neonclient.event.events.TickEvent;
import ruipokoim.neonclient.mixininterface.IMinecraftClient;

@Mixin(value = MinecraftClient.class, priority = 1001)
public abstract class MinecraftClientMixin implements IMinecraftClient {
    @Shadow public ClientWorld world;
    @Shadow
    @Final
    @Mutable
    private Framebuffer framebuffer;
    @Inject(at = @At("HEAD"), method = "tick")
    private void onPreTick(CallbackInfo info) {
        NeonMain.EVENT_BUS.post(TickEvent.Pre.get());
    }
    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo info) {
        NeonMain.EVENT_BUS.post(TickEvent.Post.get());
    }
    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;ZZ)V", at = @At("HEAD"))
    private void onDisconnect(Screen screen, boolean transferring, boolean stopSound, CallbackInfo info) {
        if (world != null) {
            NeonMain.EVENT_BUS.post(GameLeftEvent.get());
        }
    }

    @Override
    public void neon$setFramebuffer(Framebuffer framebuffer) {
        this.framebuffer = framebuffer;
    }
}