package net.ctd.ctdmod.meditation;

import net.ctd.ctdmod.data.CultivationSettings;
import net.ctd.ctdmod.data.CultivationUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gère l'état de méditation côté serveur : enregistrement des joueurs
 * « en méditation » et attribution du Qi à intervalle lorsqu'ils restent immobiles.
 */
public final class MeditationServerHandler {

    private static final double MOVEMENT_THRESHOLD_SQ = 1.0E-6;

    private static final Map<UUID, MeditationState> STATE_BY_PLAYER = new ConcurrentHashMap<>();

    private MeditationServerHandler() {
    }

    /**
     * Enregistre ou désenregistre le joueur comme étant en méditation.
     * Appelé par le handler du paquet {@link net.ctd.ctdmod.network.MeditatePayload}.
     */
    public static void setMeditating(ServerPlayer player, boolean active) {
        UUID id = player.getUUID();
        if (active) {
            STATE_BY_PLAYER.put(id, new MeditationState(true, 0));
        } else {
            STATE_BY_PLAYER.remove(id);
        }
    }

    /**
     * Appelé chaque tick côté serveur pour chaque joueur.
     * Si le joueur est en méditation et immobile, incrémente le compteur
     * et accorde du Qi à chaque intervalle.
     */
    public static void tick(ServerPlayer player) {
        MeditationState state = STATE_BY_PLAYER.get(player.getUUID());
        if (state == null || !state.meditating) {
            return;
        }

        if (player.getDeltaMovement().lengthSqr() >= MOVEMENT_THRESHOLD_SQ) {
            STATE_BY_PLAYER.put(player.getUUID(), new MeditationState(true, 0));
            return;
        }

        int next = state.ticksAccumulated + 1;
        if (next >= CultivationSettings.MEDITATION_INTERVAL_TICKS) {
            CultivationUtil.modifyQi(player, CultivationSettings.MEDITATION_QI_GAIN);
            next = 0;
        }
        STATE_BY_PLAYER.put(player.getUUID(), new MeditationState(true, next));
    }

    /**
     * Retire l'état de méditation du joueur (appelé à la déconnexion).
     */
    public static void remove(Player player) {
        STATE_BY_PLAYER.remove(player.getUUID());
    }

    private record MeditationState(boolean meditating, int ticksAccumulated) {
    }
}
