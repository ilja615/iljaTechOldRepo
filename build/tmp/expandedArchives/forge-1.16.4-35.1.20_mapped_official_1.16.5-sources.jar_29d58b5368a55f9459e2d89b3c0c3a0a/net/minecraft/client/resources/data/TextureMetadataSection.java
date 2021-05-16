package net.minecraft.client.resources.data;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureMetadataSection {
   public static final TextureMetadataSectionSerializer SERIALIZER = new TextureMetadataSectionSerializer();
   private final boolean blur;
   private final boolean clamp;

   public TextureMetadataSection(boolean p_i46538_1_, boolean p_i46538_2_) {
      this.blur = p_i46538_1_;
      this.clamp = p_i46538_2_;
   }

   public boolean isBlur() {
      return this.blur;
   }

   public boolean isClamp() {
      return this.clamp;
   }
}
