package ruipokoim.neonclient.modules.modules;

import net.minecraft.block.Blocks;
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

public class SafeAnchor extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private int steps = 0;
    private boolean isAnchor = false;
    private boolean isSneaking = false;
    public boolean doSneak(){
        return isSneaking;
    }
    private final Setting<Integer> anchorSlot = sgGeneral.add(new IntSetting.Builder()
            .name("anchor-slot")
            .description("Slot after anchor is fully charged")
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
                isSneaking = false;
                steps = 0;
            })
            .build()
    );
    public SafeAnchor(){
        super(Categories.Combat, "safe-anchor", "Self-explain");
    }
    @EventHandler
    private void onTick(TickEvent.Pre event){
        if(!isAnchor || mc.player == null || mc.world == null || mc.currentScreen != null) {isAnchor=false;return;}
        HitResult hitResult = mc.crosshairTarget;
        if(!(InvUtils.hotbarHas(Items.GLOWSTONE) && InvUtils.hotbarHas(Items.RESPAWN_ANCHOR))) return;
        if(steps == 0 && (hitResult instanceof BlockHitResult blockHitResult) && mc.world.getBlockState(blockHitResult.getBlockPos()).isAir()) isAnchor = false;
        if(!(hitResult instanceof BlockHitResult blockHitResult) || mc.world.getBlockState(blockHitResult.getBlockPos()).isAir()){
            isSneaking = false;
            isAnchor = false;
            return;
        }
        if(steps == 0 && mc.world.getBlockState(blockHitResult.getBlockPos()).isOf(Blocks.RESPAWN_ANCHOR)) steps = 2;
        switch(steps){
            case 0: InvUtils.hotbarSwitch(Items.RESPAWN_ANCHOR);break;
            case 1: InteractionUtils.interact(blockHitResult, swing.get());break;
            case 2: InvUtils.hotbarSwitch(Items.GLOWSTONE);break;
            case 3: InteractionUtils.interact(blockHitResult, swing.get());break;
            case 4: isSneaking = true;break;
            case 5: InteractionUtils.interact(blockHitResult, swing.get());break;
            case 6: InvUtils.hotbarSwitch(anchorSlot.get());isSneaking=false;isAnchor = false;steps = 0;return;
        }
        ++steps;
    }
}
