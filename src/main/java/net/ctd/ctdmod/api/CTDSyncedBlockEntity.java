package net.ctd.ctdmod.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public interface CTDSyncedBlockEntity {
    /*
     * Mark the entity as changed to trigger a save and a network update to clients.
     */
    public void setChanged();
    /**
     * Send the packet to the clients when the block entity is updated.
     */
    ClientboundBlockEntityDataPacket getUpdatePacket();

    /**
     * Make the tag sent to the clients when the block entity is updated.
     */
    CompoundTag getUpdateTag(HolderLookup.Provider registries);

    /**
     * Handle the tag received from the server when the block entity is updated.
     */
    void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries);
}
