package net.ctd.ctdmod.blockentity.entity.alchemy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.ctd.ctdmod.customrecipe.AnimatedCraftingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class AlchemyCauldronRenderer implements BlockEntityRenderer<AlchemyCauldronEntity> {
    public AlchemyCauldronRenderer(BlockEntityRendererProvider.Context context) {

    }

    private void renderClassicItemHolder(AlchemyCauldronEntity pBlockEntity, float pPartialTick,
                PoseStack pPostStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay, ItemRenderer itemRenderer) {
        // Get the index of the last non-empty slot to forme the circle
        int itemCount = pBlockEntity.getLastNonEmptySlot();
        if (itemCount == 0) { // If there is only one item, render it in the center of the cauldron
            ItemStack stack = pBlockEntity.inventory.getStackInSlot(0);
            pPostStack.pushPose();
            pPostStack.translate(0.5, 0.35, 0.5);
            pPostStack.scale(0.2f, 0.2f, 0.2f);
            // Rotate around the Y axis updated every tick
            pPostStack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getRenderingRotation()));

            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(),
                    pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPostStack, pBufferSource, pBlockEntity.getLevel(), 1);
            pPostStack.popPose();
        }else{
            float itemRotation = pBlockEntity.getRenderingRotation();
            for (int i = 0; i <= itemCount; i++){
                ItemStack stack = pBlockEntity.inventory.getStackInSlot(i);

                if (!stack.isEmpty()) {
                    double angle = (2 * Math.PI / (itemCount + 1)) * (i + 1); // Angle for the circle (+ 1 to avoid 0 angle for the first item)
                    double radius = 0.25; // Circle radius
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;

                    pPostStack.pushPose();
                    pPostStack.translate(0.5 + x, 0.35, 0.5 + z);
                    pPostStack.scale(0.2f, 0.2f, 0.2f);
                    // Rotate around the Y axis updated every tick
                    pPostStack.mulPose(Axis.YP.rotationDegrees(itemRotation));

                    
                    itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, getLightLevel(pBlockEntity.getLevel(),
                    pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPostStack, pBufferSource, pBlockEntity.getLevel(), 1);
                    pPostStack.popPose();
                }
            }
        }
    }

    @Override
    public void render(AlchemyCauldronEntity pBlockEntity, float pPartialTick,
                    PoseStack pPostStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();


        if (pBlockEntity.getCurrentRecipe() != null && pBlockEntity.getCurrentRecipe() instanceof AnimatedCraftingRecipe<AlchemyCauldronEntity, AlchemyCauldronRenderer> animatedRecipe) {
            animatedRecipe.render(pBlockEntity, pPartialTick, pPostStack, pBufferSource, pPackedLight, pPackedOverlay, itemRenderer, this);
        } else {
            renderClassicItemHolder(pBlockEntity, pPartialTick, pPostStack, pBufferSource, pPackedLight, pPackedOverlay, itemRenderer);
        }
    }

    public int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}