package net.ctd.ctdmod.technique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RaySelection {

    private List<BlockPos> targetBlocks;
    private List<Mob> targetMobs;

    private final CylinderArea cylinderArea;
    private final Player player;

    public RaySelection(Player player, Level level, Double radius, Double length) {
        Vec3 viewDirection = player.getLookAngle();
        Vec3 startPoint = player.getEyePosition().add(viewDirection.scale(2.0));
        this.player = player;
        this.cylinderArea = new CylinderArea(startPoint, viewDirection, radius, length, level);
        this.targetBlocks = this.cylinderArea.getBlocks();
        this.targetMobs = this.cylinderArea.getMobs();
    }

    public List<BlockPos> getTargetBlocks() {
        return targetBlocks;
    }

    public List<Mob> getTargetMobs() {
        return targetMobs;
    }

    public List<BlockPos> getSortedBlocksInRay() {
        this.filterBlocksWithoutFluid()
                .sortBlocksByDistanceToPlayer();
        return this.targetBlocks;
    }

    public RaySelection sortBlocksByDistanceToPlayer() {
        Vec3 playerPos = this.player.position();
        this.targetBlocks = this.targetBlocks.stream()
                .sorted(Comparator.comparingDouble(pos -> pos.distToCenterSqr(playerPos)))
                .collect(Collectors.toCollection(ArrayList::new));
        return this;
    }

    public RaySelection filterBlocksWithoutFluid() {
        this.targetBlocks = this.targetBlocks.stream()
                .filter(pos -> this.cylinderArea.getLevel().getFluidState(pos).isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
        return this;
    }

    @Deprecated
    public static List<Mob> getMobsInRay(CylinderArea cylinderArea) {
        return cylinderArea.getMobs();
    }
}