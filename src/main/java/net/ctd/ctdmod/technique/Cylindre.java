package net.ctd.ctdmod.technique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Cylindre {
    private final Vec3 start;
    private final Vec3 direction;
    private final double radius;
    private final double length;
    private final Level level;

    public Cylindre(Vec3 start, Vec3 direction, double radius, double length, Level level) {
        this.start = start;
        this.direction = direction.normalize();
        this.radius = radius;
        this.length = length;
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public List<BlockPos> getBlocks() {
        Set<BlockPos> hitBlocks = new LinkedHashSet<>();
        Vec3 endPoint = start.add(direction.scale(this.length));

        BlockPos minPos = BlockPos.containing(
            Math.min(start.x, endPoint.x) - radius,
            Math.min(start.y, endPoint.y) - radius,
            Math.min(start.z, endPoint.z) - radius
        );
        BlockPos maxPos = BlockPos.containing(
            Math.max(start.x, endPoint.x) + radius,
            Math.max(start.y, endPoint.y) + radius,
            Math.max(start.z, endPoint.z) + radius
        );

        double radiusSqr = radius * radius;

        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            Vec3 posCenter = Vec3.atCenterOf(pos);
            Vec3 vecStartToPos = posCenter.subtract(start);
            double distOnLine = vecStartToPos.dot(direction);

            if (distOnLine >= 0 && distOnLine <= length) {
                double distToLineSqr = vecStartToPos.cross(direction).lengthSqr();
                if (distToLineSqr <= radiusSqr && !level.getBlockState(pos).isAir()) {
                    hitBlocks.add(pos.immutable());
                }
            }
        }
        return new ArrayList<>(hitBlocks);
    }

    public List<Mob> getMobs() {
        List<Mob> mobsFound = new ArrayList<>();
        Vec3 endPoint = start.add(direction.scale(length));
        
        // Create an AABB that encompasses the cylinder plus padding for radius
        AABB searchBox = new AABB(start.x, start.y, start.z, endPoint.x, endPoint.y, endPoint.z).inflate(radius);
        double radiusSqr = radius * radius;

        List<Mob> candidates = level.getEntitiesOfClass(Mob.class, searchBox, Mob::isAlive);
        
        for (Mob mob : candidates) {
            Vec3 mobPos = mob.position(); // Or mob.getBoundingBox().getCenter() for better accuracy
            Vec3 vecStartToMob = mobPos.subtract(start);
            double distOnLine = vecStartToMob.dot(direction);

            if (distOnLine >= 0 && distOnLine <= length) {
                double distToLineSqr = vecStartToMob.cross(direction).lengthSqr();
                if (distToLineSqr <= radiusSqr) {
                    mobsFound.add(mob);
                }
            }
        }
        return mobsFound;
    }
}
