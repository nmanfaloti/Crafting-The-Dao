package net.ctd.ctdmod.blockentity.entity.alchemy;

import java.util.ArrayList;
import java.util.List;

import net.ctd.ctdmod.api.CTDInventoryBlock;
import net.ctd.ctdmod.api.CTDSyncedBlockEntity;
import net.ctd.ctdmod.blockentity.CTDCraftingBlockEntity;
import net.ctd.ctdmod.core.definition.CTDBlockEntities;
import net.ctd.ctdmod.core.definition.CTDRecipes;
import net.ctd.ctdmod.customrecipe.AlchemyRecipe;
import net.ctd.ctdmod.customrecipe.FluidIngredient;
import net.ctd.ctdmod.customrecipe.ItemIngredient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class AlchemyCauldronEntity extends CTDCraftingBlockEntity<AlchemyRecipe> implements CTDInventoryBlock<ItemStack, ItemStackHandler>, CTDSyncedBlockEntity {
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
    
    public AlchemyCauldronEntity(BlockPos pos, BlockState state) {
        super(CTDBlockEntities.ALCHEMY_CAULDRON.get(), pos, state);
    }

    @Override
    public void tick() {
        tickCrafting(); // Use the crafting logic from the base class to handle crafting progress and completion
    }

    @Override
    protected void performCraft(AlchemyRecipe recipe) {
        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0; i < recipe.ingredients.size(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                ingredients.add(stack.copy());
                inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
        }

        ItemStack resultStack = recipe.generateResult(ingredients, level);
        if (resultStack == null && recipe.resultFactory.get() instanceof ItemStack factoryStack) {
            resultStack = factoryStack.copy();
        }

        int emptySlot = getFirstEmptySlot();
        if (emptySlot != -1 && resultStack != null) {
            inventory.setStackInSlot(emptySlot, resultStack);
        }

        recipe.CraftEnded(this);
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

    /*
     * INVENTORY MANAGEMENT 
     */

    public void clearContents() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        resetCrafting();
    }

    public void addObject(ItemStack object, Player player){
        if (object != null && !object.isEmpty()) {
            int emptySlot = getFirstEmptySlot();
            if (emptySlot != -1) { // Check if not full 
                inventory.setStackInSlot(emptySlot, object.copy());
                object.setCount(0);
                if (player != null) {
                    level.playSound(player, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                if (!level.isClientSide()) {
                    checkRecipe(CTDRecipes.getRecipes(AlchemyRecipe.class));
                }   
            }
        }
    } 

    /**
     * Remove an object from the inventory and give it to the player if possible
     * @param slot
     * @param player
     * @return
     */
    public ItemStack removeObject(int slot, Player player){
        if (slot != -1) { // Check if not empty
            ItemStack itemStack = inventory.getStackInSlot(slot).copy();
            if (player != null && player.getInventory().add(itemStack)) {
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
                level.playSound(player, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);

                if (!level.isClientSide()) {
                    checkRecipe(CTDRecipes.getRecipes(AlchemyRecipe.class));
                }   
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
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
        entity.resetCrafting();
    }

    @Override
    public int getInventorySize() {
        return inventory.getSlots();
    }

    @Override
    public ItemStack getObject(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStackHandler getInventory() {
        return inventory;
    }

    public void checkRecipe(List<AlchemyRecipe> recipes) {
        // If currently crafting, reset the recipe and progress to avoid 
        if (isCrafting()) {
            currentRecipe = null;
            craftProgress = 0;
        }

        for (AlchemyRecipe recipe : recipes) {
            boolean match = true;
            List<ItemStack> inventoryCopy = new ArrayList<>();
            // Copy the inventory to avoid modifying it while checking the recipes
            for (int i = 0; i < inventory.getSlots(); i++) {
                // Only add non-empty stacks and non-air to the copy to optimize the checking
                if (!inventory.getStackInSlot(i).isEmpty()) {   
                    inventoryCopy.add(inventory.getStackInSlot(i).copy());
                }
            }

            // Check if the recipes correspond
            for (var ingredient : recipe.ingredients) {
                if (ingredient instanceof ItemIngredient itemIng ) {
                    ItemStack required = itemIng.getIngredient();
                    boolean found = false;
                    // Take the list in reverse so we can remove items without affecting the index of the next items to check 
                    for (int i = inventoryCopy.size() - 1; i >= 0; i--) {
                        ItemStack stack = inventoryCopy.get(i);
                        if (!stack.isEmpty() && stack.getItem() == required.getItem() && stack.getCount() == required.getCount()) {
                            stack.shrink(required.getCount());
                            if (stack.isEmpty()) {
                                inventoryCopy.remove(i);
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        match = false;
                        break;
                    }
                }  else if (ingredient instanceof FluidIngredient) {
                    // TODO : Fluid Logique 
                    match = false; 
                    break;
                } else {
                    match = false;
                    break;
                }
            }
            // If matched set the recipe
            if (match && inventoryCopy.isEmpty()) { // Check if their is no extra items
                currentRecipe = recipe;
                craftProgress = 0;

                this.setChanged();
                // Force block-entity sync to clients so crafting animation can run (chunk sync is often delayed)
                if (level instanceof ServerLevel sl) {
                    var packet = getUpdatePacket();
                    if (packet != null) {
                        for (ServerPlayer player : sl.players()) {
                            if (player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) < 64 * 64) {
                                player.connection.send(packet);
                            }
                        }
                    }
                }
                return; // Stop at the first matching recipe
            }
        }

        // If no recipe matched, reset the current recipe and progress
        currentRecipe = null;
        craftProgress = 0;
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

    /**
     * Data sent to the client in the block-entity sync packet. Must include inventory, CraftProgress and CurrentRecipe
     * so the client can show the crafting animation. Default implementation may not include saveAdditional() data.
     */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        return tag;
    }

    // GETTERS AND SETTERS
    public AlchemyRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public int getCraftProgress() {
        return craftProgress;
    }
}