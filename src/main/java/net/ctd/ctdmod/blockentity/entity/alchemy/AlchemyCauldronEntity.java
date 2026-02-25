package net.ctd.ctdmod.blockentity.entity.alchemy;

import java.util.List;

import net.ctd.ctdmod.core.definition.CTDBlockEntities;
import net.ctd.ctdmod.customrecipe.AlchemyRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.level.Level;

public class AlchemyCauldronEntity extends BlockEntity {
    public final ItemStackHandler inventory = new ItemStackHandler(9){
        /*
         * Set the stack limit for each slot.
         */
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 64;
        };

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                // Update everything to know that the block entity has changed
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };
    private float rotation;
    private AlchemyRecipe currentRecipe = null;
    private int craftProgress = 0;

    public AlchemyCauldronEntity(BlockPos pos, BlockState state) {
        super(CTDBlockEntities.ALCHEMY_CAULDRON.get(), pos, state);
    }

    // Make the item in the cauldron rotate for rendering
    public float getRenderingRotation() {
        rotation += 0.5f;
        if (rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    // return the slot index of the first empty slot, or -1 if all slots are full
    public int getFirstEmptySlot() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    // return the slot index of the last non-empty slot, or -1 if all slots are empty
    public int getLastNonEmptySlot() {
        for (int i = inventory.getSlots() - 1; i >= 0; i--) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }



    public void clearContents() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        craftProgress = 0;
        currentRecipe = null;
    }

    /*
     *  Drops the inventory of the block entity when the block is removed. 
     */
    public static void drops(AlchemyCauldronEntity entity) {
        SimpleContainer container = new SimpleContainer(entity.inventory.getSlots());
        for (int i = 0; i < entity.inventory.getSlots(); i++) {
            container.setItem(i, entity.inventory.getStackInSlot(i));
        }
        Containers.dropContents(entity.level, entity.worldPosition, container);
        entity.craftProgress = 0;
        entity.currentRecipe = null;
    }

    

    public void checkRecipe(List<AlchemyRecipe> recipes) {
        for (AlchemyRecipe recipe : recipes) {
            boolean match = true;
            // Check if the recipes correspond
            for (int i = 0; i < recipe.ingredients.size(); i++) {
                var ingredient = recipe.ingredients.get(i);
                var stackInSlot = inventory.getStackInSlot(i);
                if (ingredient instanceof net.ctd.ctdmod.customrecipe.ItemIngredient itemIng) {
                    ItemStack required = itemIng.getIngredient();
                    if (!ItemStack.matches(stackInSlot, required)) {
                        match = false;
                        break;
                    }
                } else if (ingredient instanceof net.ctd.ctdmod.customrecipe.FluidIngredient fluidIng) {
                    // TODO : Fluid Logique 
                    match = false; 
                    break;
                } else {
                    match = false;
                    break;
                }
            }
            // If matched set the recipe
            if (match) {
                currentRecipe = recipe;
                craftProgress = 0;
                break;
            }
        }
    }

    public void tick() {
        if (currentRecipe != null) {
            craftProgress++;
            if (craftProgress >= currentRecipe.craftTime) {
                // Consume the ingredients
                for (int i = 0; i < currentRecipe.ingredients.size(); i++) {
                    int currentCount = inventory.getStackInSlot(i).getCount();
                    int newCount = currentCount - currentRecipe.ingredients.get(i).getCount();
                    if (newCount <= 0) {
                        inventory.setStackInSlot(i, ItemStack.EMPTY);
                    } else {
                        ItemStack stack = inventory.getStackInSlot(i);
                        stack.setCount(newCount);
                        inventory.setStackInSlot(i, stack);
                    }
                }
                // Give the result
                int emptySlot = getFirstEmptySlot();
                if (emptySlot != -1) {
                    // Create the result item from the factory
                    if (currentRecipe.resultFactory.get() instanceof ItemStack resultStack) {
                        inventory.setStackInSlot(emptySlot, resultStack.copy());
                    } else {
                        // TODO : Handle other types of results (e.g., fluids)
                    }
                }
                currentRecipe.CraftEnded(this);
                currentRecipe = null;
                craftProgress = 0;
            }
        }
    }

    /*
    * Save the inventory to NBT when the block entity is saved.
    */
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    // GETTERS AND SETTERS
    public AlchemyRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public int getCraftProgress() {
        return craftProgress;
    }
}