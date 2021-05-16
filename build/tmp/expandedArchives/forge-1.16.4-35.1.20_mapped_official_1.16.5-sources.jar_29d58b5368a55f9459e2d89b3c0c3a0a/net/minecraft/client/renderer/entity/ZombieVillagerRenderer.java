package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.client.renderer.entity.model.ZombieVillagerModel;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieVillagerRenderer extends BipedRenderer<ZombieVillagerEntity, ZombieVillagerModel<ZombieVillagerEntity>> {
   private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

   public ZombieVillagerRenderer(EntityRendererManager p_i50952_1_, IReloadableResourceManager p_i50952_2_) {
      super(p_i50952_1_, new ZombieVillagerModel<>(0.0F, false), 0.5F);
      this.addLayer(new BipedArmorLayer<>(this, new ZombieVillagerModel(0.5F, true), new ZombieVillagerModel(1.0F, true)));
      this.addLayer(new VillagerLevelPendantLayer<>(this, p_i50952_2_, "zombie_villager"));
   }

   public ResourceLocation getTextureLocation(ZombieVillagerEntity p_110775_1_) {
      return ZOMBIE_VILLAGER_LOCATION;
   }

   protected boolean isShaking(ZombieVillagerEntity p_230495_1_) {
      return p_230495_1_.isConverting();
   }
}
