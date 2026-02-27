package net.ctd.ctdmod.mechanique;

import net.ctd.ctdmod.playerData.PlayerStat;
import net.ctd.ctdmod.technique.RayBehavior;
import net.ctd.ctdmod.technique.DeferredBehaviorScheduler;
import net.ctd.ctdmod.technique.RaySelection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

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

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                player.getX(), player.getY() + 1.0, player.getZ(),
                24, 0.5, 0.6, 0.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                player.getX(), player.getY() + 1.0, player.getZ(),
                8, 0.35, 0.45, 0.35, 0.01);
            serverLevel.playSound(null,
                player.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS,
                0.5F,
                1.25F);
    }

    player.displayClientMessage(Component.literal("✦ Qi +1  |  Current Qi: " + cultivationLevel), true);
	}

	public static void triggerPowerAction(Player player) {

        if ( PlayerStat.getQiLevel(player) > QI_COST ) {
            PlayerStat.decreaseQiLevel(player, QI_COST);
            Level level = player.level();
            RaySelection raySelection = new RaySelection(player, level, (double) RADIUS, (double) MAX_LENGTH);
            List<BlockPos> targetBlocks = raySelection.getSortedBlocksInRay();
            List<Mob> targetMobs = raySelection.getTargetMobs();
                
            if (!targetBlocks.isEmpty() || !targetMobs.isEmpty()) {
                DeferredBehaviorScheduler.add(new RayBehavior(targetBlocks, targetMobs, player, level));
            }
            player.displayClientMessage(Component.literal("black hole created! consumed " + QI_COST + " Qi"), true);

        }
        else {
            int missingQi = QI_COST - PlayerStat.getQiLevel(player);
            player.displayClientMessage(Component.literal(QI_NOT + " Missing: " + missingQi + " Qi"), true);
        }
        
	}
}
