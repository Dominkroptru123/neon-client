package ruipokoim.neonclient.event.events;

import net.minecraft.client.util.math.MatrixStack;

public class RenderEvent {
    public final MatrixStack matrices;
    public RenderEvent(MatrixStack matrices) { this.matrices = matrices; }
}