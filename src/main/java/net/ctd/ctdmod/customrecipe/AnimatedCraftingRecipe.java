package net.ctd.ctdmod.customrecipe;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface AnimatedCraftingRecipe<T extends BlockEntity, R extends BlockEntityRenderer<T>> {
    void render(T pBlockEntity, float pPartialTick, PoseStack pPostStack, MultiBufferSource pBufferSource,
        int pPackedLight, int pPackedOverlay, ItemRenderer itemRenderer, R renderer);
}