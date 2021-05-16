package net.minecraft.world.gen.feature.jigsaw;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class JigsawPiece {
   public static final Codec<JigsawPiece> CODEC = Registry.STRUCTURE_POOL_ELEMENT.dispatch("element_type", JigsawPiece::getType, IJigsawDeserializer::codec);
   @Nullable
   private volatile JigsawPattern.PlacementBehaviour projection;

   protected static <E extends JigsawPiece> RecordCodecBuilder<E, JigsawPattern.PlacementBehaviour> projectionCodec() {
      return JigsawPattern.PlacementBehaviour.CODEC.fieldOf("projection").forGetter(JigsawPiece::getProjection);
   }

   protected JigsawPiece(JigsawPattern.PlacementBehaviour p_i51398_1_) {
      this.projection = p_i51398_1_;
   }

   public abstract List<Template.BlockInfo> getShuffledJigsawBlocks(TemplateManager p_214849_1_, BlockPos p_214849_2_, Rotation p_214849_3_, Random p_214849_4_);

   public abstract MutableBoundingBox getBoundingBox(TemplateManager p_214852_1_, BlockPos p_214852_2_, Rotation p_214852_3_);

   public abstract boolean place(TemplateManager p_230378_1_, ISeedReader p_230378_2_, StructureManager p_230378_3_, ChunkGenerator p_230378_4_, BlockPos p_230378_5_, BlockPos p_230378_6_, Rotation p_230378_7_, MutableBoundingBox p_230378_8_, Random p_230378_9_, boolean p_230378_10_);

   public abstract IJigsawDeserializer<?> getType();

   public void handleDataMarker(IWorld p_214846_1_, Template.BlockInfo p_214846_2_, BlockPos p_214846_3_, Rotation p_214846_4_, Random p_214846_5_, MutableBoundingBox p_214846_6_) {
   }

   public JigsawPiece setProjection(JigsawPattern.PlacementBehaviour p_214845_1_) {
      this.projection = p_214845_1_;
      return this;
   }

   public JigsawPattern.PlacementBehaviour getProjection() {
      JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = this.projection;
      if (jigsawpattern$placementbehaviour == null) {
         throw new IllegalStateException();
      } else {
         return jigsawpattern$placementbehaviour;
      }
   }

   public int getGroundLevelDelta() {
      return 1;
   }

   public static Function<JigsawPattern.PlacementBehaviour, EmptyJigsawPiece> empty() {
      return (p_242857_0_) -> {
         return EmptyJigsawPiece.INSTANCE;
      };
   }

   public static Function<JigsawPattern.PlacementBehaviour, LegacySingleJigsawPiece> legacy(String p_242849_0_) {
      return (p_242860_1_) -> {
         return new LegacySingleJigsawPiece(Either.left(new ResourceLocation(p_242849_0_)), () -> {
            return ProcessorLists.EMPTY;
         }, p_242860_1_);
      };
   }

   public static Function<JigsawPattern.PlacementBehaviour, LegacySingleJigsawPiece> legacy(String p_242851_0_, StructureProcessorList p_242851_1_) {
      return (p_242862_2_) -> {
         return new LegacySingleJigsawPiece(Either.left(new ResourceLocation(p_242851_0_)), () -> {
            return p_242851_1_;
         }, p_242862_2_);
      };
   }

   public static Function<JigsawPattern.PlacementBehaviour, SingleJigsawPiece> single(String p_242859_0_) {
      return (p_242850_1_) -> {
         return new SingleJigsawPiece(Either.left(new ResourceLocation(p_242859_0_)), () -> {
            return ProcessorLists.EMPTY;
         }, p_242850_1_);
      };
   }

   public static Function<JigsawPattern.PlacementBehaviour, SingleJigsawPiece> single(String p_242861_0_, StructureProcessorList p_242861_1_) {
      return (p_242852_2_) -> {
         return new SingleJigsawPiece(Either.left(new ResourceLocation(p_242861_0_)), () -> {
            return p_242861_1_;
         }, p_242852_2_);
      };
   }

   public static Function<JigsawPattern.PlacementBehaviour, FeatureJigsawPiece> feature(ConfiguredFeature<?, ?> p_242845_0_) {
      return (p_242846_1_) -> {
         return new FeatureJigsawPiece(() -> {
            return p_242845_0_;
         }, p_242846_1_);
      };
   }

   public static Function<JigsawPattern.PlacementBehaviour, ListJigsawPiece> list(List<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>> p_242853_0_) {
      return (p_242854_1_) -> {
         return new ListJigsawPiece(p_242853_0_.stream().map((p_242847_1_) -> {
            return p_242847_1_.apply(p_242854_1_);
         }).collect(Collectors.toList()), p_242854_1_);
      };
   }
}
