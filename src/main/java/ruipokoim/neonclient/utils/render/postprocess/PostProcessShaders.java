package ruipokoim.neonclient.utils.render.postprocess;

import ruipokoim.neonclient.NeonMain;

import static ruipokoim.neonclient.NeonMain.mc;

public class PostProcessShaders {
    public static PostProcessShader STORAGE_OUTLINE;

    private PostProcessShaders() {}
    public static void init() {
        STORAGE_OUTLINE = new StorageOutlineShader();

        NeonMain.EVENT_BUS.subscribe(PostProcessShaders.class);
    }

    public static void beginRender() {
        if(STORAGE_OUTLINE != null){
        STORAGE_OUTLINE.clearTexture();}
    }

    public static void onResized(int width, int height) {
        if (mc == null) return;
        if(STORAGE_OUTLINE != null){
        STORAGE_OUTLINE.onResized(width, height);}
    }
}