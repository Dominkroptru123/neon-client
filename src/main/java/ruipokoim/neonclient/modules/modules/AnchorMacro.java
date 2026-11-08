package ruipokoim.neonclient.modules.modules;

import net.minecraft.block.BlockKeys;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.TickEvent;
import ruipokoim.neonclient.modules.Categories;
import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.settings.*;
import ruipokoim.neonclient.utils.misc.input.Keybind;
import ruipokoim.neonclient.utils.player.InteractionUtils;
import ruipokoim.neonclient.utils.player.InvUtils;

public class AnchorMacro extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private int steps = 0;
    private boolean isAnchor = false;
    private final Setting<Integer> totemSlot = sgGeneral.add(new IntSetting.Builder()
            .name("totem-slot")
            .description("Self-explain")
            .defaultValue(9)
            .min(1)
            .max(9)
            .sliderMin(1)
            .sliderMax(9)
            .build()
    );
    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
            .name("swing")
            .description("Swings hand on interact")
            .defaultValue(true)
            .build()
    );
    private final Setting<Keybind> anchorkeybind = sgGeneral.add(new KeybindSetting.Builder()
            .name("keybind")
            .description("Press the keybind to activate")
            .actionStart(() -> {
                isAnchor = true;
                steps = 0;
            })
            .build()
    );
    public AnchorMacro(){
        super(Categories.Combat, "anchor-macro", "Self-explain");
    }
    @EventHandler
    private void onTick(TickEvent.Pre event){
        if(!isAnchor || mc.player == null || mc.world == null || mc.currentScreen != null) {isAnchor=false;return;}
        HitResult hitResult = mc.crosshairTarget;
        if(!(InvUtils.hotbarHas(Items.GLOWSTONE) && InvUtils.hotbarHas(Items.RESPAWN_ANCHOR))) return;
        if(steps == 0 && (hitResult instanceof BlockHitResult blockHitResult) && mc.world.getBlockState(blockHitResult.getBlockPos()).isAir()) isAnchor = false;
        if(!(hitResult instanceof BlockHitResult blockHitResult) || mc.world.getBlockState(blockHitResult.getBlockPos()).isAir()){
            isAnchor = false;
            return;
        }
        if(steps == 0 && mc.world.getBlockState(blockHitResult.getBlockPos()).isOf(Blocks.RESPAWN_ANCHOR)) steps = 2;
        if(steps == 0 && mc.world.getBlockState(blockHitResult.getBlockPos()).isOf(Blocks.RESPAWN_ANCHOR) && mc.world.getBlockState(blockHitResult.getBlockPos()).get(RespawnAnchorBlock.CHARGES) >= 1) steps = 4;
        switch(steps){
            case 0: InvUtils.hotbarSwitch(Items.RESPAWN_ANCHOR);break;
            case 1: InteractionUtils.interact(blockHitResult, swing.get());break;
            case 2: InvUtils.hotbarSwitch(Items.GLOWSTONE);break;
            case 3: InteractionUtils.interact(blockHitResult, swing.get());break;
            case 4: InvUtils.hotbarSwitch(totemSlot.get());break;
            case 5: InteractionUtils.interact(blockHitResult, swing.get());break;
            case 6: isAnchor = false;steps = 0;return;
        }
        ++steps;
    }
}
