package net.ctd.ctdmod.client;

import net.ctd.ctdmod.CTDMod;
import net.ctd.ctdmod.network.payload.MeditatePayload;
import net.ctd.ctdmod.network.payload.PowerActionPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CTDMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class CTDClientInputHandler {
    private CTDClientInputHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        while (CTDKeyMappings.MEDITATE.consumeClick()) {
            PacketDistributor.sendToServer(new MeditatePayload());
        }

        //demande au serveur de déclencher l'action de pouvoir chaque fois que la touche est pressée
        while (CTDKeyMappings.POWER_ACTION.consumeClick()) {
            PacketDistributor.sendToServer(new PowerActionPayload());
        }
    }
}