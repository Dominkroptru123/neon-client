package ruipokoim.neonclient.modules.modules;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.TickEvent;
import ruipokoim.neonclient.mixin.StatusEffectInstanceAccessor;
import ruipokoim.neonclient.modules.Categories;
import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.settings.EnumSetting;
import ruipokoim.neonclient.settings.Setting;
import ruipokoim.neonclient.settings.SettingGroup;

public class Fullbright extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("The mode to use for Fullbright.")
            .defaultValue(Mode.Gamma)
            .onChanged(mode -> {
                if (isActive()) {
                    if (mode != Mode.Potion) disableNightVision();
                    if (mc.worldRenderer != null) mc.worldRenderer.reload();
                }
            })
            .build()
    );

    public Fullbright() {
        super(Categories.Render, "fullbright", "Self-explain");
    }
    @Override
    public void onDeactivate() {
        if (mode.get() == Mode.Potion) disableNightVision();
    }
    public boolean getGamma() {
        return isActive() && mode.get() == Mode.Gamma;
    }
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || !mode.get().equals(Mode.Potion)) return;
        if (mc.player.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(StatusEffects.NIGHT_VISION.value()))) {
            StatusEffectInstance instance = mc.player.getStatusEffect(Registries.STATUS_EFFECT.getEntry(StatusEffects.NIGHT_VISION.value()));
            if (instance != null && instance.getDuration() < 420) ((StatusEffectInstanceAccessor) instance).neon$setDuration(420);
        }
        else{
            mc.player.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(StatusEffects.NIGHT_VISION.value()), 420, 0));
        }
    }
    private void disableNightVision() {
        if (mc.player == null) return;
        if (mc.player.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(StatusEffects.NIGHT_VISION.value()))) {
            mc.player.removeStatusEffect(Registries.STATUS_EFFECT.getEntry(StatusEffects.NIGHT_VISION.value()));
        }
    }

    public enum Mode {
        Gamma,
        Potion
    }
}
