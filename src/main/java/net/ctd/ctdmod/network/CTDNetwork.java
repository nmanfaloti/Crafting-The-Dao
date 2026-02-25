package net.ctd.ctdmod.network;

import net.ctd.ctdmod.CTDMod;
import net.ctd.ctdmod.mechanique.Meditation;
import net.ctd.ctdmod.network.payload.MeditatePayload;
import net.ctd.ctdmod.network.payload.PowerActionPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

// This class is responsible for registering the custom payload handlers and defining how to handle incoming payloads
// this class is the bridge between the client and server for our custom network communication, allowing us to trigger server-side logic in response to client input without needing to define custom packets manually.

@EventBusSubscriber(modid = CTDMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class CTDNetwork {
    private CTDNetwork() {
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(MeditatePayload.TYPE, MeditatePayload.STREAM_CODEC, CTDNetwork::handleMeditate);
        registrar.playToServer(PowerActionPayload.TYPE, PowerActionPayload.STREAM_CODEC, CTDNetwork::handlePowerAction);
    }

    private static void handleMeditate(MeditatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                Meditation.trigger(serverPlayer);
            }
        });
    }

    //gère la réception du PowerActionPayload et déclenche l'action de pouvoir correspondante pour le joueur
    private static void handlePowerAction(PowerActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                Meditation.triggerPowerAction(serverPlayer);
            }
        });
    }
}