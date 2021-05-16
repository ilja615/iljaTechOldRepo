package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import net.minecraft.realms.IPersistentSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsWorldResetDto extends ValueObject implements IPersistentSerializable {
   @SerializedName("seed")
   private final String seed;
   @SerializedName("worldTemplateId")
   private final long worldTemplateId;
   @SerializedName("levelType")
   private final int levelType;
   @SerializedName("generateStructures")
   private final boolean generateStructures;

   public RealmsWorldResetDto(String p_i51640_1_, long p_i51640_2_, int p_i51640_4_, boolean p_i51640_5_) {
      this.seed = p_i51640_1_;
      this.worldTemplateId = p_i51640_2_;
      this.levelType = p_i51640_4_;
      this.generateStructures = p_i51640_5_;
   }
}
