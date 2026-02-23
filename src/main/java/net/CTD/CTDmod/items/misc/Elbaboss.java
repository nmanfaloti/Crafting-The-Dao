package net.CTD.CTDmod.items.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;

public class Elbaboss extends Item {
    public Elbaboss(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context){
        // Dont execute on server
        if (!context.getLevel().isClientSide()) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        if (player != null){
            player.displayClientMessage(Component.literal("El Primo!"), true);
        }
        return InteractionResult.SUCCESS;
    }
}
