package net.ctd.ctdmod.network.payload;

import net.ctd.ctdmod.CTDMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MeditatePayload() implements CustomPacketPayload {
    public static final Type<MeditatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "meditate"));

    public static final StreamCodec<FriendlyByteBuf, MeditatePayload> STREAM_CODEC =
            StreamCodec.unit(new MeditatePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}