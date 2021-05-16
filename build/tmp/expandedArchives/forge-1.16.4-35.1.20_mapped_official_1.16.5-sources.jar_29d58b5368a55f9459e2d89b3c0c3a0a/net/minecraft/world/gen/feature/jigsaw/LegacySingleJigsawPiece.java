package net.minecraft.world.gen.feature.jigsaw;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.feature.template.Template;

public class LegacySingleJigsawPiece extends SingleJigsawPiece {
   public static final Codec<LegacySingleJigsawPiece> CODEC = RecordCodecBuilder.create((p_236833_0_) -> {
      return p_236833_0_.group(templateCodec(), processorsCodec(), projectionCodec()).apply(p_236833_0_, LegacySingleJigsawPiece::new);
   });

   protected LegacySingleJigsawPiece(Either<ResourceLocation, Template> p_i242007_1_, Supplier<StructureProcessorList> p_i242007_2_, JigsawPattern.PlacementBehaviour p_i242007_3_) {
      super(p_i242007_1_, p_i242007_2_, p_i242007_3_);
   }

   protected PlacementSettings getSettings(Rotation p_230379_1_, MutableBoundingBox p_230379_2_, boolean p_230379_3_) {
      PlacementSettings placementsettings = super.getSettings(p_230379_1_, p_230379_2_, p_230379_3_);
      placementsettings.popProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
      placementsettings.addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR);
      return placementsettings;
   }

   public IJigsawDeserializer<?> getType() {
      return IJigsawDeserializer.LEGACY;
   }

   public String toString() {
      return "LegacySingle[" + this.template + "]";
   }
}
