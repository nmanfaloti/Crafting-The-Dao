package net.ctd.ctdmod.core.definition;

import com.mojang.serialization.Codec;

import net.ctd.ctdmod.CTDMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CTDDataComponents {
    public static final DeferredRegister.DataComponents DR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CTDMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GRADE = 
        DR.register("grade", () -> DataComponentType.<Integer>builder()
            .persistent(Codec.INT) //Save to NBT
            .networkSynchronized(ByteBufCodecs.VAR_INT) //Sync
            .build()
        );

    public static void register(IEventBus eventBus) {
        DR.register(eventBus);
    }
}
