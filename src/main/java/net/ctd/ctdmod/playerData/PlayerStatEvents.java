package net.ctd.ctdmod.playerData;

import net.ctd.ctdmod.CTDMod;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = CTDMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class PlayerStatEvents {
    private PlayerStatEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        PlayerStat.forceInitializeDefaults(serverPlayer);
        PlayerStat.initializeDefaultsIfNeeded(serverPlayer);

        int loginCount = PlayerStat.incrementLoginCount(serverPlayer);
        String stats = PlayerStat.viewStats(serverPlayer);
        CTDMod.LOGGER.info("{} has connected {} time(s)\n{}", serverPlayer.getGameProfile().getName(), loginCount, stats);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerStat.copyAllStats(event.getOriginal(), event.getEntity());
    }
}