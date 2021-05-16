package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FeatureJigsawPiece extends JigsawPiece {
   public static final Codec<FeatureJigsawPiece> CODEC = RecordCodecBuilder.create((p_236817_0_) -> {
      return p_236817_0_.group(ConfiguredFeature.CODEC.fieldOf("feature").forGetter((p_236818_0_) -> {
         return p_236818_0_.feature;
      }), projectionCodec()).apply(p_236817_0_, FeatureJigsawPiece::new);
   });
   private final Supplier<ConfiguredFeature<?, ?>> feature;
   private final CompoundNBT defaultJigsawNBT;

   protected FeatureJigsawPiece(Supplier<ConfiguredFeature<?, ?>> p_i242004_1_, JigsawPattern.PlacementBehaviour p_i242004_2_) {
      super(p_i242004_2_);
      this.feature = p_i242004_1_;
      this.defaultJigsawNBT = this.fillDefaultJigsawNBT();
   }

   private CompoundNBT fillDefaultJigsawNBT() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("name", "minecraft:bottom");
      compoundnbt.putString("final_state", "minecraft:air");
      compoundnbt.putString("pool", "minecraft:empty");
      compoundnbt.putString("target", "minecraft:empty");
      compoundnbt.putString("joint", JigsawTileEntity.OrientationType.ROLLABLE.getSerializedName());
      return compoundnbt;
   }

   public BlockPos getSize(TemplateManager p_214868_1_, Rotation p_214868_2_) {
      return BlockPos.ZERO;
   }

   public List<Template.BlockInfo> getShuffledJigsawBlocks(TemplateManager p_214849_1_, BlockPos p_214849_2_, Rotation p_214849_3_, Random p_214849_4_) {
      List<Template.BlockInfo> list = Lists.newArrayList();
      list.add(new Template.BlockInfo(p_214849_2_, Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION, JigsawOrientation.fromFrontAndTop(Direction.DOWN, Direction.SOUTH)), this.defaultJigsawNBT));
      return list;
   }

   public MutableBoundingBox getBoundingBox(TemplateManager p_214852_1_, BlockPos p_214852_2_, Rotation p_214852_3_) {
      BlockPos blockpos = this.getSize(p_214852_1_, p_214852_3_);
      return new MutableBoundingBox(p_214852_2_.getX(), p_214852_2_.getY(), p_214852_2_.getZ(), p_214852_2_.getX() + blockpos.getX(), p_214852_2_.getY() + blockpos.getY(), p_214852_2_.getZ() + blockpos.getZ());
   }

   public boolean place(TemplateManager p_230378_1_, ISeedReader p_230378_2_, StructureManager p_230378_3_, ChunkGenerator p_230378_4_, BlockPos p_230378_5_, BlockPos p_230378_6_, Rotation p_230378_7_, MutableBoundingBox p_230378_8_, Random p_230378_9_, boolean p_230378_10_) {
      return this.feature.get().place(p_230378_2_, p_230378_4_, p_230378_9_, p_230378_5_);
   }

   public IJigsawDeserializer<?> getType() {
      return IJigsawDeserializer.FEATURE;
   }

   public String toString() {
      return "Feature[" + Registry.FEATURE.getKey(this.feature.get().feature()) + "]";
   }
}
