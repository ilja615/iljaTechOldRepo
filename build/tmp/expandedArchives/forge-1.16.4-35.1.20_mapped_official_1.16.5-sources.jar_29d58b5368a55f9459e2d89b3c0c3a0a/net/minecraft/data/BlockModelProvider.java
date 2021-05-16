package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallHeight;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BambooLeaves;
import net.minecraft.state.properties.BellAttachment;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.PistonType;
import net.minecraft.state.properties.RailShape;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawOrientation;

public class BlockModelProvider {
   private final Consumer<IFinishedBlockState> blockStateOutput;
   private final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;
   private final Consumer<Item> skippedAutoModelsOutput;

   public BlockModelProvider(Consumer<IFinishedBlockState> p_i232514_1_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_i232514_2_, Consumer<Item> p_i232514_3_) {
      this.blockStateOutput = p_i232514_1_;
      this.modelOutput = p_i232514_2_;
      this.skippedAutoModelsOutput = p_i232514_3_;
   }

   private void skipAutoItemBlock(Block p_239869_1_) {
      this.skippedAutoModelsOutput.accept(p_239869_1_.asItem());
   }

   private void delegateItemModel(Block p_239957_1_, ResourceLocation p_239957_2_) {
      this.modelOutput.accept(ModelsResourceUtil.getModelLocation(p_239957_1_.asItem()), new BlockModelWriter(p_239957_2_));
   }

   private void delegateItemModel(Item p_239867_1_, ResourceLocation p_239867_2_) {
      this.modelOutput.accept(ModelsResourceUtil.getModelLocation(p_239867_1_), new BlockModelWriter(p_239867_2_));
   }

   private void createSimpleFlatItemModel(Item p_239866_1_) {
      StockModelShapes.FLAT_ITEM.create(ModelsResourceUtil.getModelLocation(p_239866_1_), ModelTextures.layer0(p_239866_1_), this.modelOutput);
   }

   private void createSimpleFlatItemModel(Block p_239934_1_) {
      Item item = p_239934_1_.asItem();
      if (item != Items.AIR) {
         StockModelShapes.FLAT_ITEM.create(ModelsResourceUtil.getModelLocation(item), ModelTextures.layer0(p_239934_1_), this.modelOutput);
      }

   }

   private void createSimpleFlatItemModel(Block p_239885_1_, String p_239885_2_) {
      Item item = p_239885_1_.asItem();
      StockModelShapes.FLAT_ITEM.create(ModelsResourceUtil.getModelLocation(item), ModelTextures.layer0(ModelTextures.getBlockTexture(p_239885_1_, p_239885_2_)), this.modelOutput);
   }

   private static BlockStateVariantBuilder createHorizontalFacingDispatch() {
      return BlockStateVariantBuilder.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.NORTH, BlockModelDefinition.variant());
   }

   private static BlockStateVariantBuilder createHorizontalFacingDispatchAlt() {
      return BlockStateVariantBuilder.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.SOUTH, BlockModelDefinition.variant()).select(Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270));
   }

   private static BlockStateVariantBuilder createTorchHorizontalDispatch() {
      return BlockStateVariantBuilder.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, BlockModelDefinition.variant()).select(Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270));
   }

   private static BlockStateVariantBuilder createFacingDispatch() {
      return BlockStateVariantBuilder.property(BlockStateProperties.FACING).select(Direction.DOWN, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90)).select(Direction.UP, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270)).select(Direction.NORTH, BlockModelDefinition.variant()).select(Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90));
   }

   private static FinishedVariantBlockState createRotatedVariant(Block p_239968_0_, ResourceLocation p_239968_1_) {
      return FinishedVariantBlockState.multiVariant(p_239968_0_, createRotatedVariants(p_239968_1_));
   }

   private static BlockModelDefinition[] createRotatedVariants(ResourceLocation p_239915_0_) {
      return new BlockModelDefinition[]{BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239915_0_), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239915_0_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239915_0_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239915_0_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)};
   }

   private static FinishedVariantBlockState createRotatedVariant(Block p_239979_0_, ResourceLocation p_239979_1_, ResourceLocation p_239979_2_) {
      return FinishedVariantBlockState.multiVariant(p_239979_0_, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239979_1_), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239979_2_), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239979_1_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239979_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180));
   }

   private static BlockStateVariantBuilder createBooleanModelDispatch(BooleanProperty p_239894_0_, ResourceLocation p_239894_1_, ResourceLocation p_239894_2_) {
      return BlockStateVariantBuilder.property(p_239894_0_).select(true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239894_1_)).select(false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239894_2_));
   }

   private void createRotatedMirroredVariantBlock(Block p_239953_1_) {
      ResourceLocation resourcelocation = TexturedModel.CUBE.create(p_239953_1_, this.modelOutput);
      ResourceLocation resourcelocation1 = TexturedModel.CUBE_MIRRORED.create(p_239953_1_, this.modelOutput);
      this.blockStateOutput.accept(createRotatedVariant(p_239953_1_, resourcelocation, resourcelocation1));
   }

   private void createRotatedVariantBlock(Block p_239965_1_) {
      ResourceLocation resourcelocation = TexturedModel.CUBE.create(p_239965_1_, this.modelOutput);
      this.blockStateOutput.accept(createRotatedVariant(p_239965_1_, resourcelocation));
   }

   private static IFinishedBlockState createButton(Block p_239987_0_, ResourceLocation p_239987_1_, ResourceLocation p_239987_2_) {
      return FinishedVariantBlockState.multiVariant(p_239987_0_).with(BlockStateVariantBuilder.property(BlockStateProperties.POWERED).select(false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239987_1_)).select(true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239987_2_))).with(BlockStateVariantBuilder.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(AttachFace.FLOOR, Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(AttachFace.FLOOR, Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.FLOOR, Direction.NORTH, BlockModelDefinition.variant()).select(AttachFace.WALL, Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(AttachFace.WALL, Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(AttachFace.WALL, Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(AttachFace.WALL, Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(AttachFace.CEILING, Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.CEILING, Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.CEILING, Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.CEILING, Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180)));
   }

   private static BlockStateVariantBuilder.Four<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> configureDoorHalf(BlockStateVariantBuilder.Four<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> p_239903_0_, DoubleBlockHalf p_239903_1_, ResourceLocation p_239903_2_, ResourceLocation p_239903_3_) {
      return p_239903_0_.select(Direction.EAST, p_239903_1_, DoorHingeSide.LEFT, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_2_)).select(Direction.SOUTH, p_239903_1_, DoorHingeSide.LEFT, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.WEST, p_239903_1_, DoorHingeSide.LEFT, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.NORTH, p_239903_1_, DoorHingeSide.LEFT, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.EAST, p_239903_1_, DoorHingeSide.RIGHT, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_3_)).select(Direction.SOUTH, p_239903_1_, DoorHingeSide.RIGHT, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.WEST, p_239903_1_, DoorHingeSide.RIGHT, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.NORTH, p_239903_1_, DoorHingeSide.RIGHT, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.EAST, p_239903_1_, DoorHingeSide.LEFT, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.SOUTH, p_239903_1_, DoorHingeSide.LEFT, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.WEST, p_239903_1_, DoorHingeSide.LEFT, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.NORTH, p_239903_1_, DoorHingeSide.LEFT, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_3_)).select(Direction.EAST, p_239903_1_, DoorHingeSide.RIGHT, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.SOUTH, p_239903_1_, DoorHingeSide.RIGHT, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_2_)).select(Direction.WEST, p_239903_1_, DoorHingeSide.RIGHT, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.NORTH, p_239903_1_, DoorHingeSide.RIGHT, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239903_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180));
   }

   private static IFinishedBlockState createDoor(Block p_239943_0_, ResourceLocation p_239943_1_, ResourceLocation p_239943_2_, ResourceLocation p_239943_3_, ResourceLocation p_239943_4_) {
      return FinishedVariantBlockState.multiVariant(p_239943_0_).with(configureDoorHalf(configureDoorHalf(BlockStateVariantBuilder.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF, BlockStateProperties.DOOR_HINGE, BlockStateProperties.OPEN), DoubleBlockHalf.LOWER, p_239943_1_, p_239943_2_), DoubleBlockHalf.UPPER, p_239943_3_, p_239943_4_));
   }

   private static IFinishedBlockState createFence(Block p_239994_0_, ResourceLocation p_239994_1_, ResourceLocation p_239994_2_) {
      return FinishedMultiPartBlockState.multiPart(p_239994_0_).with(BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239994_1_)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239994_2_).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239994_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239994_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239994_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true));
   }

   private static IFinishedBlockState createWall(Block p_239970_0_, ResourceLocation p_239970_1_, ResourceLocation p_239970_2_, ResourceLocation p_239970_3_) {
      return FinishedMultiPartBlockState.multiPart(p_239970_0_).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.UP, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239970_1_)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH_WALL, WallHeight.LOW), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239970_2_).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST_WALL, WallHeight.LOW), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239970_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH_WALL, WallHeight.LOW), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239970_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST_WALL, WallHeight.LOW), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239970_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH_WALL, WallHeight.TALL), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239970_3_).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST_WALL, WallHeight.TALL), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239970_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH_WALL, WallHeight.TALL), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239970_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST_WALL, WallHeight.TALL), BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239970_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true));
   }

   private static IFinishedBlockState createFenceGate(Block p_239960_0_, ResourceLocation p_239960_1_, ResourceLocation p_239960_2_, ResourceLocation p_239960_3_, ResourceLocation p_239960_4_) {
      return FinishedVariantBlockState.multiVariant(p_239960_0_, BlockModelDefinition.variant().with(BlockModelFields.UV_LOCK, true)).with(createHorizontalFacingDispatchAlt()).with(BlockStateVariantBuilder.properties(BlockStateProperties.IN_WALL, BlockStateProperties.OPEN).select(false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239960_2_)).select(true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239960_4_)).select(false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239960_1_)).select(true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239960_3_)));
   }

   private static IFinishedBlockState createStairs(Block p_239980_0_, ResourceLocation p_239980_1_, ResourceLocation p_239980_2_, ResourceLocation p_239980_3_) {
      return FinishedVariantBlockState.multiVariant(p_239980_0_).with(BlockStateVariantBuilder.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE).select(Direction.EAST, Half.BOTTOM, StairsShape.STRAIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_2_)).select(Direction.WEST, Half.BOTTOM, StairsShape.STRAIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.STRAIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.STRAIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_)).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_)).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_)).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_)).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.STRAIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_2_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.STRAIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_2_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.STRAIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_2_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.STRAIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_2_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.INNER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.INNER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_RIGHT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.INNER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.INNER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_LEFT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239980_1_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)));
   }

   private static IFinishedBlockState createOrientableTrapdoor(Block p_239988_0_, ResourceLocation p_239988_1_, ResourceLocation p_239988_2_, ResourceLocation p_239988_3_) {
      return FinishedVariantBlockState.multiVariant(p_239988_0_).with(BlockStateVariantBuilder.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_2_)).select(Direction.SOUTH, Half.BOTTOM, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_2_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.NORTH, Half.TOP, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_1_)).select(Direction.SOUTH, Half.TOP, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_1_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.EAST, Half.TOP, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_1_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.WEST, Half.TOP, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_1_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.NORTH, Half.BOTTOM, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_3_)).select(Direction.SOUTH, Half.BOTTOM, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.NORTH, Half.TOP, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.SOUTH, Half.TOP, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R0)).select(Direction.EAST, Half.TOP, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.WEST, Half.TOP, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239988_3_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)));
   }

   private static IFinishedBlockState createTrapdoor(Block p_239995_0_, ResourceLocation p_239995_1_, ResourceLocation p_239995_2_, ResourceLocation p_239995_3_) {
      return FinishedVariantBlockState.multiVariant(p_239995_0_).with(BlockStateVariantBuilder.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_2_)).select(Direction.SOUTH, Half.BOTTOM, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_2_)).select(Direction.EAST, Half.BOTTOM, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_2_)).select(Direction.WEST, Half.BOTTOM, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_2_)).select(Direction.NORTH, Half.TOP, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_1_)).select(Direction.SOUTH, Half.TOP, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_1_)).select(Direction.EAST, Half.TOP, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_1_)).select(Direction.WEST, Half.TOP, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_1_)).select(Direction.NORTH, Half.BOTTOM, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_3_)).select(Direction.SOUTH, Half.BOTTOM, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.NORTH, Half.TOP, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_3_)).select(Direction.SOUTH, Half.TOP, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.EAST, Half.TOP, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.WEST, Half.TOP, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239995_3_).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)));
   }

   private static FinishedVariantBlockState createSimpleBlock(Block p_239978_0_, ResourceLocation p_239978_1_) {
      return FinishedVariantBlockState.multiVariant(p_239978_0_, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239978_1_));
   }

   private static BlockStateVariantBuilder createRotatedPillar() {
      return BlockStateVariantBuilder.property(BlockStateProperties.AXIS).select(Direction.Axis.Y, BlockModelDefinition.variant()).select(Direction.Axis.Z, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90)).select(Direction.Axis.X, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90));
   }

   private static IFinishedBlockState createAxisAlignedPillarBlock(Block p_239986_0_, ResourceLocation p_239986_1_) {
      return FinishedVariantBlockState.multiVariant(p_239986_0_, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239986_1_)).with(createRotatedPillar());
   }

   private void createAxisAlignedPillarBlockCustomModel(Block p_243685_1_, ResourceLocation p_243685_2_) {
      this.blockStateOutput.accept(createAxisAlignedPillarBlock(p_243685_1_, p_243685_2_));
   }

   private void createAxisAlignedPillarBlock(Block p_239882_1_, TexturedModel.ISupplier p_239882_2_) {
      ResourceLocation resourcelocation = p_239882_2_.create(p_239882_1_, this.modelOutput);
      this.blockStateOutput.accept(createAxisAlignedPillarBlock(p_239882_1_, resourcelocation));
   }

   private void createHorizontallyRotatedBlock(Block p_239939_1_, TexturedModel.ISupplier p_239939_2_) {
      ResourceLocation resourcelocation = p_239939_2_.create(p_239939_1_, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239939_1_, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).with(createHorizontalFacingDispatch()));
   }

   private static IFinishedBlockState createRotatedPillarWithHorizontalVariant(Block p_240000_0_, ResourceLocation p_240000_1_, ResourceLocation p_240000_2_) {
      return FinishedVariantBlockState.multiVariant(p_240000_0_).with(BlockStateVariantBuilder.property(BlockStateProperties.AXIS).select(Direction.Axis.Y, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_240000_1_)).select(Direction.Axis.Z, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_240000_2_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90)).select(Direction.Axis.X, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_240000_2_).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)));
   }

   private void createRotatedPillarWithHorizontalVariant(Block p_239883_1_, TexturedModel.ISupplier p_239883_2_, TexturedModel.ISupplier p_239883_3_) {
      ResourceLocation resourcelocation = p_239883_2_.create(p_239883_1_, this.modelOutput);
      ResourceLocation resourcelocation1 = p_239883_3_.create(p_239883_1_, this.modelOutput);
      this.blockStateOutput.accept(createRotatedPillarWithHorizontalVariant(p_239883_1_, resourcelocation, resourcelocation1));
   }

   private ResourceLocation createSuffixedVariant(Block p_239886_1_, String p_239886_2_, ModelsUtil p_239886_3_, Function<ResourceLocation, ModelTextures> p_239886_4_) {
      return p_239886_3_.createWithSuffix(p_239886_1_, p_239886_2_, p_239886_4_.apply(ModelTextures.getBlockTexture(p_239886_1_, p_239886_2_)), this.modelOutput);
   }

   private static IFinishedBlockState createPressurePlate(Block p_240006_0_, ResourceLocation p_240006_1_, ResourceLocation p_240006_2_) {
      return FinishedVariantBlockState.multiVariant(p_240006_0_).with(createBooleanModelDispatch(BlockStateProperties.POWERED, p_240006_2_, p_240006_1_));
   }

   private static IFinishedBlockState createSlab(Block p_240001_0_, ResourceLocation p_240001_1_, ResourceLocation p_240001_2_, ResourceLocation p_240001_3_) {
      return FinishedVariantBlockState.multiVariant(p_240001_0_).with(BlockStateVariantBuilder.property(BlockStateProperties.SLAB_TYPE).select(SlabType.BOTTOM, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_240001_1_)).select(SlabType.TOP, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_240001_2_)).select(SlabType.DOUBLE, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_240001_3_)));
   }

   private void createTrivialCube(Block p_239975_1_) {
      this.createTrivialBlock(p_239975_1_, TexturedModel.CUBE);
   }

   private void createTrivialBlock(Block p_239956_1_, TexturedModel.ISupplier p_239956_2_) {
      this.blockStateOutput.accept(createSimpleBlock(p_239956_1_, p_239956_2_.create(p_239956_1_, this.modelOutput)));
   }

   private void createTrivialBlock(Block p_239880_1_, ModelTextures p_239880_2_, ModelsUtil p_239880_3_) {
      ResourceLocation resourcelocation = p_239880_3_.create(p_239880_1_, p_239880_2_, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_239880_1_, resourcelocation));
   }

   private BlockModelProvider.BlockTextureCombiner family(Block p_239884_1_, TexturedModel p_239884_2_) {
      return (new BlockModelProvider.BlockTextureCombiner(p_239884_2_.getMapping())).fullBlock(p_239884_1_, p_239884_2_.getTemplate());
   }

   private BlockModelProvider.BlockTextureCombiner family(Block p_239967_1_, TexturedModel.ISupplier p_239967_2_) {
      TexturedModel texturedmodel = p_239967_2_.get(p_239967_1_);
      return (new BlockModelProvider.BlockTextureCombiner(texturedmodel.getMapping())).fullBlock(p_239967_1_, texturedmodel.getTemplate());
   }

   private BlockModelProvider.BlockTextureCombiner family(Block p_239984_1_) {
      return this.family(p_239984_1_, TexturedModel.CUBE);
   }

   private BlockModelProvider.BlockTextureCombiner family(ModelTextures p_239905_1_) {
      return new BlockModelProvider.BlockTextureCombiner(p_239905_1_);
   }

   private void createDoor(Block p_239991_1_) {
      ModelTextures modeltextures = ModelTextures.door(p_239991_1_);
      ResourceLocation resourcelocation = StockModelShapes.DOOR_BOTTOM.create(p_239991_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.DOOR_BOTTOM_HINGE.create(p_239991_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation2 = StockModelShapes.DOOR_TOP.create(p_239991_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation3 = StockModelShapes.DOOR_TOP_HINGE.create(p_239991_1_, modeltextures, this.modelOutput);
      this.createSimpleFlatItemModel(p_239991_1_.asItem());
      this.blockStateOutput.accept(createDoor(p_239991_1_, resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3));
   }

   private void createOrientableTrapdoor(Block p_239998_1_) {
      ModelTextures modeltextures = ModelTextures.defaultTexture(p_239998_1_);
      ResourceLocation resourcelocation = StockModelShapes.ORIENTABLE_TRAPDOOR_TOP.create(p_239998_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.ORIENTABLE_TRAPDOOR_BOTTOM.create(p_239998_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation2 = StockModelShapes.ORIENTABLE_TRAPDOOR_OPEN.create(p_239998_1_, modeltextures, this.modelOutput);
      this.blockStateOutput.accept(createOrientableTrapdoor(p_239998_1_, resourcelocation, resourcelocation1, resourcelocation2));
      this.delegateItemModel(p_239998_1_, resourcelocation1);
   }

   private void createTrapdoor(Block p_240004_1_) {
      ModelTextures modeltextures = ModelTextures.defaultTexture(p_240004_1_);
      ResourceLocation resourcelocation = StockModelShapes.TRAPDOOR_TOP.create(p_240004_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.TRAPDOOR_BOTTOM.create(p_240004_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation2 = StockModelShapes.TRAPDOOR_OPEN.create(p_240004_1_, modeltextures, this.modelOutput);
      this.blockStateOutput.accept(createTrapdoor(p_240004_1_, resourcelocation, resourcelocation1, resourcelocation2));
      this.delegateItemModel(p_240004_1_, resourcelocation1);
   }

   private BlockModelProvider.LogsVariantHelper woodProvider(Block p_240009_1_) {
      return new BlockModelProvider.LogsVariantHelper(ModelTextures.logColumn(p_240009_1_));
   }

   private void createNonTemplateModelBlock(Block p_240014_1_) {
      this.createNonTemplateModelBlock(p_240014_1_, p_240014_1_);
   }

   private void createNonTemplateModelBlock(Block p_239872_1_, Block p_239872_2_) {
      this.blockStateOutput.accept(createSimpleBlock(p_239872_1_, ModelsResourceUtil.getModelLocation(p_239872_2_)));
   }

   private void createCrossBlockWithDefaultItem(Block p_239877_1_, BlockModelProvider.TintMode p_239877_2_) {
      this.createSimpleFlatItemModel(p_239877_1_);
      this.createCrossBlock(p_239877_1_, p_239877_2_);
   }

   private void createCrossBlockWithDefaultItem(Block p_239878_1_, BlockModelProvider.TintMode p_239878_2_, ModelTextures p_239878_3_) {
      this.createSimpleFlatItemModel(p_239878_1_);
      this.createCrossBlock(p_239878_1_, p_239878_2_, p_239878_3_);
   }

   private void createCrossBlock(Block p_239937_1_, BlockModelProvider.TintMode p_239937_2_) {
      ModelTextures modeltextures = ModelTextures.cross(p_239937_1_);
      this.createCrossBlock(p_239937_1_, p_239937_2_, modeltextures);
   }

   private void createCrossBlock(Block p_239938_1_, BlockModelProvider.TintMode p_239938_2_, ModelTextures p_239938_3_) {
      ResourceLocation resourcelocation = p_239938_2_.getCross().create(p_239938_1_, p_239938_3_, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_239938_1_, resourcelocation));
   }

   private void createPlant(Block p_239874_1_, Block p_239874_2_, BlockModelProvider.TintMode p_239874_3_) {
      this.createCrossBlockWithDefaultItem(p_239874_1_, p_239874_3_);
      ModelTextures modeltextures = ModelTextures.plant(p_239874_1_);
      ResourceLocation resourcelocation = p_239874_3_.getCrossPot().create(p_239874_2_, modeltextures, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_239874_2_, resourcelocation));
   }

   private void createCoralFans(Block p_239935_1_, Block p_239935_2_) {
      TexturedModel texturedmodel = TexturedModel.CORAL_FAN.get(p_239935_1_);
      ResourceLocation resourcelocation = texturedmodel.create(p_239935_1_, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_239935_1_, resourcelocation));
      ResourceLocation resourcelocation1 = StockModelShapes.CORAL_WALL_FAN.create(p_239935_2_, texturedmodel.getMapping(), this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239935_2_, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1)).with(createHorizontalFacingDispatch()));
      this.createSimpleFlatItemModel(p_239935_1_);
   }

   private void createStems(Block p_239954_1_, Block p_239954_2_) {
      this.createSimpleFlatItemModel(p_239954_1_.asItem());
      ModelTextures modeltextures = ModelTextures.stem(p_239954_1_);
      ModelTextures modeltextures1 = ModelTextures.attachedStem(p_239954_1_, p_239954_2_);
      ResourceLocation resourcelocation = StockModelShapes.ATTACHED_STEM.create(p_239954_2_, modeltextures1, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239954_2_, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).with(BlockStateVariantBuilder.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.WEST, BlockModelDefinition.variant()).select(Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180))));
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239954_1_).with(BlockStateVariantBuilder.property(BlockStateProperties.AGE_7).generate((p_239881_3_) -> {
         return BlockModelDefinition.variant().with(BlockModelFields.MODEL, StockModelShapes.STEMS[p_239881_3_].create(p_239954_1_, modeltextures, this.modelOutput));
      })));
   }

   private void createCoral(Block p_239873_1_, Block p_239873_2_, Block p_239873_3_, Block p_239873_4_, Block p_239873_5_, Block p_239873_6_, Block p_239873_7_, Block p_239873_8_) {
      this.createCrossBlockWithDefaultItem(p_239873_1_, BlockModelProvider.TintMode.NOT_TINTED);
      this.createCrossBlockWithDefaultItem(p_239873_2_, BlockModelProvider.TintMode.NOT_TINTED);
      this.createTrivialCube(p_239873_3_);
      this.createTrivialCube(p_239873_4_);
      this.createCoralFans(p_239873_5_, p_239873_7_);
      this.createCoralFans(p_239873_6_, p_239873_8_);
   }

   private void createDoublePlant(Block p_239955_1_, BlockModelProvider.TintMode p_239955_2_) {
      this.createSimpleFlatItemModel(p_239955_1_, "_top");
      ResourceLocation resourcelocation = this.createSuffixedVariant(p_239955_1_, "_top", p_239955_2_.getCross(), ModelTextures::cross);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(p_239955_1_, "_bottom", p_239955_2_.getCross(), ModelTextures::cross);
      this.createDoubleBlock(p_239955_1_, resourcelocation, resourcelocation1);
   }

   private void createSunflower() {
      this.createSimpleFlatItemModel(Blocks.SUNFLOWER, "_front");
      ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(Blocks.SUNFLOWER, "_top");
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(Blocks.SUNFLOWER, "_bottom", BlockModelProvider.TintMode.NOT_TINTED.getCross(), ModelTextures::cross);
      this.createDoubleBlock(Blocks.SUNFLOWER, resourcelocation, resourcelocation1);
   }

   private void createTallSeagrass() {
      ResourceLocation resourcelocation = this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_top", StockModelShapes.SEAGRASS, ModelTextures::defaultTexture);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_bottom", StockModelShapes.SEAGRASS, ModelTextures::defaultTexture);
      this.createDoubleBlock(Blocks.TALL_SEAGRASS, resourcelocation, resourcelocation1);
   }

   private void createDoubleBlock(Block p_240011_1_, ResourceLocation p_240011_2_, ResourceLocation p_240011_3_) {
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_240011_1_).with(BlockStateVariantBuilder.property(BlockStateProperties.DOUBLE_BLOCK_HALF).select(DoubleBlockHalf.LOWER, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_240011_3_)).select(DoubleBlockHalf.UPPER, BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_240011_2_))));
   }

   private void createPassiveRail(Block p_240018_1_) {
      ModelTextures modeltextures = ModelTextures.rail(p_240018_1_);
      ModelTextures modeltextures1 = ModelTextures.rail(ModelTextures.getBlockTexture(p_240018_1_, "_corner"));
      ResourceLocation resourcelocation = StockModelShapes.RAIL_FLAT.create(p_240018_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.RAIL_CURVED.create(p_240018_1_, modeltextures1, this.modelOutput);
      ResourceLocation resourcelocation2 = StockModelShapes.RAIL_RAISED_NE.create(p_240018_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation3 = StockModelShapes.RAIL_RAISED_SW.create(p_240018_1_, modeltextures, this.modelOutput);
      this.createSimpleFlatItemModel(p_240018_1_);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_240018_1_).with(BlockStateVariantBuilder.property(BlockStateProperties.RAIL_SHAPE).select(RailShape.NORTH_SOUTH, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).select(RailShape.EAST_WEST, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(RailShape.ASCENDING_EAST, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(RailShape.ASCENDING_WEST, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(RailShape.ASCENDING_NORTH, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2)).select(RailShape.ASCENDING_SOUTH, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3)).select(RailShape.SOUTH_EAST, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1)).select(RailShape.SOUTH_WEST, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(RailShape.NORTH_WEST, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(RailShape.NORTH_EAST, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270))));
   }

   private void createActiveRail(Block p_240021_1_) {
      ResourceLocation resourcelocation = this.createSuffixedVariant(p_240021_1_, "", StockModelShapes.RAIL_FLAT, ModelTextures::rail);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(p_240021_1_, "", StockModelShapes.RAIL_RAISED_NE, ModelTextures::rail);
      ResourceLocation resourcelocation2 = this.createSuffixedVariant(p_240021_1_, "", StockModelShapes.RAIL_RAISED_SW, ModelTextures::rail);
      ResourceLocation resourcelocation3 = this.createSuffixedVariant(p_240021_1_, "_on", StockModelShapes.RAIL_FLAT, ModelTextures::rail);
      ResourceLocation resourcelocation4 = this.createSuffixedVariant(p_240021_1_, "_on", StockModelShapes.RAIL_RAISED_NE, ModelTextures::rail);
      ResourceLocation resourcelocation5 = this.createSuffixedVariant(p_240021_1_, "_on", StockModelShapes.RAIL_RAISED_SW, ModelTextures::rail);
      BlockStateVariantBuilder blockstatevariantbuilder = BlockStateVariantBuilder.properties(BlockStateProperties.POWERED, BlockStateProperties.RAIL_SHAPE_STRAIGHT).generate((p_239919_6_, p_239919_7_) -> {
         switch(p_239919_7_) {
         case NORTH_SOUTH:
            return BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239919_6_ ? resourcelocation3 : resourcelocation);
         case EAST_WEST:
            return BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239919_6_ ? resourcelocation3 : resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90);
         case ASCENDING_EAST:
            return BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239919_6_ ? resourcelocation4 : resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90);
         case ASCENDING_WEST:
            return BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239919_6_ ? resourcelocation5 : resourcelocation2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90);
         case ASCENDING_NORTH:
            return BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239919_6_ ? resourcelocation4 : resourcelocation1);
         case ASCENDING_SOUTH:
            return BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239919_6_ ? resourcelocation5 : resourcelocation2);
         default:
            throw new UnsupportedOperationException("Fix you generator!");
         }
      });
      this.createSimpleFlatItemModel(p_240021_1_);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_240021_1_).with(blockstatevariantbuilder));
   }

   private BlockModelProvider.BreakParticleHelper blockEntityModels(ResourceLocation p_239916_1_, Block p_239916_2_) {
      return new BlockModelProvider.BreakParticleHelper(p_239916_1_, p_239916_2_);
   }

   private BlockModelProvider.BreakParticleHelper blockEntityModels(Block p_239966_1_, Block p_239966_2_) {
      return new BlockModelProvider.BreakParticleHelper(ModelsResourceUtil.getModelLocation(p_239966_1_), p_239966_2_);
   }

   private void createAirLikeBlock(Block p_239871_1_, Item p_239871_2_) {
      ResourceLocation resourcelocation = StockModelShapes.PARTICLE_ONLY.create(p_239871_1_, ModelTextures.particleFromItem(p_239871_2_), this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_239871_1_, resourcelocation));
   }

   private void createAirLikeBlock(Block p_239993_1_, ResourceLocation p_239993_2_) {
      ResourceLocation resourcelocation = StockModelShapes.PARTICLE_ONLY.create(p_239993_1_, ModelTextures.particle(p_239993_2_), this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_239993_1_, resourcelocation));
   }

   private void createWoolBlocks(Block p_239976_1_, Block p_239976_2_) {
      this.createTrivialBlock(p_239976_1_, TexturedModel.CUBE);
      ResourceLocation resourcelocation = TexturedModel.CARPET.get(p_239976_1_).create(p_239976_2_, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_239976_2_, resourcelocation));
   }

   private void createColoredBlockWithRandomRotations(TexturedModel.ISupplier p_239907_1_, Block... p_239907_2_) {
      for(Block block : p_239907_2_) {
         ResourceLocation resourcelocation = p_239907_1_.create(block, this.modelOutput);
         this.blockStateOutput.accept(createRotatedVariant(block, resourcelocation));
      }

   }

   private void createColoredBlockWithStateRotations(TexturedModel.ISupplier p_239948_1_, Block... p_239948_2_) {
      for(Block block : p_239948_2_) {
         ResourceLocation resourcelocation = p_239948_1_.create(block, this.modelOutput);
         this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(block, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).with(createHorizontalFacingDispatchAlt()));
      }

   }

   private void createGlassBlocks(Block p_239985_1_, Block p_239985_2_) {
      this.createTrivialCube(p_239985_1_);
      ModelTextures modeltextures = ModelTextures.pane(p_239985_1_, p_239985_2_);
      ResourceLocation resourcelocation = StockModelShapes.STAINED_GLASS_PANE_POST.create(p_239985_2_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.STAINED_GLASS_PANE_SIDE.create(p_239985_2_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation2 = StockModelShapes.STAINED_GLASS_PANE_SIDE_ALT.create(p_239985_2_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation3 = StockModelShapes.STAINED_GLASS_PANE_NOSIDE.create(p_239985_2_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation4 = StockModelShapes.STAINED_GLASS_PANE_NOSIDE_ALT.create(p_239985_2_, modeltextures, this.modelOutput);
      Item item = p_239985_2_.asItem();
      StockModelShapes.FLAT_ITEM.create(ModelsResourceUtil.getModelLocation(item), ModelTextures.layer0(p_239985_1_), this.modelOutput);
      this.blockStateOutput.accept(FinishedMultiPartBlockState.multiPart(p_239985_2_).with(BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation4)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation4).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)));
   }

   private void createCommandBlock(Block p_240023_1_) {
      ModelTextures modeltextures = ModelTextures.commandBlock(p_240023_1_);
      ResourceLocation resourcelocation = StockModelShapes.COMMAND_BLOCK.create(p_240023_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(p_240023_1_, "_conditional", StockModelShapes.COMMAND_BLOCK, (p_239947_1_) -> {
         return modeltextures.copyAndUpdate(StockTextureAliases.SIDE, p_239947_1_);
      });
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_240023_1_).with(createBooleanModelDispatch(BlockStateProperties.CONDITIONAL, resourcelocation1, resourcelocation)).with(createFacingDispatch()));
   }

   private void createAnvil(Block p_240025_1_) {
      ResourceLocation resourcelocation = TexturedModel.ANVIL.create(p_240025_1_, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_240025_1_, resourcelocation).with(createHorizontalFacingDispatchAlt()));
   }

   private List<BlockModelDefinition> createBambooModels(int p_239864_1_) {
      String s = "_age" + p_239864_1_;
      return IntStream.range(1, 5).mapToObj((p_239913_1_) -> {
         return BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.BAMBOO, p_239913_1_ + s));
      }).collect(Collectors.toList());
   }

   private void createBamboo() {
      this.skipAutoItemBlock(Blocks.BAMBOO);
      this.blockStateOutput.accept(FinishedMultiPartBlockState.multiPart(Blocks.BAMBOO).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.AGE_1, 0), this.createBambooModels(0)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.AGE_1, 1), this.createBambooModels(1)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.SMALL), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.BAMBOO, "_small_leaves"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.LARGE), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.BAMBOO, "_large_leaves"))));
   }

   private BlockStateVariantBuilder createColumnWithFacing() {
      return BlockStateVariantBuilder.property(BlockStateProperties.FACING).select(Direction.DOWN, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180)).select(Direction.UP, BlockModelDefinition.variant()).select(Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90)).select(Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90));
   }

   private void createBarrel() {
      ResourceLocation resourcelocation = ModelTextures.getBlockTexture(Blocks.BARREL, "_top_open");
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.BARREL).with(this.createColumnWithFacing()).with(BlockStateVariantBuilder.property(BlockStateProperties.OPEN).select(false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, TexturedModel.CUBE_TOP_BOTTOM.create(Blocks.BARREL, this.modelOutput))).select(true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.BARREL).updateTextures((p_239973_1_) -> {
         p_239973_1_.put(StockTextureAliases.TOP, resourcelocation);
      }).createWithSuffix(Blocks.BARREL, "_open", this.modelOutput)))));
   }

   private static <T extends Comparable<T>> BlockStateVariantBuilder createEmptyOrFullDispatch(Property<T> p_239895_0_, T p_239895_1_, ResourceLocation p_239895_2_, ResourceLocation p_239895_3_) {
      BlockModelDefinition blockmodeldefinition = BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239895_2_);
      BlockModelDefinition blockmodeldefinition1 = BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239895_3_);
      return BlockStateVariantBuilder.property(p_239895_0_).generate((p_239909_3_) -> {
         boolean flag = p_239909_3_.compareTo(p_239895_1_) >= 0;
         return flag ? blockmodeldefinition : blockmodeldefinition1;
      });
   }

   private void createBeeNest(Block p_239887_1_, Function<Block, ModelTextures> p_239887_2_) {
      ModelTextures modeltextures = p_239887_2_.apply(p_239887_1_).copyForced(StockTextureAliases.SIDE, StockTextureAliases.PARTICLE);
      ModelTextures modeltextures1 = modeltextures.copyAndUpdate(StockTextureAliases.FRONT, ModelTextures.getBlockTexture(p_239887_1_, "_front_honey"));
      ResourceLocation resourcelocation = StockModelShapes.CUBE_ORIENTABLE_TOP_BOTTOM.create(p_239887_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.CUBE_ORIENTABLE_TOP_BOTTOM.createWithSuffix(p_239887_1_, "_honey", modeltextures1, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239887_1_).with(createHorizontalFacingDispatch()).with(createEmptyOrFullDispatch(BlockStateProperties.LEVEL_HONEY, 5, resourcelocation1, resourcelocation)));
   }

   private void createCropBlock(Block p_239876_1_, Property<Integer> p_239876_2_, int... p_239876_3_) {
      if (p_239876_2_.getPossibleValues().size() != p_239876_3_.length) {
         throw new IllegalArgumentException();
      } else {
         Int2ObjectMap<ResourceLocation> int2objectmap = new Int2ObjectOpenHashMap<>();
         BlockStateVariantBuilder blockstatevariantbuilder = BlockStateVariantBuilder.property(p_239876_2_).generate((p_239920_4_) -> {
            int i = p_239876_3_[p_239920_4_];
            ResourceLocation resourcelocation = int2objectmap.computeIfAbsent(i, (p_239870_3_) -> {
               return this.createSuffixedVariant(p_239876_1_, "_stage" + i, StockModelShapes.CROP, ModelTextures::crop);
            });
            return BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation);
         });
         this.createSimpleFlatItemModel(p_239876_1_.asItem());
         this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239876_1_).with(blockstatevariantbuilder));
      }
   }

   private void createBell() {
      ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(Blocks.BELL, "_floor");
      ResourceLocation resourcelocation1 = ModelsResourceUtil.getModelLocation(Blocks.BELL, "_ceiling");
      ResourceLocation resourcelocation2 = ModelsResourceUtil.getModelLocation(Blocks.BELL, "_wall");
      ResourceLocation resourcelocation3 = ModelsResourceUtil.getModelLocation(Blocks.BELL, "_between_walls");
      this.createSimpleFlatItemModel(Items.BELL);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.BELL).with(BlockStateVariantBuilder.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.BELL_ATTACHMENT).select(Direction.NORTH, BellAttachment.FLOOR, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).select(Direction.SOUTH, BellAttachment.FLOOR, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.EAST, BellAttachment.FLOOR, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.WEST, BellAttachment.FLOOR, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.NORTH, BellAttachment.CEILING, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1)).select(Direction.SOUTH, BellAttachment.CEILING, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.EAST, BellAttachment.CEILING, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.WEST, BellAttachment.CEILING, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.NORTH, BellAttachment.SINGLE_WALL, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.SOUTH, BellAttachment.SINGLE_WALL, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.EAST, BellAttachment.SINGLE_WALL, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2)).select(Direction.WEST, BellAttachment.SINGLE_WALL, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.SOUTH, BellAttachment.DOUBLE_WALL, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.NORTH, BellAttachment.DOUBLE_WALL, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(Direction.EAST, BellAttachment.DOUBLE_WALL, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3)).select(Direction.WEST, BellAttachment.DOUBLE_WALL, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180))));
   }

   private void createGrindstone() {
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.GRINDSTONE, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.GRINDSTONE))).with(BlockStateVariantBuilder.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.NORTH, BlockModelDefinition.variant()).select(AttachFace.FLOOR, Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(AttachFace.FLOOR, Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.FLOOR, Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(AttachFace.WALL, Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90)).select(AttachFace.WALL, Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(AttachFace.WALL, Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.WALL, Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(AttachFace.CEILING, Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.CEILING, Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(AttachFace.CEILING, Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.CEILING, Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270))));
   }

   private void createFurnace(Block p_239977_1_, TexturedModel.ISupplier p_239977_2_) {
      ResourceLocation resourcelocation = p_239977_2_.create(p_239977_1_, this.modelOutput);
      ResourceLocation resourcelocation1 = ModelTextures.getBlockTexture(p_239977_1_, "_front_on");
      ResourceLocation resourcelocation2 = p_239977_2_.get(p_239977_1_).updateTextures((p_239963_1_) -> {
         p_239963_1_.put(StockTextureAliases.FRONT, resourcelocation1);
      }).createWithSuffix(p_239977_1_, "_on", this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239977_1_).with(createBooleanModelDispatch(BlockStateProperties.LIT, resourcelocation2, resourcelocation)).with(createHorizontalFacingDispatch()));
   }

   private void createCampfires(Block... p_239921_1_) {
      ResourceLocation resourcelocation = ModelsResourceUtil.decorateBlockModelLocation("campfire_off");

      for(Block block : p_239921_1_) {
         ResourceLocation resourcelocation1 = StockModelShapes.CAMPFIRE.create(block, ModelTextures.campfire(block), this.modelOutput);
         this.createSimpleFlatItemModel(block.asItem());
         this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(block).with(createBooleanModelDispatch(BlockStateProperties.LIT, resourcelocation1, resourcelocation)).with(createHorizontalFacingDispatchAlt()));
      }

   }

   private void createBookshelf() {
      ModelTextures modeltextures = ModelTextures.column(ModelTextures.getBlockTexture(Blocks.BOOKSHELF), ModelTextures.getBlockTexture(Blocks.OAK_PLANKS));
      ResourceLocation resourcelocation = StockModelShapes.CUBE_COLUMN.create(Blocks.BOOKSHELF, modeltextures, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(Blocks.BOOKSHELF, resourcelocation));
   }

   private void createRedstoneWire() {
      this.createSimpleFlatItemModel(Items.REDSTONE);
      this.blockStateOutput.accept(FinishedMultiPartBlockState.multiPart(Blocks.REDSTONE_WIRE).with(IMultiPartPredicateBuilder.or(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.NONE), IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP)), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.decorateBlockModelLocation("redstone_dust_dot"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.decorateBlockModelLocation("redstone_dust_side0"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.decorateBlockModelLocation("redstone_dust_side_alt0"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.decorateBlockModelLocation("redstone_dust_side_alt1")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.decorateBlockModelLocation("redstone_dust_side1")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.UP), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.decorateBlockModelLocation("redstone_dust_up"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.UP), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.decorateBlockModelLocation("redstone_dust_up")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.UP), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.decorateBlockModelLocation("redstone_dust_up")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.UP), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.decorateBlockModelLocation("redstone_dust_up")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)));
   }

   private void createComparator() {
      this.createSimpleFlatItemModel(Items.COMPARATOR);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.COMPARATOR).with(createHorizontalFacingDispatchAlt()).with(BlockStateVariantBuilder.properties(BlockStateProperties.MODE_COMPARATOR, BlockStateProperties.POWERED).select(ComparatorMode.COMPARE, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.COMPARATOR))).select(ComparatorMode.COMPARE, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.COMPARATOR, "_on"))).select(ComparatorMode.SUBTRACT, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.COMPARATOR, "_subtract"))).select(ComparatorMode.SUBTRACT, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.COMPARATOR, "_on_subtract")))));
   }

   private void createSmoothStoneSlab() {
      ModelTextures modeltextures = ModelTextures.cube(Blocks.SMOOTH_STONE);
      ModelTextures modeltextures1 = ModelTextures.column(ModelTextures.getBlockTexture(Blocks.SMOOTH_STONE_SLAB, "_side"), modeltextures.get(StockTextureAliases.TOP));
      ResourceLocation resourcelocation = StockModelShapes.SLAB_BOTTOM.create(Blocks.SMOOTH_STONE_SLAB, modeltextures1, this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.SLAB_TOP.create(Blocks.SMOOTH_STONE_SLAB, modeltextures1, this.modelOutput);
      ResourceLocation resourcelocation2 = StockModelShapes.CUBE_COLUMN.createWithOverride(Blocks.SMOOTH_STONE_SLAB, "_double", modeltextures1, this.modelOutput);
      this.blockStateOutput.accept(createSlab(Blocks.SMOOTH_STONE_SLAB, resourcelocation, resourcelocation1, resourcelocation2));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SMOOTH_STONE, StockModelShapes.CUBE_ALL.create(Blocks.SMOOTH_STONE, modeltextures, this.modelOutput)));
   }

   private void createBrewingStand() {
      this.createSimpleFlatItemModel(Items.BREWING_STAND);
      this.blockStateOutput.accept(FinishedMultiPartBlockState.multiPart(Blocks.BREWING_STAND).with(BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.BREWING_STAND))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.HAS_BOTTLE_0, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.BREWING_STAND, "_bottle0"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.HAS_BOTTLE_1, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.BREWING_STAND, "_bottle1"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.HAS_BOTTLE_2, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.BREWING_STAND, "_bottle2"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.HAS_BOTTLE_0, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.BREWING_STAND, "_empty0"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.HAS_BOTTLE_1, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.BREWING_STAND, "_empty1"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.HAS_BOTTLE_2, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.BREWING_STAND, "_empty2"))));
   }

   private void createMushroomBlock(Block p_240027_1_) {
      ResourceLocation resourcelocation = StockModelShapes.SINGLE_FACE.create(p_240027_1_, ModelTextures.defaultTexture(p_240027_1_), this.modelOutput);
      ResourceLocation resourcelocation1 = ModelsResourceUtil.decorateBlockModelLocation("mushroom_block_inside");
      this.blockStateOutput.accept(FinishedMultiPartBlockState.multiPart(p_240027_1_).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.UP, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.DOWN, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, false)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, false)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, false)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.UP, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, false)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.DOWN, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, false)));
      this.delegateItemModel(p_240027_1_, TexturedModel.CUBE.createWithSuffix(p_240027_1_, "_inventory", this.modelOutput));
   }

   private void createCakeBlock() {
      this.createSimpleFlatItemModel(Items.CAKE);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.CAKE).with(BlockStateVariantBuilder.property(BlockStateProperties.BITES).select(0, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.CAKE))).select(1, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.CAKE, "_slice1"))).select(2, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.CAKE, "_slice2"))).select(3, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.CAKE, "_slice3"))).select(4, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.CAKE, "_slice4"))).select(5, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.CAKE, "_slice5"))).select(6, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.CAKE, "_slice6")))));
   }

   private void createCartographyTable() {
      ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.PARTICLE, ModelTextures.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(StockTextureAliases.DOWN, ModelTextures.getBlockTexture(Blocks.DARK_OAK_PLANKS)).put(StockTextureAliases.UP, ModelTextures.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_top")).put(StockTextureAliases.NORTH, ModelTextures.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(StockTextureAliases.EAST, ModelTextures.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(StockTextureAliases.SOUTH, ModelTextures.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side1")).put(StockTextureAliases.WEST, ModelTextures.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side2"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.CARTOGRAPHY_TABLE, StockModelShapes.CUBE.create(Blocks.CARTOGRAPHY_TABLE, modeltextures, this.modelOutput)));
   }

   private void createSmithingTable() {
      ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.PARTICLE, ModelTextures.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(StockTextureAliases.DOWN, ModelTextures.getBlockTexture(Blocks.SMITHING_TABLE, "_bottom")).put(StockTextureAliases.UP, ModelTextures.getBlockTexture(Blocks.SMITHING_TABLE, "_top")).put(StockTextureAliases.NORTH, ModelTextures.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(StockTextureAliases.SOUTH, ModelTextures.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(StockTextureAliases.EAST, ModelTextures.getBlockTexture(Blocks.SMITHING_TABLE, "_side")).put(StockTextureAliases.WEST, ModelTextures.getBlockTexture(Blocks.SMITHING_TABLE, "_side"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SMITHING_TABLE, StockModelShapes.CUBE.create(Blocks.SMITHING_TABLE, modeltextures, this.modelOutput)));
   }

   private void createCraftingTableLike(Block p_239875_1_, Block p_239875_2_, BiFunction<Block, Block, ModelTextures> p_239875_3_) {
      ModelTextures modeltextures = p_239875_3_.apply(p_239875_1_, p_239875_2_);
      this.blockStateOutput.accept(createSimpleBlock(p_239875_1_, StockModelShapes.CUBE.create(p_239875_1_, modeltextures, this.modelOutput)));
   }

   private void createPumpkins() {
      ModelTextures modeltextures = ModelTextures.column(Blocks.PUMPKIN);
      this.blockStateOutput.accept(createSimpleBlock(Blocks.PUMPKIN, ModelsResourceUtil.getModelLocation(Blocks.PUMPKIN)));
      this.createPumpkinVariant(Blocks.CARVED_PUMPKIN, modeltextures);
      this.createPumpkinVariant(Blocks.JACK_O_LANTERN, modeltextures);
   }

   private void createPumpkinVariant(Block p_239879_1_, ModelTextures p_239879_2_) {
      ResourceLocation resourcelocation = StockModelShapes.CUBE_ORIENTABLE.create(p_239879_1_, p_239879_2_.copyAndUpdate(StockTextureAliases.FRONT, ModelTextures.getBlockTexture(p_239879_1_)), this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239879_1_, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).with(createHorizontalFacingDispatch()));
   }

   private void createCauldron() {
      this.createSimpleFlatItemModel(Items.CAULDRON);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.CAULDRON).with(BlockStateVariantBuilder.property(BlockStateProperties.LEVEL_CAULDRON).select(0, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.CAULDRON))).select(1, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.CAULDRON, "_level1"))).select(2, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.CAULDRON, "_level2"))).select(3, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.CAULDRON, "_level3")))));
   }

   private void createChiseledSandsone(Block p_239992_1_, Block p_239992_2_) {
      ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.END, ModelTextures.getBlockTexture(p_239992_2_, "_top")).put(StockTextureAliases.SIDE, ModelTextures.getBlockTexture(p_239992_1_));
      this.createTrivialBlock(p_239992_1_, modeltextures, StockModelShapes.CUBE_COLUMN);
   }

   private void createChorusFlower() {
      ModelTextures modeltextures = ModelTextures.defaultTexture(Blocks.CHORUS_FLOWER);
      ResourceLocation resourcelocation = StockModelShapes.CHORUS_FLOWER.create(Blocks.CHORUS_FLOWER, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(Blocks.CHORUS_FLOWER, "_dead", StockModelShapes.CHORUS_FLOWER, (p_239906_1_) -> {
         return modeltextures.copyAndUpdate(StockTextureAliases.TEXTURE, p_239906_1_);
      });
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.CHORUS_FLOWER).with(createEmptyOrFullDispatch(BlockStateProperties.AGE_5, 5, resourcelocation1, resourcelocation)));
   }

   private void createDispenserBlock(Block p_240029_1_) {
      ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.TOP, ModelTextures.getBlockTexture(Blocks.FURNACE, "_top")).put(StockTextureAliases.SIDE, ModelTextures.getBlockTexture(Blocks.FURNACE, "_side")).put(StockTextureAliases.FRONT, ModelTextures.getBlockTexture(p_240029_1_, "_front"));
      ModelTextures modeltextures1 = (new ModelTextures()).put(StockTextureAliases.SIDE, ModelTextures.getBlockTexture(Blocks.FURNACE, "_top")).put(StockTextureAliases.FRONT, ModelTextures.getBlockTexture(p_240029_1_, "_front_vertical"));
      ResourceLocation resourcelocation = StockModelShapes.CUBE_ORIENTABLE.create(p_240029_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.CUBE_ORIENTABLE_VERTICAL.create(p_240029_1_, modeltextures1, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_240029_1_).with(BlockStateVariantBuilder.property(BlockStateProperties.FACING).select(Direction.DOWN, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180)).select(Direction.UP, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1)).select(Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).select(Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270))));
   }

   private void createEndPortalFrame() {
      ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(Blocks.END_PORTAL_FRAME);
      ResourceLocation resourcelocation1 = ModelsResourceUtil.getModelLocation(Blocks.END_PORTAL_FRAME, "_filled");
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.END_PORTAL_FRAME).with(BlockStateVariantBuilder.property(BlockStateProperties.EYE).select(false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).select(true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1))).with(createHorizontalFacingDispatchAlt()));
   }

   private void createChorusPlant() {
      ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(Blocks.CHORUS_PLANT, "_side");
      ResourceLocation resourcelocation1 = ModelsResourceUtil.getModelLocation(Blocks.CHORUS_PLANT, "_noside");
      ResourceLocation resourcelocation2 = ModelsResourceUtil.getModelLocation(Blocks.CHORUS_PLANT, "_noside1");
      ResourceLocation resourcelocation3 = ModelsResourceUtil.getModelLocation(Blocks.CHORUS_PLANT, "_noside2");
      ResourceLocation resourcelocation4 = ModelsResourceUtil.getModelLocation(Blocks.CHORUS_PLANT, "_noside3");
      this.blockStateOutput.accept(FinishedMultiPartBlockState.multiPart(Blocks.CHORUS_PLANT).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.UP, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.DOWN, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.WEIGHT, 2), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation4)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation4).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.WEIGHT, 2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation4).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.WEIGHT, 2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation4).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.WEIGHT, 2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.UP, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.WEIGHT, 2).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation4).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.UV_LOCK, true)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.DOWN, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation4).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.WEIGHT, 2).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.UV_LOCK, true)));
   }

   private void createComposter() {
      this.blockStateOutput.accept(FinishedMultiPartBlockState.multiPart(Blocks.COMPOSTER).with(BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.COMPOSTER))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 1), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.COMPOSTER, "_contents1"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 2), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.COMPOSTER, "_contents2"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 3), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.COMPOSTER, "_contents3"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 4), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.COMPOSTER, "_contents4"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 5), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.COMPOSTER, "_contents5"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 6), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.COMPOSTER, "_contents6"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 7), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.COMPOSTER, "_contents7"))).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 8), BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.COMPOSTER, "_contents_ready"))));
   }

   private void createNyliumBlock(Block p_240031_1_) {
      ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.BOTTOM, ModelTextures.getBlockTexture(Blocks.NETHERRACK)).put(StockTextureAliases.TOP, ModelTextures.getBlockTexture(p_240031_1_)).put(StockTextureAliases.SIDE, ModelTextures.getBlockTexture(p_240031_1_, "_side"));
      this.blockStateOutput.accept(createSimpleBlock(p_240031_1_, StockModelShapes.CUBE_BOTTOM_TOP.create(p_240031_1_, modeltextures, this.modelOutput)));
   }

   private void createDaylightDetector() {
      ResourceLocation resourcelocation = ModelTextures.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_side");
      ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.TOP, ModelTextures.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_top")).put(StockTextureAliases.SIDE, resourcelocation);
      ModelTextures modeltextures1 = (new ModelTextures()).put(StockTextureAliases.TOP, ModelTextures.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_inverted_top")).put(StockTextureAliases.SIDE, resourcelocation);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.DAYLIGHT_DETECTOR).with(BlockStateVariantBuilder.property(BlockStateProperties.INVERTED).select(false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, StockModelShapes.DAYLIGHT_DETECTOR.create(Blocks.DAYLIGHT_DETECTOR, modeltextures, this.modelOutput))).select(true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, StockModelShapes.DAYLIGHT_DETECTOR.create(ModelsResourceUtil.getModelLocation(Blocks.DAYLIGHT_DETECTOR, "_inverted"), modeltextures1, this.modelOutput)))));
   }

   private void createRotatableColumn(Block p_239839_1_) {
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239839_1_, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(p_239839_1_))).with(this.createColumnWithFacing()));
   }

   private void createFarmland() {
      ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.DIRT, ModelTextures.getBlockTexture(Blocks.DIRT)).put(StockTextureAliases.TOP, ModelTextures.getBlockTexture(Blocks.FARMLAND));
      ModelTextures modeltextures1 = (new ModelTextures()).put(StockTextureAliases.DIRT, ModelTextures.getBlockTexture(Blocks.DIRT)).put(StockTextureAliases.TOP, ModelTextures.getBlockTexture(Blocks.FARMLAND, "_moist"));
      ResourceLocation resourcelocation = StockModelShapes.FARMLAND.create(Blocks.FARMLAND, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.FARMLAND.create(ModelTextures.getBlockTexture(Blocks.FARMLAND, "_moist"), modeltextures1, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.FARMLAND).with(createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, resourcelocation1, resourcelocation)));
   }

   private List<ResourceLocation> createFloorFireModels(Block p_240033_1_) {
      ResourceLocation resourcelocation = StockModelShapes.FIRE_FLOOR.create(ModelsResourceUtil.getModelLocation(p_240033_1_, "_floor0"), ModelTextures.fire0(p_240033_1_), this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.FIRE_FLOOR.create(ModelsResourceUtil.getModelLocation(p_240033_1_, "_floor1"), ModelTextures.fire1(p_240033_1_), this.modelOutput);
      return ImmutableList.of(resourcelocation, resourcelocation1);
   }

   private List<ResourceLocation> createSideFireModels(Block p_240035_1_) {
      ResourceLocation resourcelocation = StockModelShapes.FIRE_SIDE.create(ModelsResourceUtil.getModelLocation(p_240035_1_, "_side0"), ModelTextures.fire0(p_240035_1_), this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.FIRE_SIDE.create(ModelsResourceUtil.getModelLocation(p_240035_1_, "_side1"), ModelTextures.fire1(p_240035_1_), this.modelOutput);
      ResourceLocation resourcelocation2 = StockModelShapes.FIRE_SIDE_ALT.create(ModelsResourceUtil.getModelLocation(p_240035_1_, "_side_alt0"), ModelTextures.fire0(p_240035_1_), this.modelOutput);
      ResourceLocation resourcelocation3 = StockModelShapes.FIRE_SIDE_ALT.create(ModelsResourceUtil.getModelLocation(p_240035_1_, "_side_alt1"), ModelTextures.fire1(p_240035_1_), this.modelOutput);
      return ImmutableList.of(resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3);
   }

   private List<ResourceLocation> createTopFireModels(Block p_240037_1_) {
      ResourceLocation resourcelocation = StockModelShapes.FIRE_UP.create(ModelsResourceUtil.getModelLocation(p_240037_1_, "_up0"), ModelTextures.fire0(p_240037_1_), this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.FIRE_UP.create(ModelsResourceUtil.getModelLocation(p_240037_1_, "_up1"), ModelTextures.fire1(p_240037_1_), this.modelOutput);
      ResourceLocation resourcelocation2 = StockModelShapes.FIRE_UP_ALT.create(ModelsResourceUtil.getModelLocation(p_240037_1_, "_up_alt0"), ModelTextures.fire0(p_240037_1_), this.modelOutput);
      ResourceLocation resourcelocation3 = StockModelShapes.FIRE_UP_ALT.create(ModelsResourceUtil.getModelLocation(p_240037_1_, "_up_alt1"), ModelTextures.fire1(p_240037_1_), this.modelOutput);
      return ImmutableList.of(resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3);
   }

   private static List<BlockModelDefinition> wrapModels(List<ResourceLocation> p_239914_0_, UnaryOperator<BlockModelDefinition> p_239914_1_) {
      return p_239914_0_.stream().map((p_239950_0_) -> {
         return BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239950_0_);
      }).map(p_239914_1_).collect(Collectors.toList());
   }

   private void createFire() {
      IMultiPartPredicateBuilder imultipartpredicatebuilder = IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false).term(BlockStateProperties.UP, false);
      List<ResourceLocation> list = this.createFloorFireModels(Blocks.FIRE);
      List<ResourceLocation> list1 = this.createSideFireModels(Blocks.FIRE);
      List<ResourceLocation> list2 = this.createTopFireModels(Blocks.FIRE);
      this.blockStateOutput.accept(FinishedMultiPartBlockState.multiPart(Blocks.FIRE).with(imultipartpredicatebuilder, wrapModels(list, (p_240016_0_) -> {
         return p_240016_0_;
      })).with(IMultiPartPredicateBuilder.or(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, true), imultipartpredicatebuilder), wrapModels(list1, (p_240012_0_) -> {
         return p_240012_0_;
      })).with(IMultiPartPredicateBuilder.or(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST, true), imultipartpredicatebuilder), wrapModels(list1, (p_240007_0_) -> {
         return p_240007_0_.with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90);
      })).with(IMultiPartPredicateBuilder.or(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH, true), imultipartpredicatebuilder), wrapModels(list1, (p_240002_0_) -> {
         return p_240002_0_.with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180);
      })).with(IMultiPartPredicateBuilder.or(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST, true), imultipartpredicatebuilder), wrapModels(list1, (p_239996_0_) -> {
         return p_239996_0_.with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270);
      })).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.UP, true), wrapModels(list2, (p_239989_0_) -> {
         return p_239989_0_;
      })));
   }

   private void createSoulFire() {
      List<ResourceLocation> list = this.createFloorFireModels(Blocks.SOUL_FIRE);
      List<ResourceLocation> list1 = this.createSideFireModels(Blocks.SOUL_FIRE);
      this.blockStateOutput.accept(FinishedMultiPartBlockState.multiPart(Blocks.SOUL_FIRE).with(wrapModels(list, (p_239981_0_) -> {
         return p_239981_0_;
      })).with(wrapModels(list1, (p_239971_0_) -> {
         return p_239971_0_;
      })).with(wrapModels(list1, (p_239961_0_) -> {
         return p_239961_0_.with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90);
      })).with(wrapModels(list1, (p_239945_0_) -> {
         return p_239945_0_.with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180);
      })).with(wrapModels(list1, (p_239904_0_) -> {
         return p_239904_0_.with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270);
      })));
   }

   private void createLantern(Block p_240039_1_) {
      ResourceLocation resourcelocation = TexturedModel.LANTERN.create(p_240039_1_, this.modelOutput);
      ResourceLocation resourcelocation1 = TexturedModel.HANGING_LANTERN.create(p_240039_1_, this.modelOutput);
      this.createSimpleFlatItemModel(p_240039_1_.asItem());
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_240039_1_).with(createBooleanModelDispatch(BlockStateProperties.HANGING, resourcelocation1, resourcelocation)));
   }

   private void createFrostedIce() {
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.FROSTED_ICE).with(BlockStateVariantBuilder.property(BlockStateProperties.AGE_3).select(0, BlockModelDefinition.variant().with(BlockModelFields.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_0", StockModelShapes.CUBE_ALL, ModelTextures::cube))).select(1, BlockModelDefinition.variant().with(BlockModelFields.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_1", StockModelShapes.CUBE_ALL, ModelTextures::cube))).select(2, BlockModelDefinition.variant().with(BlockModelFields.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_2", StockModelShapes.CUBE_ALL, ModelTextures::cube))).select(3, BlockModelDefinition.variant().with(BlockModelFields.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_3", StockModelShapes.CUBE_ALL, ModelTextures::cube)))));
   }

   private void createGrassBlocks() {
      ResourceLocation resourcelocation = ModelTextures.getBlockTexture(Blocks.DIRT);
      ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.BOTTOM, resourcelocation).copyForced(StockTextureAliases.BOTTOM, StockTextureAliases.PARTICLE).put(StockTextureAliases.TOP, ModelTextures.getBlockTexture(Blocks.GRASS_BLOCK, "_top")).put(StockTextureAliases.SIDE, ModelTextures.getBlockTexture(Blocks.GRASS_BLOCK, "_snow"));
      BlockModelDefinition blockmodeldefinition = BlockModelDefinition.variant().with(BlockModelFields.MODEL, StockModelShapes.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.GRASS_BLOCK, "_snow", modeltextures, this.modelOutput));
      this.createGrassLikeBlock(Blocks.GRASS_BLOCK, ModelsResourceUtil.getModelLocation(Blocks.GRASS_BLOCK), blockmodeldefinition);
      ResourceLocation resourcelocation1 = TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.MYCELIUM).updateTextures((p_239951_1_) -> {
         p_239951_1_.put(StockTextureAliases.BOTTOM, resourcelocation);
      }).create(Blocks.MYCELIUM, this.modelOutput);
      this.createGrassLikeBlock(Blocks.MYCELIUM, resourcelocation1, blockmodeldefinition);
      ResourceLocation resourcelocation2 = TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.PODZOL).updateTextures((p_239917_1_) -> {
         p_239917_1_.put(StockTextureAliases.BOTTOM, resourcelocation);
      }).create(Blocks.PODZOL, this.modelOutput);
      this.createGrassLikeBlock(Blocks.PODZOL, resourcelocation2, blockmodeldefinition);
   }

   private void createGrassLikeBlock(Block p_239889_1_, ResourceLocation p_239889_2_, BlockModelDefinition p_239889_3_) {
      List<BlockModelDefinition> list = Arrays.asList(createRotatedVariants(p_239889_2_));
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239889_1_).with(BlockStateVariantBuilder.property(BlockStateProperties.SNOWY).select(true, p_239889_3_).select(false, list)));
   }

   private void createCocoa() {
      this.createSimpleFlatItemModel(Items.COCOA_BEANS);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.COCOA).with(BlockStateVariantBuilder.property(BlockStateProperties.AGE_2).select(0, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.COCOA, "_stage0"))).select(1, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.COCOA, "_stage1"))).select(2, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.COCOA, "_stage2")))).with(createHorizontalFacingDispatchAlt()));
   }

   private void createGrassPath() {
      this.blockStateOutput.accept(createRotatedVariant(Blocks.GRASS_PATH, ModelsResourceUtil.getModelLocation(Blocks.GRASS_PATH)));
   }

   private void createWeightedPressurePlate(Block p_239999_1_, Block p_239999_2_) {
      ModelTextures modeltextures = ModelTextures.defaultTexture(p_239999_2_);
      ResourceLocation resourcelocation = StockModelShapes.PRESSURE_PLATE_UP.create(p_239999_1_, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.PRESSURE_PLATE_DOWN.create(p_239999_1_, modeltextures, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239999_1_).with(createEmptyOrFullDispatch(BlockStateProperties.POWER, 1, resourcelocation1, resourcelocation)));
   }

   private void createHopper() {
      ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(Blocks.HOPPER);
      ResourceLocation resourcelocation1 = ModelsResourceUtil.getModelLocation(Blocks.HOPPER, "_side");
      this.createSimpleFlatItemModel(Items.HOPPER);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.HOPPER).with(BlockStateVariantBuilder.property(BlockStateProperties.FACING_HOPPER).select(Direction.DOWN, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).select(Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1)).select(Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270))));
   }

   private void copyModel(Block p_240005_1_, Block p_240005_2_) {
      ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(p_240005_1_);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_240005_2_, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)));
      this.delegateItemModel(p_240005_2_, resourcelocation);
   }

   private void createIronBars() {
      ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(Blocks.IRON_BARS, "_post_ends");
      ResourceLocation resourcelocation1 = ModelsResourceUtil.getModelLocation(Blocks.IRON_BARS, "_post");
      ResourceLocation resourcelocation2 = ModelsResourceUtil.getModelLocation(Blocks.IRON_BARS, "_cap");
      ResourceLocation resourcelocation3 = ModelsResourceUtil.getModelLocation(Blocks.IRON_BARS, "_cap_alt");
      ResourceLocation resourcelocation4 = ModelsResourceUtil.getModelLocation(Blocks.IRON_BARS, "_side");
      ResourceLocation resourcelocation5 = ModelsResourceUtil.getModelLocation(Blocks.IRON_BARS, "_side_alt");
      this.blockStateOutput.accept(FinishedMultiPartBlockState.multiPart(Blocks.IRON_BARS).with(BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation1)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, true).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, true).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation2).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, true).term(BlockStateProperties.WEST, false), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation3).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.NORTH, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation4)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.EAST, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation4).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.SOUTH, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation5)).with(IMultiPartPredicateBuilder.condition().term(BlockStateProperties.WEST, true), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation5).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)));
      this.createSimpleFlatItemModel(Blocks.IRON_BARS);
   }

   private void createNonTemplateHorizontalBlock(Block p_240041_1_) {
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_240041_1_, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(p_240041_1_))).with(createHorizontalFacingDispatch()));
   }

   private void createLever() {
      ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(Blocks.LEVER);
      ResourceLocation resourcelocation1 = ModelsResourceUtil.getModelLocation(Blocks.LEVER, "_on");
      this.createSimpleFlatItemModel(Blocks.LEVER);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.LEVER).with(createBooleanModelDispatch(BlockStateProperties.POWERED, resourcelocation, resourcelocation1)).with(BlockStateVariantBuilder.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.CEILING, Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.CEILING, Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(AttachFace.CEILING, Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.CEILING, Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(AttachFace.FLOOR, Direction.NORTH, BlockModelDefinition.variant()).select(AttachFace.FLOOR, Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(AttachFace.FLOOR, Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.FLOOR, Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(AttachFace.WALL, Direction.NORTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90)).select(AttachFace.WALL, Direction.EAST, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(AttachFace.WALL, Direction.SOUTH, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(AttachFace.WALL, Direction.WEST, BlockModelDefinition.variant().with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270))));
   }

   private void createLilyPad() {
      this.createSimpleFlatItemModel(Blocks.LILY_PAD);
      this.blockStateOutput.accept(createRotatedVariant(Blocks.LILY_PAD, ModelsResourceUtil.getModelLocation(Blocks.LILY_PAD)));
   }

   private void createNetherPortalBlock() {
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.NETHER_PORTAL).with(BlockStateVariantBuilder.property(BlockStateProperties.HORIZONTAL_AXIS).select(Direction.Axis.X, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.NETHER_PORTAL, "_ns"))).select(Direction.Axis.Z, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.NETHER_PORTAL, "_ew")))));
   }

   private void createNetherrack() {
      ResourceLocation resourcelocation = TexturedModel.CUBE.create(Blocks.NETHERRACK, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.NETHERRACK, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R180), BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270)));
   }

   private void createObserver() {
      ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(Blocks.OBSERVER);
      ResourceLocation resourcelocation1 = ModelsResourceUtil.getModelLocation(Blocks.OBSERVER, "_on");
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.OBSERVER).with(createBooleanModelDispatch(BlockStateProperties.POWERED, resourcelocation1, resourcelocation)).with(createFacingDispatch()));
   }

   private void createPistons() {
      ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.BOTTOM, ModelTextures.getBlockTexture(Blocks.PISTON, "_bottom")).put(StockTextureAliases.SIDE, ModelTextures.getBlockTexture(Blocks.PISTON, "_side"));
      ResourceLocation resourcelocation = ModelTextures.getBlockTexture(Blocks.PISTON, "_top_sticky");
      ResourceLocation resourcelocation1 = ModelTextures.getBlockTexture(Blocks.PISTON, "_top");
      ModelTextures modeltextures1 = modeltextures.copyAndUpdate(StockTextureAliases.PLATFORM, resourcelocation);
      ModelTextures modeltextures2 = modeltextures.copyAndUpdate(StockTextureAliases.PLATFORM, resourcelocation1);
      ResourceLocation resourcelocation2 = ModelsResourceUtil.getModelLocation(Blocks.PISTON, "_base");
      this.createPistonVariant(Blocks.PISTON, resourcelocation2, modeltextures2);
      this.createPistonVariant(Blocks.STICKY_PISTON, resourcelocation2, modeltextures1);
      ResourceLocation resourcelocation3 = StockModelShapes.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.PISTON, "_inventory", modeltextures.copyAndUpdate(StockTextureAliases.TOP, resourcelocation1), this.modelOutput);
      ResourceLocation resourcelocation4 = StockModelShapes.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.STICKY_PISTON, "_inventory", modeltextures.copyAndUpdate(StockTextureAliases.TOP, resourcelocation), this.modelOutput);
      this.delegateItemModel(Blocks.PISTON, resourcelocation3);
      this.delegateItemModel(Blocks.STICKY_PISTON, resourcelocation4);
   }

   private void createPistonVariant(Block p_239890_1_, ResourceLocation p_239890_2_, ModelTextures p_239890_3_) {
      ResourceLocation resourcelocation = StockModelShapes.PISTON.create(p_239890_1_, p_239890_3_, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_239890_1_).with(createBooleanModelDispatch(BlockStateProperties.EXTENDED, p_239890_2_, resourcelocation)).with(createFacingDispatch()));
   }

   private void createPistonHeads() {
      ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.UNSTICKY, ModelTextures.getBlockTexture(Blocks.PISTON, "_top")).put(StockTextureAliases.SIDE, ModelTextures.getBlockTexture(Blocks.PISTON, "_side"));
      ModelTextures modeltextures1 = modeltextures.copyAndUpdate(StockTextureAliases.PLATFORM, ModelTextures.getBlockTexture(Blocks.PISTON, "_top_sticky"));
      ModelTextures modeltextures2 = modeltextures.copyAndUpdate(StockTextureAliases.PLATFORM, ModelTextures.getBlockTexture(Blocks.PISTON, "_top"));
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.PISTON_HEAD).with(BlockStateVariantBuilder.properties(BlockStateProperties.SHORT, BlockStateProperties.PISTON_TYPE).select(false, PistonType.DEFAULT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, StockModelShapes.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head", modeltextures2, this.modelOutput))).select(false, PistonType.STICKY, BlockModelDefinition.variant().with(BlockModelFields.MODEL, StockModelShapes.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head_sticky", modeltextures1, this.modelOutput))).select(true, PistonType.DEFAULT, BlockModelDefinition.variant().with(BlockModelFields.MODEL, StockModelShapes.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short", modeltextures2, this.modelOutput))).select(true, PistonType.STICKY, BlockModelDefinition.variant().with(BlockModelFields.MODEL, StockModelShapes.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short_sticky", modeltextures1, this.modelOutput)))).with(createFacingDispatch()));
   }

   private void createScaffolding() {
      ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(Blocks.SCAFFOLDING, "_stable");
      ResourceLocation resourcelocation1 = ModelsResourceUtil.getModelLocation(Blocks.SCAFFOLDING, "_unstable");
      this.delegateItemModel(Blocks.SCAFFOLDING, resourcelocation);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.SCAFFOLDING).with(createBooleanModelDispatch(BlockStateProperties.BOTTOM, resourcelocation1, resourcelocation)));
   }

   private void createRedstoneLamp() {
      ResourceLocation resourcelocation = TexturedModel.CUBE.create(Blocks.REDSTONE_LAMP, this.modelOutput);
      ResourceLocation resourcelocation1 = this.createSuffixedVariant(Blocks.REDSTONE_LAMP, "_on", StockModelShapes.CUBE_ALL, ModelTextures::cube);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.REDSTONE_LAMP).with(createBooleanModelDispatch(BlockStateProperties.LIT, resourcelocation1, resourcelocation)));
   }

   private void createNormalTorch(Block p_240010_1_, Block p_240010_2_) {
      ModelTextures modeltextures = ModelTextures.torch(p_240010_1_);
      this.blockStateOutput.accept(createSimpleBlock(p_240010_1_, StockModelShapes.TORCH.create(p_240010_1_, modeltextures, this.modelOutput)));
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(p_240010_2_, BlockModelDefinition.variant().with(BlockModelFields.MODEL, StockModelShapes.WALL_TORCH.create(p_240010_2_, modeltextures, this.modelOutput))).with(createTorchHorizontalDispatch()));
      this.createSimpleFlatItemModel(p_240010_1_);
      this.skipAutoItemBlock(p_240010_2_);
   }

   private void createRedstoneTorch() {
      ModelTextures modeltextures = ModelTextures.torch(Blocks.REDSTONE_TORCH);
      ModelTextures modeltextures1 = ModelTextures.torch(ModelTextures.getBlockTexture(Blocks.REDSTONE_TORCH, "_off"));
      ResourceLocation resourcelocation = StockModelShapes.TORCH.create(Blocks.REDSTONE_TORCH, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation1 = StockModelShapes.TORCH.createWithSuffix(Blocks.REDSTONE_TORCH, "_off", modeltextures1, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.REDSTONE_TORCH).with(createBooleanModelDispatch(BlockStateProperties.LIT, resourcelocation, resourcelocation1)));
      ResourceLocation resourcelocation2 = StockModelShapes.WALL_TORCH.create(Blocks.REDSTONE_WALL_TORCH, modeltextures, this.modelOutput);
      ResourceLocation resourcelocation3 = StockModelShapes.WALL_TORCH.createWithSuffix(Blocks.REDSTONE_WALL_TORCH, "_off", modeltextures1, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.REDSTONE_WALL_TORCH).with(createBooleanModelDispatch(BlockStateProperties.LIT, resourcelocation2, resourcelocation3)).with(createTorchHorizontalDispatch()));
      this.createSimpleFlatItemModel(Blocks.REDSTONE_TORCH);
      this.skipAutoItemBlock(Blocks.REDSTONE_WALL_TORCH);
   }

   private void createRepeater() {
      this.createSimpleFlatItemModel(Items.REPEATER);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.REPEATER).with(BlockStateVariantBuilder.properties(BlockStateProperties.DELAY, BlockStateProperties.LOCKED, BlockStateProperties.POWERED).generate((p_239911_0_, p_239911_1_, p_239911_2_) -> {
         StringBuilder stringbuilder = new StringBuilder();
         stringbuilder.append('_').append((Object)p_239911_0_).append("tick");
         if (p_239911_2_) {
            stringbuilder.append("_on");
         }

         if (p_239911_1_) {
            stringbuilder.append("_locked");
         }

         return BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.REPEATER, stringbuilder.toString()));
      })).with(createHorizontalFacingDispatchAlt()));
   }

   private void createSeaPickle() {
      this.createSimpleFlatItemModel(Items.SEA_PICKLE);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.SEA_PICKLE).with(BlockStateVariantBuilder.properties(BlockStateProperties.PICKLES, BlockStateProperties.WATERLOGGED).select(1, false, Arrays.asList(createRotatedVariants(ModelsResourceUtil.decorateBlockModelLocation("dead_sea_pickle")))).select(2, false, Arrays.asList(createRotatedVariants(ModelsResourceUtil.decorateBlockModelLocation("two_dead_sea_pickles")))).select(3, false, Arrays.asList(createRotatedVariants(ModelsResourceUtil.decorateBlockModelLocation("three_dead_sea_pickles")))).select(4, false, Arrays.asList(createRotatedVariants(ModelsResourceUtil.decorateBlockModelLocation("four_dead_sea_pickles")))).select(1, true, Arrays.asList(createRotatedVariants(ModelsResourceUtil.decorateBlockModelLocation("sea_pickle")))).select(2, true, Arrays.asList(createRotatedVariants(ModelsResourceUtil.decorateBlockModelLocation("two_sea_pickles")))).select(3, true, Arrays.asList(createRotatedVariants(ModelsResourceUtil.decorateBlockModelLocation("three_sea_pickles")))).select(4, true, Arrays.asList(createRotatedVariants(ModelsResourceUtil.decorateBlockModelLocation("four_sea_pickles"))))));
   }

   private void createSnowBlocks() {
      ModelTextures modeltextures = ModelTextures.cube(Blocks.SNOW);
      ResourceLocation resourcelocation = StockModelShapes.CUBE_ALL.create(Blocks.SNOW_BLOCK, modeltextures, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.SNOW).with(BlockStateVariantBuilder.property(BlockStateProperties.LAYERS).generate((p_239918_1_) -> {
         return BlockModelDefinition.variant().with(BlockModelFields.MODEL, p_239918_1_ < 8 ? ModelsResourceUtil.getModelLocation(Blocks.SNOW, "_height" + p_239918_1_ * 2) : resourcelocation);
      })));
      this.delegateItemModel(Blocks.SNOW, ModelsResourceUtil.getModelLocation(Blocks.SNOW, "_height2"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SNOW_BLOCK, resourcelocation));
   }

   private void createStonecutter() {
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.STONECUTTER, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.STONECUTTER))).with(createHorizontalFacingDispatch()));
   }

   private void createStructureBlock() {
      ResourceLocation resourcelocation = TexturedModel.CUBE.create(Blocks.STRUCTURE_BLOCK, this.modelOutput);
      this.delegateItemModel(Blocks.STRUCTURE_BLOCK, resourcelocation);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.STRUCTURE_BLOCK).with(BlockStateVariantBuilder.property(BlockStateProperties.STRUCTUREBLOCK_MODE).generate((p_239896_1_) -> {
         return BlockModelDefinition.variant().with(BlockModelFields.MODEL, this.createSuffixedVariant(Blocks.STRUCTURE_BLOCK, "_" + p_239896_1_.getSerializedName(), StockModelShapes.CUBE_ALL, ModelTextures::cube));
      })));
   }

   private void createSweetBerryBush() {
      this.createSimpleFlatItemModel(Items.SWEET_BERRIES);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.SWEET_BERRY_BUSH).with(BlockStateVariantBuilder.property(BlockStateProperties.AGE_3).generate((p_239910_1_) -> {
         return BlockModelDefinition.variant().with(BlockModelFields.MODEL, this.createSuffixedVariant(Blocks.SWEET_BERRY_BUSH, "_stage" + p_239910_1_, StockModelShapes.CROSS, ModelTextures::cross));
      })));
   }

   private void createTripwire() {
      this.createSimpleFlatItemModel(Items.STRING);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.TRIPWIRE).with(BlockStateVariantBuilder.properties(BlockStateProperties.ATTACHED, BlockStateProperties.EAST, BlockStateProperties.NORTH, BlockStateProperties.SOUTH, BlockStateProperties.WEST).select(false, false, false, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_ns"))).select(false, true, false, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_n")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(false, false, true, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_n"))).select(false, false, false, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_n")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(false, false, false, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_n")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(false, true, true, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_ne"))).select(false, true, false, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(false, false, false, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(false, false, true, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(false, false, true, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_ns"))).select(false, true, false, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_ns")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(false, true, true, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_nse"))).select(false, true, false, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(false, false, true, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(false, true, true, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(false, true, true, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_nsew"))).select(true, false, false, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))).select(true, false, true, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_n"))).select(true, false, false, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(true, true, false, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(true, false, false, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(true, true, true, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_ne"))).select(true, true, false, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(true, false, false, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(true, false, true, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(true, false, true, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))).select(true, true, false, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_ns")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(true, true, true, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_nse"))).select(true, true, false, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(true, false, true, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(true, true, true, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(true, true, true, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.TRIPWIRE, "_attached_nsew")))));
   }

   private void createTripwireHook() {
      this.createSimpleFlatItemModel(Blocks.TRIPWIRE_HOOK);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.TRIPWIRE_HOOK).with(BlockStateVariantBuilder.properties(BlockStateProperties.ATTACHED, BlockStateProperties.POWERED).generate((p_239908_0_, p_239908_1_) -> {
         return BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelTextures.getBlockTexture(Blocks.TRIPWIRE_HOOK, (p_239908_0_ ? "_attached" : "") + (p_239908_1_ ? "_on" : "")));
      })).with(createHorizontalFacingDispatch()));
   }

   private ResourceLocation createTurtleEggModel(int p_239865_1_, String p_239865_2_, ModelTextures p_239865_3_) {
      switch(p_239865_1_) {
      case 1:
         return StockModelShapes.TURTLE_EGG.create(ModelsResourceUtil.decorateBlockModelLocation(p_239865_2_ + "turtle_egg"), p_239865_3_, this.modelOutput);
      case 2:
         return StockModelShapes.TWO_TURTLE_EGGS.create(ModelsResourceUtil.decorateBlockModelLocation("two_" + p_239865_2_ + "turtle_eggs"), p_239865_3_, this.modelOutput);
      case 3:
         return StockModelShapes.THREE_TURTLE_EGGS.create(ModelsResourceUtil.decorateBlockModelLocation("three_" + p_239865_2_ + "turtle_eggs"), p_239865_3_, this.modelOutput);
      case 4:
         return StockModelShapes.FOUR_TURTLE_EGGS.create(ModelsResourceUtil.decorateBlockModelLocation("four_" + p_239865_2_ + "turtle_eggs"), p_239865_3_, this.modelOutput);
      default:
         throw new UnsupportedOperationException();
      }
   }

   private ResourceLocation createTurtleEggModel(Integer p_239912_1_, Integer p_239912_2_) {
      switch(p_239912_2_) {
      case 0:
         return this.createTurtleEggModel(p_239912_1_, "", ModelTextures.cube(ModelTextures.getBlockTexture(Blocks.TURTLE_EGG)));
      case 1:
         return this.createTurtleEggModel(p_239912_1_, "slightly_cracked_", ModelTextures.cube(ModelTextures.getBlockTexture(Blocks.TURTLE_EGG, "_slightly_cracked")));
      case 2:
         return this.createTurtleEggModel(p_239912_1_, "very_cracked_", ModelTextures.cube(ModelTextures.getBlockTexture(Blocks.TURTLE_EGG, "_very_cracked")));
      default:
         throw new UnsupportedOperationException();
      }
   }

   private void createTurtleEgg() {
      this.createSimpleFlatItemModel(Items.TURTLE_EGG);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.TURTLE_EGG).with(BlockStateVariantBuilder.properties(BlockStateProperties.EGGS, BlockStateProperties.HATCH).generateList((p_239949_1_, p_239949_2_) -> {
         return Arrays.asList(createRotatedVariants(this.createTurtleEggModel(p_239949_1_, p_239949_2_)));
      })));
   }

   private void createVine() {
      this.createSimpleFlatItemModel(Blocks.VINE);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.VINE).with(BlockStateVariantBuilder.properties(BlockStateProperties.EAST, BlockStateProperties.NORTH, BlockStateProperties.SOUTH, BlockStateProperties.UP, BlockStateProperties.WEST).select(false, false, false, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_1"))).select(false, false, true, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_1"))).select(false, false, false, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_1")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(false, true, false, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_1")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(true, false, false, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_1")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(true, true, false, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2"))).select(true, false, true, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(false, false, true, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(false, true, false, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(true, false, false, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2_opposite"))).select(false, true, true, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2_opposite")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(true, true, true, false, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_3"))).select(true, false, true, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_3")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(false, true, true, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_3")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(true, true, false, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_3")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(true, true, true, false, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_4"))).select(false, false, false, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_u"))).select(false, false, true, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_1u"))).select(false, false, false, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_1u")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(false, true, false, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_1u")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(true, false, false, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_1u")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(true, true, false, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2u"))).select(true, false, true, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2u")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(false, false, true, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2u")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(false, true, false, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2u")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(true, false, false, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2u_opposite"))).select(false, true, true, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_2u_opposite")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(true, true, true, true, false, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_3u"))).select(true, false, true, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_3u")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90)).select(false, true, true, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_3u")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180)).select(true, true, false, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_3u")).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270)).select(true, true, true, true, true, BlockModelDefinition.variant().with(BlockModelFields.MODEL, ModelsResourceUtil.getModelLocation(Blocks.VINE, "_4u")))));
   }

   private void createMagmaBlock() {
      this.blockStateOutput.accept(createSimpleBlock(Blocks.MAGMA_BLOCK, StockModelShapes.CUBE_ALL.create(Blocks.MAGMA_BLOCK, ModelTextures.cube(ModelsResourceUtil.decorateBlockModelLocation("magma")), this.modelOutput)));
   }

   private void createShulkerBox(Block p_240043_1_) {
      this.createTrivialBlock(p_240043_1_, TexturedModel.PARTICLE_ONLY);
      StockModelShapes.SHULKER_BOX_INVENTORY.create(ModelsResourceUtil.getModelLocation(p_240043_1_.asItem()), ModelTextures.particle(p_240043_1_), this.modelOutput);
   }

   private void createGrowingPlant(Block p_239936_1_, Block p_239936_2_, BlockModelProvider.TintMode p_239936_3_) {
      this.createCrossBlock(p_239936_1_, p_239936_3_);
      this.createCrossBlock(p_239936_2_, p_239936_3_);
   }

   private void createBedItem(Block p_240015_1_, Block p_240015_2_) {
      StockModelShapes.BED_INVENTORY.create(ModelsResourceUtil.getModelLocation(p_240015_1_.asItem()), ModelTextures.particle(p_240015_2_), this.modelOutput);
   }

   private void createInfestedStone() {
      ResourceLocation resourcelocation = ModelsResourceUtil.getModelLocation(Blocks.STONE);
      ResourceLocation resourcelocation1 = ModelsResourceUtil.getModelLocation(Blocks.STONE, "_mirrored");
      this.blockStateOutput.accept(createRotatedVariant(Blocks.INFESTED_STONE, resourcelocation, resourcelocation1));
      this.delegateItemModel(Blocks.INFESTED_STONE, resourcelocation);
   }

   private void createNetherRoots(Block p_240019_1_, Block p_240019_2_) {
      this.createCrossBlockWithDefaultItem(p_240019_1_, BlockModelProvider.TintMode.NOT_TINTED);
      ModelTextures modeltextures = ModelTextures.plant(ModelTextures.getBlockTexture(p_240019_1_, "_pot"));
      ResourceLocation resourcelocation = BlockModelProvider.TintMode.NOT_TINTED.getCrossPot().create(p_240019_2_, modeltextures, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(p_240019_2_, resourcelocation));
   }

   private void createRespawnAnchor() {
      ResourceLocation resourcelocation = ModelTextures.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_bottom");
      ResourceLocation resourcelocation1 = ModelTextures.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top_off");
      ResourceLocation resourcelocation2 = ModelTextures.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top");
      ResourceLocation[] aresourcelocation = new ResourceLocation[5];

      for(int i = 0; i < 5; ++i) {
         ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.BOTTOM, resourcelocation).put(StockTextureAliases.TOP, i == 0 ? resourcelocation1 : resourcelocation2).put(StockTextureAliases.SIDE, ModelTextures.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_side" + i));
         aresourcelocation[i] = StockModelShapes.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.RESPAWN_ANCHOR, "_" + i, modeltextures, this.modelOutput);
      }

      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.RESPAWN_ANCHOR).with(BlockStateVariantBuilder.property(BlockStateProperties.RESPAWN_ANCHOR_CHARGES).generate((p_239922_1_) -> {
         return BlockModelDefinition.variant().with(BlockModelFields.MODEL, aresourcelocation[p_239922_1_]);
      })));
      this.delegateItemModel(Items.RESPAWN_ANCHOR, aresourcelocation[0]);
   }

   private BlockModelDefinition applyRotation(JigsawOrientation p_239898_1_, BlockModelDefinition p_239898_2_) {
      switch(p_239898_1_) {
      case DOWN_NORTH:
         return p_239898_2_.with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90);
      case DOWN_SOUTH:
         return p_239898_2_.with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180);
      case DOWN_WEST:
         return p_239898_2_.with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270);
      case DOWN_EAST:
         return p_239898_2_.with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R90).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90);
      case UP_NORTH:
         return p_239898_2_.with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180);
      case UP_SOUTH:
         return p_239898_2_.with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270);
      case UP_WEST:
         return p_239898_2_.with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90);
      case UP_EAST:
         return p_239898_2_.with(BlockModelFields.X_ROT, BlockModelFields.Rotation.R270).with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270);
      case NORTH_UP:
         return p_239898_2_;
      case SOUTH_UP:
         return p_239898_2_.with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R180);
      case WEST_UP:
         return p_239898_2_.with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R270);
      case EAST_UP:
         return p_239898_2_.with(BlockModelFields.Y_ROT, BlockModelFields.Rotation.R90);
      default:
         throw new UnsupportedOperationException("Rotation " + p_239898_1_ + " can't be expressed with existing x and y values");
      }
   }

   private void createJigsaw() {
      ResourceLocation resourcelocation = ModelTextures.getBlockTexture(Blocks.JIGSAW, "_top");
      ResourceLocation resourcelocation1 = ModelTextures.getBlockTexture(Blocks.JIGSAW, "_bottom");
      ResourceLocation resourcelocation2 = ModelTextures.getBlockTexture(Blocks.JIGSAW, "_side");
      ResourceLocation resourcelocation3 = ModelTextures.getBlockTexture(Blocks.JIGSAW, "_lock");
      ModelTextures modeltextures = (new ModelTextures()).put(StockTextureAliases.DOWN, resourcelocation2).put(StockTextureAliases.WEST, resourcelocation2).put(StockTextureAliases.EAST, resourcelocation2).put(StockTextureAliases.PARTICLE, resourcelocation).put(StockTextureAliases.NORTH, resourcelocation).put(StockTextureAliases.SOUTH, resourcelocation1).put(StockTextureAliases.UP, resourcelocation3);
      ResourceLocation resourcelocation4 = StockModelShapes.CUBE_DIRECTIONAL.create(Blocks.JIGSAW, modeltextures, this.modelOutput);
      this.blockStateOutput.accept(FinishedVariantBlockState.multiVariant(Blocks.JIGSAW, BlockModelDefinition.variant().with(BlockModelFields.MODEL, resourcelocation4)).with(BlockStateVariantBuilder.property(BlockStateProperties.ORIENTATION).generate((p_239897_1_) -> {
         return this.applyRotation(p_239897_1_, BlockModelDefinition.variant());
      })));
   }

   public void run() {
      this.createNonTemplateModelBlock(Blocks.AIR);
      this.createNonTemplateModelBlock(Blocks.CAVE_AIR, Blocks.AIR);
      this.createNonTemplateModelBlock(Blocks.VOID_AIR, Blocks.AIR);
      this.createNonTemplateModelBlock(Blocks.BEACON);
      this.createNonTemplateModelBlock(Blocks.CACTUS);
      this.createNonTemplateModelBlock(Blocks.BUBBLE_COLUMN, Blocks.WATER);
      this.createNonTemplateModelBlock(Blocks.DRAGON_EGG);
      this.createNonTemplateModelBlock(Blocks.DRIED_KELP_BLOCK);
      this.createNonTemplateModelBlock(Blocks.ENCHANTING_TABLE);
      this.createNonTemplateModelBlock(Blocks.FLOWER_POT);
      this.createSimpleFlatItemModel(Items.FLOWER_POT);
      this.createNonTemplateModelBlock(Blocks.HONEY_BLOCK);
      this.createNonTemplateModelBlock(Blocks.WATER);
      this.createNonTemplateModelBlock(Blocks.LAVA);
      this.createNonTemplateModelBlock(Blocks.SLIME_BLOCK);
      this.createSimpleFlatItemModel(Items.CHAIN);
      this.createNonTemplateModelBlock(Blocks.POTTED_BAMBOO);
      this.createNonTemplateModelBlock(Blocks.POTTED_CACTUS);
      this.createAirLikeBlock(Blocks.BARRIER, Items.BARRIER);
      this.createSimpleFlatItemModel(Items.BARRIER);
      this.createAirLikeBlock(Blocks.STRUCTURE_VOID, Items.STRUCTURE_VOID);
      this.createSimpleFlatItemModel(Items.STRUCTURE_VOID);
      this.createAirLikeBlock(Blocks.MOVING_PISTON, ModelTextures.getBlockTexture(Blocks.PISTON, "_side"));
      this.createTrivialBlock(Blocks.COAL_ORE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.COAL_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.DIAMOND_ORE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.DIAMOND_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.EMERALD_ORE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.EMERALD_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.GOLD_ORE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.NETHER_GOLD_ORE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.GOLD_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.IRON_ORE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.IRON_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.ANCIENT_DEBRIS, TexturedModel.COLUMN);
      this.createTrivialBlock(Blocks.NETHERITE_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.LAPIS_ORE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.LAPIS_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.NETHER_QUARTZ_ORE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.REDSTONE_ORE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.REDSTONE_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.GILDED_BLACKSTONE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.BLUE_ICE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.CHISELED_NETHER_BRICKS, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.CLAY, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.COARSE_DIRT, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.CRACKED_NETHER_BRICKS, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.CRACKED_STONE_BRICKS, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.CRYING_OBSIDIAN, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.END_STONE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.GLOWSTONE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.GRAVEL, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.HONEYCOMB_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.ICE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.JUKEBOX, TexturedModel.CUBE_TOP);
      this.createTrivialBlock(Blocks.LODESTONE, TexturedModel.COLUMN);
      this.createTrivialBlock(Blocks.MELON, TexturedModel.COLUMN);
      this.createTrivialBlock(Blocks.NETHER_WART_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.NOTE_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.PACKED_ICE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.OBSIDIAN, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.QUARTZ_BRICKS, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.SEA_LANTERN, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.SHROOMLIGHT, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.SOUL_SAND, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.SOUL_SOIL, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.SPAWNER, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.SPONGE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.SEAGRASS, TexturedModel.SEAGRASS);
      this.createSimpleFlatItemModel(Items.SEAGRASS);
      this.createTrivialBlock(Blocks.TNT, TexturedModel.CUBE_TOP_BOTTOM);
      this.createTrivialBlock(Blocks.TARGET, TexturedModel.COLUMN);
      this.createTrivialBlock(Blocks.WARPED_WART_BLOCK, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.WET_SPONGE, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, TexturedModel.CUBE);
      this.createTrivialBlock(Blocks.CHISELED_QUARTZ_BLOCK, TexturedModel.COLUMN.updateTexture((p_239982_0_) -> {
         p_239982_0_.put(StockTextureAliases.SIDE, ModelTextures.getBlockTexture(Blocks.CHISELED_QUARTZ_BLOCK));
      }));
      this.createTrivialBlock(Blocks.CHISELED_STONE_BRICKS, TexturedModel.CUBE);
      this.createChiseledSandsone(Blocks.CHISELED_SANDSTONE, Blocks.SANDSTONE);
      this.createChiseledSandsone(Blocks.CHISELED_RED_SANDSTONE, Blocks.RED_SANDSTONE);
      this.createTrivialBlock(Blocks.CHISELED_POLISHED_BLACKSTONE, TexturedModel.CUBE);
      this.createWeightedPressurePlate(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.GOLD_BLOCK);
      this.createWeightedPressurePlate(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.IRON_BLOCK);
      this.createBookshelf();
      this.createBrewingStand();
      this.createCakeBlock();
      this.createCampfires(Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
      this.createCartographyTable();
      this.createCauldron();
      this.createChorusFlower();
      this.createChorusPlant();
      this.createComposter();
      this.createDaylightDetector();
      this.createEndPortalFrame();
      this.createRotatableColumn(Blocks.END_ROD);
      this.createFarmland();
      this.createFire();
      this.createSoulFire();
      this.createFrostedIce();
      this.createGrassBlocks();
      this.createCocoa();
      this.createGrassPath();
      this.createGrindstone();
      this.createHopper();
      this.createIronBars();
      this.createLever();
      this.createLilyPad();
      this.createNetherPortalBlock();
      this.createNetherrack();
      this.createObserver();
      this.createPistons();
      this.createPistonHeads();
      this.createScaffolding();
      this.createRedstoneTorch();
      this.createRedstoneLamp();
      this.createRepeater();
      this.createSeaPickle();
      this.createSmithingTable();
      this.createSnowBlocks();
      this.createStonecutter();
      this.createStructureBlock();
      this.createSweetBerryBush();
      this.createTripwire();
      this.createTripwireHook();
      this.createTurtleEgg();
      this.createVine();
      this.createMagmaBlock();
      this.createJigsaw();
      this.createNonTemplateHorizontalBlock(Blocks.LADDER);
      this.createSimpleFlatItemModel(Blocks.LADDER);
      this.createNonTemplateHorizontalBlock(Blocks.LECTERN);
      this.createNormalTorch(Blocks.TORCH, Blocks.WALL_TORCH);
      this.createNormalTorch(Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH);
      this.createCraftingTableLike(Blocks.CRAFTING_TABLE, Blocks.OAK_PLANKS, ModelTextures::craftingTable);
      this.createCraftingTableLike(Blocks.FLETCHING_TABLE, Blocks.BIRCH_PLANKS, ModelTextures::fletchingTable);
      this.createNyliumBlock(Blocks.CRIMSON_NYLIUM);
      this.createNyliumBlock(Blocks.WARPED_NYLIUM);
      this.createDispenserBlock(Blocks.DISPENSER);
      this.createDispenserBlock(Blocks.DROPPER);
      this.createLantern(Blocks.LANTERN);
      this.createLantern(Blocks.SOUL_LANTERN);
      this.createAxisAlignedPillarBlockCustomModel(Blocks.CHAIN, ModelsResourceUtil.getModelLocation(Blocks.CHAIN));
      this.createAxisAlignedPillarBlock(Blocks.BASALT, TexturedModel.COLUMN);
      this.createAxisAlignedPillarBlock(Blocks.POLISHED_BASALT, TexturedModel.COLUMN);
      this.createAxisAlignedPillarBlock(Blocks.BONE_BLOCK, TexturedModel.COLUMN);
      this.createRotatedVariantBlock(Blocks.DIRT);
      this.createRotatedVariantBlock(Blocks.SAND);
      this.createRotatedVariantBlock(Blocks.RED_SAND);
      this.createRotatedMirroredVariantBlock(Blocks.BEDROCK);
      this.createRotatedPillarWithHorizontalVariant(Blocks.HAY_BLOCK, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
      this.createRotatedPillarWithHorizontalVariant(Blocks.PURPUR_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
      this.createRotatedPillarWithHorizontalVariant(Blocks.QUARTZ_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
      this.createHorizontallyRotatedBlock(Blocks.LOOM, TexturedModel.ORIENTABLE);
      this.createPumpkins();
      this.createBeeNest(Blocks.BEE_NEST, ModelTextures::orientableCube);
      this.createBeeNest(Blocks.BEEHIVE, ModelTextures::orientableCubeSameEnds);
      this.createCropBlock(Blocks.BEETROOTS, BlockStateProperties.AGE_3, 0, 1, 2, 3);
      this.createCropBlock(Blocks.CARROTS, BlockStateProperties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
      this.createCropBlock(Blocks.NETHER_WART, BlockStateProperties.AGE_3, 0, 1, 1, 2);
      this.createCropBlock(Blocks.POTATOES, BlockStateProperties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
      this.createCropBlock(Blocks.WHEAT, BlockStateProperties.AGE_7, 0, 1, 2, 3, 4, 5, 6, 7);
      this.blockEntityModels(ModelsResourceUtil.decorateBlockModelLocation("banner"), Blocks.OAK_PLANKS).createWithCustomBlockItemModel(StockModelShapes.BANNER_INVENTORY, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER).createWithoutBlockItem(Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER);
      this.blockEntityModels(ModelsResourceUtil.decorateBlockModelLocation("bed"), Blocks.OAK_PLANKS).createWithoutBlockItem(Blocks.WHITE_BED, Blocks.ORANGE_BED, Blocks.MAGENTA_BED, Blocks.LIGHT_BLUE_BED, Blocks.YELLOW_BED, Blocks.LIME_BED, Blocks.PINK_BED, Blocks.GRAY_BED, Blocks.LIGHT_GRAY_BED, Blocks.CYAN_BED, Blocks.PURPLE_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.GREEN_BED, Blocks.RED_BED, Blocks.BLACK_BED);
      this.createBedItem(Blocks.WHITE_BED, Blocks.WHITE_WOOL);
      this.createBedItem(Blocks.ORANGE_BED, Blocks.ORANGE_WOOL);
      this.createBedItem(Blocks.MAGENTA_BED, Blocks.MAGENTA_WOOL);
      this.createBedItem(Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
      this.createBedItem(Blocks.YELLOW_BED, Blocks.YELLOW_WOOL);
      this.createBedItem(Blocks.LIME_BED, Blocks.LIME_WOOL);
      this.createBedItem(Blocks.PINK_BED, Blocks.PINK_WOOL);
      this.createBedItem(Blocks.GRAY_BED, Blocks.GRAY_WOOL);
      this.createBedItem(Blocks.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
      this.createBedItem(Blocks.CYAN_BED, Blocks.CYAN_WOOL);
      this.createBedItem(Blocks.PURPLE_BED, Blocks.PURPLE_WOOL);
      this.createBedItem(Blocks.BLUE_BED, Blocks.BLUE_WOOL);
      this.createBedItem(Blocks.BROWN_BED, Blocks.BROWN_WOOL);
      this.createBedItem(Blocks.GREEN_BED, Blocks.GREEN_WOOL);
      this.createBedItem(Blocks.RED_BED, Blocks.RED_WOOL);
      this.createBedItem(Blocks.BLACK_BED, Blocks.BLACK_WOOL);
      this.blockEntityModels(ModelsResourceUtil.decorateBlockModelLocation("skull"), Blocks.SOUL_SAND).createWithCustomBlockItemModel(StockModelShapes.SKULL_INVENTORY, Blocks.CREEPER_HEAD, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL).create(Blocks.DRAGON_HEAD).createWithoutBlockItem(Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.SKELETON_WALL_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL);
      this.createShulkerBox(Blocks.SHULKER_BOX);
      this.createShulkerBox(Blocks.WHITE_SHULKER_BOX);
      this.createShulkerBox(Blocks.ORANGE_SHULKER_BOX);
      this.createShulkerBox(Blocks.MAGENTA_SHULKER_BOX);
      this.createShulkerBox(Blocks.LIGHT_BLUE_SHULKER_BOX);
      this.createShulkerBox(Blocks.YELLOW_SHULKER_BOX);
      this.createShulkerBox(Blocks.LIME_SHULKER_BOX);
      this.createShulkerBox(Blocks.PINK_SHULKER_BOX);
      this.createShulkerBox(Blocks.GRAY_SHULKER_BOX);
      this.createShulkerBox(Blocks.LIGHT_GRAY_SHULKER_BOX);
      this.createShulkerBox(Blocks.CYAN_SHULKER_BOX);
      this.createShulkerBox(Blocks.PURPLE_SHULKER_BOX);
      this.createShulkerBox(Blocks.BLUE_SHULKER_BOX);
      this.createShulkerBox(Blocks.BROWN_SHULKER_BOX);
      this.createShulkerBox(Blocks.GREEN_SHULKER_BOX);
      this.createShulkerBox(Blocks.RED_SHULKER_BOX);
      this.createShulkerBox(Blocks.BLACK_SHULKER_BOX);
      this.createTrivialBlock(Blocks.CONDUIT, TexturedModel.PARTICLE_ONLY);
      this.skipAutoItemBlock(Blocks.CONDUIT);
      this.blockEntityModels(ModelsResourceUtil.decorateBlockModelLocation("chest"), Blocks.OAK_PLANKS).createWithoutBlockItem(Blocks.CHEST, Blocks.TRAPPED_CHEST);
      this.blockEntityModels(ModelsResourceUtil.decorateBlockModelLocation("ender_chest"), Blocks.OBSIDIAN).createWithoutBlockItem(Blocks.ENDER_CHEST);
      this.blockEntityModels(Blocks.END_PORTAL, Blocks.OBSIDIAN).create(Blocks.END_PORTAL, Blocks.END_GATEWAY);
      this.createTrivialCube(Blocks.WHITE_CONCRETE);
      this.createTrivialCube(Blocks.ORANGE_CONCRETE);
      this.createTrivialCube(Blocks.MAGENTA_CONCRETE);
      this.createTrivialCube(Blocks.LIGHT_BLUE_CONCRETE);
      this.createTrivialCube(Blocks.YELLOW_CONCRETE);
      this.createTrivialCube(Blocks.LIME_CONCRETE);
      this.createTrivialCube(Blocks.PINK_CONCRETE);
      this.createTrivialCube(Blocks.GRAY_CONCRETE);
      this.createTrivialCube(Blocks.LIGHT_GRAY_CONCRETE);
      this.createTrivialCube(Blocks.CYAN_CONCRETE);
      this.createTrivialCube(Blocks.PURPLE_CONCRETE);
      this.createTrivialCube(Blocks.BLUE_CONCRETE);
      this.createTrivialCube(Blocks.BROWN_CONCRETE);
      this.createTrivialCube(Blocks.GREEN_CONCRETE);
      this.createTrivialCube(Blocks.RED_CONCRETE);
      this.createTrivialCube(Blocks.BLACK_CONCRETE);
      this.createColoredBlockWithRandomRotations(TexturedModel.CUBE, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
      this.createTrivialCube(Blocks.TERRACOTTA);
      this.createTrivialCube(Blocks.WHITE_TERRACOTTA);
      this.createTrivialCube(Blocks.ORANGE_TERRACOTTA);
      this.createTrivialCube(Blocks.MAGENTA_TERRACOTTA);
      this.createTrivialCube(Blocks.LIGHT_BLUE_TERRACOTTA);
      this.createTrivialCube(Blocks.YELLOW_TERRACOTTA);
      this.createTrivialCube(Blocks.LIME_TERRACOTTA);
      this.createTrivialCube(Blocks.PINK_TERRACOTTA);
      this.createTrivialCube(Blocks.GRAY_TERRACOTTA);
      this.createTrivialCube(Blocks.LIGHT_GRAY_TERRACOTTA);
      this.createTrivialCube(Blocks.CYAN_TERRACOTTA);
      this.createTrivialCube(Blocks.PURPLE_TERRACOTTA);
      this.createTrivialCube(Blocks.BLUE_TERRACOTTA);
      this.createTrivialCube(Blocks.BROWN_TERRACOTTA);
      this.createTrivialCube(Blocks.GREEN_TERRACOTTA);
      this.createTrivialCube(Blocks.RED_TERRACOTTA);
      this.createTrivialCube(Blocks.BLACK_TERRACOTTA);
      this.createGlassBlocks(Blocks.GLASS, Blocks.GLASS_PANE);
      this.createGlassBlocks(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
      this.createColoredBlockWithStateRotations(TexturedModel.GLAZED_TERRACOTTA, Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
      this.createWoolBlocks(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
      this.createWoolBlocks(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
      this.createWoolBlocks(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
      this.createWoolBlocks(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
      this.createWoolBlocks(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
      this.createWoolBlocks(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
      this.createWoolBlocks(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
      this.createWoolBlocks(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
      this.createWoolBlocks(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
      this.createWoolBlocks(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
      this.createWoolBlocks(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
      this.createWoolBlocks(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
      this.createWoolBlocks(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
      this.createWoolBlocks(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
      this.createWoolBlocks(Blocks.RED_WOOL, Blocks.RED_CARPET);
      this.createWoolBlocks(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);
      this.createPlant(Blocks.FERN, Blocks.POTTED_FERN, BlockModelProvider.TintMode.TINTED);
      this.createPlant(Blocks.DANDELION, Blocks.POTTED_DANDELION, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.POPPY, Blocks.POTTED_POPPY, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.ALLIUM, Blocks.POTTED_ALLIUM, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, BlockModelProvider.TintMode.NOT_TINTED);
      this.createPlant(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH, BlockModelProvider.TintMode.NOT_TINTED);
      this.createMushroomBlock(Blocks.BROWN_MUSHROOM_BLOCK);
      this.createMushroomBlock(Blocks.RED_MUSHROOM_BLOCK);
      this.createMushroomBlock(Blocks.MUSHROOM_STEM);
      this.createCrossBlockWithDefaultItem(Blocks.GRASS, BlockModelProvider.TintMode.TINTED);
      this.createCrossBlock(Blocks.SUGAR_CANE, BlockModelProvider.TintMode.TINTED);
      this.createSimpleFlatItemModel(Items.SUGAR_CANE);
      this.createGrowingPlant(Blocks.KELP, Blocks.KELP_PLANT, BlockModelProvider.TintMode.TINTED);
      this.createSimpleFlatItemModel(Items.KELP);
      this.skipAutoItemBlock(Blocks.KELP_PLANT);
      this.createGrowingPlant(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, BlockModelProvider.TintMode.NOT_TINTED);
      this.createGrowingPlant(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT, BlockModelProvider.TintMode.NOT_TINTED);
      this.createSimpleFlatItemModel(Blocks.WEEPING_VINES, "_plant");
      this.skipAutoItemBlock(Blocks.WEEPING_VINES_PLANT);
      this.createSimpleFlatItemModel(Blocks.TWISTING_VINES, "_plant");
      this.skipAutoItemBlock(Blocks.TWISTING_VINES_PLANT);
      this.createCrossBlockWithDefaultItem(Blocks.BAMBOO_SAPLING, BlockModelProvider.TintMode.TINTED, ModelTextures.cross(ModelTextures.getBlockTexture(Blocks.BAMBOO, "_stage0")));
      this.createBamboo();
      this.createCrossBlockWithDefaultItem(Blocks.COBWEB, BlockModelProvider.TintMode.NOT_TINTED);
      this.createDoublePlant(Blocks.LILAC, BlockModelProvider.TintMode.NOT_TINTED);
      this.createDoublePlant(Blocks.ROSE_BUSH, BlockModelProvider.TintMode.NOT_TINTED);
      this.createDoublePlant(Blocks.PEONY, BlockModelProvider.TintMode.NOT_TINTED);
      this.createDoublePlant(Blocks.TALL_GRASS, BlockModelProvider.TintMode.TINTED);
      this.createDoublePlant(Blocks.LARGE_FERN, BlockModelProvider.TintMode.TINTED);
      this.createSunflower();
      this.createTallSeagrass();
      this.createCoral(Blocks.TUBE_CORAL, Blocks.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN);
      this.createCoral(Blocks.BRAIN_CORAL, Blocks.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN);
      this.createCoral(Blocks.BUBBLE_CORAL, Blocks.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN);
      this.createCoral(Blocks.FIRE_CORAL, Blocks.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN);
      this.createCoral(Blocks.HORN_CORAL, Blocks.DEAD_HORN_CORAL, Blocks.HORN_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN);
      this.createStems(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM);
      this.createStems(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
      this.family(Blocks.ACACIA_PLANKS).button(Blocks.ACACIA_BUTTON).fence(Blocks.ACACIA_FENCE).fenceGate(Blocks.ACACIA_FENCE_GATE).pressurePlate(Blocks.ACACIA_PRESSURE_PLATE).sign(Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN).slab(Blocks.ACACIA_SLAB).stairs(Blocks.ACACIA_STAIRS);
      this.createDoor(Blocks.ACACIA_DOOR);
      this.createOrientableTrapdoor(Blocks.ACACIA_TRAPDOOR);
      this.woodProvider(Blocks.ACACIA_LOG).logWithHorizontal(Blocks.ACACIA_LOG).wood(Blocks.ACACIA_WOOD);
      this.woodProvider(Blocks.STRIPPED_ACACIA_LOG).logWithHorizontal(Blocks.STRIPPED_ACACIA_LOG).wood(Blocks.STRIPPED_ACACIA_WOOD);
      this.createPlant(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING, BlockModelProvider.TintMode.NOT_TINTED);
      this.createTrivialBlock(Blocks.ACACIA_LEAVES, TexturedModel.LEAVES);
      this.family(Blocks.BIRCH_PLANKS).button(Blocks.BIRCH_BUTTON).fence(Blocks.BIRCH_FENCE).fenceGate(Blocks.BIRCH_FENCE_GATE).pressurePlate(Blocks.BIRCH_PRESSURE_PLATE).sign(Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN).slab(Blocks.BIRCH_SLAB).stairs(Blocks.BIRCH_STAIRS);
      this.createDoor(Blocks.BIRCH_DOOR);
      this.createOrientableTrapdoor(Blocks.BIRCH_TRAPDOOR);
      this.woodProvider(Blocks.BIRCH_LOG).logWithHorizontal(Blocks.BIRCH_LOG).wood(Blocks.BIRCH_WOOD);
      this.woodProvider(Blocks.STRIPPED_BIRCH_LOG).logWithHorizontal(Blocks.STRIPPED_BIRCH_LOG).wood(Blocks.STRIPPED_BIRCH_WOOD);
      this.createPlant(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING, BlockModelProvider.TintMode.NOT_TINTED);
      this.createTrivialBlock(Blocks.BIRCH_LEAVES, TexturedModel.LEAVES);
      this.family(Blocks.OAK_PLANKS).button(Blocks.OAK_BUTTON).fence(Blocks.OAK_FENCE).fenceGate(Blocks.OAK_FENCE_GATE).pressurePlate(Blocks.OAK_PRESSURE_PLATE).sign(Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN).slab(Blocks.OAK_SLAB).slab(Blocks.PETRIFIED_OAK_SLAB).stairs(Blocks.OAK_STAIRS);
      this.createDoor(Blocks.OAK_DOOR);
      this.createTrapdoor(Blocks.OAK_TRAPDOOR);
      this.woodProvider(Blocks.OAK_LOG).logWithHorizontal(Blocks.OAK_LOG).wood(Blocks.OAK_WOOD);
      this.woodProvider(Blocks.STRIPPED_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_OAK_LOG).wood(Blocks.STRIPPED_OAK_WOOD);
      this.createPlant(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING, BlockModelProvider.TintMode.NOT_TINTED);
      this.createTrivialBlock(Blocks.OAK_LEAVES, TexturedModel.LEAVES);
      this.family(Blocks.SPRUCE_PLANKS).button(Blocks.SPRUCE_BUTTON).fence(Blocks.SPRUCE_FENCE).fenceGate(Blocks.SPRUCE_FENCE_GATE).pressurePlate(Blocks.SPRUCE_PRESSURE_PLATE).sign(Blocks.SPRUCE_SIGN, Blocks.SPRUCE_WALL_SIGN).slab(Blocks.SPRUCE_SLAB).stairs(Blocks.SPRUCE_STAIRS);
      this.createDoor(Blocks.SPRUCE_DOOR);
      this.createOrientableTrapdoor(Blocks.SPRUCE_TRAPDOOR);
      this.woodProvider(Blocks.SPRUCE_LOG).logWithHorizontal(Blocks.SPRUCE_LOG).wood(Blocks.SPRUCE_WOOD);
      this.woodProvider(Blocks.STRIPPED_SPRUCE_LOG).logWithHorizontal(Blocks.STRIPPED_SPRUCE_LOG).wood(Blocks.STRIPPED_SPRUCE_WOOD);
      this.createPlant(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, BlockModelProvider.TintMode.NOT_TINTED);
      this.createTrivialBlock(Blocks.SPRUCE_LEAVES, TexturedModel.LEAVES);
      this.family(Blocks.DARK_OAK_PLANKS).button(Blocks.DARK_OAK_BUTTON).fence(Blocks.DARK_OAK_FENCE).fenceGate(Blocks.DARK_OAK_FENCE_GATE).pressurePlate(Blocks.DARK_OAK_PRESSURE_PLATE).sign(Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_WALL_SIGN).slab(Blocks.DARK_OAK_SLAB).stairs(Blocks.DARK_OAK_STAIRS);
      this.createDoor(Blocks.DARK_OAK_DOOR);
      this.createTrapdoor(Blocks.DARK_OAK_TRAPDOOR);
      this.woodProvider(Blocks.DARK_OAK_LOG).logWithHorizontal(Blocks.DARK_OAK_LOG).wood(Blocks.DARK_OAK_WOOD);
      this.woodProvider(Blocks.STRIPPED_DARK_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_DARK_OAK_LOG).wood(Blocks.STRIPPED_DARK_OAK_WOOD);
      this.createPlant(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, BlockModelProvider.TintMode.NOT_TINTED);
      this.createTrivialBlock(Blocks.DARK_OAK_LEAVES, TexturedModel.LEAVES);
      this.family(Blocks.JUNGLE_PLANKS).button(Blocks.JUNGLE_BUTTON).fence(Blocks.JUNGLE_FENCE).fenceGate(Blocks.JUNGLE_FENCE_GATE).pressurePlate(Blocks.JUNGLE_PRESSURE_PLATE).sign(Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN).slab(Blocks.JUNGLE_SLAB).stairs(Blocks.JUNGLE_STAIRS);
      this.createDoor(Blocks.JUNGLE_DOOR);
      this.createOrientableTrapdoor(Blocks.JUNGLE_TRAPDOOR);
      this.woodProvider(Blocks.JUNGLE_LOG).logWithHorizontal(Blocks.JUNGLE_LOG).wood(Blocks.JUNGLE_WOOD);
      this.woodProvider(Blocks.STRIPPED_JUNGLE_LOG).logWithHorizontal(Blocks.STRIPPED_JUNGLE_LOG).wood(Blocks.STRIPPED_JUNGLE_WOOD);
      this.createPlant(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, BlockModelProvider.TintMode.NOT_TINTED);
      this.createTrivialBlock(Blocks.JUNGLE_LEAVES, TexturedModel.LEAVES);
      this.family(Blocks.CRIMSON_PLANKS).button(Blocks.CRIMSON_BUTTON).fence(Blocks.CRIMSON_FENCE).fenceGate(Blocks.CRIMSON_FENCE_GATE).pressurePlate(Blocks.CRIMSON_PRESSURE_PLATE).sign(Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN).slab(Blocks.CRIMSON_SLAB).stairs(Blocks.CRIMSON_STAIRS);
      this.createDoor(Blocks.CRIMSON_DOOR);
      this.createOrientableTrapdoor(Blocks.CRIMSON_TRAPDOOR);
      this.woodProvider(Blocks.CRIMSON_STEM).log(Blocks.CRIMSON_STEM).wood(Blocks.CRIMSON_HYPHAE);
      this.woodProvider(Blocks.STRIPPED_CRIMSON_STEM).log(Blocks.STRIPPED_CRIMSON_STEM).wood(Blocks.STRIPPED_CRIMSON_HYPHAE);
      this.createPlant(Blocks.CRIMSON_FUNGUS, Blocks.POTTED_CRIMSON_FUNGUS, BlockModelProvider.TintMode.NOT_TINTED);
      this.createNetherRoots(Blocks.CRIMSON_ROOTS, Blocks.POTTED_CRIMSON_ROOTS);
      this.family(Blocks.WARPED_PLANKS).button(Blocks.WARPED_BUTTON).fence(Blocks.WARPED_FENCE).fenceGate(Blocks.WARPED_FENCE_GATE).pressurePlate(Blocks.WARPED_PRESSURE_PLATE).sign(Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN).slab(Blocks.WARPED_SLAB).stairs(Blocks.WARPED_STAIRS);
      this.createDoor(Blocks.WARPED_DOOR);
      this.createOrientableTrapdoor(Blocks.WARPED_TRAPDOOR);
      this.woodProvider(Blocks.WARPED_STEM).log(Blocks.WARPED_STEM).wood(Blocks.WARPED_HYPHAE);
      this.woodProvider(Blocks.STRIPPED_WARPED_STEM).log(Blocks.STRIPPED_WARPED_STEM).wood(Blocks.STRIPPED_WARPED_HYPHAE);
      this.createPlant(Blocks.WARPED_FUNGUS, Blocks.POTTED_WARPED_FUNGUS, BlockModelProvider.TintMode.NOT_TINTED);
      this.createNetherRoots(Blocks.WARPED_ROOTS, Blocks.POTTED_WARPED_ROOTS);
      this.createCrossBlock(Blocks.NETHER_SPROUTS, BlockModelProvider.TintMode.NOT_TINTED);
      this.createSimpleFlatItemModel(Items.NETHER_SPROUTS);
      this.family(ModelTextures.cube(Blocks.STONE)).fullBlock((p_239972_1_) -> {
         ResourceLocation resourcelocation = StockModelShapes.CUBE_ALL.create(Blocks.STONE, p_239972_1_, this.modelOutput);
         ResourceLocation resourcelocation1 = StockModelShapes.CUBE_MIRRORED_ALL.create(Blocks.STONE, p_239972_1_, this.modelOutput);
         this.blockStateOutput.accept(createRotatedVariant(Blocks.STONE, resourcelocation, resourcelocation1));
         return resourcelocation;
      }).slab(Blocks.STONE_SLAB).pressurePlate(Blocks.STONE_PRESSURE_PLATE).button(Blocks.STONE_BUTTON).stairs(Blocks.STONE_STAIRS);
      this.createDoor(Blocks.IRON_DOOR);
      this.createTrapdoor(Blocks.IRON_TRAPDOOR);
      this.family(Blocks.STONE_BRICKS).wall(Blocks.STONE_BRICK_WALL).stairs(Blocks.STONE_BRICK_STAIRS).slab(Blocks.STONE_BRICK_SLAB);
      this.family(Blocks.MOSSY_STONE_BRICKS).wall(Blocks.MOSSY_STONE_BRICK_WALL).stairs(Blocks.MOSSY_STONE_BRICK_STAIRS).slab(Blocks.MOSSY_STONE_BRICK_SLAB);
      this.family(Blocks.COBBLESTONE).wall(Blocks.COBBLESTONE_WALL).stairs(Blocks.COBBLESTONE_STAIRS).slab(Blocks.COBBLESTONE_SLAB);
      this.family(Blocks.MOSSY_COBBLESTONE).wall(Blocks.MOSSY_COBBLESTONE_WALL).stairs(Blocks.MOSSY_COBBLESTONE_STAIRS).slab(Blocks.MOSSY_COBBLESTONE_SLAB);
      this.family(Blocks.PRISMARINE).wall(Blocks.PRISMARINE_WALL).stairs(Blocks.PRISMARINE_STAIRS).slab(Blocks.PRISMARINE_SLAB);
      this.family(Blocks.PRISMARINE_BRICKS).stairs(Blocks.PRISMARINE_BRICK_STAIRS).slab(Blocks.PRISMARINE_BRICK_SLAB);
      this.family(Blocks.DARK_PRISMARINE).stairs(Blocks.DARK_PRISMARINE_STAIRS).slab(Blocks.DARK_PRISMARINE_SLAB);
      this.family(Blocks.SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL).wall(Blocks.SANDSTONE_WALL).stairs(Blocks.SANDSTONE_STAIRS).slab(Blocks.SANDSTONE_SLAB);
      this.family(Blocks.SMOOTH_SANDSTONE, TexturedModel.createAllSame(ModelTextures.getBlockTexture(Blocks.SANDSTONE, "_top"))).slab(Blocks.SMOOTH_SANDSTONE_SLAB).stairs(Blocks.SMOOTH_SANDSTONE_STAIRS);
      this.family(Blocks.CUT_SANDSTONE, TexturedModel.COLUMN.get(Blocks.SANDSTONE).updateTextures((p_239962_0_) -> {
         p_239962_0_.put(StockTextureAliases.SIDE, ModelTextures.getBlockTexture(Blocks.CUT_SANDSTONE));
      })).slab(Blocks.CUT_SANDSTONE_SLAB);
      this.family(Blocks.RED_SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL).wall(Blocks.RED_SANDSTONE_WALL).stairs(Blocks.RED_SANDSTONE_STAIRS).slab(Blocks.RED_SANDSTONE_SLAB);
      this.family(Blocks.SMOOTH_RED_SANDSTONE, TexturedModel.createAllSame(ModelTextures.getBlockTexture(Blocks.RED_SANDSTONE, "_top"))).slab(Blocks.SMOOTH_RED_SANDSTONE_SLAB).stairs(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
      this.family(Blocks.CUT_RED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.RED_SANDSTONE).updateTextures((p_239946_0_) -> {
         p_239946_0_.put(StockTextureAliases.SIDE, ModelTextures.getBlockTexture(Blocks.CUT_RED_SANDSTONE));
      })).slab(Blocks.CUT_RED_SANDSTONE_SLAB);
      this.family(Blocks.BRICKS).wall(Blocks.BRICK_WALL).stairs(Blocks.BRICK_STAIRS).slab(Blocks.BRICK_SLAB);
      this.family(Blocks.NETHER_BRICKS).fence(Blocks.NETHER_BRICK_FENCE).wall(Blocks.NETHER_BRICK_WALL).stairs(Blocks.NETHER_BRICK_STAIRS).slab(Blocks.NETHER_BRICK_SLAB);
      this.family(Blocks.PURPUR_BLOCK).stairs(Blocks.PURPUR_STAIRS).slab(Blocks.PURPUR_SLAB);
      this.family(Blocks.DIORITE).wall(Blocks.DIORITE_WALL).stairs(Blocks.DIORITE_STAIRS).slab(Blocks.DIORITE_SLAB);
      this.family(Blocks.POLISHED_DIORITE).stairs(Blocks.POLISHED_DIORITE_STAIRS).slab(Blocks.POLISHED_DIORITE_SLAB);
      this.family(Blocks.GRANITE).wall(Blocks.GRANITE_WALL).stairs(Blocks.GRANITE_STAIRS).slab(Blocks.GRANITE_SLAB);
      this.family(Blocks.POLISHED_GRANITE).stairs(Blocks.POLISHED_GRANITE_STAIRS).slab(Blocks.POLISHED_GRANITE_SLAB);
      this.family(Blocks.ANDESITE).wall(Blocks.ANDESITE_WALL).stairs(Blocks.ANDESITE_STAIRS).slab(Blocks.ANDESITE_SLAB);
      this.family(Blocks.POLISHED_ANDESITE).stairs(Blocks.POLISHED_ANDESITE_STAIRS).slab(Blocks.POLISHED_ANDESITE_SLAB);
      this.family(Blocks.END_STONE_BRICKS).wall(Blocks.END_STONE_BRICK_WALL).stairs(Blocks.END_STONE_BRICK_STAIRS).slab(Blocks.END_STONE_BRICK_SLAB);
      this.family(Blocks.QUARTZ_BLOCK, TexturedModel.COLUMN).stairs(Blocks.QUARTZ_STAIRS).slab(Blocks.QUARTZ_SLAB);
      this.family(Blocks.SMOOTH_QUARTZ, TexturedModel.createAllSame(ModelTextures.getBlockTexture(Blocks.QUARTZ_BLOCK, "_bottom"))).stairs(Blocks.SMOOTH_QUARTZ_STAIRS).slab(Blocks.SMOOTH_QUARTZ_SLAB);
      this.family(Blocks.RED_NETHER_BRICKS).slab(Blocks.RED_NETHER_BRICK_SLAB).stairs(Blocks.RED_NETHER_BRICK_STAIRS).wall(Blocks.RED_NETHER_BRICK_WALL);
      this.family(Blocks.BLACKSTONE, TexturedModel.COLUMN_WITH_WALL).wall(Blocks.BLACKSTONE_WALL).stairs(Blocks.BLACKSTONE_STAIRS).slab(Blocks.BLACKSTONE_SLAB);
      this.family(Blocks.POLISHED_BLACKSTONE_BRICKS).wall(Blocks.POLISHED_BLACKSTONE_BRICK_WALL).stairs(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS).slab(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
      this.family(Blocks.POLISHED_BLACKSTONE).wall(Blocks.POLISHED_BLACKSTONE_WALL).pressurePlate(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE).button(Blocks.POLISHED_BLACKSTONE_BUTTON).stairs(Blocks.POLISHED_BLACKSTONE_STAIRS).slab(Blocks.POLISHED_BLACKSTONE_SLAB);
      this.createSmoothStoneSlab();
      this.createPassiveRail(Blocks.RAIL);
      this.createActiveRail(Blocks.POWERED_RAIL);
      this.createActiveRail(Blocks.DETECTOR_RAIL);
      this.createActiveRail(Blocks.ACTIVATOR_RAIL);
      this.createComparator();
      this.createCommandBlock(Blocks.COMMAND_BLOCK);
      this.createCommandBlock(Blocks.REPEATING_COMMAND_BLOCK);
      this.createCommandBlock(Blocks.CHAIN_COMMAND_BLOCK);
      this.createAnvil(Blocks.ANVIL);
      this.createAnvil(Blocks.CHIPPED_ANVIL);
      this.createAnvil(Blocks.DAMAGED_ANVIL);
      this.createBarrel();
      this.createBell();
      this.createFurnace(Blocks.FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
      this.createFurnace(Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
      this.createFurnace(Blocks.SMOKER, TexturedModel.ORIENTABLE);
      this.createRedstoneWire();
      this.createRespawnAnchor();
      this.copyModel(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
      this.copyModel(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE);
      this.copyModel(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
      this.copyModel(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS);
      this.createInfestedStone();
      this.copyModel(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);
      SpawnEggItem.eggs().forEach((p_239868_1_) -> {
         this.delegateItemModel(p_239868_1_, ModelsResourceUtil.decorateItemModelLocation("template_spawn_egg"));
      });
   }

   class BlockTextureCombiner {
      private final ModelTextures mapping;
      @Nullable
      private ResourceLocation fullBlock;

      public BlockTextureCombiner(ModelTextures p_i232516_2_) {
         this.mapping = p_i232516_2_;
      }

      public BlockModelProvider.BlockTextureCombiner fullBlock(Block p_240058_1_, ModelsUtil p_240058_2_) {
         this.fullBlock = p_240058_2_.create(p_240058_1_, this.mapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createSimpleBlock(p_240058_1_, this.fullBlock));
         return this;
      }

      public BlockModelProvider.BlockTextureCombiner fullBlock(Function<ModelTextures, ResourceLocation> p_240059_1_) {
         this.fullBlock = p_240059_1_.apply(this.mapping);
         return this;
      }

      public BlockModelProvider.BlockTextureCombiner button(Block p_240056_1_) {
         ResourceLocation resourcelocation = StockModelShapes.BUTTON.create(p_240056_1_, this.mapping, BlockModelProvider.this.modelOutput);
         ResourceLocation resourcelocation1 = StockModelShapes.BUTTON_PRESSED.create(p_240056_1_, this.mapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createButton(p_240056_1_, resourcelocation, resourcelocation1));
         ResourceLocation resourcelocation2 = StockModelShapes.BUTTON_INVENTORY.create(p_240056_1_, this.mapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.delegateItemModel(p_240056_1_, resourcelocation2);
         return this;
      }

      public BlockModelProvider.BlockTextureCombiner wall(Block p_240060_1_) {
         ResourceLocation resourcelocation = StockModelShapes.WALL_POST.create(p_240060_1_, this.mapping, BlockModelProvider.this.modelOutput);
         ResourceLocation resourcelocation1 = StockModelShapes.WALL_LOW_SIDE.create(p_240060_1_, this.mapping, BlockModelProvider.this.modelOutput);
         ResourceLocation resourcelocation2 = StockModelShapes.WALL_TALL_SIDE.create(p_240060_1_, this.mapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createWall(p_240060_1_, resourcelocation, resourcelocation1, resourcelocation2));
         ResourceLocation resourcelocation3 = StockModelShapes.WALL_INVENTORY.create(p_240060_1_, this.mapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.delegateItemModel(p_240060_1_, resourcelocation3);
         return this;
      }

      public BlockModelProvider.BlockTextureCombiner fence(Block p_240061_1_) {
         ResourceLocation resourcelocation = StockModelShapes.FENCE_POST.create(p_240061_1_, this.mapping, BlockModelProvider.this.modelOutput);
         ResourceLocation resourcelocation1 = StockModelShapes.FENCE_SIDE.create(p_240061_1_, this.mapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createFence(p_240061_1_, resourcelocation, resourcelocation1));
         ResourceLocation resourcelocation2 = StockModelShapes.FENCE_INVENTORY.create(p_240061_1_, this.mapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.delegateItemModel(p_240061_1_, resourcelocation2);
         return this;
      }

      public BlockModelProvider.BlockTextureCombiner fenceGate(Block p_240062_1_) {
         ResourceLocation resourcelocation = StockModelShapes.FENCE_GATE_OPEN.create(p_240062_1_, this.mapping, BlockModelProvider.this.modelOutput);
         ResourceLocation resourcelocation1 = StockModelShapes.FENCE_GATE_CLOSED.create(p_240062_1_, this.mapping, BlockModelProvider.this.modelOutput);
         ResourceLocation resourcelocation2 = StockModelShapes.FENCE_GATE_WALL_OPEN.create(p_240062_1_, this.mapping, BlockModelProvider.this.modelOutput);
         ResourceLocation resourcelocation3 = StockModelShapes.FENCE_GATE_WALL_CLOSED.create(p_240062_1_, this.mapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createFenceGate(p_240062_1_, resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3));
         return this;
      }

      public BlockModelProvider.BlockTextureCombiner pressurePlate(Block p_240063_1_) {
         ResourceLocation resourcelocation = StockModelShapes.PRESSURE_PLATE_UP.create(p_240063_1_, this.mapping, BlockModelProvider.this.modelOutput);
         ResourceLocation resourcelocation1 = StockModelShapes.PRESSURE_PLATE_DOWN.create(p_240063_1_, this.mapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createPressurePlate(p_240063_1_, resourcelocation, resourcelocation1));
         return this;
      }

      public BlockModelProvider.BlockTextureCombiner sign(Block p_240057_1_, Block p_240057_2_) {
         ResourceLocation resourcelocation = StockModelShapes.PARTICLE_ONLY.create(p_240057_1_, this.mapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createSimpleBlock(p_240057_1_, resourcelocation));
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createSimpleBlock(p_240057_2_, resourcelocation));
         BlockModelProvider.this.createSimpleFlatItemModel(p_240057_1_.asItem());
         BlockModelProvider.this.skipAutoItemBlock(p_240057_2_);
         return this;
      }

      public BlockModelProvider.BlockTextureCombiner slab(Block p_240064_1_) {
         if (this.fullBlock == null) {
            throw new IllegalStateException("Full block not generated yet");
         } else {
            ResourceLocation resourcelocation = StockModelShapes.SLAB_BOTTOM.create(p_240064_1_, this.mapping, BlockModelProvider.this.modelOutput);
            ResourceLocation resourcelocation1 = StockModelShapes.SLAB_TOP.create(p_240064_1_, this.mapping, BlockModelProvider.this.modelOutput);
            BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createSlab(p_240064_1_, resourcelocation, resourcelocation1, this.fullBlock));
            return this;
         }
      }

      public BlockModelProvider.BlockTextureCombiner stairs(Block p_240065_1_) {
         ResourceLocation resourcelocation = StockModelShapes.STAIRS_INNER.create(p_240065_1_, this.mapping, BlockModelProvider.this.modelOutput);
         ResourceLocation resourcelocation1 = StockModelShapes.STAIRS_STRAIGHT.create(p_240065_1_, this.mapping, BlockModelProvider.this.modelOutput);
         ResourceLocation resourcelocation2 = StockModelShapes.STAIRS_OUTER.create(p_240065_1_, this.mapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createStairs(p_240065_1_, resourcelocation, resourcelocation1, resourcelocation2));
         return this;
      }
   }

   class BreakParticleHelper {
      private final ResourceLocation baseModel;

      public BreakParticleHelper(ResourceLocation p_i232515_2_, Block p_i232515_3_) {
         this.baseModel = StockModelShapes.PARTICLE_ONLY.create(p_i232515_2_, ModelTextures.particle(p_i232515_3_), BlockModelProvider.this.modelOutput);
      }

      public BlockModelProvider.BreakParticleHelper create(Block... p_240051_1_) {
         for(Block block : p_240051_1_) {
            BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createSimpleBlock(block, this.baseModel));
         }

         return this;
      }

      public BlockModelProvider.BreakParticleHelper createWithoutBlockItem(Block... p_240052_1_) {
         for(Block block : p_240052_1_) {
            BlockModelProvider.this.skipAutoItemBlock(block);
         }

         return this.create(p_240052_1_);
      }

      public BlockModelProvider.BreakParticleHelper createWithCustomBlockItemModel(ModelsUtil p_240050_1_, Block... p_240050_2_) {
         for(Block block : p_240050_2_) {
            p_240050_1_.create(ModelsResourceUtil.getModelLocation(block.asItem()), ModelTextures.particle(block), BlockModelProvider.this.modelOutput);
         }

         return this.create(p_240050_2_);
      }
   }

   class LogsVariantHelper {
      private final ModelTextures logMapping;

      public LogsVariantHelper(ModelTextures p_i232518_2_) {
         this.logMapping = p_i232518_2_;
      }

      public BlockModelProvider.LogsVariantHelper wood(Block p_240070_1_) {
         ModelTextures modeltextures = this.logMapping.copyAndUpdate(StockTextureAliases.END, this.logMapping.get(StockTextureAliases.SIDE));
         ResourceLocation resourcelocation = StockModelShapes.CUBE_COLUMN.create(p_240070_1_, modeltextures, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createAxisAlignedPillarBlock(p_240070_1_, resourcelocation));
         return this;
      }

      public BlockModelProvider.LogsVariantHelper log(Block p_240071_1_) {
         ResourceLocation resourcelocation = StockModelShapes.CUBE_COLUMN.create(p_240071_1_, this.logMapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createAxisAlignedPillarBlock(p_240071_1_, resourcelocation));
         return this;
      }

      public BlockModelProvider.LogsVariantHelper logWithHorizontal(Block p_240072_1_) {
         ResourceLocation resourcelocation = StockModelShapes.CUBE_COLUMN.create(p_240072_1_, this.logMapping, BlockModelProvider.this.modelOutput);
         ResourceLocation resourcelocation1 = StockModelShapes.CUBE_COLUMN_HORIZONTAL.create(p_240072_1_, this.logMapping, BlockModelProvider.this.modelOutput);
         BlockModelProvider.this.blockStateOutput.accept(BlockModelProvider.createRotatedPillarWithHorizontalVariant(p_240072_1_, resourcelocation, resourcelocation1));
         return this;
      }
   }

   static enum TintMode {
      TINTED,
      NOT_TINTED;

      public ModelsUtil getCross() {
         return this == TINTED ? StockModelShapes.TINTED_CROSS : StockModelShapes.CROSS;
      }

      public ModelsUtil getCrossPot() {
         return this == TINTED ? StockModelShapes.TINTED_FLOWER_POT_CROSS : StockModelShapes.FLOWER_POT_CROSS;
      }
   }
}
