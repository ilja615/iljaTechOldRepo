package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FossilsFeature extends Feature<NoFeatureConfig> {
   private static final ResourceLocation SPINE_1 = new ResourceLocation("fossil/spine_1");
   private static final ResourceLocation SPINE_2 = new ResourceLocation("fossil/spine_2");
   private static final ResourceLocation SPINE_3 = new ResourceLocation("fossil/spine_3");
   private static final ResourceLocation SPINE_4 = new ResourceLocation("fossil/spine_4");
   private static final ResourceLocation SPINE_1_COAL = new ResourceLocation("fossil/spine_1_coal");
   private static final ResourceLocation SPINE_2_COAL = new ResourceLocation("fossil/spine_2_coal");
   private static final ResourceLocation SPINE_3_COAL = new ResourceLocation("fossil/spine_3_coal");
   private static final ResourceLocation SPINE_4_COAL = new ResourceLocation("fossil/spine_4_coal");
   private static final ResourceLocation SKULL_1 = new ResourceLocation("fossil/skull_1");
   private static final ResourceLocation SKULL_2 = new ResourceLocation("fossil/skull_2");
   private static final ResourceLocation SKULL_3 = new ResourceLocation("fossil/skull_3");
   private static final ResourceLocation SKULL_4 = new ResourceLocation("fossil/skull_4");
   private static final ResourceLocation SKULL_1_COAL = new ResourceLocation("fossil/skull_1_coal");
   private static final ResourceLocation SKULL_2_COAL = new ResourceLocation("fossil/skull_2_coal");
   private static final ResourceLocation SKULL_3_COAL = new ResourceLocation("fossil/skull_3_coal");
   private static final ResourceLocation SKULL_4_COAL = new ResourceLocation("fossil/skull_4_coal");
   private static final ResourceLocation[] fossils = new ResourceLocation[]{SPINE_1, SPINE_2, SPINE_3, SPINE_4, SKULL_1, SKULL_2, SKULL_3, SKULL_4};
   private static final ResourceLocation[] fossilsCoal = new ResourceLocation[]{SPINE_1_COAL, SPINE_2_COAL, SPINE_3_COAL, SPINE_4_COAL, SKULL_1_COAL, SKULL_2_COAL, SKULL_3_COAL, SKULL_4_COAL};

   public FossilsFeature(Codec<NoFeatureConfig> p_i231955_1_) {
      super(p_i231955_1_);
   }

   public boolean place(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_) {
      Rotation rotation = Rotation.getRandom(p_241855_3_);
      int i = p_241855_3_.nextInt(fossils.length);
      TemplateManager templatemanager = p_241855_1_.getLevel().getServer().getStructureManager();
      Template template = templatemanager.getOrCreate(fossils[i]);
      Template template1 = templatemanager.getOrCreate(fossilsCoal[i]);
      ChunkPos chunkpos = new ChunkPos(p_241855_4_);
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(chunkpos.getMinBlockX(), 0, chunkpos.getMinBlockZ(), chunkpos.getMaxBlockX(), 256, chunkpos.getMaxBlockZ());
      PlacementSettings placementsettings = (new PlacementSettings()).setRotation(rotation).setBoundingBox(mutableboundingbox).setRandom(p_241855_3_).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR);
      BlockPos blockpos = template.getSize(rotation);
      int j = p_241855_3_.nextInt(16 - blockpos.getX());
      int k = p_241855_3_.nextInt(16 - blockpos.getZ());
      int l = 256;

      for(int i1 = 0; i1 < blockpos.getX(); ++i1) {
         for(int j1 = 0; j1 < blockpos.getZ(); ++j1) {
            l = Math.min(l, p_241855_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, p_241855_4_.getX() + i1 + j, p_241855_4_.getZ() + j1 + k));
         }
      }

      int k1 = Math.max(l - 15 - p_241855_3_.nextInt(10), 10);
      BlockPos blockpos1 = template.getZeroPositionWithTransform(p_241855_4_.offset(j, k1, k), Mirror.NONE, rotation);
      IntegrityProcessor integrityprocessor = new IntegrityProcessor(0.9F);
      placementsettings.clearProcessors().addProcessor(integrityprocessor);
      template.placeInWorld(p_241855_1_, blockpos1, blockpos1, placementsettings, p_241855_3_, 4);
      placementsettings.popProcessor(integrityprocessor);
      IntegrityProcessor integrityprocessor1 = new IntegrityProcessor(0.1F);
      placementsettings.clearProcessors().addProcessor(integrityprocessor1);
      template1.placeInWorld(p_241855_1_, blockpos1, blockpos1, placementsettings, p_241855_3_, 4);
      return true;
   }
}
