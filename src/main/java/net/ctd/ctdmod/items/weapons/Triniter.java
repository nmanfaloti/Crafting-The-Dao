package net.ctd.ctdmod.items.weapons;

import javax.annotation.Nonnull;

import net.ctd.ctdmod.technique.ComportementDellaier;
import net.ctd.ctdmod.technique.Dellaier;
import net.ctd.ctdmod.technique.Rayon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Triniter extends SwordItem {
    public Triniter(Item.Properties properties) {
        // NeoForge 1.21.4: Tier/Tiers remplacés par ToolMaterial ; dégâts 3, vitesse -2.4F
        super(ToolMaterial.IRON, 3, -2.4F, properties);
    }

    @Override
    public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {

        if (!level.isClientSide()) {
            List<BlockPos> blocsCibles = new ArrayList<>(Rayon.blocsDansRayon(player, level, 30.0));
            if (!blocsCibles.isEmpty()) {
                Dellaier.ajouter(new ComportementRayon(player, level, 30.0));
            }
        }

        return InteractionResult.SUCCESS;
    }



    class ComportementRayon implements ComportementDellaier {
        private int index = 0;
        private final List<BlockPos> blocsCibles;
        private final Level level;

        public ComportementRayon(@Nonnull Player player,@Nonnull Level level , Double length) {
            this.blocsCibles = new ArrayList<>(Rayon.blocsDansRayon(player, level, length));
            this.level = level;
        }

        @Override
        public void executer() {
            if (index >= blocsCibles.size()) {
                return;
            }

            BlockPos blockPos = Objects.requireNonNull(blocsCibles.get(index));
            index++;

            if (!level.getBlockState(blockPos).isAir()) {
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        @Override
        public int nombreExecutionsParCycle() {
            int timetousExecute = 80;
            return Math.max(1, blocsCibles.size() / timetousExecute);
        }

        @Override
        public Iterable<?> getElements() {
            return blocsCibles;
        }
    }
}
