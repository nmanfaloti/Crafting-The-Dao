package net.CTD.CTDmod.items.weapons;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;

public class Katana extends SwordItem {
    public Katana() {
        // NeoForge 1.21.4: Tier/Tiers remplacés par ToolMaterial ; dégâts 3, vitesse -2.4F
        super(ToolMaterial.IRON, 3, -2.4F, new Item.Properties());
    }
}
