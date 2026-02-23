package net.ctd.ctdmod.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class CTDBaseBlockItem extends BlockItem {
    private final CTDBaseBlock blockType;

    public CTDBaseBlockItem(Block id, Properties props){
        super(id, props);
        this.blockType = (CTDBaseBlock) id;
    }

    @Override
    public boolean isBookEnchantable(final ItemStack itemstack1, final ItemStack itemstack2){
        return false;
    }

    public String getDescriptionId(ItemStack is){
        return this.blockType.getDescriptionId();
    }
}
