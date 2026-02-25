package net.ctd.ctdmod.items.weapons;

import javax.annotation.Nonnull;

import net.ctd.ctdmod.technique.DeferredBehaviorScheduler;
import net.ctd.ctdmod.technique.Rayon;
import net.ctd.ctdmod.technique.ComportementRayon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Mob;


import java.util.List;

public class Triniter extends SwordItem {

    private static final int MAX_LENGTH = 30;
    private static final int RADIUS = 5;


    public Triniter(Item.Properties properties) {
        // NeoForge 1.21.4: Tier/Tiers remplacés par ToolMaterial ; dégâts 3, vitesse -2.4F
        super(ToolMaterial.IRON, 3, -2.4F, properties);
    }

    @Override
    public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {

        if (!level.isClientSide()) {
            Rayon rayon = new Rayon(player, level, (double) RADIUS, (double) MAX_LENGTH);
            List<BlockPos> blocsCibles = rayon.blocsDansRayonTrier();
            List<Mob> mobsCibles = rayon.getMobsCibles();
            
            if (!blocsCibles.isEmpty() || !mobsCibles.isEmpty()) {
                DeferredBehaviorScheduler.ajouter(new ComportementRayon(blocsCibles, mobsCibles, player, level));
            }
        }

        return InteractionResult.SUCCESS;
    }
}
