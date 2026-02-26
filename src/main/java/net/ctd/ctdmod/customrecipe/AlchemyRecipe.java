package net.ctd.ctdmod.customrecipe;


import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.ctd.ctdmod.blockentity.entity.alchemy.AlchemyCauldronEntity;
import net.ctd.ctdmod.blockentity.entity.alchemy.AlchemyCauldronRenderer;
import net.ctd.ctdmod.items.GradedItemStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

//log import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlchemyRecipe extends CTDBaseRecipe implements AnimatedCraftingRecipe<AlchemyCauldronEntity, AlchemyCauldronRenderer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlchemyRecipe.class);
    public AlchemyRecipe(String name, List<CTDIngredient> ingredients, Supplier<?> resultFactory, int craftTime){
        super(name, ingredients, resultFactory, craftTime);
    }

    public void render(AlchemyCauldronEntity pBlockEntity, float pPartialTick,
            PoseStack pPostStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay, ItemRenderer itemRenderer, AlchemyCauldronRenderer renderer) {
        float itemRotation = pBlockEntity.getRenderingRotation();
        int itemCount = pBlockEntity.getLastNonEmptySlot();
    
        
        float progress = (float) pBlockEntity.getCraftProgress() / this.craftTime;
        double dynamicRadius = 0.25 * (1.0 - progress); // 0.25 au début, 0 à la fin

        for (int i = 0; i <= itemCount; i++) {
            ItemStack stack = pBlockEntity.inventory.getStackInSlot(i);

            if (!stack.isEmpty()) {
                double angle = (2 * Math.PI / (itemCount + 1)) * (i + 1);
                double x = Math.cos(angle) * dynamicRadius;
                double z = Math.sin(angle) * dynamicRadius;

                pPostStack.pushPose();
                pPostStack.translate(0.5 + x, 0.35, 0.5 + z);
                pPostStack.scale(0.2f, 0.2f, 0.2f);
                pPostStack.mulPose(Axis.YP.rotationDegrees(itemRotation));

                itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, renderer.getLightLevel(pBlockEntity.getLevel(),
                    pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPostStack, pBufferSource, pBlockEntity.getLevel(), 1);
                pPostStack.popPose();
            }
        }
    }

    public int generateResulteGrade(List<ItemStack> ingredients, Object result, Level level) {
        float averageIngredientGrade = 0;
        int gradedItemsCount = 0;
        // Calculate the average grade of the ingredients
        for (ItemStack stack : ingredients) {
            int g = GradedItemStack.getGradeFromStack(stack);
            if (g > 0) {
                averageIngredientGrade += g;
                gradedItemsCount++;
            }
        }
        if (gradedItemsCount > 0) {
            averageIngredientGrade /= gradedItemsCount;
        }else averageIngredientGrade = 1;
        
        // Introduce some randomness to the final grade, influenced by environemnts
        float stability = 0.5f;
        if (level.isThundering()) {
            stability += 0.2f;
        }
        float rngFactor = (float) level.random.nextGaussian() * (1.0f - stability);

        float finalGrade = averageIngredientGrade + (rngFactor * 2.0f);
        return Math.max(1, Math.round(finalGrade)); // Ensure at least grade 1
    }

    @Nullable
    public ItemStack generateResult(List<ItemStack> ingredients, Level level) {
        if (this.resultFactory.get() instanceof ItemStack itemResult) {
            int grade = generateResulteGrade(ingredients, itemResult, level);   
            LOGGER.info("GRADE :  result with grade {} for recipe {} with ingredients : {}", grade, this.name, ingredients);
            GradedItemStack gradedResult = GradedItemStack.createFromStack(itemResult, grade);
            return gradedResult.getStack();
        } else {
            LOGGER.warn("Result factory returned an unsupported type: {}", this.resultFactory.get().getClass().getSimpleName());
            return null;
        }
    }

    @Override
    public void CraftEnded(BlockEntity blockEntity, Object result) {
        if (blockEntity instanceof AlchemyCauldronEntity cauldron) {
             CraftEnded(cauldron);
        }
    }

    public void CraftEnded(AlchemyCauldronEntity blockEntity) {
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        if (level != null && !level.isClientSide()) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
            
            if (lightning != null) {
                lightning.moveTo(Vec3.atBottomCenterOf(pos));
                level.addFreshEntity(lightning);
            }
        }
    }
}