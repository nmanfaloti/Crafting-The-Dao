package net.CTD.CTDmod.items.weapons;

import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;

public class Katana extends SwordItem {
    public Katana() {
        // Dans NeoForge 1.21.1, on définit les dégâts (3) et la vitesse (-2.4F)
        // directement dans les propriétés de l'objet via .attributes()
        super(Tiers.IRON, new Item.Properties().attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4F)));
    }
}