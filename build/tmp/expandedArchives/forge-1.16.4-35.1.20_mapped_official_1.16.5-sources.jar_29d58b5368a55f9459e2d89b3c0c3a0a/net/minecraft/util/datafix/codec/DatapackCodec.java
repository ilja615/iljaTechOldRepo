package net.minecraft.util.datafix.codec;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class DatapackCodec {
   public static final DatapackCodec DEFAULT = new DatapackCodec(ImmutableList.of("vanilla"), ImmutableList.of());
   public static final Codec<DatapackCodec> CODEC = RecordCodecBuilder.create((p_234886_0_) -> {
      return p_234886_0_.group(Codec.STRING.listOf().fieldOf("Enabled").forGetter((p_234888_0_) -> {
         return p_234888_0_.enabled;
      }), Codec.STRING.listOf().fieldOf("Disabled").forGetter((p_234885_0_) -> {
         return p_234885_0_.disabled;
      })).apply(p_234886_0_, DatapackCodec::new);
   });
   private final List<String> enabled;
   private final List<String> disabled;

   public DatapackCodec(List<String> p_i231607_1_, List<String> p_i231607_2_) {
      this.enabled = com.google.common.collect.Lists.newArrayList(p_i231607_1_);
      this.disabled = ImmutableList.copyOf(p_i231607_2_);
   }

   public List<String> getEnabled() {
      return this.enabled;
   }

   public List<String> getDisabled() {
      return this.disabled;
   }

   public void addModPacks(List<String> modPacks) {
      enabled.addAll(modPacks.stream().filter(p->!enabled.contains(p)).collect(java.util.stream.Collectors.toList()));
   }
}
