package ruipokoim.neonclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ruipokoim.neonclient.config.Config;
import ruipokoim.neonclient.event.EventBus;
import ruipokoim.neonclient.event.EventHandler;
import ruipokoim.neonclient.event.events.KeyEvent;
import ruipokoim.neonclient.gui.ClickGUI;
import ruipokoim.neonclient.modules.Categories;
import ruipokoim.neonclient.modules.ModuleManager;
import ruipokoim.neonclient.renderer.fonts.CustomTextRenderer;
import ruipokoim.neonclient.renderer.fonts.TextRenderer;
import ruipokoim.neonclient.utils.Utils;
import ruipokoim.neonclient.utils.misc.input.KeyAction;
import ruipokoim.neonclient.utils.render.postprocess.PostProcessShaders;

import java.io.File;

public final class NeonMain implements ClientModInitializer {
    public static final String MOD_ID = "neon-client";
    public static final ModMetadata MOD_META;
    public static final String NAME;
    public static final String VERSION;

    public static NeonMain INSTANCE;
    public static MinecraftClient mc;

    public static final File FOLDER = FabricLoader.getInstance().getGameDir().resolve(MOD_ID).toFile();
    public static final Logger LOG;
    public static final EventBus EVENT_BUS = new EventBus();

    public static ModuleManager ModuleManager;
    public static Config Config;
    public static TextRenderer TEXT_RENDERER = TextRenderer.get();

    static {
        MOD_META = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata();
        NAME = MOD_META.getName();
        VERSION = "v1.0";
        LOG = LoggerFactory.getLogger(NAME);
    }

    @Override
    public void onInitializeClient() {
        try {
            if (INSTANCE == null) {
                INSTANCE = this;
            }
            mc = MinecraftClient.getInstance();

            if (!FOLDER.exists()) {
                FOLDER.getParentFile().mkdirs();
                FOLDER.mkdir();
            }

            // Initialize Modules & Categories
            ModuleManager = new ModuleManager();
            ModuleManager.sortModules();
            Categories.init();

            // Client Started Event
            ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
                PostProcessShaders.init();
                LOG.info("PostProcess shaders initialized.");
            });

            // Initialize Config & Events
            EVENT_BUS.subscribe(this);
            Config = new Config();
            Config.load();
            // Note: The JVM Shutdown hook was safely moved into Config.java's constructor.

            // Font Loader
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                @Override
                public Identifier getFabricId() {
                    return Identifier.of(MOD_ID, "font-loader");
                }

                @Override
                public void reload(ResourceManager manager) {
                    CustomTextRenderer.INSTANCE.setup();
                }
            });

            LOG.info("{} {} initialized successfully!", NAME, VERSION);

        } catch (Exception e) {
            // FIX: Never leave a catch block empty during initialization!
            // This will now print exactly what broke if your client fails to launch.
            LOG.error("Failed to initialize Neon Client!", e);
        }
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Press && event.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            toggleGui();
        }
    }

    private void toggleGui() {
        if (Utils.canCloseGui()) {
            mc.currentScreen.close();
        } else if (Utils.canOpenGui()) {
            mc.setScreen(new ClickGUI());
        }
    }

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
    }
}