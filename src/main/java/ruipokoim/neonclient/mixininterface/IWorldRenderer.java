package ruipokoim.neonclient.mixininterface;

import net.minecraft.client.gl.Framebuffer;

public interface IWorldRenderer {
    void neon$pushEntityOutlineFramebuffer(Framebuffer framebuffer);

    void neon$popEntityOutlineFramebuffer();
}