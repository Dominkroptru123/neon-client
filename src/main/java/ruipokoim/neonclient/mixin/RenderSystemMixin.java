package ruipokoim.neonclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ruipokoim.neonclient.renderer.MeshUniforms;
import ruipokoim.neonclient.utils.render.postprocess.OutlineUniforms;
import ruipokoim.neonclient.utils.render.postprocess.PostProcessShader;

import static ruipokoim.neonclient.NeonMain.ModuleManager;
import static ruipokoim.neonclient.NeonMain.mc;

@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin {
    @Inject(method = "flipFrame", at = @At("TAIL"))
    private static void neon$flipFrame(CallbackInfo info) {
        MeshUniforms.flipFrame();
        PostProcessShader.flipFrame();
        OutlineUniforms.flipFrame();

        if (ModuleManager.get() == null || mc.player == null) return;
    }
}