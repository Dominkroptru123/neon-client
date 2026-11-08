package ruipokoim.neonclient.modules.modules;


import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.TickEvent;
import ruipokoim.neonclient.mixin.HandledScreenAccessor;
import ruipokoim.neonclient.modules.Categories;
import ruipokoim.neonclient.settings.*;
import ruipokoim.neonclient.modules.Module;

public class HoverTotem extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> SwapDelay = sgGeneral.add(new IntSetting.Builder()
            .name("swap-tick-delay")
            .description("Ticks to wait to (20 ticks = 1 seconds)")
            .defaultValue(1)
            .min(0)
            .max(20)
            .sliderMin(0)
            .sliderMax(20)
            .build()
    );
    private final Setting<Boolean> IsHotbar = sgGeneral.add(new BoolSetting.Builder()
            .name("hotbar")
            .description("Switch totem to hotbar if offhanded")
            .defaultValue(true)
            .build()
    );
    private final Setting<Integer> HotbarSlot = sgGeneral.add(new IntSetting.Builder()
            .name("hotbar-slot")
            .description("Self-explain")
            .visible(IsHotbar.get())
            .defaultValue(9)
            .min(1)
            .max(9)
            .sliderMin(1)
            .sliderMax(9)
            .build()
    );
    private final Setting<Integer> HotbarDelay = sgGeneral.add(new IntSetting.Builder()
            .name("hotbar-tick-delay")
            .description("Ticks to wait to change (20 ticks = 1 seconds)")
            .visible(IsHotbar.get())
            .defaultValue(1)
            .min(0)
            .max(20)
            .sliderMin(0)
            .sliderMax(20)
            .build()
    );
    private final Setting<Boolean> Switch = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-switch-hotbar-totem")
            .description("Switches to totem slot when inventory is opened")
            .defaultValue(true)
            .build()
    );
    private int tick = SwapDelay.get();
    private int hotbartick = HotbarDelay.get();
    public HoverTotem() {
        super(Categories.Combat, "hover-totem", "Self-explain");
    }
    @EventHandler
    private void onTick(TickEvent.Pre event){
        if(mc.player == null || mc.world == null) return;
        if(mc.currentScreen instanceof InventoryScreen inventoryScreen){
            if(Switch.get()){
                mc.player.getInventory().setSelectedSlot(HotbarSlot.get()-1);
            }
            Slot hoveredSlot = ((HandledScreenAccessor) inventoryScreen).neon$getFocusedSlot();
            if (hoveredSlot != null && hoveredSlot.hasStack()) {
                int slot = hoveredSlot.getIndex();
                int hotbar = HotbarSlot.get()-1;
                if(slot >= 9 && slot <= 35 && hoveredSlot.getStack().isOf(Items.TOTEM_OF_UNDYING)){
                    if(!mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)){
                        if(tick <= 0){
                            mc.interactionManager.clickSlot(inventoryScreen.getScreenHandler().syncId, slot, 40, SlotActionType.SWAP, mc.player);
                            tick = SwapDelay.get();
                        }
                        else{
                            tick = Math.min(tick, SwapDelay.get());
                            --tick;
                        }
                    }
                    else if(IsHotbar.get() && mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) && !mc.player.getInventory().getStack(hotbar).isOf(Items.TOTEM_OF_UNDYING)){
                        if(hotbartick <= 0){
                            mc.interactionManager.clickSlot(inventoryScreen.getScreenHandler().syncId, slot, hotbar, SlotActionType.SWAP, mc.player);
                            hotbartick = HotbarDelay.get();
                        }
                        else{
                            hotbartick = Math.min(hotbartick, HotbarDelay.get());
                            --hotbartick;
                        }
                    }
                }
            }
        }
    }
}