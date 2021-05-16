package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class Template {
   private final List<Template.Palette> palettes = Lists.newArrayList();
   private final List<Template.EntityInfo> entityInfoList = Lists.newArrayList();
   private BlockPos size = BlockPos.ZERO;
   private String author = "?";

   public BlockPos getSize() {
      return this.size;
   }

   public void setAuthor(String p_186252_1_) {
      this.author = p_186252_1_;
   }

   public String getAuthor() {
      return this.author;
   }

   public void fillFromWorld(World p_186254_1_, BlockPos p_186254_2_, BlockPos p_186254_3_, boolean p_186254_4_, @Nullable Block p_186254_5_) {
      if (p_186254_3_.getX() >= 1 && p_186254_3_.getY() >= 1 && p_186254_3_.getZ() >= 1) {
         BlockPos blockpos = p_186254_2_.offset(p_186254_3_).offset(-1, -1, -1);
         List<Template.BlockInfo> list = Lists.newArrayList();
         List<Template.BlockInfo> list1 = Lists.newArrayList();
         List<Template.BlockInfo> list2 = Lists.newArrayList();
         BlockPos blockpos1 = new BlockPos(Math.min(p_186254_2_.getX(), blockpos.getX()), Math.min(p_186254_2_.getY(), blockpos.getY()), Math.min(p_186254_2_.getZ(), blockpos.getZ()));
         BlockPos blockpos2 = new BlockPos(Math.max(p_186254_2_.getX(), blockpos.getX()), Math.max(p_186254_2_.getY(), blockpos.getY()), Math.max(p_186254_2_.getZ(), blockpos.getZ()));
         this.size = p_186254_3_;

         for(BlockPos blockpos3 : BlockPos.betweenClosed(blockpos1, blockpos2)) {
            BlockPos blockpos4 = blockpos3.subtract(blockpos1);
            BlockState blockstate = p_186254_1_.getBlockState(blockpos3);
            if (p_186254_5_ == null || p_186254_5_ != blockstate.getBlock()) {
               TileEntity tileentity = p_186254_1_.getBlockEntity(blockpos3);
               Template.BlockInfo template$blockinfo;
               if (tileentity != null) {
                  CompoundNBT compoundnbt = tileentity.save(new CompoundNBT());
                  compoundnbt.remove("x");
                  compoundnbt.remove("y");
                  compoundnbt.remove("z");
                  template$blockinfo = new Template.BlockInfo(blockpos4, blockstate, compoundnbt.copy());
               } else {
                  template$blockinfo = new Template.BlockInfo(blockpos4, blockstate, (CompoundNBT)null);
               }

               addToLists(template$blockinfo, list, list1, list2);
            }
         }

         List<Template.BlockInfo> list3 = buildInfoList(list, list1, list2);
         this.palettes.clear();
         this.palettes.add(new Template.Palette(list3));
         if (p_186254_4_) {
            this.fillEntityList(p_186254_1_, blockpos1, blockpos2.offset(1, 1, 1));
         } else {
            this.entityInfoList.clear();
         }

      }
   }

   private static void addToLists(Template.BlockInfo p_237149_0_, List<Template.BlockInfo> p_237149_1_, List<Template.BlockInfo> p_237149_2_, List<Template.BlockInfo> p_237149_3_) {
      if (p_237149_0_.nbt != null) {
         p_237149_2_.add(p_237149_0_);
      } else if (!p_237149_0_.state.getBlock().hasDynamicShape() && p_237149_0_.state.isCollisionShapeFullBlock(EmptyBlockReader.INSTANCE, BlockPos.ZERO)) {
         p_237149_1_.add(p_237149_0_);
      } else {
         p_237149_3_.add(p_237149_0_);
      }

   }

   private static List<Template.BlockInfo> buildInfoList(List<Template.BlockInfo> p_237151_0_, List<Template.BlockInfo> p_237151_1_, List<Template.BlockInfo> p_237151_2_) {
      Comparator<Template.BlockInfo> comparator = Comparator.<Template.BlockInfo>comparingInt((p_237154_0_) -> {
         return p_237154_0_.pos.getY();
      }).thenComparingInt((p_237153_0_) -> {
         return p_237153_0_.pos.getX();
      }).thenComparingInt((p_237148_0_) -> {
         return p_237148_0_.pos.getZ();
      });
      p_237151_0_.sort(comparator);
      p_237151_2_.sort(comparator);
      p_237151_1_.sort(comparator);
      List<Template.BlockInfo> list = Lists.newArrayList();
      list.addAll(p_237151_0_);
      list.addAll(p_237151_2_);
      list.addAll(p_237151_1_);
      return list;
   }

   private void fillEntityList(World p_186255_1_, BlockPos p_186255_2_, BlockPos p_186255_3_) {
      List<Entity> list = p_186255_1_.getEntitiesOfClass(Entity.class, new AxisAlignedBB(p_186255_2_, p_186255_3_), (p_237142_0_) -> {
         return !(p_237142_0_ instanceof PlayerEntity);
      });
      this.entityInfoList.clear();

      for(Entity entity : list) {
         Vector3d vector3d = new Vector3d(entity.getX() - (double)p_186255_2_.getX(), entity.getY() - (double)p_186255_2_.getY(), entity.getZ() - (double)p_186255_2_.getZ());
         CompoundNBT compoundnbt = new CompoundNBT();
         entity.save(compoundnbt);
         BlockPos blockpos;
         if (entity instanceof PaintingEntity) {
            blockpos = ((PaintingEntity)entity).getPos().subtract(p_186255_2_);
         } else {
            blockpos = new BlockPos(vector3d);
         }

         this.entityInfoList.add(new Template.EntityInfo(vector3d, blockpos, compoundnbt.copy()));
      }

   }

   public List<Template.BlockInfo> filterBlocks(BlockPos p_215381_1_, PlacementSettings p_215381_2_, Block p_215381_3_) {
      return this.filterBlocks(p_215381_1_, p_215381_2_, p_215381_3_, true);
   }

   public List<Template.BlockInfo> filterBlocks(BlockPos p_215386_1_, PlacementSettings p_215386_2_, Block p_215386_3_, boolean p_215386_4_) {
      List<Template.BlockInfo> list = Lists.newArrayList();
      MutableBoundingBox mutableboundingbox = p_215386_2_.getBoundingBox();
      if (this.palettes.isEmpty()) {
         return Collections.emptyList();
      } else {
         for(Template.BlockInfo template$blockinfo : p_215386_2_.getRandomPalette(this.palettes, p_215386_1_).blocks(p_215386_3_)) {
            BlockPos blockpos = p_215386_4_ ? calculateRelativePosition(p_215386_2_, template$blockinfo.pos).offset(p_215386_1_) : template$blockinfo.pos;
            if (mutableboundingbox == null || mutableboundingbox.isInside(blockpos)) {
               list.add(new Template.BlockInfo(blockpos, template$blockinfo.state.rotate(p_215386_2_.getRotation()), template$blockinfo.nbt));
            }
         }

         return list;
      }
   }

   public BlockPos calculateConnectedPosition(PlacementSettings p_186262_1_, BlockPos p_186262_2_, PlacementSettings p_186262_3_, BlockPos p_186262_4_) {
      BlockPos blockpos = calculateRelativePosition(p_186262_1_, p_186262_2_);
      BlockPos blockpos1 = calculateRelativePosition(p_186262_3_, p_186262_4_);
      return blockpos.subtract(blockpos1);
   }

   public static BlockPos calculateRelativePosition(PlacementSettings p_186266_0_, BlockPos p_186266_1_) {
      return transform(p_186266_1_, p_186266_0_.getMirror(), p_186266_0_.getRotation(), p_186266_0_.getRotationPivot());
   }

   public static Vector3d transformedVec3d(PlacementSettings placementIn, Vector3d pos) {
      return transform(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getRotationPivot());
   }

   public void placeInWorldChunk(IServerWorld p_237144_1_, BlockPos p_237144_2_, PlacementSettings p_237144_3_, Random p_237144_4_) {
      p_237144_3_.updateBoundingBoxFromChunkPos();
      this.placeInWorld(p_237144_1_, p_237144_2_, p_237144_3_, p_237144_4_);
   }

   public void placeInWorld(IServerWorld p_237152_1_, BlockPos p_237152_2_, PlacementSettings p_237152_3_, Random p_237152_4_) {
      this.placeInWorld(p_237152_1_, p_237152_2_, p_237152_2_, p_237152_3_, p_237152_4_, 2);
   }

   public boolean placeInWorld(IServerWorld p_237146_1_, BlockPos p_237146_2_, BlockPos p_237146_3_, PlacementSettings p_237146_4_, Random p_237146_5_, int p_237146_6_) {
      if (this.palettes.isEmpty()) {
         return false;
      } else {
         List<Template.BlockInfo> list = p_237146_4_.getRandomPalette(this.palettes, p_237146_2_).blocks();
         if ((!list.isEmpty() || !p_237146_4_.isIgnoreEntities() && !this.entityInfoList.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
            MutableBoundingBox mutableboundingbox = p_237146_4_.getBoundingBox();
            List<BlockPos> list1 = Lists.newArrayListWithCapacity(p_237146_4_.shouldKeepLiquids() ? list.size() : 0);
            List<Pair<BlockPos, CompoundNBT>> list2 = Lists.newArrayListWithCapacity(list.size());
            int i = Integer.MAX_VALUE;
            int j = Integer.MAX_VALUE;
            int k = Integer.MAX_VALUE;
            int l = Integer.MIN_VALUE;
            int i1 = Integer.MIN_VALUE;
            int j1 = Integer.MIN_VALUE;

            for(Template.BlockInfo template$blockinfo : processBlockInfos(p_237146_1_, p_237146_2_, p_237146_3_, p_237146_4_, list, this)) {
               BlockPos blockpos = template$blockinfo.pos;
               if (mutableboundingbox == null || mutableboundingbox.isInside(blockpos)) {
                  FluidState fluidstate = p_237146_4_.shouldKeepLiquids() ? p_237146_1_.getFluidState(blockpos) : null;
                  BlockState blockstate = template$blockinfo.state.mirror(p_237146_4_.getMirror()).rotate(p_237146_4_.getRotation());
                  if (template$blockinfo.nbt != null) {
                     TileEntity tileentity = p_237146_1_.getBlockEntity(blockpos);
                     IClearable.tryClear(tileentity);
                     p_237146_1_.setBlock(blockpos, Blocks.BARRIER.defaultBlockState(), 20);
                  }

                  if (p_237146_1_.setBlock(blockpos, blockstate, p_237146_6_)) {
                     i = Math.min(i, blockpos.getX());
                     j = Math.min(j, blockpos.getY());
                     k = Math.min(k, blockpos.getZ());
                     l = Math.max(l, blockpos.getX());
                     i1 = Math.max(i1, blockpos.getY());
                     j1 = Math.max(j1, blockpos.getZ());
                     list2.add(Pair.of(blockpos, template$blockinfo.nbt));
                     if (template$blockinfo.nbt != null) {
                        TileEntity tileentity1 = p_237146_1_.getBlockEntity(blockpos);
                        if (tileentity1 != null) {
                           template$blockinfo.nbt.putInt("x", blockpos.getX());
                           template$blockinfo.nbt.putInt("y", blockpos.getY());
                           template$blockinfo.nbt.putInt("z", blockpos.getZ());
                           if (tileentity1 instanceof LockableLootTileEntity) {
                              template$blockinfo.nbt.putLong("LootTableSeed", p_237146_5_.nextLong());
                           }

                           tileentity1.load(template$blockinfo.state, template$blockinfo.nbt);
                           tileentity1.mirror(p_237146_4_.getMirror());
                           tileentity1.rotate(p_237146_4_.getRotation());
                        }
                     }

                     if (fluidstate != null && blockstate.getBlock() instanceof ILiquidContainer) {
                        ((ILiquidContainer)blockstate.getBlock()).placeLiquid(p_237146_1_, blockpos, blockstate, fluidstate);
                        if (!fluidstate.isSource()) {
                           list1.add(blockpos);
                        }
                     }
                  }
               }
            }

            boolean flag = true;
            Direction[] adirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

            while(flag && !list1.isEmpty()) {
               flag = false;
               Iterator<BlockPos> iterator = list1.iterator();

               while(iterator.hasNext()) {
                  BlockPos blockpos2 = iterator.next();
                  BlockPos blockpos3 = blockpos2;
                  FluidState fluidstate2 = p_237146_1_.getFluidState(blockpos2);

                  for(int k1 = 0; k1 < adirection.length && !fluidstate2.isSource(); ++k1) {
                     BlockPos blockpos1 = blockpos3.relative(adirection[k1]);
                     FluidState fluidstate1 = p_237146_1_.getFluidState(blockpos1);
                     if (fluidstate1.getHeight(p_237146_1_, blockpos1) > fluidstate2.getHeight(p_237146_1_, blockpos3) || fluidstate1.isSource() && !fluidstate2.isSource()) {
                        fluidstate2 = fluidstate1;
                        blockpos3 = blockpos1;
                     }
                  }

                  if (fluidstate2.isSource()) {
                     BlockState blockstate2 = p_237146_1_.getBlockState(blockpos2);
                     Block block = blockstate2.getBlock();
                     if (block instanceof ILiquidContainer) {
                        ((ILiquidContainer)block).placeLiquid(p_237146_1_, blockpos2, blockstate2, fluidstate2);
                        flag = true;
                        iterator.remove();
                     }
                  }
               }
            }

            if (i <= l) {
               if (!p_237146_4_.getKnownShape()) {
                  VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(l - i + 1, i1 - j + 1, j1 - k + 1);
                  int l1 = i;
                  int i2 = j;
                  int j2 = k;

                  for(Pair<BlockPos, CompoundNBT> pair1 : list2) {
                     BlockPos blockpos5 = pair1.getFirst();
                     voxelshapepart.setFull(blockpos5.getX() - l1, blockpos5.getY() - i2, blockpos5.getZ() - j2, true, true);
                  }

                  updateShapeAtEdge(p_237146_1_, p_237146_6_, voxelshapepart, l1, i2, j2);
               }

               for(Pair<BlockPos, CompoundNBT> pair : list2) {
                  BlockPos blockpos4 = pair.getFirst();
                  if (!p_237146_4_.getKnownShape()) {
                     BlockState blockstate1 = p_237146_1_.getBlockState(blockpos4);
                     BlockState blockstate3 = Block.updateFromNeighbourShapes(blockstate1, p_237146_1_, blockpos4);
                     if (blockstate1 != blockstate3) {
                        p_237146_1_.setBlock(blockpos4, blockstate3, p_237146_6_ & -2 | 16);
                     }

                     p_237146_1_.blockUpdated(blockpos4, blockstate3.getBlock());
                  }

                  if (pair.getSecond() != null) {
                     TileEntity tileentity2 = p_237146_1_.getBlockEntity(blockpos4);
                     if (tileentity2 != null) {
                        tileentity2.setChanged();
                     }
                  }
               }
            }

            if (!p_237146_4_.isIgnoreEntities()) {
               this.addEntitiesToWorld(p_237146_1_, p_237146_2_, p_237146_4_);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static void updateShapeAtEdge(IWorld p_222857_0_, int p_222857_1_, VoxelShapePart p_222857_2_, int p_222857_3_, int p_222857_4_, int p_222857_5_) {
      p_222857_2_.forAllFaces((p_237141_5_, p_237141_6_, p_237141_7_, p_237141_8_) -> {
         BlockPos blockpos = new BlockPos(p_222857_3_ + p_237141_6_, p_222857_4_ + p_237141_7_, p_222857_5_ + p_237141_8_);
         BlockPos blockpos1 = blockpos.relative(p_237141_5_);
         BlockState blockstate = p_222857_0_.getBlockState(blockpos);
         BlockState blockstate1 = p_222857_0_.getBlockState(blockpos1);
         BlockState blockstate2 = blockstate.updateShape(p_237141_5_, blockstate1, p_222857_0_, blockpos, blockpos1);
         if (blockstate != blockstate2) {
            p_222857_0_.setBlock(blockpos, blockstate2, p_222857_1_ & -2);
         }

         BlockState blockstate3 = blockstate1.updateShape(p_237141_5_.getOpposite(), blockstate2, p_222857_0_, blockpos1, blockpos);
         if (blockstate1 != blockstate3) {
            p_222857_0_.setBlock(blockpos1, blockstate3, p_222857_1_ & -2);
         }

      });
   }

   @Deprecated //Use Forge version
   public static List<Template.BlockInfo> processBlockInfos(IWorld p_237145_0_, BlockPos p_237145_1_, BlockPos p_237145_2_, PlacementSettings p_237145_3_, List<Template.BlockInfo> p_237145_4_) {
      return processBlockInfos(p_237145_0_, p_237145_1_, p_237145_2_, p_237145_3_, p_237145_4_, null);
   }

   public static List<Template.BlockInfo> processBlockInfos(IWorld p_237145_0_, BlockPos p_237145_1_, BlockPos p_237145_2_, PlacementSettings p_237145_3_, List<Template.BlockInfo> p_237145_4_, @Nullable Template template) {
      List<Template.BlockInfo> list = Lists.newArrayList();

      for(Template.BlockInfo template$blockinfo : p_237145_4_) {
         BlockPos blockpos = calculateRelativePosition(p_237145_3_, template$blockinfo.pos).offset(p_237145_1_);
         Template.BlockInfo template$blockinfo1 = new Template.BlockInfo(blockpos, template$blockinfo.state, template$blockinfo.nbt != null ? template$blockinfo.nbt.copy() : null);

         for(Iterator<StructureProcessor> iterator = p_237145_3_.getProcessors().iterator(); template$blockinfo1 != null && iterator.hasNext(); template$blockinfo1 = iterator.next().process(p_237145_0_, p_237145_1_, p_237145_2_, template$blockinfo, template$blockinfo1, p_237145_3_, template)) {
         }

         if (template$blockinfo1 != null) {
            list.add(template$blockinfo1);
         }
      }

      return list;
   }

   public static List<Template.EntityInfo> processEntityInfos(@Nullable Template template, IWorld p_215387_0_, BlockPos p_215387_1_, PlacementSettings p_215387_2_, List<Template.EntityInfo> p_215387_3_) {
      List<Template.EntityInfo> list = Lists.newArrayList();
      for(Template.EntityInfo entityInfo : p_215387_3_) {
         Vector3d pos = transformedVec3d(p_215387_2_, entityInfo.pos).add(Vector3d.atLowerCornerOf(p_215387_1_));
         BlockPos blockpos = calculateRelativePosition(p_215387_2_, entityInfo.blockPos).offset(p_215387_1_);
         Template.EntityInfo info = new Template.EntityInfo(pos, blockpos, entityInfo.nbt);
         for (StructureProcessor proc : p_215387_2_.getProcessors()) {
            info = proc.processEntity(p_215387_0_, p_215387_1_, entityInfo, info, p_215387_2_, template);
            if (info == null)
               break;
         }
         if (info != null)
            list.add(info);
      }
      return list;
   }

   private void addEntitiesToWorld(IServerWorld p_237143_1_, BlockPos p_237143_2_, PlacementSettings placementIn) {
      for(Template.EntityInfo template$entityinfo : processEntityInfos(this, p_237143_1_, p_237143_2_, placementIn, this.entityInfoList)) {
         BlockPos blockpos = transform(template$entityinfo.blockPos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getRotationPivot()).offset(p_237143_2_);
         blockpos = template$entityinfo.blockPos; // FORGE: Position will have already been transformed by processEntityInfos
         if (placementIn.getBoundingBox() == null || placementIn.getBoundingBox().isInside(blockpos)) {
            CompoundNBT compoundnbt = template$entityinfo.nbt.copy();
            Vector3d vector3d1 = template$entityinfo.pos; // FORGE: Position will have already been transformed by processEntityInfos
            ListNBT listnbt = new ListNBT();
            listnbt.add(DoubleNBT.valueOf(vector3d1.x));
            listnbt.add(DoubleNBT.valueOf(vector3d1.y));
            listnbt.add(DoubleNBT.valueOf(vector3d1.z));
            compoundnbt.put("Pos", listnbt);
            compoundnbt.remove("UUID");
            createEntityIgnoreException(p_237143_1_, compoundnbt).ifPresent((p_242927_6_) -> {
               float f = p_242927_6_.mirror(placementIn.getMirror());
               f = f + (p_242927_6_.yRot - p_242927_6_.rotate(placementIn.getRotation()));
               p_242927_6_.moveTo(vector3d1.x, vector3d1.y, vector3d1.z, f, p_242927_6_.xRot);
               if (placementIn.shouldFinalizeEntities() && p_242927_6_ instanceof MobEntity) {
                  ((MobEntity)p_242927_6_).finalizeSpawn(p_237143_1_, p_237143_1_.getCurrentDifficultyAt(new BlockPos(vector3d1)), SpawnReason.STRUCTURE, (ILivingEntityData)null, compoundnbt);
               }

               p_237143_1_.addFreshEntityWithPassengers(p_242927_6_);
            });
         }
      }

   }

   private static Optional<Entity> createEntityIgnoreException(IServerWorld p_215382_0_, CompoundNBT p_215382_1_) {
      try {
         return EntityType.create(p_215382_1_, p_215382_0_.getLevel());
      } catch (Exception exception) {
         return Optional.empty();
      }
   }

   public BlockPos getSize(Rotation p_186257_1_) {
      switch(p_186257_1_) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
      default:
         return this.size;
      }
   }

   public static BlockPos transform(BlockPos p_207669_0_, Mirror p_207669_1_, Rotation p_207669_2_, BlockPos p_207669_3_) {
      int i = p_207669_0_.getX();
      int j = p_207669_0_.getY();
      int k = p_207669_0_.getZ();
      boolean flag = true;
      switch(p_207669_1_) {
      case LEFT_RIGHT:
         k = -k;
         break;
      case FRONT_BACK:
         i = -i;
         break;
      default:
         flag = false;
      }

      int l = p_207669_3_.getX();
      int i1 = p_207669_3_.getZ();
      switch(p_207669_2_) {
      case COUNTERCLOCKWISE_90:
         return new BlockPos(l - i1 + k, j, l + i1 - i);
      case CLOCKWISE_90:
         return new BlockPos(l + i1 - k, j, i1 - l + i);
      case CLOCKWISE_180:
         return new BlockPos(l + l - i, j, i1 + i1 - k);
      default:
         return flag ? new BlockPos(i, j, k) : p_207669_0_;
      }
   }

   public static Vector3d transform(Vector3d p_207667_0_, Mirror p_207667_1_, Rotation p_207667_2_, BlockPos p_207667_3_) {
      double d0 = p_207667_0_.x;
      double d1 = p_207667_0_.y;
      double d2 = p_207667_0_.z;
      boolean flag = true;
      switch(p_207667_1_) {
      case LEFT_RIGHT:
         d2 = 1.0D - d2;
         break;
      case FRONT_BACK:
         d0 = 1.0D - d0;
         break;
      default:
         flag = false;
      }

      int i = p_207667_3_.getX();
      int j = p_207667_3_.getZ();
      switch(p_207667_2_) {
      case COUNTERCLOCKWISE_90:
         return new Vector3d((double)(i - j) + d2, d1, (double)(i + j + 1) - d0);
      case CLOCKWISE_90:
         return new Vector3d((double)(i + j + 1) - d2, d1, (double)(j - i) + d0);
      case CLOCKWISE_180:
         return new Vector3d((double)(i + i + 1) - d0, d1, (double)(j + j + 1) - d2);
      default:
         return flag ? new Vector3d(d0, d1, d2) : p_207667_0_;
      }
   }

   public BlockPos getZeroPositionWithTransform(BlockPos p_189961_1_, Mirror p_189961_2_, Rotation p_189961_3_) {
      return getZeroPositionWithTransform(p_189961_1_, p_189961_2_, p_189961_3_, this.getSize().getX(), this.getSize().getZ());
   }

   public static BlockPos getZeroPositionWithTransform(BlockPos p_191157_0_, Mirror p_191157_1_, Rotation p_191157_2_, int p_191157_3_, int p_191157_4_) {
      --p_191157_3_;
      --p_191157_4_;
      int i = p_191157_1_ == Mirror.FRONT_BACK ? p_191157_3_ : 0;
      int j = p_191157_1_ == Mirror.LEFT_RIGHT ? p_191157_4_ : 0;
      BlockPos blockpos = p_191157_0_;
      switch(p_191157_2_) {
      case COUNTERCLOCKWISE_90:
         blockpos = p_191157_0_.offset(j, 0, p_191157_3_ - i);
         break;
      case CLOCKWISE_90:
         blockpos = p_191157_0_.offset(p_191157_4_ - j, 0, i);
         break;
      case CLOCKWISE_180:
         blockpos = p_191157_0_.offset(p_191157_3_ - i, 0, p_191157_4_ - j);
         break;
      case NONE:
         blockpos = p_191157_0_.offset(i, 0, j);
      }

      return blockpos;
   }

   public MutableBoundingBox getBoundingBox(PlacementSettings p_215388_1_, BlockPos p_215388_2_) {
      return this.getBoundingBox(p_215388_2_, p_215388_1_.getRotation(), p_215388_1_.getRotationPivot(), p_215388_1_.getMirror());
   }

   public MutableBoundingBox getBoundingBox(BlockPos p_237150_1_, Rotation p_237150_2_, BlockPos p_237150_3_, Mirror p_237150_4_) {
      BlockPos blockpos = this.getSize(p_237150_2_);
      int i = p_237150_3_.getX();
      int j = p_237150_3_.getZ();
      int k = blockpos.getX() - 1;
      int l = blockpos.getY() - 1;
      int i1 = blockpos.getZ() - 1;
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(0, 0, 0, 0, 0, 0);
      switch(p_237150_2_) {
      case COUNTERCLOCKWISE_90:
         mutableboundingbox = new MutableBoundingBox(i - j, 0, i + j - i1, i - j + k, l, i + j);
         break;
      case CLOCKWISE_90:
         mutableboundingbox = new MutableBoundingBox(i + j - k, 0, j - i, i + j, l, j - i + i1);
         break;
      case CLOCKWISE_180:
         mutableboundingbox = new MutableBoundingBox(i + i - k, 0, j + j - i1, i + i, l, j + j);
         break;
      case NONE:
         mutableboundingbox = new MutableBoundingBox(0, 0, 0, k, l, i1);
      }

      switch(p_237150_4_) {
      case LEFT_RIGHT:
         this.mirrorAABB(p_237150_2_, i1, k, mutableboundingbox, Direction.NORTH, Direction.SOUTH);
         break;
      case FRONT_BACK:
         this.mirrorAABB(p_237150_2_, k, i1, mutableboundingbox, Direction.WEST, Direction.EAST);
      case NONE:
      }

      mutableboundingbox.move(p_237150_1_.getX(), p_237150_1_.getY(), p_237150_1_.getZ());
      return mutableboundingbox;
   }

   private void mirrorAABB(Rotation p_215385_1_, int p_215385_2_, int p_215385_3_, MutableBoundingBox p_215385_4_, Direction p_215385_5_, Direction p_215385_6_) {
      BlockPos blockpos = BlockPos.ZERO;
      if (p_215385_1_ != Rotation.CLOCKWISE_90 && p_215385_1_ != Rotation.COUNTERCLOCKWISE_90) {
         if (p_215385_1_ == Rotation.CLOCKWISE_180) {
            blockpos = blockpos.relative(p_215385_6_, p_215385_2_);
         } else {
            blockpos = blockpos.relative(p_215385_5_, p_215385_2_);
         }
      } else {
         blockpos = blockpos.relative(p_215385_1_.rotate(p_215385_5_), p_215385_3_);
      }

      p_215385_4_.move(blockpos.getX(), 0, blockpos.getZ());
   }

   public CompoundNBT save(CompoundNBT p_189552_1_) {
      if (this.palettes.isEmpty()) {
         p_189552_1_.put("blocks", new ListNBT());
         p_189552_1_.put("palette", new ListNBT());
      } else {
         List<Template.BasicPalette> list = Lists.newArrayList();
         Template.BasicPalette template$basicpalette = new Template.BasicPalette();
         list.add(template$basicpalette);

         for(int i = 1; i < this.palettes.size(); ++i) {
            list.add(new Template.BasicPalette());
         }

         ListNBT listnbt1 = new ListNBT();
         List<Template.BlockInfo> list1 = this.palettes.get(0).blocks();

         for(int j = 0; j < list1.size(); ++j) {
            Template.BlockInfo template$blockinfo = list1.get(j);
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.put("pos", this.newIntegerList(template$blockinfo.pos.getX(), template$blockinfo.pos.getY(), template$blockinfo.pos.getZ()));
            int k = template$basicpalette.idFor(template$blockinfo.state);
            compoundnbt.putInt("state", k);
            if (template$blockinfo.nbt != null) {
               compoundnbt.put("nbt", template$blockinfo.nbt);
            }

            listnbt1.add(compoundnbt);

            for(int l = 1; l < this.palettes.size(); ++l) {
               Template.BasicPalette template$basicpalette1 = list.get(l);
               template$basicpalette1.addMapping((this.palettes.get(l).blocks().get(j)).state, k);
            }
         }

         p_189552_1_.put("blocks", listnbt1);
         if (list.size() == 1) {
            ListNBT listnbt2 = new ListNBT();

            for(BlockState blockstate : template$basicpalette) {
               listnbt2.add(NBTUtil.writeBlockState(blockstate));
            }

            p_189552_1_.put("palette", listnbt2);
         } else {
            ListNBT listnbt3 = new ListNBT();

            for(Template.BasicPalette template$basicpalette2 : list) {
               ListNBT listnbt4 = new ListNBT();

               for(BlockState blockstate1 : template$basicpalette2) {
                  listnbt4.add(NBTUtil.writeBlockState(blockstate1));
               }

               listnbt3.add(listnbt4);
            }

            p_189552_1_.put("palettes", listnbt3);
         }
      }

      ListNBT listnbt = new ListNBT();

      for(Template.EntityInfo template$entityinfo : this.entityInfoList) {
         CompoundNBT compoundnbt1 = new CompoundNBT();
         compoundnbt1.put("pos", this.newDoubleList(template$entityinfo.pos.x, template$entityinfo.pos.y, template$entityinfo.pos.z));
         compoundnbt1.put("blockPos", this.newIntegerList(template$entityinfo.blockPos.getX(), template$entityinfo.blockPos.getY(), template$entityinfo.blockPos.getZ()));
         if (template$entityinfo.nbt != null) {
            compoundnbt1.put("nbt", template$entityinfo.nbt);
         }

         listnbt.add(compoundnbt1);
      }

      p_189552_1_.put("entities", listnbt);
      p_189552_1_.put("size", this.newIntegerList(this.size.getX(), this.size.getY(), this.size.getZ()));
      p_189552_1_.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      return p_189552_1_;
   }

   public void load(CompoundNBT p_186256_1_) {
      this.palettes.clear();
      this.entityInfoList.clear();
      ListNBT listnbt = p_186256_1_.getList("size", 3);
      this.size = new BlockPos(listnbt.getInt(0), listnbt.getInt(1), listnbt.getInt(2));
      ListNBT listnbt1 = p_186256_1_.getList("blocks", 10);
      if (p_186256_1_.contains("palettes", 9)) {
         ListNBT listnbt2 = p_186256_1_.getList("palettes", 9);

         for(int i = 0; i < listnbt2.size(); ++i) {
            this.loadPalette(listnbt2.getList(i), listnbt1);
         }
      } else {
         this.loadPalette(p_186256_1_.getList("palette", 10), listnbt1);
      }

      ListNBT listnbt5 = p_186256_1_.getList("entities", 10);

      for(int j = 0; j < listnbt5.size(); ++j) {
         CompoundNBT compoundnbt = listnbt5.getCompound(j);
         ListNBT listnbt3 = compoundnbt.getList("pos", 6);
         Vector3d vector3d = new Vector3d(listnbt3.getDouble(0), listnbt3.getDouble(1), listnbt3.getDouble(2));
         ListNBT listnbt4 = compoundnbt.getList("blockPos", 3);
         BlockPos blockpos = new BlockPos(listnbt4.getInt(0), listnbt4.getInt(1), listnbt4.getInt(2));
         if (compoundnbt.contains("nbt")) {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("nbt");
            this.entityInfoList.add(new Template.EntityInfo(vector3d, blockpos, compoundnbt1));
         }
      }

   }

   private void loadPalette(ListNBT p_204768_1_, ListNBT p_204768_2_) {
      Template.BasicPalette template$basicpalette = new Template.BasicPalette();

      for(int i = 0; i < p_204768_1_.size(); ++i) {
         template$basicpalette.addMapping(NBTUtil.readBlockState(p_204768_1_.getCompound(i)), i);
      }

      List<Template.BlockInfo> list2 = Lists.newArrayList();
      List<Template.BlockInfo> list = Lists.newArrayList();
      List<Template.BlockInfo> list1 = Lists.newArrayList();

      for(int j = 0; j < p_204768_2_.size(); ++j) {
         CompoundNBT compoundnbt = p_204768_2_.getCompound(j);
         ListNBT listnbt = compoundnbt.getList("pos", 3);
         BlockPos blockpos = new BlockPos(listnbt.getInt(0), listnbt.getInt(1), listnbt.getInt(2));
         BlockState blockstate = template$basicpalette.stateFor(compoundnbt.getInt("state"));
         CompoundNBT compoundnbt1;
         if (compoundnbt.contains("nbt")) {
            compoundnbt1 = compoundnbt.getCompound("nbt");
         } else {
            compoundnbt1 = null;
         }

         Template.BlockInfo template$blockinfo = new Template.BlockInfo(blockpos, blockstate, compoundnbt1);
         addToLists(template$blockinfo, list2, list, list1);
      }

      List<Template.BlockInfo> list3 = buildInfoList(list2, list, list1);
      this.palettes.add(new Template.Palette(list3));
   }

   private ListNBT newIntegerList(int... p_186267_1_) {
      ListNBT listnbt = new ListNBT();

      for(int i : p_186267_1_) {
         listnbt.add(IntNBT.valueOf(i));
      }

      return listnbt;
   }

   private ListNBT newDoubleList(double... p_186264_1_) {
      ListNBT listnbt = new ListNBT();

      for(double d0 : p_186264_1_) {
         listnbt.add(DoubleNBT.valueOf(d0));
      }

      return listnbt;
   }

   static class BasicPalette implements Iterable<BlockState> {
      public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
      private final ObjectIntIdentityMap<BlockState> ids = new ObjectIntIdentityMap<>(16);
      private int lastId;

      private BasicPalette() {
      }

      public int idFor(BlockState p_189954_1_) {
         int i = this.ids.getId(p_189954_1_);
         if (i == -1) {
            i = this.lastId++;
            this.ids.addMapping(p_189954_1_, i);
         }

         return i;
      }

      @Nullable
      public BlockState stateFor(int p_189955_1_) {
         BlockState blockstate = this.ids.byId(p_189955_1_);
         return blockstate == null ? DEFAULT_BLOCK_STATE : blockstate;
      }

      public Iterator<BlockState> iterator() {
         return this.ids.iterator();
      }

      public void addMapping(BlockState p_189956_1_, int p_189956_2_) {
         this.ids.addMapping(p_189956_1_, p_189956_2_);
      }
   }

   public static class BlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      public final CompoundNBT nbt;

      public BlockInfo(BlockPos p_i47042_1_, BlockState p_i47042_2_, @Nullable CompoundNBT p_i47042_3_) {
         this.pos = p_i47042_1_;
         this.state = p_i47042_2_;
         this.nbt = p_i47042_3_;
      }

      public String toString() {
         return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
      }
   }

   public static class EntityInfo {
      public final Vector3d pos;
      public final BlockPos blockPos;
      public final CompoundNBT nbt;

      public EntityInfo(Vector3d p_i47101_1_, BlockPos p_i47101_2_, CompoundNBT p_i47101_3_) {
         this.pos = p_i47101_1_;
         this.blockPos = p_i47101_2_;
         this.nbt = p_i47101_3_;
      }
   }

   public static final class Palette {
      private final List<Template.BlockInfo> blocks;
      private final Map<Block, List<Template.BlockInfo>> cache = Maps.newHashMap();

      private Palette(List<Template.BlockInfo> p_i232120_1_) {
         this.blocks = p_i232120_1_;
      }

      public List<Template.BlockInfo> blocks() {
         return this.blocks;
      }

      public List<Template.BlockInfo> blocks(Block p_237158_1_) {
         return this.cache.computeIfAbsent(p_237158_1_, (p_237160_1_) -> {
            return this.blocks.stream().filter((p_237159_1_) -> {
               return p_237159_1_.state.is(p_237160_1_);
            }).collect(Collectors.toList());
         });
      }
   }
}
