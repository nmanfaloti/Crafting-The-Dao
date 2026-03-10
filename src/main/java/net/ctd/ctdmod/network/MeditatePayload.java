package net.ctd.ctdmod.network;

import net.ctd.ctdmod.CTDMod;
import net.ctd.ctdmod.meditation.MeditationServerHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Paquet envoyé du client vers le serveur pour indiquer que le joueur
 * maintient ou relâche la touche de méditation.
 */
public record MeditatePayload(boolean active) implements CustomPacketPayload {

    public static final Type<MeditatePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "meditate"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MeditatePayload> STREAM_CODEC =
        StreamCodec.of(
            (buf, payload) -> buf.writeBoolean(payload.active),
            buf -> new MeditatePayload(buf.readBoolean())
        );

    @Override
    public Type<MeditatePayload> type() {
        return TYPE;
    }

    public static final class Handler {

        /**
         * Enregistre ou désenregistre le joueur comme « en méditation » côté serveur.
         */
        public static void handle(MeditatePayload payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer serverPlayer) {
                    MeditationServerHandler.setMeditating(serverPlayer, payload.active());
                }
            });
        }
    }
}
