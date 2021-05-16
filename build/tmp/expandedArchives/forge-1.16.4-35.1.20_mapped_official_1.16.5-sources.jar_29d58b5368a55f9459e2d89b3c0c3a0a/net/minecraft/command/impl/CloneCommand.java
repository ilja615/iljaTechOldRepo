package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class CloneCommand {
   private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType(new TranslationTextComponent("commands.clone.overlap"));
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_208796_0_, p_208796_1_) -> {
      return new TranslationTextComponent("commands.clone.toobig", p_208796_0_, p_208796_1_);
   });
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.clone.failed"));
   public static final Predicate<CachedBlockInfo> FILTER_AIR = (p_198275_0_) -> {
      return !p_198275_0_.getState().isAir();
   };

   public static void register(CommandDispatcher<CommandSource> p_198265_0_) {
      p_198265_0_.register(Commands.literal("clone").requires((p_198271_0_) -> {
         return p_198271_0_.hasPermission(2);
      }).then(Commands.argument("begin", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(Commands.argument("destination", BlockPosArgument.blockPos()).executes((p_198264_0_) -> {
         return clone(p_198264_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "destination"), (p_198269_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("replace").executes((p_198268_0_) -> {
         return clone(p_198268_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "destination"), (p_198272_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_198277_0_) -> {
         return clone(p_198277_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "destination"), (p_198262_0_) -> {
            return true;
         }, CloneCommand.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_198280_0_) -> {
         return clone(p_198280_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "destination"), (p_198281_0_) -> {
            return true;
         }, CloneCommand.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_198270_0_) -> {
         return clone(p_198270_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "destination"), (p_198279_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      }))).then(Commands.literal("masked").executes((p_198276_0_) -> {
         return clone(p_198276_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "destination"), FILTER_AIR, CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_198282_0_) -> {
         return clone(p_198282_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "destination"), FILTER_AIR, CloneCommand.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_198263_0_) -> {
         return clone(p_198263_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "destination"), FILTER_AIR, CloneCommand.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_198266_0_) -> {
         return clone(p_198266_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "destination"), FILTER_AIR, CloneCommand.Mode.NORMAL);
      }))).then(Commands.literal("filtered").then(Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((p_198273_0_) -> {
         return clone(p_198273_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198273_0_, "filter"), CloneCommand.Mode.NORMAL);
      }).then(Commands.literal("force").executes((p_198267_0_) -> {
         return clone(p_198267_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198267_0_, "filter"), CloneCommand.Mode.FORCE);
      })).then(Commands.literal("move").executes((p_198261_0_) -> {
         return clone(p_198261_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198261_0_, "filter"), CloneCommand.Mode.MOVE);
      })).then(Commands.literal("normal").executes((p_198278_0_) -> {
         return clone(p_198278_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198278_0_, "filter"), CloneCommand.Mode.NORMAL);
      }))))))));
   }

   private static int clone(CommandSource p_198274_0_, BlockPos p_198274_1_, BlockPos p_198274_2_, BlockPos p_198274_3_, Predicate<CachedBlockInfo> p_198274_4_, CloneCommand.Mode p_198274_5_) throws CommandSyntaxException {
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(p_198274_1_, p_198274_2_);
      BlockPos blockpos = p_198274_3_.offset(mutableboundingbox.getLength());
      MutableBoundingBox mutableboundingbox1 = new MutableBoundingBox(p_198274_3_, blockpos);
      if (!p_198274_5_.canOverlap() && mutableboundingbox1.intersects(mutableboundingbox)) {
         throw ERROR_OVERLAP.create();
      } else {
         int i = mutableboundingbox.getXSpan() * mutableboundingbox.getYSpan() * mutableboundingbox.getZSpan();
         if (i > 32768) {
            throw ERROR_AREA_TOO_LARGE.create(32768, i);
         } else {
            ServerWorld serverworld = p_198274_0_.getLevel();
            if (serverworld.hasChunksAt(p_198274_1_, p_198274_2_) && serverworld.hasChunksAt(p_198274_3_, blockpos)) {
               List<CloneCommand.BlockInfo> list = Lists.newArrayList();
               List<CloneCommand.BlockInfo> list1 = Lists.newArrayList();
               List<CloneCommand.BlockInfo> list2 = Lists.newArrayList();
               Deque<BlockPos> deque = Lists.newLinkedList();
               BlockPos blockpos1 = new BlockPos(mutableboundingbox1.x0 - mutableboundingbox.x0, mutableboundingbox1.y0 - mutableboundingbox.y0, mutableboundingbox1.z0 - mutableboundingbox.z0);

               for(int j = mutableboundingbox.z0; j <= mutableboundingbox.z1; ++j) {
                  for(int k = mutableboundingbox.y0; k <= mutableboundingbox.y1; ++k) {
                     for(int l = mutableboundingbox.x0; l <= mutableboundingbox.x1; ++l) {
                        BlockPos blockpos2 = new BlockPos(l, k, j);
                        BlockPos blockpos3 = blockpos2.offset(blockpos1);
                        CachedBlockInfo cachedblockinfo = new CachedBlockInfo(serverworld, blockpos2, false);
                        BlockState blockstate = cachedblockinfo.getState();
                        if (p_198274_4_.test(cachedblockinfo)) {
                           TileEntity tileentity = serverworld.getBlockEntity(blockpos2);
                           if (tileentity != null) {
                              CompoundNBT compoundnbt = tileentity.save(new CompoundNBT());
                              list1.add(new CloneCommand.BlockInfo(blockpos3, blockstate, compoundnbt));
                              deque.addLast(blockpos2);
                           } else if (!blockstate.isSolidRender(serverworld, blockpos2) && !blockstate.isCollisionShapeFullBlock(serverworld, blockpos2)) {
                              list2.add(new CloneCommand.BlockInfo(blockpos3, blockstate, (CompoundNBT)null));
                              deque.addFirst(blockpos2);
                           } else {
                              list.add(new CloneCommand.BlockInfo(blockpos3, blockstate, (CompoundNBT)null));
                              deque.addLast(blockpos2);
                           }
                        }
                     }
                  }
               }

               if (p_198274_5_ == CloneCommand.Mode.MOVE) {
                  for(BlockPos blockpos4 : deque) {
                     TileEntity tileentity1 = serverworld.getBlockEntity(blockpos4);
                     IClearable.tryClear(tileentity1);
                     serverworld.setBlock(blockpos4, Blocks.BARRIER.defaultBlockState(), 2);
                  }

                  for(BlockPos blockpos5 : deque) {
                     serverworld.setBlock(blockpos5, Blocks.AIR.defaultBlockState(), 3);
                  }
               }

               List<CloneCommand.BlockInfo> list3 = Lists.newArrayList();
               list3.addAll(list);
               list3.addAll(list1);
               list3.addAll(list2);
               List<CloneCommand.BlockInfo> list4 = Lists.reverse(list3);

               for(CloneCommand.BlockInfo clonecommand$blockinfo : list4) {
                  TileEntity tileentity2 = serverworld.getBlockEntity(clonecommand$blockinfo.pos);
                  IClearable.tryClear(tileentity2);
                  serverworld.setBlock(clonecommand$blockinfo.pos, Blocks.BARRIER.defaultBlockState(), 2);
               }

               int i1 = 0;

               for(CloneCommand.BlockInfo clonecommand$blockinfo1 : list3) {
                  if (serverworld.setBlock(clonecommand$blockinfo1.pos, clonecommand$blockinfo1.state, 2)) {
                     ++i1;
                  }
               }

               for(CloneCommand.BlockInfo clonecommand$blockinfo2 : list1) {
                  TileEntity tileentity3 = serverworld.getBlockEntity(clonecommand$blockinfo2.pos);
                  if (clonecommand$blockinfo2.tag != null && tileentity3 != null) {
                     clonecommand$blockinfo2.tag.putInt("x", clonecommand$blockinfo2.pos.getX());
                     clonecommand$blockinfo2.tag.putInt("y", clonecommand$blockinfo2.pos.getY());
                     clonecommand$blockinfo2.tag.putInt("z", clonecommand$blockinfo2.pos.getZ());
                     tileentity3.load(clonecommand$blockinfo2.state, clonecommand$blockinfo2.tag);
                     tileentity3.setChanged();
                  }

                  serverworld.setBlock(clonecommand$blockinfo2.pos, clonecommand$blockinfo2.state, 2);
               }

               for(CloneCommand.BlockInfo clonecommand$blockinfo3 : list4) {
                  serverworld.blockUpdated(clonecommand$blockinfo3.pos, clonecommand$blockinfo3.state.getBlock());
               }

               serverworld.getBlockTicks().copy(mutableboundingbox, blockpos1);
               if (i1 == 0) {
                  throw ERROR_FAILED.create();
               } else {
                  p_198274_0_.sendSuccess(new TranslationTextComponent("commands.clone.success", i1), true);
                  return i1;
               }
            } else {
               throw BlockPosArgument.ERROR_NOT_LOADED.create();
            }
         }
      }
   }

   static class BlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      @Nullable
      public final CompoundNBT tag;

      public BlockInfo(BlockPos p_i47708_1_, BlockState p_i47708_2_, @Nullable CompoundNBT p_i47708_3_) {
         this.pos = p_i47708_1_;
         this.state = p_i47708_2_;
         this.tag = p_i47708_3_;
      }
   }

   static enum Mode {
      FORCE(true),
      MOVE(true),
      NORMAL(false);

      private final boolean canOverlap;

      private Mode(boolean p_i47707_3_) {
         this.canOverlap = p_i47707_3_;
      }

      public boolean canOverlap() {
         return this.canOverlap;
      }
   }
}
