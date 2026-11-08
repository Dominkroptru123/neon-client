package ruipokoim.neonclient.modules.modules;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.TickEvent;
import ruipokoim.neonclient.modules.Categories;
import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.settings.*;
import ruipokoim.neonclient.utils.misc.input.Keybind;
import ruipokoim.neonclient.utils.player.InteractionUtils;
import ruipokoim.neonclient.utils.player.InvUtils;

public class AutoCrystal extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private boolean isCrystal = false;
    private boolean obsidianPlaced = false;
    private int tickTimer = 0;

    private final Setting<Integer> minTicksPerAction = sgGeneral.add(new IntSetting.Builder()
            .name("min-ticks-per-action")
            .description("Ticks to wait between each action (0 = fastest)")
            .defaultValue(1)
            .min(0)
            .max(20)
            .sliderMin(0)
            .sliderMax(20)
            .build()
    );

    private final Setting<Integer> maxTicksPerAction = sgGeneral.add(new IntSetting.Builder()
            .name("max-ticks-per-action")
            .description("Ticks to wait between each action (0 = fastest), must be higher or equals to min ticks, otherwise wont work")
            .defaultValue(1)
            .min(0)
            .max(20)
            .sliderMin(0)
            .sliderMax(20)
            .build()
    );

    private final Setting<Boolean> placesObsidian = sgGeneral.add(new BoolSetting.Builder()
            .name("place-obsidian")
            .description("Place obsidian if not looking at obsidian")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
            .name("swing")
            .description("Swings hand on interact")
            .defaultValue(true)
            .build()
    );

    private final Setting<Keybind> crystalkeybind = sgGeneral.add(new KeybindSetting.Builder()
            .name("keybind")
            .description("Hold the keybind to crystals")
            .actionStart(() -> {
                isCrystal = true;
                obsidianPlaced = false;
                tickTimer = 0; // Set to 0 to allow firing instantly on the first press
            })
            .actionEnd(() -> isCrystal = false)
            .build()
    );

    public AutoCrystal() {
        super(Categories.Combat, "auto-crystal", "Automatically crystals fast for you");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!isCrystal || mc.player == null || mc.world == null || mc.currentScreen instanceof Screen) return;

        // Process the action delay countdown
        if (tickTimer > 0) {
            tickTimer--;
            return;
        }

        boolean actionDone = false;
        HitResult hitResult = mc.crosshairTarget;

        if (hitResult instanceof EntityHitResult entityHit) {
            if (entityHit.getEntity() instanceof EndCrystalEntity crystal) {
                if (!(crystal.getBlockPos().getY() >= (mc.player.getEntityPos().getY()) + 1.0)) return;

                InteractionUtils.attack(entityHit, swing.get());
                actionDone = true;
            }
        }
        else if (hitResult instanceof BlockHitResult blockHit) {
            Block block = mc.world.getBlockState(blockHit.getBlockPos()).getBlock();

            if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
                if (!obsidianPlaced && blockHit.getBlockPos().getY() + 1.0 <= mc.player.getEntityPos().getY() && blockHit.getSide() == Direction.UP && mc.world.getBlockState(blockHit.getBlockPos().up()).isAir() && placesObsidian.get()) {
                    if (!InvUtils.isHolding(Items.OBSIDIAN)) {
                        InvUtils.hotbarSwitch(Items.OBSIDIAN);
                    } else {
                        InteractionUtils.interact(blockHit, swing.get());
                        obsidianPlaced = true;
                        actionDone = true;
                    }
                }
                else if (hasSpace(blockHit.getBlockPos()) && !(blockHit.getBlockPos().getY() + 1.0 <= mc.player.getEntityPos().getY())) {
                    if (InvUtils.isHolding(Items.END_CRYSTAL) || InvUtils.isOffHolding(Items.END_CRYSTAL)) {
                        InteractionUtils.interact(blockHit, swing.get());
                        actionDone = true;
                    } else {
                        InvUtils.hotbarSwitch(Items.END_CRYSTAL);
                    }
                }
            } else if (!mc.world.getBlockState(blockHit.getBlockPos()).isAir() && placesObsidian.get()) {
                if (!obsidianPlaced) {
                    if (!InvUtils.isHolding(Items.OBSIDIAN)) {
                        InvUtils.hotbarSwitch(Items.OBSIDIAN);
                    } else {
                        InteractionUtils.interact(blockHit, swing.get());
                        obsidianPlaced = true;
                        actionDone = true;
                    }
                }
            }
        }

        // Apply random delay using min and max if an action occurred this tick
        if (actionDone) {
            tickTimer = getRandomDelay();
        }
    }

    private boolean hasSpace(BlockPos pos) {
        if (!mc.world.isAir(pos.up())) return false;
        int x = pos.up().getX(), y = pos.up().getY(), z = pos.up().getZ();
        return mc.world.getOtherEntities(null, new Box(x, y, z, x + 1.0, y + 2.0, z + 1.0)).isEmpty();
    }

    // Helper method to calculate a random tick delay between min and max
    private int getRandomDelay() {
        int min = minTicksPerAction.get();
        int max = maxTicksPerAction.get();

        // Failsafe in case the user configures max lower than min
        if (min >= max) {
            return min;
        }

        // Return a random number between min and max (inclusive)
        return min + (int) (Math.random() * (max - min + 1));
    }
}