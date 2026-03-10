package net.ctd.ctdmod.network;

import net.ctd.ctdmod.CTDMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Enregistrement des paquets réseau personnalisés du mod.
 */
@EventBusSubscriber(modid = CTDMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class CTDNetworking {

    private CTDNetworking() {
        // Classe utilitaire : pas d'instanciation.
    }

    /**
     * Enregistre les handlers de {@link net.minecraft.network.protocol.common.custom.CustomPacketPayload}.
     *
     * @param event évènement d'enregistrement fourni par NeoForge
     */
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(CTDMod.MODID);

        registrar.playToClient(
            SyncCultivationPayload.TYPE,
            SyncCultivationPayload.STREAM_CODEC,
            SyncCultivationPayload.Handler::handle
        );

        registrar.playToServer(
            MeditatePayload.TYPE,
            MeditatePayload.STREAM_CODEC,
            MeditatePayload.Handler::handle
        );
    }
}

