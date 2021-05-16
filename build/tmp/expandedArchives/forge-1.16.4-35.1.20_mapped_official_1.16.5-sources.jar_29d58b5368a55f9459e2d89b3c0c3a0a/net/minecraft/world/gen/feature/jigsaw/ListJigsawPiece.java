package net.minecraft.world.gen.feature.jigsaw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class ListJigsawPiece extends JigsawPiece {
   public static final Codec<ListJigsawPiece> CODEC = RecordCodecBuilder.create((p_236835_0_) -> {
      return p_236835_0_.group(JigsawPiece.CODEC.listOf().fieldOf("elements").forGetter((p_236836_0_) -> {
         return p_236836_0_.elements;
      }), projectionCodec()).apply(p_236835_0_, ListJigsawPiece::new);
   });
   private final List<JigsawPiece> elements;

   public ListJigsawPiece(List<JigsawPiece> p_i51405_1_, JigsawPattern.PlacementBehaviour p_i51405_2_) {
      super(p_i51405_2_);
      if (p_i51405_1_.isEmpty()) {
         throw new IllegalArgumentException("Elements are empty");
      } else {
         this.elements = p_i51405_1_;
         this.setProjectionOnEachElement(p_i51405_2_);
      }
   }

   public List<Template.BlockInfo> getShuffledJigsawBlocks(TemplateManager p_214849_1_, BlockPos p_214849_2_, Rotation p_214849_3_, Random p_214849_4_) {
      return this.elements.get(0).getShuffledJigsawBlocks(p_214849_1_, p_214849_2_, p_214849_3_, p_214849_4_);
   }

   public MutableBoundingBox getBoundingBox(TemplateManager p_214852_1_, BlockPos p_214852_2_, Rotation p_214852_3_) {
      MutableBoundingBox mutableboundingbox = MutableBoundingBox.getUnknownBox();

      for(JigsawPiece jigsawpiece : this.elements) {
         MutableBoundingBox mutableboundingbox1 = jigsawpiece.getBoundingBox(p_214852_1_, p_214852_2_, p_214852_3_);
         mutableboundingbox.expand(mutableboundingbox1);
      }

      return mutableboundingbox;
   }

   public boolean place(TemplateManager p_230378_1_, ISeedReader p_230378_2_, StructureManager p_230378_3_, ChunkGenerator p_230378_4_, BlockPos p_230378_5_, BlockPos p_230378_6_, Rotation p_230378_7_, MutableBoundingBox p_230378_8_, Random p_230378_9_, boolean p_230378_10_) {
      for(JigsawPiece jigsawpiece : this.elements) {
         if (!jigsawpiece.place(p_230378_1_, p_230378_2_, p_230378_3_, p_230378_4_, p_230378_5_, p_230378_6_, p_230378_7_, p_230378_8_, p_230378_9_, p_230378_10_)) {
            return false;
         }
      }

      return true;
   }

   public IJigsawDeserializer<?> getType() {
      return IJigsawDeserializer.LIST;
   }

   public JigsawPiece setProjection(JigsawPattern.PlacementBehaviour p_214845_1_) {
      super.setProjection(p_214845_1_);
      this.setProjectionOnEachElement(p_214845_1_);
      return this;
   }

   public String toString() {
      return "List[" + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
   }

   private void setProjectionOnEachElement(JigsawPattern.PlacementBehaviour p_214864_1_) {
      this.elements.forEach((p_214863_1_) -> {
         p_214863_1_.setProjection(p_214864_1_);
      });
   }
}
