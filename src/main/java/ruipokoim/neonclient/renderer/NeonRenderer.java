package ruipokoim.neonclient.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Pair;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import ruipokoim.neonclient.utils.render.RenderUtils;
import ruipokoim.neonclient.utils.render.color.Color;

import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static ruipokoim.neonclient.NeonMain.mc;

public class NeonRenderer {
    private static final NeonRenderer INSTANCE = new NeonRenderer();

    private static boolean taken;
    public Matrix4f projection;
    private GpuTextureView colorAttachment;
    private GpuTextureView depthAttachment;
    private Color clearColor;
    private RenderPipeline pipeline;
    private @Nullable MeshBuilder mesh;
    private @Nullable GpuBuffer vertexBuffer;
    private @Nullable GpuBuffer indexBuffer;
    private Matrix4f matrix;
    private final HashMap<String, GpuBufferSlice> uniforms = new HashMap<>();
    private final HashMap<String, Pair<GpuTextureView, GpuSampler>> samplers = new HashMap<>();

    private NeonRenderer() {}

    public static NeonRenderer begin() {
        if (taken)
            throw new IllegalStateException("Previous instance of MeshRenderer was not ended");

        taken = true;
        return INSTANCE;
    }

    public NeonRenderer attachments(GpuTextureView color, GpuTextureView depth) {
        colorAttachment = color;
        depthAttachment = depth;
        return this;
    }

    public NeonRenderer attachments(Framebuffer framebuffer) {
        colorAttachment = framebuffer.getColorAttachmentView();
        depthAttachment = framebuffer.getDepthAttachmentView();
        return this;
    }

    public NeonRenderer clearColor(Color color) {
        clearColor = color;
        return this;
    }

    public NeonRenderer pipeline(RenderPipeline pipeline) {
        this.pipeline = pipeline;
        return this;
    }

    public NeonRenderer mesh(GpuBuffer vertices, GpuBuffer indices) {
        this.vertexBuffer = vertices;
        this.indexBuffer = indices;
        return this;
    }

    public NeonRenderer mesh(MeshBuilder mesh) {
        this.mesh = mesh;
        return this;
    }

    public NeonRenderer mesh(MeshBuilder mesh, Matrix4f matrix) {
        this.mesh = mesh;
        return this.transform(matrix);
    }

    public NeonRenderer mesh(MeshBuilder mesh, MatrixStack matrices) {
        this.mesh = mesh;
        return this.transform(matrices);
    }

    public NeonRenderer transform(Matrix4f matrix) {
        this.matrix = matrix;
        return this;
    }

    public NeonRenderer transform(MatrixStack matrices) {
        this.matrix = matrices.peek().getPositionMatrix();
        return this;
    }

    public NeonRenderer fullscreen() {
        return this.mesh(FullScreenRenderer.vbo, FullScreenRenderer.ibo);
    }

    public NeonRenderer uniform(String name, GpuBufferSlice slice) {
        uniforms.put(name, slice);
        return this;
    }

    public NeonRenderer sampler(String name, GpuTextureView view, GpuSampler sampler) {
        if (name != null && view != null && sampler != null) {
            samplers.put(name, new Pair<>(view, sampler));
        }

        return this;
    }

    public void end() {
        if (mesh != null && mesh.isBuilding()) {
            mesh.end();
        }

        int indexCount = mesh != null ? mesh.getIndicesCount()
                : (int) (indexBuffer != null ? indexBuffer.size() / Integer.BYTES : -1);

        if (indexCount > 0) {

            if (matrix != null) {
                RenderSystem.getModelViewStack().pushMatrix();
            }

            if (matrix != null) {
                RenderSystem.getModelViewStack().mul(matrix);
            }

            applyCameraPos();

            GpuBuffer vertexBuffer = mesh != null ? mesh.getVertexBuffer() : this.vertexBuffer;
            GpuBuffer indexBuffer = mesh != null ? mesh.getIndexBuffer() : this.indexBuffer;

            {
                OptionalInt clearColor = this.clearColor != null ?
                        OptionalInt.of(ColorHelper.getArgb(this.clearColor.a, this.clearColor.r, this.clearColor.g, this.clearColor.b)) :
                        OptionalInt.empty();

                GpuBufferSlice meshData = MeshUniforms.write(RenderUtils.projection, RenderSystem.getModelViewStack());

                RenderPass pass = (depthAttachment != null && pipeline.wantsDepthTexture()) ?
                        RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Neon MeshRenderer", colorAttachment, clearColor, depthAttachment, OptionalDouble.empty()) :
                        RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Neon MeshRenderer", colorAttachment, clearColor);

                pass.setPipeline(pipeline);
                pass.setUniform("MeshData", meshData);

                for (var name : uniforms.keySet()) {
                    pass.setUniform(name, uniforms.get(name));
                }

                for (var name : samplers.keySet()) {
                    pass.bindTexture(name, samplers.get(name).getLeft(), samplers.get(name).getRight());
                }

                pass.setVertexBuffer(0, vertexBuffer);
                pass.setIndexBuffer(indexBuffer, VertexFormat.IndexType.INT);
                pass.drawIndexed(0, 0, indexCount, 1);

                pass.close();
            }

            if (matrix != null) {
                RenderSystem.getModelViewStack().popMatrix();
            }
        }

        colorAttachment = null;
        depthAttachment = null;
        clearColor = null;
        pipeline = null;
        mesh = null;
        vertexBuffer = null;
        indexBuffer = null;
        matrix = null;
        uniforms.clear();
        samplers.clear();

        taken = false;
    }

    private static void applyCameraPos() {
        Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
        RenderSystem.getModelViewStack().translate(0, (float) -cameraPos.y, 0);
    }
}