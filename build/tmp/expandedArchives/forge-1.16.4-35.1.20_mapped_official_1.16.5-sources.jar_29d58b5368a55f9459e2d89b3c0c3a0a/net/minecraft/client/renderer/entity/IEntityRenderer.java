package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IEntityRenderer<T extends Entity, M extends EntityModel<T>> {
   M getModel();

   ResourceLocation getTextureLocation(T p_110775_1_);
}
