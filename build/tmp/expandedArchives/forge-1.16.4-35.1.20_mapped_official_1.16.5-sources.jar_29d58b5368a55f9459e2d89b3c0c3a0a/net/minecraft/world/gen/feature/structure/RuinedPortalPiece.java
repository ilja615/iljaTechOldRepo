package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.AlwaysTrueRuleTest;
import net.minecraft.world.gen.feature.template.BlackStoneReplacementProcessor;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.BlockMosinessProcessor;
import net.minecraft.world.gen.feature.template.LavaSubmergingProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.RandomBlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleEntry;
import net.minecraft.world.gen.feature.template.RuleStructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RuinedPortalPiece extends TemplateStructurePiece {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ResourceLocation templateLocation;
   private final Rotation rotation;
   private final Mirror mirror;
   private final RuinedPortalPiece.Location verticalPlacement;
   private final RuinedPortalPiece.Serializer properties;

   public RuinedPortalPiece(BlockPos p_i232111_1_, RuinedPortalPiece.Location p_i232111_2_, RuinedPortalPiece.Serializer p_i232111_3_, ResourceLocation p_i232111_4_, Template p_i232111_5_, Rotation p_i232111_6_, Mirror p_i232111_7_, BlockPos p_i232111_8_) {
      super(IStructurePieceType.RUINED_PORTAL, 0);
      this.templatePosition = p_i232111_1_;
      this.templateLocation = p_i232111_4_;
      this.rotation = p_i232111_6_;
      this.mirror = p_i232111_7_;
      this.verticalPlacement = p_i232111_2_;
      this.properties = p_i232111_3_;
      this.loadTemplate(p_i232111_5_, p_i232111_8_);
   }

   public RuinedPortalPiece(TemplateManager p_i232110_1_, CompoundNBT p_i232110_2_) {
      super(IStructurePieceType.RUINED_PORTAL, p_i232110_2_);
      this.templateLocation = new ResourceLocation(p_i232110_2_.getString("Template"));
      this.rotation = Rotation.valueOf(p_i232110_2_.getString("Rotation"));
      this.mirror = Mirror.valueOf(p_i232110_2_.getString("Mirror"));
      this.verticalPlacement = RuinedPortalPiece.Location.byName(p_i232110_2_.getString("VerticalPlacement"));
      this.properties = RuinedPortalPiece.Serializer.CODEC.parse(new Dynamic<>(NBTDynamicOps.INSTANCE, p_i232110_2_.get("Properties"))).getOrThrow(true, LOGGER::error);
      Template template = p_i232110_1_.getOrCreate(this.templateLocation);
      this.loadTemplate(template, new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2));
   }

   protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
      super.addAdditionalSaveData(p_143011_1_);
      p_143011_1_.putString("Template", this.templateLocation.toString());
      p_143011_1_.putString("Rotation", this.rotation.name());
      p_143011_1_.putString("Mirror", this.mirror.name());
      p_143011_1_.putString("VerticalPlacement", this.verticalPlacement.getName());
      RuinedPortalPiece.Serializer.CODEC.encodeStart(NBTDynamicOps.INSTANCE, this.properties).resultOrPartial(LOGGER::error).ifPresent((p_237018_1_) -> {
         p_143011_1_.put("Properties", p_237018_1_);
      });
   }

   private void loadTemplate(Template p_237014_1_, BlockPos p_237014_2_) {
      BlockIgnoreStructureProcessor blockignorestructureprocessor = this.properties.airPocket ? BlockIgnoreStructureProcessor.STRUCTURE_BLOCK : BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR;
      List<RuleEntry> list = Lists.newArrayList();
      list.add(getBlockReplaceRule(Blocks.GOLD_BLOCK, 0.3F, Blocks.AIR));
      list.add(this.getLavaProcessorRule());
      if (!this.properties.cold) {
         list.add(getBlockReplaceRule(Blocks.NETHERRACK, 0.07F, Blocks.MAGMA_BLOCK));
      }

      PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(this.mirror).setRotationPivot(p_237014_2_).addProcessor(blockignorestructureprocessor).addProcessor(new RuleStructureProcessor(list)).addProcessor(new BlockMosinessProcessor(this.properties.mossiness)).addProcessor(new LavaSubmergingProcessor());
      if (this.properties.replaceWithBlackstone) {
         placementsettings.addProcessor(BlackStoneReplacementProcessor.INSTANCE);
      }

      this.setup(p_237014_1_, this.templatePosition, placementsettings);
   }

   private RuleEntry getLavaProcessorRule() {
      if (this.verticalPlacement == RuinedPortalPiece.Location.ON_OCEAN_FLOOR) {
         return getBlockReplaceRule(Blocks.LAVA, Blocks.MAGMA_BLOCK);
      } else {
         return this.properties.cold ? getBlockReplaceRule(Blocks.LAVA, Blocks.NETHERRACK) : getBlockReplaceRule(Blocks.LAVA, 0.2F, Blocks.MAGMA_BLOCK);
      }
   }

   public boolean postProcess(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
      if (!p_230383_5_.isInside(this.templatePosition)) {
         return true;
      } else {
         p_230383_5_.expand(this.template.getBoundingBox(this.placeSettings, this.templatePosition));
         boolean flag = super.postProcess(p_230383_1_, p_230383_2_, p_230383_3_, p_230383_4_, p_230383_5_, p_230383_6_, p_230383_7_);
         this.spreadNetherrack(p_230383_4_, p_230383_1_);
         this.addNetherrackDripColumnsBelowPortal(p_230383_4_, p_230383_1_);
         if (this.properties.vines || this.properties.overgrown) {
            BlockPos.betweenClosedStream(this.getBoundingBox()).forEach((p_237017_3_) -> {
               if (this.properties.vines) {
                  this.maybeAddVines(p_230383_4_, p_230383_1_, p_237017_3_);
               }

               if (this.properties.overgrown) {
                  this.maybeAddLeavesAbove(p_230383_4_, p_230383_1_, p_237017_3_);
               }

            });
         }

         return flag;
      }
   }

   protected void handleDataMarker(String p_186175_1_, BlockPos p_186175_2_, IServerWorld p_186175_3_, Random p_186175_4_, MutableBoundingBox p_186175_5_) {
   }

   private void maybeAddVines(Random p_237016_1_, IWorld p_237016_2_, BlockPos p_237016_3_) {
      BlockState blockstate = p_237016_2_.getBlockState(p_237016_3_);
      if (!blockstate.isAir() && !blockstate.is(Blocks.VINE)) {
         Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(p_237016_1_);
         BlockPos blockpos = p_237016_3_.relative(direction);
         BlockState blockstate1 = p_237016_2_.getBlockState(blockpos);
         if (blockstate1.isAir()) {
            if (Block.isFaceFull(blockstate.getCollisionShape(p_237016_2_, p_237016_3_), direction)) {
               BooleanProperty booleanproperty = VineBlock.getPropertyForFace(direction.getOpposite());
               p_237016_2_.setBlock(blockpos, Blocks.VINE.defaultBlockState().setValue(booleanproperty, Boolean.valueOf(true)), 3);
            }
         }
      }
   }

   private void maybeAddLeavesAbove(Random p_237020_1_, IWorld p_237020_2_, BlockPos p_237020_3_) {
      if (p_237020_1_.nextFloat() < 0.5F && p_237020_2_.getBlockState(p_237020_3_).is(Blocks.NETHERRACK) && p_237020_2_.getBlockState(p_237020_3_.above()).isAir()) {
         p_237020_2_.setBlock(p_237020_3_.above(), Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, Boolean.valueOf(true)), 3);
      }

   }

   private void addNetherrackDripColumnsBelowPortal(Random p_237015_1_, IWorld p_237015_2_) {
      for(int i = this.boundingBox.x0 + 1; i < this.boundingBox.x1; ++i) {
         for(int j = this.boundingBox.z0 + 1; j < this.boundingBox.z1; ++j) {
            BlockPos blockpos = new BlockPos(i, this.boundingBox.y0, j);
            if (p_237015_2_.getBlockState(blockpos).is(Blocks.NETHERRACK)) {
               this.addNetherrackDripColumn(p_237015_1_, p_237015_2_, blockpos.below());
            }
         }
      }

   }

   private void addNetherrackDripColumn(Random p_237022_1_, IWorld p_237022_2_, BlockPos p_237022_3_) {
      BlockPos.Mutable blockpos$mutable = p_237022_3_.mutable();
      this.placeNetherrackOrMagma(p_237022_1_, p_237022_2_, blockpos$mutable);
      int i = 8;

      while(i > 0 && p_237022_1_.nextFloat() < 0.5F) {
         blockpos$mutable.move(Direction.DOWN);
         --i;
         this.placeNetherrackOrMagma(p_237022_1_, p_237022_2_, blockpos$mutable);
      }

   }

   private void spreadNetherrack(Random p_237019_1_, IWorld p_237019_2_) {
      boolean flag = this.verticalPlacement == RuinedPortalPiece.Location.ON_LAND_SURFACE || this.verticalPlacement == RuinedPortalPiece.Location.ON_OCEAN_FLOOR;
      Vector3i vector3i = this.boundingBox.getCenter();
      int i = vector3i.getX();
      int j = vector3i.getZ();
      float[] afloat = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.9F, 0.9F, 0.8F, 0.7F, 0.6F, 0.4F, 0.2F};
      int k = afloat.length;
      int l = (this.boundingBox.getXSpan() + this.boundingBox.getZSpan()) / 2;
      int i1 = p_237019_1_.nextInt(Math.max(1, 8 - l / 2));
      int j1 = 3;
      BlockPos.Mutable blockpos$mutable = BlockPos.ZERO.mutable();

      for(int k1 = i - k; k1 <= i + k; ++k1) {
         for(int l1 = j - k; l1 <= j + k; ++l1) {
            int i2 = Math.abs(k1 - i) + Math.abs(l1 - j);
            int j2 = Math.max(0, i2 + i1);
            if (j2 < k) {
               float f = afloat[j2];
               if (p_237019_1_.nextDouble() < (double)f) {
                  int k2 = getSurfaceY(p_237019_2_, k1, l1, this.verticalPlacement);
                  int l2 = flag ? k2 : Math.min(this.boundingBox.y0, k2);
                  blockpos$mutable.set(k1, l2, l1);
                  if (Math.abs(l2 - this.boundingBox.y0) <= 3 && this.canBlockBeReplacedByNetherrackOrMagma(p_237019_2_, blockpos$mutable)) {
                     this.placeNetherrackOrMagma(p_237019_1_, p_237019_2_, blockpos$mutable);
                     if (this.properties.overgrown) {
                        this.maybeAddLeavesAbove(p_237019_1_, p_237019_2_, blockpos$mutable);
                     }

                     this.addNetherrackDripColumn(p_237019_1_, p_237019_2_, blockpos$mutable.below());
                  }
               }
            }
         }
      }

   }

   private boolean canBlockBeReplacedByNetherrackOrMagma(IWorld p_237010_1_, BlockPos p_237010_2_) {
      BlockState blockstate = p_237010_1_.getBlockState(p_237010_2_);
      return !blockstate.is(Blocks.AIR) && !blockstate.is(Blocks.OBSIDIAN) && !blockstate.is(Blocks.CHEST) && (this.verticalPlacement == RuinedPortalPiece.Location.IN_NETHER || !blockstate.is(Blocks.LAVA));
   }

   private void placeNetherrackOrMagma(Random p_237023_1_, IWorld p_237023_2_, BlockPos p_237023_3_) {
      if (!this.properties.cold && p_237023_1_.nextFloat() < 0.07F) {
         p_237023_2_.setBlock(p_237023_3_, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
      } else {
         p_237023_2_.setBlock(p_237023_3_, Blocks.NETHERRACK.defaultBlockState(), 3);
      }

   }

   private static int getSurfaceY(IWorld p_237009_0_, int p_237009_1_, int p_237009_2_, RuinedPortalPiece.Location p_237009_3_) {
      return p_237009_0_.getHeight(getHeightMapType(p_237009_3_), p_237009_1_, p_237009_2_) - 1;
   }

   public static Heightmap.Type getHeightMapType(RuinedPortalPiece.Location p_237013_0_) {
      return p_237013_0_ == RuinedPortalPiece.Location.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
   }

   private static RuleEntry getBlockReplaceRule(Block p_237011_0_, float p_237011_1_, Block p_237011_2_) {
      return new RuleEntry(new RandomBlockMatchRuleTest(p_237011_0_, p_237011_1_), AlwaysTrueRuleTest.INSTANCE, p_237011_2_.defaultBlockState());
   }

   private static RuleEntry getBlockReplaceRule(Block p_237012_0_, Block p_237012_1_) {
      return new RuleEntry(new BlockMatchRuleTest(p_237012_0_), AlwaysTrueRuleTest.INSTANCE, p_237012_1_.defaultBlockState());
   }

   public static enum Location {
      ON_LAND_SURFACE("on_land_surface"),
      PARTLY_BURIED("partly_buried"),
      ON_OCEAN_FLOOR("on_ocean_floor"),
      IN_MOUNTAIN("in_mountain"),
      UNDERGROUND("underground"),
      IN_NETHER("in_nether");

      private static final Map<String, RuinedPortalPiece.Location> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(RuinedPortalPiece.Location::getName, (p_237041_0_) -> {
         return p_237041_0_;
      }));
      private final String name;

      private Location(String p_i232113_3_) {
         this.name = p_i232113_3_;
      }

      public String getName() {
         return this.name;
      }

      public static RuinedPortalPiece.Location byName(String p_237042_0_) {
         return BY_NAME.get(p_237042_0_);
      }
   }

   public static class Serializer {
      public static final Codec<RuinedPortalPiece.Serializer> CODEC = RecordCodecBuilder.create((p_237031_0_) -> {
         return p_237031_0_.group(Codec.BOOL.fieldOf("cold").forGetter((p_237037_0_) -> {
            return p_237037_0_.cold;
         }), Codec.FLOAT.fieldOf("mossiness").forGetter((p_237036_0_) -> {
            return p_237036_0_.mossiness;
         }), Codec.BOOL.fieldOf("air_pocket").forGetter((p_237035_0_) -> {
            return p_237035_0_.airPocket;
         }), Codec.BOOL.fieldOf("overgrown").forGetter((p_237034_0_) -> {
            return p_237034_0_.overgrown;
         }), Codec.BOOL.fieldOf("vines").forGetter((p_237033_0_) -> {
            return p_237033_0_.vines;
         }), Codec.BOOL.fieldOf("replace_with_blackstone").forGetter((p_237032_0_) -> {
            return p_237032_0_.replaceWithBlackstone;
         })).apply(p_237031_0_, RuinedPortalPiece.Serializer::new);
      });
      public boolean cold;
      public float mossiness = 0.2F;
      public boolean airPocket;
      public boolean overgrown;
      public boolean vines;
      public boolean replaceWithBlackstone;

      public Serializer() {
      }

      public <T> Serializer(boolean p_i232112_1_, float p_i232112_2_, boolean p_i232112_3_, boolean p_i232112_4_, boolean p_i232112_5_, boolean p_i232112_6_) {
         this.cold = p_i232112_1_;
         this.mossiness = p_i232112_2_;
         this.airPocket = p_i232112_3_;
         this.overgrown = p_i232112_4_;
         this.vines = p_i232112_5_;
         this.replaceWithBlackstone = p_i232112_6_;
      }
   }
}
