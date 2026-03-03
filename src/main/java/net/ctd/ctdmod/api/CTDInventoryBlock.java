package net.ctd.ctdmod.api;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.ItemStackHandler;

/*
* Interface for blocks that have an inventory (e.g. a chest or a cauldron).
* @param <T> The type of object stored in the inventory (e.g. ItemStack or FluidStack)
* @param <H> The type of inventory handler used by the block entity (e.g. ItemStackHandler or FluidTank)
*/
public interface CTDInventoryBlock<T, H>{

    // Add an object to the block entity's inventory (e.g. an item stack or a fluid stack)
    void addObject(T object, @Nullable Player player);
    // Remove an object from the block entity's inventory (e.g. an item stack or a fluid stack)
    T removeObject(int slot, @Nullable Player player);
    // Get an object from the block entity's inventory (e.g. an item stack or a fluid stack)
    T getObject(int slot);
    // Get the number of slots in the block entity's inventory
    int getInventorySize();
    // Get the block entity's inventory handler (e.g. an ItemStackHandler or a FluidTank)
    H getInventory();
}
