package net.ctd.ctdmod.network;

import net.ctd.ctdmod.CTDMod;
import net.ctd.ctdmod.data.CTDAttachments;
import net.ctd.ctdmod.data.CultivationData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Paquet de synchronisation des données de cultivation d'un joueur.
 * <p>
 * Envoyé depuis le serveur vers le client pour mettre à jour l'attachement {@link CTDAttachments#CULTIVATION}.
 */
public record SyncCultivationPayload(float qi, float maxQi, int level) implements CustomPacketPayload {

    /**
     * Type de paquet pour l'API {@link CustomPacketPayload}.
     */
    public static final Type<SyncCultivationPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "sync_cultivation"));

    /**
     * Codec réseau pour lire/écrire ce paquet dans un {@link RegistryFriendlyByteBuf}.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCultivationPayload> STREAM_CODEC =
        StreamCodec.of(
            (buf, payload) -> {
                buf.writeFloat(payload.qi);
                buf.writeFloat(payload.maxQi);
                buf.writeVarInt(payload.level);
            },
            buf -> new SyncCultivationPayload(
                buf.readFloat(),
                buf.readFloat(),
                buf.readVarInt()
            )
        );

    @Override
    public Type<SyncCultivationPayload> type() {
        return TYPE;
    }

    /**
     * Gestionnaire côté client : applique les valeurs reçues dans l'attachement du joueur local.
     */
    public static final class Handler {

        /**
         * Met à jour les données de cultivation sur le client via {@link Player#setData}.
         *
         * @param payload données de cultivation envoyées par le serveur
         * @param context contexte réseau (coté client)
         */
        public static void handle(SyncCultivationPayload payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                Player player = Minecraft.getInstance().player;
                if (player == null) {
                    return;
                }

                CultivationData updated = new CultivationData(
                    payload.qi(),
                    payload.maxQi(),
                    payload.level()
                );

                player.setData(CTDAttachments.CULTIVATION, updated);
            });
        }
    }
}

