package net.ctd.ctdmod.data;

import net.ctd.ctdmod.network.SyncCultivationPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Méthodes utilitaires pour manipuler les données de cultivation d'un joueur côté serveur.
 * <p>
 * Toute modification déclenche automatiquement un paquet de synchronisation vers le(s) client(s) concernés.
 */
public final class CultivationUtil {

    private CultivationUtil() {
        // Classe utilitaire : pas d'instanciation.
    }

    /**
     * Modifie la quantité de Qi d'un joueur, applique la logique de montée de niveau
     * avec seuil évolutif (maxQi augmente à chaque niveau atteint), puis synchronise
     * l'état auprès du client ciblé.
     *
     * @param player joueur serveur concerné
     * @param delta  variation de Qi (positive ou négative)
     */
    public static void modifyQi(ServerPlayer player, float delta) {
        CultivationData current = player.getData(CTDAttachments.CULTIVATION);

        float qi = current.qi() + delta;
        float maxQi = current.maxQi();
        int level = current.level();

        if (delta > 0.0F) {
            // Montées de sous-niveau : barre pleine → +1 niveau, maxQi *= facteur ; le Qi actuel est conservé.
            while (qi >= maxQi && level < CultivationSettings.MAX_SUBLEVEL) {
                level++;
                maxQi *= CultivationSettings.QI_MAX_GROWTH_FACTOR;
            }
        }

        // Clamp final pour s'assurer que le Qi reste dans [0, maxQi].
        qi = Mth.clamp(qi, 0.0F, maxQi);

        CultivationData updated = new CultivationData(qi, maxQi, level);

        if (!updated.equals(current)) {
            player.setData(CTDAttachments.CULTIVATION, updated);

            PacketDistributor.sendToPlayer(
                player,
                new SyncCultivationPayload(updated.qi(), updated.maxQi(), updated.level())
            );
        }
    }

    /**
     * Réinitialise entièrement la cultivation d'un joueur à l'état par défaut,
     * puis synchronise les données vers le client.
     *
     * @param player joueur serveur concerné
     */
    public static void resetCultivation(ServerPlayer player) {
        CultivationData updated = CultivationData.DEFAULT;
        player.setData(CTDAttachments.CULTIVATION, updated);

        PacketDistributor.sendToPlayer(
            player,
            new SyncCultivationPayload(updated.qi(), updated.maxQi(), updated.level())
        );
    }
}

