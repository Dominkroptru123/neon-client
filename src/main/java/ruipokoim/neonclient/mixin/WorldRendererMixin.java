package ruipokoim.neonclient.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ruipokoim.neonclient.mixininterface.IWorldRenderer;
import ruipokoim.neonclient.utils.render.postprocess.PostProcessShaders;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements IWorldRenderer {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(ObjectAllocator allocator,
                              RenderTickCounter tickCounter,
                              boolean renderBlockOutline,
                              Camera camera,
                              Matrix4f positionMatrix,
                              Matrix4f projectionMatrix,
                              Matrix4f matrix4f2,
                              GpuBufferSlice fog,
                              Vector4f fogColor,
                              boolean shouldRenderSky,
                              CallbackInfo ci) {
        PostProcessShaders.beginRender();
    }
    @Inject(method = "onResized", at = @At("HEAD"))
    private void onResized(int width, int height, CallbackInfo info) {
        PostProcessShaders.onResized(width, height);
    }
}
