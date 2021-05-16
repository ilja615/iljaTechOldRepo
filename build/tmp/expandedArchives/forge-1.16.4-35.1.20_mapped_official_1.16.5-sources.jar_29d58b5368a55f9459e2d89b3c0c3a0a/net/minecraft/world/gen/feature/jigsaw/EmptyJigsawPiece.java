package net.minecraft.world.gen.feature.jigsaw;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class EmptyJigsawPiece extends JigsawPiece {
   public static final Codec<EmptyJigsawPiece> CODEC;
   public static final EmptyJigsawPiece INSTANCE = new EmptyJigsawPiece();

   private EmptyJigsawPiece() {
      super(JigsawPattern.PlacementBehaviour.TERRAIN_MATCHING);
   }

   public List<Template.BlockInfo> getShuffledJigsawBlocks(TemplateManager p_214849_1_, BlockPos p_214849_2_, Rotation p_214849_3_, Random p_214849_4_) {
      return Collections.emptyList();
   }

   public MutableBoundingBox getBoundingBox(TemplateManager p_214852_1_, BlockPos p_214852_2_, Rotation p_214852_3_) {
      return MutableBoundingBox.getUnknownBox();
   }

   public boolean place(TemplateManager p_230378_1_, ISeedReader p_230378_2_, StructureManager p_230378_3_, ChunkGenerator p_230378_4_, BlockPos p_230378_5_, BlockPos p_230378_6_, Rotation p_230378_7_, MutableBoundingBox p_230378_8_, Random p_230378_9_, boolean p_230378_10_) {
      return true;
   }

   public IJigsawDeserializer<?> getType() {
      return IJigsawDeserializer.EMPTY;
   }

   public String toString() {
      return "Empty";
   }

   static {
      CODEC = Codec.unit(() -> {
         return INSTANCE;
      });
   }
}
