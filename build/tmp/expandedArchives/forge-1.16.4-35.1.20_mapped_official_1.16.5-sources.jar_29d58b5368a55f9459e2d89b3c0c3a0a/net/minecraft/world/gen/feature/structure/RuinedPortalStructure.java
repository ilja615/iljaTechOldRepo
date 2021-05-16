package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class RuinedPortalStructure extends Structure<RuinedPortalFeature> {
   private static final String[] STRUCTURE_LOCATION_PORTALS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
   private static final String[] STRUCTURE_LOCATION_GIANT_PORTALS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};

   public RuinedPortalStructure(Codec<RuinedPortalFeature> p_i231984_1_) {
      super(p_i231984_1_);
   }

   public Structure.IStartFactory<RuinedPortalFeature> getStartFactory() {
      return RuinedPortalStructure.Start::new;
   }

   private static boolean isCold(BlockPos p_236337_0_, Biome p_236337_1_) {
      return p_236337_1_.getTemperature(p_236337_0_) < 0.15F;
   }

   private static int findSuitableY(Random p_236339_0_, ChunkGenerator p_236339_1_, RuinedPortalPiece.Location p_236339_2_, boolean p_236339_3_, int p_236339_4_, int p_236339_5_, MutableBoundingBox p_236339_6_) {
      int i;
      if (p_236339_2_ == RuinedPortalPiece.Location.IN_NETHER) {
         if (p_236339_3_) {
            i = randomIntInclusive(p_236339_0_, 32, 100);
         } else if (p_236339_0_.nextFloat() < 0.5F) {
            i = randomIntInclusive(p_236339_0_, 27, 29);
         } else {
            i = randomIntInclusive(p_236339_0_, 29, 100);
         }
      } else if (p_236339_2_ == RuinedPortalPiece.Location.IN_MOUNTAIN) {
         int j = p_236339_4_ - p_236339_5_;
         i = getRandomWithinInterval(p_236339_0_, 70, j);
      } else if (p_236339_2_ == RuinedPortalPiece.Location.UNDERGROUND) {
         int i1 = p_236339_4_ - p_236339_5_;
         i = getRandomWithinInterval(p_236339_0_, 15, i1);
      } else if (p_236339_2_ == RuinedPortalPiece.Location.PARTLY_BURIED) {
         i = p_236339_4_ - p_236339_5_ + randomIntInclusive(p_236339_0_, 2, 8);
      } else {
         i = p_236339_4_;
      }

      List<BlockPos> list1 = ImmutableList.of(new BlockPos(p_236339_6_.x0, 0, p_236339_6_.z0), new BlockPos(p_236339_6_.x1, 0, p_236339_6_.z0), new BlockPos(p_236339_6_.x0, 0, p_236339_6_.z1), new BlockPos(p_236339_6_.x1, 0, p_236339_6_.z1));
      List<IBlockReader> list = list1.stream().map((p_236333_1_) -> {
         return p_236339_1_.getBaseColumn(p_236333_1_.getX(), p_236333_1_.getZ());
      }).collect(Collectors.toList());
      Heightmap.Type heightmap$type = p_236339_2_ == RuinedPortalPiece.Location.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

      int k;
      for(k = i; k > 15; --k) {
         int l = 0;
         blockpos$mutable.set(0, k, 0);

         for(IBlockReader iblockreader : list) {
            BlockState blockstate = iblockreader.getBlockState(blockpos$mutable);
            if (blockstate != null && heightmap$type.isOpaque().test(blockstate)) {
               ++l;
               if (l == 3) {
                  return k;
               }
            }
         }
      }

      return k;
   }

   private static int randomIntInclusive(Random p_236335_0_, int p_236335_1_, int p_236335_2_) {
      return p_236335_0_.nextInt(p_236335_2_ - p_236335_1_ + 1) + p_236335_1_;
   }

   private static int getRandomWithinInterval(Random p_236338_0_, int p_236338_1_, int p_236338_2_) {
      return p_236338_1_ < p_236338_2_ ? randomIntInclusive(p_236338_0_, p_236338_1_, p_236338_2_) : p_236338_2_;
   }

   public static enum Location implements IStringSerializable {
      STANDARD("standard"),
      DESERT("desert"),
      JUNGLE("jungle"),
      SWAMP("swamp"),
      MOUNTAIN("mountain"),
      OCEAN("ocean"),
      NETHER("nether");

      public static final Codec<RuinedPortalStructure.Location> CODEC = IStringSerializable.fromEnum(RuinedPortalStructure.Location::values, RuinedPortalStructure.Location::byName);
      private static final Map<String, RuinedPortalStructure.Location> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(RuinedPortalStructure.Location::getName, (p_236345_0_) -> {
         return p_236345_0_;
      }));
      private final String name;

      private Location(String p_i231986_3_) {
         this.name = p_i231986_3_;
      }

      public String getName() {
         return this.name;
      }

      public static RuinedPortalStructure.Location byName(String p_236346_0_) {
         return BY_NAME.get(p_236346_0_);
      }

      public String getSerializedName() {
         return this.name;
      }
   }

   public static class Start extends StructureStart<RuinedPortalFeature> {
      protected Start(Structure<RuinedPortalFeature> p_i231985_1_, int p_i231985_2_, int p_i231985_3_, MutableBoundingBox p_i231985_4_, int p_i231985_5_, long p_i231985_6_) {
         super(p_i231985_1_, p_i231985_2_, p_i231985_3_, p_i231985_4_, p_i231985_5_, p_i231985_6_);
      }

      public void generatePieces(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, RuinedPortalFeature p_230364_7_) {
         RuinedPortalPiece.Serializer ruinedportalpiece$serializer = new RuinedPortalPiece.Serializer();
         RuinedPortalPiece.Location ruinedportalpiece$location;
         if (p_230364_7_.portalType == RuinedPortalStructure.Location.DESERT) {
            ruinedportalpiece$location = RuinedPortalPiece.Location.PARTLY_BURIED;
            ruinedportalpiece$serializer.airPocket = false;
            ruinedportalpiece$serializer.mossiness = 0.0F;
         } else if (p_230364_7_.portalType == RuinedPortalStructure.Location.JUNGLE) {
            ruinedportalpiece$location = RuinedPortalPiece.Location.ON_LAND_SURFACE;
            ruinedportalpiece$serializer.airPocket = this.random.nextFloat() < 0.5F;
            ruinedportalpiece$serializer.mossiness = 0.8F;
            ruinedportalpiece$serializer.overgrown = true;
            ruinedportalpiece$serializer.vines = true;
         } else if (p_230364_7_.portalType == RuinedPortalStructure.Location.SWAMP) {
            ruinedportalpiece$location = RuinedPortalPiece.Location.ON_OCEAN_FLOOR;
            ruinedportalpiece$serializer.airPocket = false;
            ruinedportalpiece$serializer.mossiness = 0.5F;
            ruinedportalpiece$serializer.vines = true;
         } else if (p_230364_7_.portalType == RuinedPortalStructure.Location.MOUNTAIN) {
            boolean flag = this.random.nextFloat() < 0.5F;
            ruinedportalpiece$location = flag ? RuinedPortalPiece.Location.IN_MOUNTAIN : RuinedPortalPiece.Location.ON_LAND_SURFACE;
            ruinedportalpiece$serializer.airPocket = flag || this.random.nextFloat() < 0.5F;
         } else if (p_230364_7_.portalType == RuinedPortalStructure.Location.OCEAN) {
            ruinedportalpiece$location = RuinedPortalPiece.Location.ON_OCEAN_FLOOR;
            ruinedportalpiece$serializer.airPocket = false;
            ruinedportalpiece$serializer.mossiness = 0.8F;
         } else if (p_230364_7_.portalType == RuinedPortalStructure.Location.NETHER) {
            ruinedportalpiece$location = RuinedPortalPiece.Location.IN_NETHER;
            ruinedportalpiece$serializer.airPocket = this.random.nextFloat() < 0.5F;
            ruinedportalpiece$serializer.mossiness = 0.0F;
            ruinedportalpiece$serializer.replaceWithBlackstone = true;
         } else {
            boolean flag1 = this.random.nextFloat() < 0.5F;
            ruinedportalpiece$location = flag1 ? RuinedPortalPiece.Location.UNDERGROUND : RuinedPortalPiece.Location.ON_LAND_SURFACE;
            ruinedportalpiece$serializer.airPocket = flag1 || this.random.nextFloat() < 0.5F;
         }

         ResourceLocation resourcelocation;
         if (this.random.nextFloat() < 0.05F) {
            resourcelocation = new ResourceLocation(RuinedPortalStructure.STRUCTURE_LOCATION_GIANT_PORTALS[this.random.nextInt(RuinedPortalStructure.STRUCTURE_LOCATION_GIANT_PORTALS.length)]);
         } else {
            resourcelocation = new ResourceLocation(RuinedPortalStructure.STRUCTURE_LOCATION_PORTALS[this.random.nextInt(RuinedPortalStructure.STRUCTURE_LOCATION_PORTALS.length)]);
         }

         Template template = p_230364_3_.getOrCreate(resourcelocation);
         Rotation rotation = Util.getRandom(Rotation.values(), this.random);
         Mirror mirror = this.random.nextFloat() < 0.5F ? Mirror.NONE : Mirror.FRONT_BACK;
         BlockPos blockpos = new BlockPos(template.getSize().getX() / 2, 0, template.getSize().getZ() / 2);
         BlockPos blockpos1 = (new ChunkPos(p_230364_4_, p_230364_5_)).getWorldPosition();
         MutableBoundingBox mutableboundingbox = template.getBoundingBox(blockpos1, rotation, blockpos, mirror);
         Vector3i vector3i = mutableboundingbox.getCenter();
         int i = vector3i.getX();
         int j = vector3i.getZ();
         int k = p_230364_2_.getBaseHeight(i, j, RuinedPortalPiece.getHeightMapType(ruinedportalpiece$location)) - 1;
         int l = RuinedPortalStructure.findSuitableY(this.random, p_230364_2_, ruinedportalpiece$location, ruinedportalpiece$serializer.airPocket, k, mutableboundingbox.getYSpan(), mutableboundingbox);
         BlockPos blockpos2 = new BlockPos(blockpos1.getX(), l, blockpos1.getZ());
         if (p_230364_7_.portalType == RuinedPortalStructure.Location.MOUNTAIN || p_230364_7_.portalType == RuinedPortalStructure.Location.OCEAN || p_230364_7_.portalType == RuinedPortalStructure.Location.STANDARD) {
            ruinedportalpiece$serializer.cold = RuinedPortalStructure.isCold(blockpos2, p_230364_6_);
         }

         this.pieces.add(new RuinedPortalPiece(blockpos2, ruinedportalpiece$location, ruinedportalpiece$serializer, resourcelocation, template, rotation, mirror, blockpos));
         this.calculateBoundingBox();
      }
   }
}
