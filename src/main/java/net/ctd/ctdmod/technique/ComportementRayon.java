package net.ctd.ctdmod.technique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class ComportementRayon implements DeferredBehavior {
        private int index = 0;
        private final List<BlockPos> blocsCibles;
        private final List<Mob> mobsCibles;
        private final Player player;
        private final Level level;
        private final Set<Integer> mobsDejaTouches = new HashSet<>();

        public ComportementRayon(List<BlockPos> blocsCibles, List<Mob> mobsCibles, Player player, Level level) {
            this.blocsCibles = blocsCibles;
            this.mobsCibles = mobsCibles;
            this.player = player;
            this.level = level;
        }

        @Override
        public void executer() {
            if (index >= blocsCibles.size()) {
                for ( Mob mob : mobsCibles) {
                    if (mob.isAlive() && !mobsDejaTouches.contains(mob.getId())) {
                        mob.hurt(player.damageSources().playerAttack(player), 10.0F);
                        mobsDejaTouches.add(mob.getId());
                    }
                }
                return;
            }
                
            BlockPos blockPos = Objects.requireNonNull(blocsCibles.get(index));
            index++;

            if (!level.getBlockState(blockPos).isAir()) {
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
            }

            Vec3 playerPos = player.position();
            double distanceBlocSqr = blockPos.distToCenterSqr(playerPos);

            for (Mob mob : mobsCibles) {
                if (!mob.isAlive() || mobsDejaTouches.contains(mob.getId())) {
                    continue;
                }

                double distanceMobSqr = mob.position().distanceToSqr(playerPos);
                if (distanceMobSqr <= distanceBlocSqr) {
                    mob.hurt(player.damageSources().playerAttack(player), 10.0F);
                    mobsDejaTouches.add(mob.getId());
                }
            }
        }

        @Override
        public int nombreExecutionsParCycle() {
            int timetousExecute = 80;
            return Math.max(1, blocsCibles.size() / timetousExecute);
        }

        @Override
        public Iterable<?> getElements() {
            if (blocsCibles.isEmpty() && !mobsCibles.isEmpty()) {
                return List.of(new Object());
            }
            return blocsCibles;
        }
}