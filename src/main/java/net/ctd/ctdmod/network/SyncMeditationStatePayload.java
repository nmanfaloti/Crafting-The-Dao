package net.ctd.ctdmod.network;

import net.ctd.ctdmod.CTDMod;
import net.ctd.ctdmod.client.CTDClientState;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Paquet envoyé du serveur vers le client pour forcer la mise à jour de l'état de méditation
 * (ex. interruption sur dégâts).
 */
public record SyncMeditationStatePayload(boolean active) implements CustomPacketPayload {

    public static final Type<SyncMeditationStatePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "sync_meditation_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncMeditationStatePayload> STREAM_CODEC =
        StreamCodec.of(
            (buf, payload) -> buf.writeBoolean(payload.active),
            buf -> new SyncMeditationStatePayload(buf.readBoolean())
        );

    @Override
    public Type<SyncMeditationStatePayload> type() {
        return TYPE;
    }

    public static final class Handler {

        /**
         * Met à jour l'état de méditation côté client (meditating + lastMeditateKeyState).
         */
        public static void handle(SyncMeditationStatePayload payload, IPayloadContext context) {
            context.enqueueWork(() -> CTDClientState.syncFromServer(payload.active()));
        }
    }
}
