package net.ctd.ctdmod.blockentity;

import java.lang.reflect.ParameterizedType;

import javax.annotation.Nullable;

import net.ctd.ctdmod.api.CTDSyncedBlockEntity;
import net.ctd.ctdmod.core.definition.CTDRecipes;
import net.ctd.ctdmod.customrecipe.AlchemyRecipe;
import net.ctd.ctdmod.customrecipe.CTDBaseRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/*
 */
public abstract class CTDCraftingBlockEntity<R extends CTDBaseRecipe> extends BlockEntity implements CTDSyncedBlockEntity {
    protected R currentRecipe = null; // The recipe currently being crafted, null if no crafting in progress
    protected int craftProgress = 0; // Progress of the current crafting operation, in ticks

    private final Class<R> recipeClass; // The class of the recipes this block entity can craft, used for recipe lookup

    public CTDCraftingBlockEntity(BlockEntityType<? extends CTDCraftingBlockEntity<?>> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        // Use reflection to determine the actual class of the recipe type parameter
        // this allows us to use the same base class for different recipe types without having to hardcode the class in each subclass
        this.recipeClass = (Class<R>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    // Crafting logic

    public boolean isCrafting() {
        return currentRecipe != null;
    }

    @Nullable
    public R getCurrentRecipe() {
        return currentRecipe;
    }

    public int getCraftProgress() {
        return craftProgress;
    }

    public void resetCrafting() {
        currentRecipe = null;
        craftProgress = 0;
    }

    /*
     * Called when the craft progress has reached the required time. 
     * Should perform the crafting operation (consume ingredients, produce result, etc.) and reset the crafting state.
     */
    protected abstract void performCraft(R recipe);

    // TICK LOGIC

    public void tickCrafting() {
        if (currentRecipe == null) {
            return;
        }
        craftProgress++;

        if (!level.isClientSide() && craftProgress >= currentRecipe.craftTime) {
            performCraft(currentRecipe);
            currentRecipe = null;
            craftProgress = 0;
            setChanged();
        }
    }
    
    // Abstract tick method to be implemented by subclasses, called every tick to update the crafting state.
    public abstract void tick(); 

    // NBT data saving/loading

    private static final String TAG_CRAFT_PROGRESS = "CraftProgress";
    private static final String TAG_CURRENT_RECIPE = "CurrentRecipe";

    /*
    * Save the inventory to NBT when the block entity is saved.
    */
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_CRAFT_PROGRESS, craftProgress);
        if (currentRecipe != null) {
            tag.putString(TAG_CURRENT_RECIPE, currentRecipe.name);
        }
    }

    /*
     * Load the inventory from NBT when the block entity is loaded.
     */
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        craftProgress = tag.getInt(TAG_CRAFT_PROGRESS);
        if (tag.contains(TAG_CURRENT_RECIPE)) {
            currentRecipe = CTDRecipes.getRecipeByName(recipeClass, tag.getString(TAG_CURRENT_RECIPE));
        } else {
            currentRecipe = null;
        }
    }

    // Sync logic

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

     /**
     * Builds the part of the update tag that contains crafting data (CraftProgress, CurrentRecipe).
     * Subclass should call this and merge into its own tag that also includes inventory, etc.
     */
    protected void addCraftingToUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt(TAG_CRAFT_PROGRESS, craftProgress);
        if (currentRecipe != null) {
            tag.putString(TAG_CURRENT_RECIPE, currentRecipe.name);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        addCraftingToUpdateTag(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
