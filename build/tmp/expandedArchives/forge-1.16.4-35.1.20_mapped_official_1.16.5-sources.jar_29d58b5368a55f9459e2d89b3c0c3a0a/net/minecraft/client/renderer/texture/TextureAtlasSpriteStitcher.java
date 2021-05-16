package net.minecraft.client.renderer.texture;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.data.VillagerMetadataSection;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureAtlasSpriteStitcher implements IMetadataSectionSerializer<VillagerMetadataSection> {
   public VillagerMetadataSection fromJson(JsonObject p_195812_1_) {
      return new VillagerMetadataSection(VillagerMetadataSection.HatType.getByName(JSONUtils.getAsString(p_195812_1_, "hat", "none")));
   }

   public String getMetadataSectionName() {
      return "villager";
   }
}
