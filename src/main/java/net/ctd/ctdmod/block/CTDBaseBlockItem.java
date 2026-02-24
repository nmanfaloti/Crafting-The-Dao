package net.ctd.ctdmod.block;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class CTDBaseBlockItem extends BlockItem {
  private final CTDBaseBlock blockType;

  public CTDBaseBlockItem(Block id, Properties props) {
    super(id, props);
    this.blockType = (CTDBaseBlock) id;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public final void appendHoverText(ItemStack itemStack, Item.TooltipContext context, List<Component> toolTip,
      TooltipFlag advancedTooltips) {
    this.addCheckedInformation(itemStack, context, toolTip, advancedTooltips);
  }

  @OnlyIn(Dist.CLIENT)
  public void addCheckedInformation(ItemStack itemStack, TooltipContext context, List<Component> toolTip,
      TooltipFlag advancedTooltips) {
    this.blockType.appendHoverText(itemStack, context, toolTip, advancedTooltips);
  }

  @Override
  public boolean isBookEnchantable(final ItemStack itemstack1, final ItemStack itemstack2) {
    return false;
  }

  public String getDescriptionId(ItemStack is) {
    return this.blockType.getDescriptionId();
  }
}
