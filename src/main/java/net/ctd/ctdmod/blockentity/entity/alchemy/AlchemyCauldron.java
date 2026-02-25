package net.ctd.ctdmod.blockentity.entity.alchemy;

import javax.annotation.Nullable;


import com.mojang.serialization.MapCodec;

import net.ctd.ctdmod.core.definition.CTDBlockEntities;
import net.ctd.ctdmod.core.definition.CTDRecipes;
import net.ctd.ctdmod.customrecipe.AlchemyRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class AlchemyCauldron extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 14, 16);
    public static final MapCodec<AlchemyCauldron> CODEC = simpleCodec(AlchemyCauldron::new);

    public AlchemyCauldron(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    // BLOCK ENTITY

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return CTDBlockEntities.ALCHEMY_CAULDRON.get().create(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // Check if the block remplaced is the same as the new block, if not drop the inventory
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof AlchemyCauldronEntity blockEntity) {
                AlchemyCauldronEntity.drops(blockEntity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }   
        super.onRemove(state, level, pos, newState, isMoving);
    }
    

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        
        if (level.getBlockEntity(pos) instanceof AlchemyCauldronEntity blockEntity) {
            if (!stack.isEmpty()){
                // Get the first empty slot 
                int emptySlot = blockEntity.getFirstEmptySlot();
                if (emptySlot != -1) { // Check if not full 
                    blockEntity.inventory.setStackInSlot(emptySlot, stack.copy());
                    stack.setCount(0);
                    level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }else{
                // Get the first non-empty slot
                int firstSlot = blockEntity.getLastNonEmptySlot();
                if (firstSlot != -1) { // Check if not empty
                    ItemStack itemStack = blockEntity.inventory.getStackInSlot(firstSlot).copy();
                    blockEntity.inventory.setStackInSlot(firstSlot, ItemStack.EMPTY);
                    if (!player.getInventory().add(itemStack)) {
                        player.drop(itemStack, false);
                    }
                    level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }

            // Check the recipe after adding or removing an item
            blockEntity.checkRecipe(CTDRecipes.getRecipes(AlchemyRecipe.class));

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /*
     * Set the ticker for the block entity
     */
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == CTDBlockEntities.ALCHEMY_CAULDRON.get()
            ? (lvl, pos, st, be) -> ((AlchemyCauldronEntity) be).tick()
            : null;
    }
}   
 
// 1 2 3 4 
// 1 2 3
// 1 2 3 5 
