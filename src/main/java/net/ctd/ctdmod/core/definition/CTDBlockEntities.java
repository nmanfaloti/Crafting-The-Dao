package net.ctd.ctdmod.core.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import net.ctd.ctdmod.CTDMod;
import net.ctd.ctdmod.blockentity.entity.alchemy.AlchemyCauldronEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CTDBlockEntities {
    private static final List<DeferredBlockEntityType<?>> BLOCK_ENTITY_TYPES = new ArrayList<>();


    public static final DeferredRegister<BlockEntityType<?>> DR = 
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CTDMod.MODID);


    public static final DeferredBlockEntityType<AlchemyCauldronEntity> ALCHEMY_CAULDRON =
        create(
            "alchemy_cauldron",
            AlchemyCauldronEntity.class,
            CTDBlocks.ALCHEMY_CAULDRON
        ); 

    public static void register(IEventBus eventBus) {
        DR.register(eventBus);
    }

    private static <T extends BlockEntity> DeferredBlockEntityType<T> create(
            String englishName,
            Class<T> entityClass,
            BlockDefinition<?> blockDefinition
        ){
            var deferredHolder = DR.register(englishName, () -> {
                AtomicReference<BlockEntityType<T>> typeHolder = new AtomicReference<>();
                // Create the supplier that will be used to create block entities of this type
                BlockEntitySupplier<T> supplier = (pos, state) -> {
                    try {
                        return entityClass.getConstructor(BlockPos.class, BlockState.class).newInstance(pos, state);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create block entity instance for " + englishName, e);
                    }
                };
                var type = BlockEntityType.Builder.of(supplier, blockDefinition.block()).build(null);
                typeHolder.set(type);

                return type;
            });
            @SuppressWarnings("unchecked")
            var typedHolder = (DeferredHolder<BlockEntityType<?>, BlockEntityType<T>>) deferredHolder;
            var result = new DeferredBlockEntityType<>(entityClass, typedHolder);
            BLOCK_ENTITY_TYPES.add(result);
            return result;
        }
}