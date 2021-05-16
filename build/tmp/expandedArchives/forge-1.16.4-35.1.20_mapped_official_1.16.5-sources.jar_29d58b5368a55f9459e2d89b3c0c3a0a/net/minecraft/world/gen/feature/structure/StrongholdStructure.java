package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class StrongholdStructure extends Structure<NoFeatureConfig> {
   public StrongholdStructure(Codec<NoFeatureConfig> p_i231996_1_) {
      super(p_i231996_1_);
   }

   public Structure.IStartFactory<NoFeatureConfig> getStartFactory() {
      return StrongholdStructure.Start::new;
   }

   protected boolean isFeatureChunk(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoFeatureConfig p_230363_10_) {
      return p_230363_1_.hasStronghold(new ChunkPos(p_230363_6_, p_230363_7_));
   }

   public static class Start extends StructureStart<NoFeatureConfig> {
      private final long seed;

      public Start(Structure<NoFeatureConfig> p_i225818_1_, int p_i225818_2_, int p_i225818_3_, MutableBoundingBox p_i225818_4_, int p_i225818_5_, long p_i225818_6_) {
         super(p_i225818_1_, p_i225818_2_, p_i225818_3_, p_i225818_4_, p_i225818_5_, p_i225818_6_);
         this.seed = p_i225818_6_;
      }

      public void generatePieces(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, NoFeatureConfig p_230364_7_) {
         int i = 0;

         StrongholdPieces.Stairs2 strongholdpieces$stairs2;
         do {
            this.pieces.clear();
            this.boundingBox = MutableBoundingBox.getUnknownBox();
            this.random.setLargeFeatureSeed(this.seed + (long)(i++), p_230364_4_, p_230364_5_);
            StrongholdPieces.resetPieces();
            strongholdpieces$stairs2 = new StrongholdPieces.Stairs2(this.random, (p_230364_4_ << 4) + 2, (p_230364_5_ << 4) + 2);
            this.pieces.add(strongholdpieces$stairs2);
            strongholdpieces$stairs2.addChildren(strongholdpieces$stairs2, this.pieces, this.random);
            List<StructurePiece> list = strongholdpieces$stairs2.pendingChildren;

            while(!list.isEmpty()) {
               int j = this.random.nextInt(list.size());
               StructurePiece structurepiece = list.remove(j);
               structurepiece.addChildren(strongholdpieces$stairs2, this.pieces, this.random);
            }

            this.calculateBoundingBox();
            this.moveBelowSeaLevel(p_230364_2_.getSeaLevel(), this.random, 10);
         } while(this.pieces.isEmpty() || strongholdpieces$stairs2.portalRoomPiece == null);

      }
   }
}
