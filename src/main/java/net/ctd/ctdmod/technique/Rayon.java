package net.ctd.ctdmod.technique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Rayon {

    private List<BlockPos> blocsCibles;
    private List<Mob> mobsCibles;

    private Cylindre cylindre;
    private Player player;

    public Rayon(Player player, Level level, Double radius, Double length) {
        Vec3 orientationViewPoint = player.getLookAngle();
        Vec3 viewPointStart = player.getEyePosition().add(orientationViewPoint.scale(2.0));
        this.player = player;
        this.cylindre = new Cylindre(viewPointStart, orientationViewPoint, radius, length, level);
        this.blocsCibles = this.cylindre.getBlocks();
        this.mobsCibles = this.cylindre.getMobs();
    }

    public List<BlockPos> getBlocsCibles() {
        return blocsCibles;
    }

    public List<Mob> getMobsCibles() {
        return mobsCibles;
    }

    public List<BlockPos> blocsDansRayonTrier() {
        this.trieBlocsNoFluideHere()
            .trieBlocsCloseToPlayer();
        return this.blocsCibles;
    }

    public Rayon trieBlocsCloseToPlayer() {
        Vec3 playerPos = this.player.position(); 
        this.blocsCibles = this.blocsCibles.stream()
                .sorted(Comparator.comparingDouble(pos -> pos.distToCenterSqr(playerPos)))
                .collect(Collectors.toCollection(ArrayList::new));
        return this;
    }

    public Rayon trieBlocsNoFluideHere() {
        this.blocsCibles = this.blocsCibles.stream()
                .filter(pos -> this.cylindre.getLevel().getFluidState(pos).isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
        return this;
    }

    /**
     * @deprecated Use {@link Cylindre#getMobs()} directly or access mobs via instance method.
     */
    @Deprecated
    public static List<Mob> mobsDansRayon(Cylindre cylindre) {
        return cylindre.getMobs();
    }
}

