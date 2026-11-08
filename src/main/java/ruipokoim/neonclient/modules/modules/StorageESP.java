package ruipokoim.neonclient.modules.modules;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.Render3DEvent;
import ruipokoim.neonclient.modules.Categories;
import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.renderer.*;
import ruipokoim.neonclient.settings.*;
import ruipokoim.neonclient.utils.Utils;
import ruipokoim.neonclient.utils.render.RenderUtils;
import ruipokoim.neonclient.utils.render.color.Color;

public class StorageESP extends Module{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Color lineColor = new Color(0, 0, 0, 0);
    private final Color sideColor = new Color(0, 0, 0, 0);
    private boolean render;
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
    public StorageESP() {
        super(Categories.Render, "storage-esp", "Renders storage blocks");
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
    private void onRender(Render3DEvent event){
        for (BlockEntity blockEntity : Utils.blockEntities()) {
            getBlockEntityColor(blockEntity);
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
            if(render){
                if (tracers.get()) {
                    event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, blockEntity.getPos().getX() + 0.5, blockEntity.getPos().getY() + 0.5, blockEntity.getPos().getZ() + 0.5, lineColor);
                }
                renderBox(event, blockEntity);
            }
        }
    }
    private void renderBox(Render3DEvent event, BlockEntity blockEntity) {
        double x1 = blockEntity.getPos().getX();
        double y1 = blockEntity.getPos().getY();
        double z1 = blockEntity.getPos().getZ();

        double x2 = blockEntity.getPos().getX() + 1;
        double y2 = blockEntity.getPos().getY() + 1;
        double z2 = blockEntity.getPos().getZ() + 1;

        int excludeDir = 0;
        if (blockEntity instanceof ChestBlockEntity) {
            BlockState state = mc.world.getBlockState(blockEntity.getPos());
            if ((state.getBlock() == Blocks.CHEST || state.getBlock() == Blocks.TRAPPED_CHEST) && state.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                excludeDir = Dir.get(ChestBlock.getFacing(state));
            }
        }

        if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof EnderChestBlockEntity) {
            double a = 1.0 / 16.0;

            if (Dir.isNot(excludeDir, Dir.WEST)) x1 += a;
            if (Dir.isNot(excludeDir, Dir.NORTH)) z1 += a;

            if (Dir.isNot(excludeDir, Dir.EAST)) x2 -= a;
            y2 -= a * 2;
            if (Dir.isNot(excludeDir, Dir.SOUTH)) z2 -= a;
        }

        event.renderer.box(x1, y1, z1, x2, y2, z2, sideColor, lineColor, shapeMode.get(), excludeDir);
    }
   public boolean isShader() {
        return false;
    }
}
