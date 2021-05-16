package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanRuinStructure extends Structure<OceanRuinConfig> {
   public OceanRuinStructure(Codec<OceanRuinConfig> p_i232109_1_) {
      super(p_i232109_1_);
   }

   public Structure.IStartFactory<OceanRuinConfig> getStartFactory() {
      return OceanRuinStructure.Start::new;
   }

   public static class Start extends StructureStart<OceanRuinConfig> {
      public Start(Structure<OceanRuinConfig> p_i225875_1_, int p_i225875_2_, int p_i225875_3_, MutableBoundingBox p_i225875_4_, int p_i225875_5_, long p_i225875_6_) {
         super(p_i225875_1_, p_i225875_2_, p_i225875_3_, p_i225875_4_, p_i225875_5_, p_i225875_6_);
      }

      public void generatePieces(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, OceanRuinConfig p_230364_7_) {
         int i = p_230364_4_ * 16;
         int j = p_230364_5_ * 16;
         BlockPos blockpos = new BlockPos(i, 90, j);
         Rotation rotation = Rotation.getRandom(this.random);
         OceanRuinPieces.addPieces(p_230364_3_, blockpos, rotation, this.pieces, this.random, p_230364_7_);
         this.calculateBoundingBox();
      }
   }

   public static enum Type implements IStringSerializable {
      WARM("warm"),
      COLD("cold");

      public static final Codec<OceanRuinStructure.Type> CODEC = IStringSerializable.fromEnum(OceanRuinStructure.Type::values, OceanRuinStructure.Type::byName);
      private static final Map<String, OceanRuinStructure.Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(OceanRuinStructure.Type::getName, (p_215134_0_) -> {
         return p_215134_0_;
      }));
      private final String name;

      private Type(String p_i50621_3_) {
         this.name = p_i50621_3_;
      }

      public String getName() {
         return this.name;
      }

      @Nullable
      public static OceanRuinStructure.Type byName(String p_215136_0_) {
         return BY_NAME.get(p_215136_0_);
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
