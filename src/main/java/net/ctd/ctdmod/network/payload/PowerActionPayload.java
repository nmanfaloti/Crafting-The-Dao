package net.ctd.ctdmod.network.payload;

import net.ctd.ctdmod.CTDMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PowerActionPayload() implements CustomPacketPayload {
    public static final Type<PowerActionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CTDMod.MODID, "power_action"));

    public static final StreamCodec<FriendlyByteBuf, PowerActionPayload> STREAM_CODEC =
            StreamCodec.unit(new PowerActionPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}