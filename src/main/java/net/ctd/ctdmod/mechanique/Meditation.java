package net.ctd.ctdmod.mechanique;

import net.ctd.ctdmod.playerData.PlayerStat;
import net.ctd.ctdmod.technique.ComportementRayon;
import net.ctd.ctdmod.technique.DeferredBehaviorScheduler;
import net.ctd.ctdmod.technique.Rayon;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class Meditation {
        private static final String QI_NOT = "Not enough Qi to perform the action.";
        private static final int MAX_LENGTH = 30;
        private static final int RADIUS = 5;
        private static final int QI_COST = 100;

	private Meditation() {
	}

	public static void trigger(Player player) {
        int cultivationLevel = PlayerStat.increaseQiLevel(player, 1);
        player.displayClientMessage(Component.literal("Qi Level: " + cultivationLevel), true);
	}

	public static void triggerPowerAction(Player player) {

        if ( PlayerStat.getQiLevel(player) > QI_COST ) {
            PlayerStat.decreaseQiLevel(player, QI_COST);
            Level level = player.level();
            Rayon rayon = new Rayon(player, level, (double) RADIUS, (double) MAX_LENGTH);
            List<BlockPos> blocsCibles = rayon.blocsDansRayonTrier();
            List<Mob> mobsCibles = rayon.getMobsCibles();
                
            if (!blocsCibles.isEmpty() || !mobsCibles.isEmpty()) {
                DeferredBehaviorScheduler.ajouter(new ComportementRayon(blocsCibles, mobsCibles, player, level));
            }
            player.displayClientMessage(Component.literal("black hole created! consumed " + QI_COST + " Qi"), true);

        }
        else {
            int missingQi = QI_COST - PlayerStat.getQiLevel(player);
            player.displayClientMessage(Component.literal(QI_NOT + "il manque : " + missingQi + " Qi"), true);
        }
        
	}
}
