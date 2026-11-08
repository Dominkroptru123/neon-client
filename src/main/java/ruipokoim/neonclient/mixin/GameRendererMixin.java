package ruipokoim.neonclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ruipokoim.neonclient.NeonMain;
import ruipokoim.neonclient.event.events.Render3DEvent;
import ruipokoim.neonclient.renderer.NeonRenderPipelines;
import ruipokoim.neonclient.renderer.Renderer3D;
import ruipokoim.neonclient.utils.render.RenderUtils;

@Mixin({GameRenderer.class})
public abstract class GameRendererMixin {
    @Unique
    private final MatrixStack matrices = new MatrixStack();
    @Unique
    private Renderer3D renderer;

    @Unique
    private Renderer3D depthRenderer;
    @Shadow @Final private Camera camera;
    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=hand"}))
    private void onRenderWorld(RenderTickCounter rtc, CallbackInfo ci, @Local(ordinal = 0) Matrix4f projection, @Local(ordinal = 1) Matrix4f position, @Local(ordinal = 0) float tickDelta, @Local MatrixStack matrixStack) {
        if (renderer == null) renderer = new Renderer3D(NeonRenderPipelines.WORLD_COLORED_LINES, NeonRenderPipelines.WORLD_COLORED);
        if (depthRenderer == null) depthRenderer = new Renderer3D(NeonRenderPipelines.WORLD_COLORED_LINES_DEPTH, NeonRenderPipelines.WORLD_COLORED_DEPTH);
        Render3DEvent event = Render3DEvent.get(matrixStack, renderer, depthRenderer, tickDelta, camera.getCameraPos().x, camera.getCameraPos().y, camera.getCameraPos().z);
        RenderSystem.getModelViewStack().pushMatrix().mul(position);
        matrices.push();
        Matrix4f inverseBob = new Matrix4f(matrices.peek().getPositionMatrix()).invert();
        RenderSystem.getModelViewStack().mul(inverseBob);
        matrices.pop();
        Matrix4f correctedPosition = position;
        RenderUtils.updateScreenCenter(projection, correctedPosition);
        renderer.begin();
        depthRenderer.begin();
        NeonMain.EVENT_BUS.post(event);
        renderer.render(matrixStack);
        depthRenderer.render(matrixStack);
        RenderSystem.getModelViewStack().popMatrix();
    }
}