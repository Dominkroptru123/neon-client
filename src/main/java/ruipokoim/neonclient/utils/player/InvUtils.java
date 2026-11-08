package ruipokoim.neonclient.utils.player;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import static ruipokoim.neonclient.NeonMain.mc;

public class InvUtils{
    public static DefaultedList<ItemStack> getInventory(ClientPlayerEntity player) {
        DefaultedList<ItemStack> list = DefaultedList.of();
        for (int i = 0; i < 36; i++) {
            list.add(player.getInventory().getStack(i));
        }
        return list;
    }
    public static void hotbarSwitch(Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(item)) {
                mc.player.getInventory().setSelectedSlot(i);
                break;
            }
        }
    }
    public static void hotbarSwitch(int pos) {
        mc.player.getInventory().setSelectedSlot(pos - 1);
    }
    public static boolean hotbarHas(Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(item)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isHolding(Item item){
        return mc.player.getMainHandStack().getItem() == item;
    }
    public static boolean isOffHolding(Item item){
        return mc.player.getOffHandStack().getItem() == item;
    }
}