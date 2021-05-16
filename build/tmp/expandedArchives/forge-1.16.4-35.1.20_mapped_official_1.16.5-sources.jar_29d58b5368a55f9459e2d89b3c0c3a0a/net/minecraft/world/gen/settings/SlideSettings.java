package net.minecraft.world.gen.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SlideSettings {
   public static final Codec<SlideSettings> CODEC = RecordCodecBuilder.create((p_236187_0_) -> {
      return p_236187_0_.group(Codec.INT.fieldOf("target").forGetter(SlideSettings::target), Codec.intRange(0, 256).fieldOf("size").forGetter(SlideSettings::size), Codec.INT.fieldOf("offset").forGetter(SlideSettings::offset)).apply(p_236187_0_, SlideSettings::new);
   });
   private final int target;
   private final int size;
   private final int offset;

   public SlideSettings(int p_i231911_1_, int p_i231911_2_, int p_i231911_3_) {
      this.target = p_i231911_1_;
      this.size = p_i231911_2_;
      this.offset = p_i231911_3_;
   }

   public int target() {
      return this.target;
   }

   public int size() {
      return this.size;
   }

   public int offset() {
      return this.offset;
   }
}
