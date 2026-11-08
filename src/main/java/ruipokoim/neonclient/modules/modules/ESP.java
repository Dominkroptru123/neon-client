package ruipokoim.neonclient.modules.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.Render3DEvent;
import ruipokoim.neonclient.modules.Categories;
import ruipokoim.neonclient.modules.Module;
import ruipokoim.neonclient.renderer.ShapeMode;
import ruipokoim.neonclient.settings.*;
import ruipokoim.neonclient.utils.render.RenderUtils;
import ruipokoim.neonclient.utils.render.color.Color;

public class ESP extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<Boolean> players = sgGeneral.add(new BoolSetting.Builder()
            .name("players")
            .description("Render players")
            .defaultValue(true)
            .build()
    );
    public final Setting<Boolean> items = sgGeneral.add(new BoolSetting.Builder()
            .name("items")
            .description("Render dropped items")
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
            .description("How the shapes are rendered")
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
    public ESP(){
        super(Categories.Render, "esp", "Renders entities through wall");
    }
    private final Color lineColor = new Color();
    private final Color sideColor = new Color();
    @EventHandler
    private void onRender3D(Render3DEvent event) {
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity && players.get()) && !(entity instanceof ItemEntity && items.get())) continue;
            if (entity == mc.player) continue;
            drawBoundingBox(event, entity);
        }
    }
    private void drawBoundingBox(Render3DEvent event, Entity entity) {
        Color color = getColor(entity);
        if (color != null) {
            lineColor.set(color);
            sideColor.a = fillOpacity.get();
        }
        if (tracers.get()) {
            event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, entity.lastX + (entity.getX() - entity.lastX) * event.tickDelta, entity.lastY + (entity.getY() - entity.lastY) * event.tickDelta, entity.lastZ + (entity.getZ() - entity.lastZ) * event.tickDelta, lineColor);
        }
        double x = MathHelper.lerp(event.tickDelta, entity.lastRenderX, entity.getX()) - entity.getX();
        double y = MathHelper.lerp(event.tickDelta, entity.lastRenderY, entity.getY()) - entity.getY();
        double z = MathHelper.lerp(event.tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ();
        ShapeMode shape = shapeMode.get();
        Box box = entity.getBoundingBox();
        event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, sideColor, lineColor, shape, 0);
    }
    private Color getColor(Entity entity) {
        Color color = new Color();
        if(entity instanceof PlayerEntity){
            color.set(new Color(255, 255, 255));
        }
        else if(entity instanceof ItemEntity){
            color.set(new Color(175,175,175));
        }
        return color;
    }
}
