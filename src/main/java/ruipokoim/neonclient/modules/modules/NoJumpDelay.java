package ruipokoim.neonclient.modules.modules;

import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.TickEvent;
import ruipokoim.neonclient.mixin.LivingEntityAccessor;
import ruipokoim.neonclient.modules.Categories;
import ruipokoim.neonclient.modules.Module;

public class NoJumpDelay extends Module {
    public NoJumpDelay(){
        super(Categories.Movement, "no-jump-delay","Self-explain");
    }
    @EventHandler
    private void onTick(TickEvent.Post event){
        if(mc.player == null) return;
        ((LivingEntityAccessor) mc.player).setJumpingCooldown(0);
    }
}
