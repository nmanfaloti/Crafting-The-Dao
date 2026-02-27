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

public class RayBehavior implements DeferredBehavior {
    private int index = 0;
    private final List<BlockPos> targetBlocks;
    private final List<Mob> targetMobs;
    private final Player player;
    private final Level level;
    private final Set<Integer> alreadyHitMobs = new HashSet<>();

    public RayBehavior(List<BlockPos> targetBlocks, List<Mob> targetMobs, Player player, Level level) {
        this.targetBlocks = targetBlocks;
        this.targetMobs = targetMobs;
        this.player = player;
        this.level = level;
    }

    @Override
    public void execute() {
        if (index >= targetBlocks.size()) {
            for (Mob mob : targetMobs) {
                if (mob.isAlive() && !alreadyHitMobs.contains(mob.getId())) {
                    mob.hurt(player.damageSources().playerAttack(player), 10.0F);
                    alreadyHitMobs.add(mob.getId());
                }
            }
            return;
        }

        BlockPos blockPos = Objects.requireNonNull(targetBlocks.get(index));
        index++;

        if (!level.getBlockState(blockPos).isAir()) {
            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
        }

        Vec3 playerPos = player.position();
        double blockDistanceSqr = blockPos.distToCenterSqr(playerPos);

        for (Mob mob : targetMobs) {
            if (!mob.isAlive() || alreadyHitMobs.contains(mob.getId())) {
                continue;
            }

            double mobDistanceSqr = mob.position().distanceToSqr(playerPos);
            if (mobDistanceSqr <= blockDistanceSqr) {
                mob.hurt(player.damageSources().playerAttack(player), 10.0F);
                alreadyHitMobs.add(mob.getId());
            }
        }
    }

    @Override
    public int getExecutionsPerCycle() {
        int totalExecutionTime = 80;
        return Math.max(1, targetBlocks.size() / totalExecutionTime);
    }

    @Override
    public Iterable<?> getElements() {
        if (targetBlocks.isEmpty() && !targetMobs.isEmpty()) {
            return List.of(new Object());
        }
        return targetBlocks;
    }
}