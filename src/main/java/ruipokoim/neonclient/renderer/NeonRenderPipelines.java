package ruipokoim.neonclient.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import ruipokoim.neonclient.NeonMain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NeonRenderPipelines {
    private static final List<RenderPipeline> PIPELINES = new ArrayList<>();
    private static final RenderPipeline.Snippet MESH_UNIFORMS = RenderPipeline.builder()
            .withUniform("MeshData", UniformType.UNIFORM_BUFFER)
            .buildSnippet();

    public static final RenderPipeline WORLD_COLORED = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
            .withLocation(NeonMain.identifier("pipeline/world_colored"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES)
            .withVertexShader(Identifier.of("neon-client", "shaders/pos_color.vert"))
            .withFragmentShader(Identifier.of("neon-client", "shaders/pos_color.frag"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .build()
    );

    public static final RenderPipeline WORLD_COLORED_LINES = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
            .withLineSmooth()
            .withLocation(NeonMain.identifier("pipeline/world_colored_lines"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINES)
            .withVertexShader(Identifier.of("neon-client", "shaders/pos_color.vert"))
            .withFragmentShader(Identifier.of("neon-client", "shaders/pos_color.frag"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .build()
    );

    public static final RenderPipeline WORLD_COLORED_DEPTH = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
            .withLocation(NeonMain.identifier("pipeline/world_colored_depth"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES)
            .withVertexShader(Identifier.of("neon-client", "shaders/pos_color.vert"))
            .withFragmentShader(Identifier.of("neon-client", "shaders/pos_color.frag"))
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .withDepthWrite(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .build()
    );

    public static final RenderPipeline WORLD_COLORED_LINES_DEPTH = add(new ExtendedRenderPipelineBuilder(MESH_UNIFORMS)
            .withLineSmooth()
            .withLocation(NeonMain.identifier("pipeline/world_colored_lines_depth"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINES)
            .withVertexShader(Identifier.of("neon-client", "shaders/pos_color.vert"))
            .withFragmentShader(Identifier.of("neon-client", "shaders/pos_color.frag"))
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .withDepthWrite(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .build()
    );
    public static final RenderPipeline POST_OUTLINE = add(new ExtendedRenderPipelineBuilder()
            .withLocation(NeonMain.identifier("pipeline/post/outline"))
            .withVertexFormat(NeonVertexFormats.POS2, VertexFormat.DrawMode.TRIANGLES)
            .withVertexShader(NeonMain.identifier("shaders/post-process/base.vert"))
            .withFragmentShader(NeonMain.identifier("shaders/post-process/outline.frag"))
            .withSampler("u_Texture")
            .withUniform("PostData", UniformType.UNIFORM_BUFFER)
            .withUniform("OutlineData", UniformType.UNIFORM_BUFFER)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .build()
    );
    public static void precompile() {
        GpuDevice device = RenderSystem.getDevice();
        ResourceManager resources = MinecraftClient.getInstance().getResourceManager();
        for (RenderPipeline pipeline : PIPELINES) {
            device.precompilePipeline(pipeline, (identifier, shaderType) -> {
                var resource = resources.getResource(identifier).get();
                try (var in = resource.getInputStream()) {
                    return IOUtils.toString(in, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    private NeonRenderPipelines() {}
    private static RenderPipeline add(RenderPipeline pipeline) {
        PIPELINES.add(pipeline);
        return pipeline;
    }
}
