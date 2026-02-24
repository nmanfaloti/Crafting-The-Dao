package net.ctd.ctdmod.technique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Rayon {
    private List<BlockPos> blocsCibles;
    private Rayon(Set<BlockPos> blocsCibles) {
        this.blocsCibles = new ArrayList<>(blocsCibles);
    }

    public static List<BlockPos> blocsDansRayon(Player player, Level level , Double lenght) {
        Vec3 viewPointStart = player.getEyePosition();
        Vec3 orientationViewPoint = player.getLookAngle();
        Rayon rayon = blockHitByRaygun(viewPointStart ,orientationViewPoint , 5.6 ,lenght, level)
                            .trieBlocsNoFluideHere(level)
                            .trieBlocsClauseToPlayer(player);
        return rayon.getBlocsCibles();
    }

    public List<BlockPos> getBlocsCibles() {
        return blocsCibles;
    }


    public static Set<BlockPos> destoyBlock(Player player, Level level , Double lenght) {
        List<BlockPos> hitResult = blocsDansRayon(player, level, lenght);

        for (BlockPos block : hitResult){
            Block leBlocRegarde = level.getBlockState(block).getBlock();
            int luck = 0;
            level.destroyBlock(block, luckTest(luck, level));
        }
        return new LinkedHashSet<>(hitResult);
    }

    private static Rayon blockHitByRaygun
                                        (Vec3 viewPointStart ,Vec3 orientationViewPoint,
                                         Double radius , Double lenght,
                                         Level level
                                        ){
        Set<BlockPos> hitBlock = new LinkedHashSet<>();

        // calculation of the end of the raygun
        Vec3 endPoint = viewPointStart.add(
                orientationViewPoint.x * lenght,
                orientationViewPoint.y * lenght,
                orientationViewPoint.z * lenght
        );

        //calculation of the cylinder
        BlockPos minPos = BlockPos.containing(
                Math.min(viewPointStart.x, endPoint.x) - radius,
                Math.min(viewPointStart.y, endPoint.y) - radius,
                Math.min(viewPointStart.z, endPoint.z) - radius
        );
        BlockPos maxPos = BlockPos.containing(
                Math.max(viewPointStart.x, endPoint.x) + radius,
                Math.max(viewPointStart.y, endPoint.y) + radius,
                Math.max(viewPointStart.z, endPoint.z) + radius
        );


        Iterable<BlockPos> zoneDeScan = BlockPos.betweenClosed(minPos, maxPos);
        double radiusSqr = radius * radius;

        for (BlockPos bloc : zoneDeScan) {
            // getting the center coordinates of the block
            Vec3 positionBloc = Vec3.atCenterOf(bloc);

            // creating a vector from the player to the block
            Vec3 vectorPlayerBloc = positionBloc.subtract(viewPointStart);

            // Calculating distance between player and bloc using dot
            double distanceSurLaLigne = vectorPlayerBloc.dot(orientationViewPoint);

            // Checking if the block is the cylinder
            if (distanceSurLaLigne >= 0 && distanceSurLaLigne <= lenght) {

                // Using the Cross Product to find the squared distance between the block and the core of the laser
                double distanceAuCentreLaserSqr = vectorPlayerBloc.cross(orientationViewPoint).lengthSqr();

                // Checking if the block is actually inside our cylinder's radius
                if (distanceAuCentreLaserSqr <= radiusSqr) {

                    // Ignoring air blocks
                    if (!level.getBlockState(bloc).isAir()) {
                        // .immutable() is required because betweenClosed reuses the same BlockPos object in memory to save RAM!
                        hitBlock.add(bloc.immutable());
                    }
                }
            }
        }

        return new Rayon(hitBlock);
    }

    // chance to drop the block
    private static boolean luckTest(Integer luck , Level level){
        int chance = level.getRandom().nextInt(100);

        return  chance < luck ;

    }

    public Rayon trieBlocsClauseToPlayer(Player player) {
        Vec3 playerPos = player.position(); 
        blocsCibles = this.blocsCibles.stream()
                .sorted(Comparator.comparingDouble(pos -> pos.distToCenterSqr(playerPos)))
                .collect(Collectors.toCollection(ArrayList::new));
        return this;
    }

    public Rayon trieBlocsNoFluideHere(Level level) {
        blocsCibles = this.blocsCibles.stream()
                .filter(pos -> level.getFluidState(pos).isEmpty())
                .sorted(Comparator.comparingInt(BlockPos::getY))
                .collect(Collectors.toCollection(ArrayList::new));
        return this;
    }
}

