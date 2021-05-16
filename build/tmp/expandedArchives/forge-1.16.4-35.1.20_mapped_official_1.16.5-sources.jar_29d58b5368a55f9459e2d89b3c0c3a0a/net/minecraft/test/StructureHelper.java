package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;

public class StructureHelper {
   public static String testStructuresDir = "gameteststructures";

   public static Rotation getRotationForRotationSteps(int p_240562_0_) {
      switch(p_240562_0_) {
      case 0:
         return Rotation.NONE;
      case 1:
         return Rotation.CLOCKWISE_90;
      case 2:
         return Rotation.CLOCKWISE_180;
      case 3:
         return Rotation.COUNTERCLOCKWISE_90;
      default:
         throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + p_240562_0_);
      }
   }

   public static AxisAlignedBB getStructureBounds(StructureBlockTileEntity p_229594_0_) {
      BlockPos blockpos = p_229594_0_.getBlockPos();
      BlockPos blockpos1 = blockpos.offset(p_229594_0_.getStructureSize().offset(-1, -1, -1));
      BlockPos blockpos2 = Template.transform(blockpos1, Mirror.NONE, p_229594_0_.getRotation(), blockpos);
      return new AxisAlignedBB(blockpos, blockpos2);
   }

   public static MutableBoundingBox getStructureBoundingBox(StructureBlockTileEntity p_240568_0_) {
      BlockPos blockpos = p_240568_0_.getBlockPos();
      BlockPos blockpos1 = blockpos.offset(p_240568_0_.getStructureSize().offset(-1, -1, -1));
      BlockPos blockpos2 = Template.transform(blockpos1, Mirror.NONE, p_240568_0_.getRotation(), blockpos);
      return new MutableBoundingBox(blockpos, blockpos2);
   }

   public static void addCommandBlockAndButtonToStartTest(BlockPos p_240564_0_, BlockPos p_240564_1_, Rotation p_240564_2_, ServerWorld p_240564_3_) {
      BlockPos blockpos = Template.transform(p_240564_0_.offset(p_240564_1_), Mirror.NONE, p_240564_2_, p_240564_0_);
      p_240564_3_.setBlockAndUpdate(blockpos, Blocks.COMMAND_BLOCK.defaultBlockState());
      CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)p_240564_3_.getBlockEntity(blockpos);
      commandblocktileentity.getCommandBlock().setCommand("test runthis");
      BlockPos blockpos1 = Template.transform(blockpos.offset(0, 0, -1), Mirror.NONE, p_240564_2_, blockpos);
      p_240564_3_.setBlockAndUpdate(blockpos1, Blocks.STONE_BUTTON.defaultBlockState().rotate(p_240564_2_));
   }

   public static void createNewEmptyStructureBlock(String p_229603_0_, BlockPos p_229603_1_, BlockPos p_229603_2_, Rotation p_229603_3_, ServerWorld p_229603_4_) {
      MutableBoundingBox mutableboundingbox = getStructureBoundingBox(p_229603_1_, p_229603_2_, p_229603_3_);
      clearSpaceForStructure(mutableboundingbox, p_229603_1_.getY(), p_229603_4_);
      p_229603_4_.setBlockAndUpdate(p_229603_1_, Blocks.STRUCTURE_BLOCK.defaultBlockState());
      StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)p_229603_4_.getBlockEntity(p_229603_1_);
      structureblocktileentity.setIgnoreEntities(false);
      structureblocktileentity.setStructureName(new ResourceLocation(p_229603_0_));
      structureblocktileentity.setStructureSize(p_229603_2_);
      structureblocktileentity.setMode(StructureMode.SAVE);
      structureblocktileentity.setShowBoundingBox(true);
   }

   public static StructureBlockTileEntity spawnStructure(String p_240565_0_, BlockPos p_240565_1_, Rotation p_240565_2_, int p_240565_3_, ServerWorld p_240565_4_, boolean p_240565_5_) {
      BlockPos blockpos = getStructureTemplate(p_240565_0_, p_240565_4_).getSize();
      MutableBoundingBox mutableboundingbox = getStructureBoundingBox(p_240565_1_, blockpos, p_240565_2_);
      BlockPos blockpos1;
      if (p_240565_2_ == Rotation.NONE) {
         blockpos1 = p_240565_1_;
      } else if (p_240565_2_ == Rotation.CLOCKWISE_90) {
         blockpos1 = p_240565_1_.offset(blockpos.getZ() - 1, 0, 0);
      } else if (p_240565_2_ == Rotation.CLOCKWISE_180) {
         blockpos1 = p_240565_1_.offset(blockpos.getX() - 1, 0, blockpos.getZ() - 1);
      } else {
         if (p_240565_2_ != Rotation.COUNTERCLOCKWISE_90) {
            throw new IllegalArgumentException("Invalid rotation: " + p_240565_2_);
         }

         blockpos1 = p_240565_1_.offset(0, 0, blockpos.getX() - 1);
      }

      forceLoadChunks(p_240565_1_, p_240565_4_);
      clearSpaceForStructure(mutableboundingbox, p_240565_1_.getY(), p_240565_4_);
      StructureBlockTileEntity structureblocktileentity = createStructureBlock(p_240565_0_, blockpos1, p_240565_2_, p_240565_4_, p_240565_5_);
      p_240565_4_.getBlockTicks().fetchTicksInArea(mutableboundingbox, true, false);
      p_240565_4_.clearBlockEvents(mutableboundingbox);
      return structureblocktileentity;
   }

   private static void forceLoadChunks(BlockPos p_229608_0_, ServerWorld p_229608_1_) {
      ChunkPos chunkpos = new ChunkPos(p_229608_0_);

      for(int i = -1; i < 4; ++i) {
         for(int j = -1; j < 4; ++j) {
            int k = chunkpos.x + i;
            int l = chunkpos.z + j;
            p_229608_1_.setChunkForced(k, l, true);
         }
      }

   }

   public static void clearSpaceForStructure(MutableBoundingBox p_229595_0_, int p_229595_1_, ServerWorld p_229595_2_) {
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(p_229595_0_.x0 - 2, p_229595_0_.y0 - 3, p_229595_0_.z0 - 3, p_229595_0_.x1 + 3, p_229595_0_.y1 + 20, p_229595_0_.z1 + 3);
      BlockPos.betweenClosedStream(mutableboundingbox).forEach((p_229592_2_) -> {
         clearBlock(p_229595_1_, p_229592_2_, p_229595_2_);
      });
      p_229595_2_.getBlockTicks().fetchTicksInArea(mutableboundingbox, true, false);
      p_229595_2_.clearBlockEvents(mutableboundingbox);
      AxisAlignedBB axisalignedbb = new AxisAlignedBB((double)mutableboundingbox.x0, (double)mutableboundingbox.y0, (double)mutableboundingbox.z0, (double)mutableboundingbox.x1, (double)mutableboundingbox.y1, (double)mutableboundingbox.z1);
      List<Entity> list = p_229595_2_.getEntitiesOfClass(Entity.class, axisalignedbb, (p_229593_0_) -> {
         return !(p_229593_0_ instanceof PlayerEntity);
      });
      list.forEach(Entity::remove);
   }

   public static MutableBoundingBox getStructureBoundingBox(BlockPos p_229598_0_, BlockPos p_229598_1_, Rotation p_229598_2_) {
      BlockPos blockpos = p_229598_0_.offset(p_229598_1_).offset(-1, -1, -1);
      BlockPos blockpos1 = Template.transform(blockpos, Mirror.NONE, p_229598_2_, p_229598_0_);
      MutableBoundingBox mutableboundingbox = MutableBoundingBox.createProper(p_229598_0_.getX(), p_229598_0_.getY(), p_229598_0_.getZ(), blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
      int i = Math.min(mutableboundingbox.x0, mutableboundingbox.x1);
      int j = Math.min(mutableboundingbox.z0, mutableboundingbox.z1);
      BlockPos blockpos2 = new BlockPos(p_229598_0_.getX() - i, 0, p_229598_0_.getZ() - j);
      mutableboundingbox.move(blockpos2);
      return mutableboundingbox;
   }

   public static Optional<BlockPos> findStructureBlockContainingPos(BlockPos p_229596_0_, int p_229596_1_, ServerWorld p_229596_2_) {
      return findStructureBlocks(p_229596_0_, p_229596_1_, p_229596_2_).stream().filter((p_229601_2_) -> {
         return doesStructureContain(p_229601_2_, p_229596_0_, p_229596_2_);
      }).findFirst();
   }

   @Nullable
   public static BlockPos findNearestStructureBlock(BlockPos p_229607_0_, int p_229607_1_, ServerWorld p_229607_2_) {
      Comparator<BlockPos> comparator = Comparator.comparingInt((p_229597_1_) -> {
         return p_229597_1_.distManhattan(p_229607_0_);
      });
      Collection<BlockPos> collection = findStructureBlocks(p_229607_0_, p_229607_1_, p_229607_2_);
      Optional<BlockPos> optional = collection.stream().min(comparator);
      return optional.orElse((BlockPos)null);
   }

   public static Collection<BlockPos> findStructureBlocks(BlockPos p_229609_0_, int p_229609_1_, ServerWorld p_229609_2_) {
      Collection<BlockPos> collection = Lists.newArrayList();
      AxisAlignedBB axisalignedbb = new AxisAlignedBB(p_229609_0_);
      axisalignedbb = axisalignedbb.inflate((double)p_229609_1_);

      for(int i = (int)axisalignedbb.minX; i <= (int)axisalignedbb.maxX; ++i) {
         for(int j = (int)axisalignedbb.minY; j <= (int)axisalignedbb.maxY; ++j) {
            for(int k = (int)axisalignedbb.minZ; k <= (int)axisalignedbb.maxZ; ++k) {
               BlockPos blockpos = new BlockPos(i, j, k);
               BlockState blockstate = p_229609_2_.getBlockState(blockpos);
               if (blockstate.is(Blocks.STRUCTURE_BLOCK)) {
                  collection.add(blockpos);
               }
            }
         }
      }

      return collection;
   }

   private static Template getStructureTemplate(String p_229605_0_, ServerWorld p_229605_1_) {
      TemplateManager templatemanager = p_229605_1_.getStructureManager();
      Template template = templatemanager.get(new ResourceLocation(p_229605_0_));
      if (template != null) {
         return template;
      } else {
         String s = p_229605_0_ + ".snbt";
         Path path = Paths.get(testStructuresDir, s);
         CompoundNBT compoundnbt = tryLoadStructure(path);
         if (compoundnbt == null) {
            throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
         } else {
            return templatemanager.readStructure(compoundnbt);
         }
      }
   }

   private static StructureBlockTileEntity createStructureBlock(String p_240566_0_, BlockPos p_240566_1_, Rotation p_240566_2_, ServerWorld p_240566_3_, boolean p_240566_4_) {
      p_240566_3_.setBlockAndUpdate(p_240566_1_, Blocks.STRUCTURE_BLOCK.defaultBlockState());
      StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)p_240566_3_.getBlockEntity(p_240566_1_);
      structureblocktileentity.setMode(StructureMode.LOAD);
      structureblocktileentity.setRotation(p_240566_2_);
      structureblocktileentity.setIgnoreEntities(false);
      structureblocktileentity.setStructureName(new ResourceLocation(p_240566_0_));
      structureblocktileentity.loadStructure(p_240566_3_, p_240566_4_);
      if (structureblocktileentity.getStructureSize() != BlockPos.ZERO) {
         return structureblocktileentity;
      } else {
         Template template = getStructureTemplate(p_240566_0_, p_240566_3_);
         structureblocktileentity.loadStructure(p_240566_3_, p_240566_4_, template);
         if (structureblocktileentity.getStructureSize() == BlockPos.ZERO) {
            throw new RuntimeException("Failed to load structure " + p_240566_0_);
         } else {
            return structureblocktileentity;
         }
      }
   }

   @Nullable
   private static CompoundNBT tryLoadStructure(Path p_229606_0_) {
      try {
         BufferedReader bufferedreader = Files.newBufferedReader(p_229606_0_);
         String s = IOUtils.toString((Reader)bufferedreader);
         return JsonToNBT.parseTag(s);
      } catch (IOException ioexception) {
         return null;
      } catch (CommandSyntaxException commandsyntaxexception) {
         throw new RuntimeException("Error while trying to load structure " + p_229606_0_, commandsyntaxexception);
      }
   }

   private static void clearBlock(int p_229591_0_, BlockPos p_229591_1_, ServerWorld p_229591_2_) {
      BlockState blockstate = null;
      FlatGenerationSettings flatgenerationsettings = FlatGenerationSettings.getDefault(p_229591_2_.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));
      if (flatgenerationsettings instanceof FlatGenerationSettings) {
         BlockState[] ablockstate = flatgenerationsettings.getLayers();
         if (p_229591_1_.getY() < p_229591_0_ && p_229591_1_.getY() <= ablockstate.length) {
            blockstate = ablockstate[p_229591_1_.getY() - 1];
         }
      } else if (p_229591_1_.getY() == p_229591_0_ - 1) {
         blockstate = p_229591_2_.getBiome(p_229591_1_).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
      } else if (p_229591_1_.getY() < p_229591_0_ - 1) {
         blockstate = p_229591_2_.getBiome(p_229591_1_).getGenerationSettings().getSurfaceBuilderConfig().getUnderMaterial();
      }

      if (blockstate == null) {
         blockstate = Blocks.AIR.defaultBlockState();
      }

      BlockStateInput blockstateinput = new BlockStateInput(blockstate, Collections.emptySet(), (CompoundNBT)null);
      blockstateinput.place(p_229591_2_, p_229591_1_, 2);
      p_229591_2_.blockUpdated(p_229591_1_, blockstate.getBlock());
   }

   private static boolean doesStructureContain(BlockPos p_229599_0_, BlockPos p_229599_1_, ServerWorld p_229599_2_) {
      StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)p_229599_2_.getBlockEntity(p_229599_0_);
      AxisAlignedBB axisalignedbb = getStructureBounds(structureblocktileentity).inflate(1.0D);
      return axisalignedbb.contains(Vector3d.atCenterOf(p_229599_1_));
   }
}
