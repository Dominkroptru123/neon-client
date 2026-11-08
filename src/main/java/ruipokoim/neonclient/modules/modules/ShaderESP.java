package ruipokoim.neonclient.modules.modules;

import net.minecraft.block.entity.*;
import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.Render3DEvent;
import ruipokoim.neonclient.modules.Categories;
import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.renderer.MeshBuilder;
import ruipokoim.neonclient.renderer.NeonRenderPipelines;
import ruipokoim.neonclient.renderer.NeonRenderer;
import ruipokoim.neonclient.renderer.ShapeMode;
import ruipokoim.neonclient.settings.*;
import ruipokoim.neonclient.utils.Utils;
import ruipokoim.neonclient.utils.render.MeshBuilderVertexConsumerProvider;
import ruipokoim.neonclient.utils.render.RenderUtils;
import ruipokoim.neonclient.utils.render.color.Color;
import ruipokoim.neonclient.utils.render.postprocess.PostProcessShaders;
import ruipokoim.neonclient.utils.render.postprocess.SimpleBlockRenderer;

public class ShaderESP extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> chests = sgGeneral.add(new BoolSetting.Builder()
            .name("chest")
            .description("Self-explained")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> enderChests = sgGeneral.add(new BoolSetting.Builder()
            .name("ender-chest")
            .description("Self-explained")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> shulkerBoxes = sgGeneral.add(new BoolSetting.Builder()
            .name("shulker-box")
            .description("Self-explained")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> barrels = sgGeneral.add(new BoolSetting.Builder()
            .name("barrel")
            .description("Self-explained")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> hoppers = sgGeneral.add(new BoolSetting.Builder()
            .name("hopper")
            .description("Self-explained")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> furnaces = sgGeneral.add(new BoolSetting.Builder()
            .name("furnace")
            .description("Self-explained")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> smokers = sgGeneral.add(new BoolSetting.Builder()
            .name("smoker")
            .description("Self-explained")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> crafters = sgGeneral.add(new BoolSetting.Builder()
            .name("crafter")
            .description("Self-explained")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> droppers = sgGeneral.add(new BoolSetting.Builder()
            .name("dropper")
            .description("Self-explained")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> dispensers = sgGeneral.add(new BoolSetting.Builder()
            .name("dispenser")
            .description("Self-explained")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> brewingStands = sgGeneral.add(new BoolSetting.Builder()
            .name("brewing-stand")
            .description("Self-explained")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> tracers = sgGeneral.add(new BoolSetting.Builder()
            .name("tracers")
            .description("Draws tracers")
            .defaultValue(false)
            .build()
    );
    public final Setting<Integer> outlineWidth = sgGeneral.add(new IntSetting.Builder()
            .name("width")
            .description("The width of the shader outline.")
            .defaultValue(1)
            .range(1, 10)
            .sliderRange(1, 5)
            .build()
    );
    public final Setting<Double> glowMultiplier = sgGeneral.add(new DoubleSetting.Builder()
            .name("glow-multiplier")
            .description("Multiplier for glow effect")
            .decimalPlaces(3)
            .defaultValue(3.5)
            .min(0)
            .sliderMax(10)
            .build()
    );
    public final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How blocks render")
            .defaultValue(ShapeMode.Both)
            .build()
    );
    public final Setting<Integer> fillOpacity = sgGeneral.add(new IntSetting.Builder()
            .name("fill-opacity")
            .description("The opacity of the shape fill")
            .visible(shapeMode.get() != ShapeMode.Lines)
            .defaultValue(50)
            .range(0, 255)
            .sliderMax(255)
            .build()
    );
    private final Color lineColor = new Color(0, 0, 0, 0);
    private final Color sideColor = new Color(0, 0, 0, 0);
    private boolean render;
    private int count;

    private final MeshBuilder mesh;
    private final MeshBuilderVertexConsumerProvider vertexConsumerProvider;
    public ShaderESP(){
        super(Categories.Render, "shader-esp", "");
        mesh = new MeshBuilder(NeonRenderPipelines.WORLD_COLORED);
        vertexConsumerProvider = new MeshBuilderVertexConsumerProvider(mesh);
    }
    private void getBlockEntityColor(BlockEntity blockEntity) {
        render = false;
        if (blockEntity instanceof TrappedChestBlockEntity) lineColor.set(new Color(255, 0, 0, 255));
        else if (blockEntity instanceof ChestBlockEntity) lineColor.set(new Color(255, 160, 0, 255));
        else if (blockEntity instanceof BarrelBlockEntity) lineColor.set(new Color(255, 160, 0, 255));
        else if (blockEntity instanceof ShulkerBoxBlockEntity) lineColor.set(new Color(255, 160, 0, 255));
        else if (blockEntity instanceof EnderChestBlockEntity) lineColor.set(new Color(120, 0, 255, 255));
        else if (blockEntity instanceof AbstractFurnaceBlockEntity || blockEntity instanceof BrewingStandBlockEntity || blockEntity instanceof CrafterBlockEntity || blockEntity instanceof DispenserBlockEntity || blockEntity instanceof DecoratedPotBlockEntity || blockEntity instanceof HopperBlockEntity) lineColor.set(new Color(140, 140, 140, 255));
        else return;
        render = true;
        if (shapeMode.get() == ShapeMode.Sides || shapeMode.get() == ShapeMode.Both) {
            sideColor.set(lineColor);
            sideColor.a = fillOpacity.get();
        }
    }
    @EventHandler
    private void onRender(Render3DEvent event) {
        count = 0;
        for (BlockEntity blockEntity : Utils.blockEntities()) {
            if (!(
                    (chests.get() && blockEntity instanceof ChestBlockEntity) ||
                            (enderChests.get() && blockEntity instanceof EnderChestBlockEntity) ||
                            (shulkerBoxes.get() && blockEntity instanceof ShulkerBoxBlockEntity) ||
                            (barrels.get() && blockEntity instanceof BarrelBlockEntity) ||
                            (hoppers.get() && blockEntity instanceof HopperBlockEntity) ||
                            (furnaces.get() && blockEntity instanceof AbstractFurnaceBlockEntity) ||
                            (smokers.get() && blockEntity instanceof SmokerBlockEntity) ||
                            (crafters.get() && blockEntity instanceof CrafterBlockEntity) ||
                            (droppers.get() && blockEntity instanceof DropperBlockEntity) ||
                            (dispensers.get() && blockEntity instanceof DispenserBlockEntity) ||
                            (brewingStands.get() && blockEntity instanceof BrewingStandBlockEntity)
            )) {
                continue;
            }
            getBlockEntityColor(blockEntity);
            if (render) {
                if (count == 0) {
                    mesh.begin();
                }

                int prevLineA = lineColor.a;
                int prevSideA = sideColor.a;

                if (tracers.get()) {
                    event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, blockEntity.getPos().getX() + 0.5, blockEntity.getPos().getY() + 0.5, blockEntity.getPos().getZ() + 0.5, lineColor);
                }
                renderShader(event, blockEntity);

                lineColor.a = prevLineA;
                sideColor.a = prevSideA;

                count++;
            }
        }

        if (count > 0) {
             NeonRenderer.begin()
                     .attachments(mc.getFramebuffer())
                     .pipeline(NeonRenderPipelines.WORLD_COLORED)
                     .mesh(mesh, event.matrices)
                     .end();


            PostProcessShaders.STORAGE_OUTLINE.render();
        }
    }
    private void renderShader(Render3DEvent event, BlockEntity blockEntity) {
        vertexConsumerProvider.setColor(lineColor);
        SimpleBlockRenderer.renderWithBlockEntity(blockEntity, event.tickDelta, vertexConsumerProvider);
    }
}
