package net.ctd.ctdmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.ctd.ctdmod.CTDMod;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public final class CTDKeyMappings {
    public static final String KEY_CATEGORY = "key.categories.ctdmod";

    // Example key mappings 1 key to trigger one action 
    public static final KeyMapping MEDITATE = new KeyMapping(
            "key.ctdmod.meditate",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            KEY_CATEGORY
    );
    public static final KeyMapping POWER_ACTION = new KeyMapping(
        "key.ctdmod.power_action",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_P,
        KEY_CATEGORY
    );

    private CTDKeyMappings() {
    }

    @EventBusSubscriber(modid = CTDMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class Registration {
        private Registration() {
        }

        @SubscribeEvent
        public static void register(RegisterKeyMappingsEvent event) {
            event.register(MEDITATE);
            event.register(POWER_ACTION);
        }
    }
}