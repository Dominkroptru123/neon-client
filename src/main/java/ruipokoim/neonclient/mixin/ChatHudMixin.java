package ruipokoim.neonclient.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import ruipokoim.neonclient.mixininterface.IChatHud;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IChatHud {
    @Unique
    private int nextId;
    @Shadow
    public abstract void addMessage(Text message);

    public void neon$add(Text message, int id) {
        nextId = id;
        addMessage(message);
        nextId = 0;
    }
}