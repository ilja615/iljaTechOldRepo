package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class FillCommand {
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_208897_0_, p_208897_1_) -> {
      return new TranslationTextComponent("commands.fill.toobig", p_208897_0_, p_208897_1_);
   });
   private static final BlockStateInput HOLLOW_CORE = new BlockStateInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), (CompoundNBT)null);
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.fill.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198465_0_) {
      p_198465_0_.register(Commands.literal("fill").requires((p_198471_0_) -> {
         return p_198471_0_.hasPermission(2);
      }).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(Commands.argument("block", BlockStateArgument.block()).executes((p_198472_0_) -> {
         return fillBlocks(p_198472_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198472_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198472_0_, "to")), BlockStateArgument.getBlock(p_198472_0_, "block"), FillCommand.Mode.REPLACE, (Predicate<CachedBlockInfo>)null);
      }).then(Commands.literal("replace").executes((p_198464_0_) -> {
         return fillBlocks(p_198464_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198464_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198464_0_, "to")), BlockStateArgument.getBlock(p_198464_0_, "block"), FillCommand.Mode.REPLACE, (Predicate<CachedBlockInfo>)null);
      }).then(Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((p_198466_0_) -> {
         return fillBlocks(p_198466_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198466_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198466_0_, "to")), BlockStateArgument.getBlock(p_198466_0_, "block"), FillCommand.Mode.REPLACE, BlockPredicateArgument.getBlockPredicate(p_198466_0_, "filter"));
      }))).then(Commands.literal("keep").executes((p_198462_0_) -> {
         return fillBlocks(p_198462_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198462_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198462_0_, "to")), BlockStateArgument.getBlock(p_198462_0_, "block"), FillCommand.Mode.REPLACE, (p_198469_0_) -> {
            return p_198469_0_.getLevel().isEmptyBlock(p_198469_0_.getPos());
         });
      })).then(Commands.literal("outline").executes((p_198467_0_) -> {
         return fillBlocks(p_198467_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198467_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198467_0_, "to")), BlockStateArgument.getBlock(p_198467_0_, "block"), FillCommand.Mode.OUTLINE, (Predicate<CachedBlockInfo>)null);
      })).then(Commands.literal("hollow").executes((p_198461_0_) -> {
         return fillBlocks(p_198461_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198461_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198461_0_, "to")), BlockStateArgument.getBlock(p_198461_0_, "block"), FillCommand.Mode.HOLLOW, (Predicate<CachedBlockInfo>)null);
      })).then(Commands.literal("destroy").executes((p_198468_0_) -> {
         return fillBlocks(p_198468_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198468_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198468_0_, "to")), BlockStateArgument.getBlock(p_198468_0_, "block"), FillCommand.Mode.DESTROY, (Predicate<CachedBlockInfo>)null);
      }))))));
   }

   private static int fillBlocks(CommandSource p_198463_0_, MutableBoundingBox p_198463_1_, BlockStateInput p_198463_2_, FillCommand.Mode p_198463_3_, @Nullable Predicate<CachedBlockInfo> p_198463_4_) throws CommandSyntaxException {
      int i = p_198463_1_.getXSpan() * p_198463_1_.getYSpan() * p_198463_1_.getZSpan();
      if (i > 32768) {
         throw ERROR_AREA_TOO_LARGE.create(32768, i);
      } else {
         List<BlockPos> list = Lists.newArrayList();
         ServerWorld serverworld = p_198463_0_.getLevel();
         int j = 0;

         for(BlockPos blockpos : BlockPos.betweenClosed(p_198463_1_.x0, p_198463_1_.y0, p_198463_1_.z0, p_198463_1_.x1, p_198463_1_.y1, p_198463_1_.z1)) {
            if (p_198463_4_ == null || p_198463_4_.test(new CachedBlockInfo(serverworld, blockpos, true))) {
               BlockStateInput blockstateinput = p_198463_3_.filter.filter(p_198463_1_, blockpos, p_198463_2_, serverworld);
               if (blockstateinput != null) {
                  TileEntity tileentity = serverworld.getBlockEntity(blockpos);
                  IClearable.tryClear(tileentity);
                  if (blockstateinput.place(serverworld, blockpos, 2)) {
                     list.add(blockpos.immutable());
                     ++j;
                  }
               }
            }
         }

         for(BlockPos blockpos1 : list) {
            Block block = serverworld.getBlockState(blockpos1).getBlock();
            serverworld.blockUpdated(blockpos1, block);
         }

         if (j == 0) {
            throw ERROR_FAILED.create();
         } else {
            p_198463_0_.sendSuccess(new TranslationTextComponent("commands.fill.success", j), true);
            return j;
         }
      }
   }

   static enum Mode {
      REPLACE((p_198450_0_, p_198450_1_, p_198450_2_, p_198450_3_) -> {
         return p_198450_2_;
      }),
      OUTLINE((p_198454_0_, p_198454_1_, p_198454_2_, p_198454_3_) -> {
         return p_198454_1_.getX() != p_198454_0_.x0 && p_198454_1_.getX() != p_198454_0_.x1 && p_198454_1_.getY() != p_198454_0_.y0 && p_198454_1_.getY() != p_198454_0_.y1 && p_198454_1_.getZ() != p_198454_0_.z0 && p_198454_1_.getZ() != p_198454_0_.z1 ? null : p_198454_2_;
      }),
      HOLLOW((p_198453_0_, p_198453_1_, p_198453_2_, p_198453_3_) -> {
         return p_198453_1_.getX() != p_198453_0_.x0 && p_198453_1_.getX() != p_198453_0_.x1 && p_198453_1_.getY() != p_198453_0_.y0 && p_198453_1_.getY() != p_198453_0_.y1 && p_198453_1_.getZ() != p_198453_0_.z0 && p_198453_1_.getZ() != p_198453_0_.z1 ? FillCommand.HOLLOW_CORE : p_198453_2_;
      }),
      DESTROY((p_198452_0_, p_198452_1_, p_198452_2_, p_198452_3_) -> {
         p_198452_3_.destroyBlock(p_198452_1_, true);
         return p_198452_2_;
      });

      public final SetBlockCommand.IFilter filter;

      private Mode(SetBlockCommand.IFilter p_i47985_3_) {
         this.filter = p_i47985_3_;
      }
   }
}
