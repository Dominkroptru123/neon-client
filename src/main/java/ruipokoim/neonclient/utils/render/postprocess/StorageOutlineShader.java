package ruipokoim.neonclient.utils.render.postprocess;

import ruipokoim.neonclient.modules.ModuleManager;
import ruipokoim.neonclient.modules.modules.ShaderESP;
import ruipokoim.neonclient.renderer.NeonRenderPipelines;
import ruipokoim.neonclient.renderer.NeonRenderer;

public class StorageOutlineShader extends PostProcessShader {
    private static ShaderESP storageESP;

    public StorageOutlineShader() {
        super(NeonRenderPipelines.POST_OUTLINE);
    }

    @Override
    protected boolean shouldDraw() {
        if (storageESP == null) storageESP = ModuleManager.getModule(ShaderESP.class);
        return storageESP.isActive();
    }

    @Override
    protected void setupPass(NeonRenderer renderer) {
        renderer.uniform("OutlineData", OutlineUniforms.write(
                storageESP.outlineWidth.get(),
                storageESP.fillOpacity.get() / 255.0f,
                storageESP.shapeMode.get().ordinal(),
                storageESP.glowMultiplier.get().floatValue()
        ));
    }
}