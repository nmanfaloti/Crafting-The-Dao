package net.ctd.ctdmod.client;

import net.ctd.ctdmod.CTDMod;
import net.ctd.ctdmod.items.GradedItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = CTDMod.MODID, value = Dist.CLIENT)
public class CTDTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        
        // Get the item grade
        int grade = GradedItemStack.getGradeFromStack(stack);

        if (grade > 0) {
            List<Component> tooltip = event.getToolTip();
            
            if (Screen.hasShiftDown() && grade > 0) {
                tooltip.add(Component.literal("Grade: " + grade).withStyle(ChatFormatting.GOLD));
            } else if (grade > 0) {
                tooltip.add(Component.literal("Press SHIFT to see grade").withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}