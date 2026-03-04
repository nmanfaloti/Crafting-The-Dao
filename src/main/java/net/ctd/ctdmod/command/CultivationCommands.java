package net.ctd.ctdmod.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.ctd.ctdmod.CTDMod;
import net.ctd.ctdmod.data.CultivationUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Commandes liées au système de cultivation.
 * <p>
 * Fournit notamment : {@code /cultivation add_qi <amount>}.
 */
@EventBusSubscriber(modid = CTDMod.MODID)
public final class CultivationCommands {

    private CultivationCommands() {
        // Classe utilitaire : pas d'instanciation.
    }

    /**
     * Enregistre les commandes de cultivation lors du rechargement des commandes.
     *
     * @param event évènement de registre des commandes
     */
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("cultivation")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("add_qi")
                .then(Commands.argument("amount", FloatArgumentType.floatArg())
                    .executes(ctx -> {
                        CommandSourceStack source = ctx.getSource();
                        ServerPlayer player = source.getPlayer();
                        if (player == null) {
                            source.sendFailure(Component.literal("Cette commande doit être exécutée par un joueur."));
                            return 0;
                        }

                        float amount = FloatArgumentType.getFloat(ctx, "amount");
                        CultivationUtil.modifyQi(player, amount);

                        source.sendSuccess(
                            () -> Component.literal("Ajout de " + amount + " Qi au joueur " + player.getName().getString()),
                            true
                        );
                        return 1;
                    })
                )
            )
            .then(Commands.literal("reset")
                .executes(ctx -> {
                    CommandSourceStack source = ctx.getSource();
                    ServerPlayer player = source.getPlayer();
                    if (player == null) {
                        source.sendFailure(Component.literal("Cette commande doit être exécutée par un joueur."));
                        return 0;
                    }

                    CultivationUtil.resetCultivation(player);

                    source.sendSuccess(
                        () -> Component.literal("Cultivation réinitialisée pour " + player.getName().getString()),
                        true
                    );
                    return 1;
                })
            );

        event.getDispatcher().register(root);
    }
}

