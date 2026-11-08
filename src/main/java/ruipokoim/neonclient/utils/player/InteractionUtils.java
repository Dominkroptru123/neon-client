package ruipokoim.neonclient.utils.player;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;

import static ruipokoim.neonclient.NeonMain.mc;

public class InteractionUtils {
    public static void interact(BlockHitResult hitResult, boolean swing, Hand hand){
        boolean wasSneaking = mc.player.isSneaking();
        mc.player.setSneaking(false);
        ActionResult result = mc.interactionManager.interactBlock(mc.player, hand, hitResult);
        if (result.isAccepted()) {
            if (swing) mc.player.swingHand(hand);
            else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
        }
        mc.player.setSneaking(wasSneaking);
    }
    public static void interact(BlockHitResult hitResult, boolean swing){
        interact(hitResult, swing, Hand.MAIN_HAND);
    }
    public static void attack(EntityHitResult hitResult, boolean swing) {
        boolean wasSneaking = mc.player.isSneaking();
        mc.player.setSneaking(false);
        mc.interactionManager.attackEntity(mc.player, hitResult.getEntity());
        if (swing) mc.player.swingHand(Hand.MAIN_HAND);
        else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        mc.player.setSneaking(wasSneaking);
    }
}
