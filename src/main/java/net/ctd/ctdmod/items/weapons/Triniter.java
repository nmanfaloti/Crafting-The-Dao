package net.ctd.ctdmod.items.weapons;

import javax.annotation.Nonnull;

import net.ctd.ctdmod.technique.DeferredBehaviorScheduler;
import net.ctd.ctdmod.technique.RaySelection;
import net.ctd.ctdmod.technique.RayBehavior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;


import java.util.List;

public class Triniter extends SwordItem {

    private static final int MAX_LENGTH = 30;
    private static final int RADIUS = 5;


    public Triniter(Item.Properties properties) {
        super(Tiers.IRON, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {

        if (!level.isClientSide()) {
            RaySelection raySelection = new RaySelection(player, level, (double) RADIUS, (double) MAX_LENGTH);
            List<BlockPos> targetBlocks = raySelection.getSortedBlocksInRay();
            List<Mob> targetMobs = raySelection.getTargetMobs();
            
            if (!targetBlocks.isEmpty() || !targetMobs.isEmpty()) {
                DeferredBehaviorScheduler.add(new RayBehavior(targetBlocks, targetMobs, player, level));
            }
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));
    }
}
