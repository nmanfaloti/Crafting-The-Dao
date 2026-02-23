package net.CTD.CTDmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;

public class CTDBaseBlock extends Block {
    protected CTDBaseBlock(Properties props){
        super(props);
    }

    public static Properties defaultProps(MapColor mapColor, SoundType soundType) {
        return Properties.of()
                .strength(2.2f, 11.f)
                .mapColor(mapColor)
                .sound(soundType);
    }

    public static Properties stoneProps(){
        return defaultProps(MapColor.STONE, SoundType.STONE).forceSolidOn();
    }

    public static Properties glassProps(){
        return defaultProps(MapColor.NONE, SoundType.GLASS);
    }

    public void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output){
        output.accept(this);
    }

    @Nullable
    public ResourceLocation getRegistryName() {
        var id = BuiltInRegistries.BLOCK.getKey(this);
        return id != BuiltInRegistries.BLOCK.getDefaultKey() ? id : null;
    }

    @Override
    public String toString() {
        String regName = this.getRegistryName() != null ? this.getRegistryName().getPath() : "unregistered";
        return this.getClass().getSimpleName() + "[" + regName + "]";
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        super.spawnDestroyParticles(level, player, pos, state);
    }
}
