package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHeadToggle;
import net.minecraft.client.resources.data.VillagerMetadataSection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerLevelPendantLayer<T extends LivingEntity & IVillagerDataHolder, M extends EntityModel<T> & IHeadToggle> extends LayerRenderer<T, M> implements IResourceManagerReloadListener {
   private static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS = Util.make(new Int2ObjectOpenHashMap<>(), (p_215348_0_) -> {
      p_215348_0_.put(1, new ResourceLocation("stone"));
      p_215348_0_.put(2, new ResourceLocation("iron"));
      p_215348_0_.put(3, new ResourceLocation("gold"));
      p_215348_0_.put(4, new ResourceLocation("emerald"));
      p_215348_0_.put(5, new ResourceLocation("diamond"));
   });
   private final Object2ObjectMap<VillagerType, VillagerMetadataSection.HatType> typeHatCache = new Object2ObjectOpenHashMap<>();
   private final Object2ObjectMap<VillagerProfession, VillagerMetadataSection.HatType> professionHatCache = new Object2ObjectOpenHashMap<>();
   private final IReloadableResourceManager resourceManager;
   private final String path;

   public VillagerLevelPendantLayer(IEntityRenderer<T, M> p_i50955_1_, IReloadableResourceManager p_i50955_2_, String p_i50955_3_) {
      super(p_i50955_1_);
      this.resourceManager = p_i50955_2_;
      this.path = p_i50955_3_;
      p_i50955_2_.registerReloadListener(this);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (!p_225628_4_.isInvisible()) {
         VillagerData villagerdata = p_225628_4_.getVillagerData();
         VillagerType villagertype = villagerdata.getType();
         VillagerProfession villagerprofession = villagerdata.getProfession();
         VillagerMetadataSection.HatType villagermetadatasection$hattype = this.getHatData(this.typeHatCache, "type", Registry.VILLAGER_TYPE, villagertype);
         VillagerMetadataSection.HatType villagermetadatasection$hattype1 = this.getHatData(this.professionHatCache, "profession", Registry.VILLAGER_PROFESSION, villagerprofession);
         M m = this.getParentModel();
         m.hatVisible(villagermetadatasection$hattype1 == VillagerMetadataSection.HatType.NONE || villagermetadatasection$hattype1 == VillagerMetadataSection.HatType.PARTIAL && villagermetadatasection$hattype != VillagerMetadataSection.HatType.FULL);
         ResourceLocation resourcelocation = this.getResourceLocation("type", Registry.VILLAGER_TYPE.getKey(villagertype));
         renderColoredCutoutModel(m, resourcelocation, p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, 1.0F, 1.0F, 1.0F);
         m.hatVisible(true);
         if (villagerprofession != VillagerProfession.NONE && !p_225628_4_.isBaby()) {
            ResourceLocation resourcelocation1 = this.getResourceLocation("profession", Registry.VILLAGER_PROFESSION.getKey(villagerprofession));
            renderColoredCutoutModel(m, resourcelocation1, p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, 1.0F, 1.0F, 1.0F);
            if (villagerprofession != VillagerProfession.NITWIT) {
               ResourceLocation resourcelocation2 = this.getResourceLocation("profession_level", LEVEL_LOCATIONS.get(MathHelper.clamp(villagerdata.getLevel(), 1, LEVEL_LOCATIONS.size())));
               renderColoredCutoutModel(m, resourcelocation2, p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, 1.0F, 1.0F, 1.0F);
            }
         }

      }
   }

   private ResourceLocation getResourceLocation(String p_215351_1_, ResourceLocation p_215351_2_) {
      return new ResourceLocation(p_215351_2_.getNamespace(), "textures/entity/" + this.path + "/" + p_215351_1_ + "/" + p_215351_2_.getPath() + ".png");
   }

   public <K> VillagerMetadataSection.HatType getHatData(Object2ObjectMap<K, VillagerMetadataSection.HatType> p_215350_1_, String p_215350_2_, DefaultedRegistry<K> p_215350_3_, K p_215350_4_) {
      return p_215350_1_.computeIfAbsent(p_215350_4_, (p_215349_4_) -> {
         try (IResource iresource = this.resourceManager.getResource(this.getResourceLocation(p_215350_2_, p_215350_3_.getKey(p_215350_4_)))) {
            VillagerMetadataSection villagermetadatasection = iresource.getMetadata(VillagerMetadataSection.SERIALIZER);
            if (villagermetadatasection != null) {
               return villagermetadatasection.getHat();
            }
         } catch (IOException ioexception) {
         }

         return VillagerMetadataSection.HatType.NONE;
      });
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.professionHatCache.clear();
      this.typeHatCache.clear();
   }
}
