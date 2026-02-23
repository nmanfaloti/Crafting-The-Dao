package net.ctd.ctdmod.items;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

public class CTDBaseItem extends Item {
    public CTDBaseItem(Properties properties) {
        super(properties);
    }

    @Nullable
    public ResourceLocation getRegisteryName() {
        var id = BuiltInRegistries.ITEM.getKey(this);
        return id != BuiltInRegistries.ITEM.getDefaultKey() ? id : null;
    }

    public void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output){
        output.accept(this);
    }

    public void addToAlchemyCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output){
        output.accept(this);
    }

    @Override
    public String toString(){
        String regName = this.getRegisteryName() != null ? this.getRegisteryName().getPath() : "unregistered";
        return this.getClass().getSimpleName() + "[" + regName + "]";
    }
}
