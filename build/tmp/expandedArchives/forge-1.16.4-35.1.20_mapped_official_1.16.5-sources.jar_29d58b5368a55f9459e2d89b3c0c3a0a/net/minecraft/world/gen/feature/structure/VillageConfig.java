package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;

public class VillageConfig implements IFeatureConfig {
   public static final Codec<VillageConfig> CODEC = RecordCodecBuilder.create((p_236535_0_) -> {
      return p_236535_0_.group(JigsawPattern.CODEC.fieldOf("start_pool").forGetter(VillageConfig::startPool), Codec.intRange(0, 7).fieldOf("size").forGetter(VillageConfig::maxDepth)).apply(p_236535_0_, VillageConfig::new);
   });
   private final Supplier<JigsawPattern> startPool;
   private final int maxDepth;

   public VillageConfig(Supplier<JigsawPattern> p_i241987_1_, int p_i241987_2_) {
      this.startPool = p_i241987_1_;
      this.maxDepth = p_i241987_2_;
   }

   public int maxDepth() {
      return this.maxDepth;
   }

   public Supplier<JigsawPattern> startPool() {
      return this.startPool;
   }
}
