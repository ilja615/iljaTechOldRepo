package net.minecraft.command.impl;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.ColumnPosArgument;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ForceLoadCommand {
   private static final Dynamic2CommandExceptionType ERROR_TOO_MANY_CHUNKS = new Dynamic2CommandExceptionType((p_212724_0_, p_212724_1_) -> {
      return new TranslationTextComponent("commands.forceload.toobig", p_212724_0_, p_212724_1_);
   });
   private static final Dynamic2CommandExceptionType ERROR_NOT_TICKING = new Dynamic2CommandExceptionType((p_212717_0_, p_212717_1_) -> {
      return new TranslationTextComponent("commands.forceload.query.failure", p_212717_0_, p_212717_1_);
   });
   private static final SimpleCommandExceptionType ERROR_ALL_ADDED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forceload.added.failure"));
   private static final SimpleCommandExceptionType ERROR_NONE_REMOVED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forceload.removed.failure"));

   public static void register(CommandDispatcher<CommandSource> p_212712_0_) {
      p_212712_0_.register(Commands.literal("forceload").requires((p_212716_0_) -> {
         return p_212716_0_.hasPermission(2);
      }).then(Commands.literal("add").then(Commands.argument("from", ColumnPosArgument.columnPos()).executes((p_212711_0_) -> {
         return changeForceLoad(p_212711_0_.getSource(), ColumnPosArgument.getColumnPos(p_212711_0_, "from"), ColumnPosArgument.getColumnPos(p_212711_0_, "from"), true);
      }).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((p_212714_0_) -> {
         return changeForceLoad(p_212714_0_.getSource(), ColumnPosArgument.getColumnPos(p_212714_0_, "from"), ColumnPosArgument.getColumnPos(p_212714_0_, "to"), true);
      })))).then(Commands.literal("remove").then(Commands.argument("from", ColumnPosArgument.columnPos()).executes((p_218850_0_) -> {
         return changeForceLoad(p_218850_0_.getSource(), ColumnPosArgument.getColumnPos(p_218850_0_, "from"), ColumnPosArgument.getColumnPos(p_218850_0_, "from"), false);
      }).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((p_212718_0_) -> {
         return changeForceLoad(p_212718_0_.getSource(), ColumnPosArgument.getColumnPos(p_212718_0_, "from"), ColumnPosArgument.getColumnPos(p_212718_0_, "to"), false);
      }))).then(Commands.literal("all").executes((p_212715_0_) -> {
         return removeAll(p_212715_0_.getSource());
      }))).then(Commands.literal("query").executes((p_212710_0_) -> {
         return listForceLoad(p_212710_0_.getSource());
      }).then(Commands.argument("pos", ColumnPosArgument.columnPos()).executes((p_212723_0_) -> {
         return queryForceLoad(p_212723_0_.getSource(), ColumnPosArgument.getColumnPos(p_212723_0_, "pos"));
      }))));
   }

   private static int queryForceLoad(CommandSource p_212713_0_, ColumnPos p_212713_1_) throws CommandSyntaxException {
      ChunkPos chunkpos = new ChunkPos(p_212713_1_.x >> 4, p_212713_1_.z >> 4);
      ServerWorld serverworld = p_212713_0_.getLevel();
      RegistryKey<World> registrykey = serverworld.dimension();
      boolean flag = serverworld.getForcedChunks().contains(chunkpos.toLong());
      if (flag) {
         p_212713_0_.sendSuccess(new TranslationTextComponent("commands.forceload.query.success", chunkpos, registrykey.location()), false);
         return 1;
      } else {
         throw ERROR_NOT_TICKING.create(chunkpos, registrykey.location());
      }
   }

   private static int listForceLoad(CommandSource p_212721_0_) {
      ServerWorld serverworld = p_212721_0_.getLevel();
      RegistryKey<World> registrykey = serverworld.dimension();
      LongSet longset = serverworld.getForcedChunks();
      int i = longset.size();
      if (i > 0) {
         String s = Joiner.on(", ").join(longset.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
         if (i == 1) {
            p_212721_0_.sendSuccess(new TranslationTextComponent("commands.forceload.list.single", registrykey.location(), s), false);
         } else {
            p_212721_0_.sendSuccess(new TranslationTextComponent("commands.forceload.list.multiple", i, registrykey.location(), s), false);
         }
      } else {
         p_212721_0_.sendFailure(new TranslationTextComponent("commands.forceload.added.none", registrykey.location()));
      }

      return i;
   }

   private static int removeAll(CommandSource p_212722_0_) {
      ServerWorld serverworld = p_212722_0_.getLevel();
      RegistryKey<World> registrykey = serverworld.dimension();
      LongSet longset = serverworld.getForcedChunks();
      longset.forEach((long p_212720_1_) -> {
         serverworld.setChunkForced(ChunkPos.getX(p_212720_1_), ChunkPos.getZ(p_212720_1_), false);
      });
      p_212722_0_.sendSuccess(new TranslationTextComponent("commands.forceload.removed.all", registrykey.location()), true);
      return 0;
   }

   private static int changeForceLoad(CommandSource p_212719_0_, ColumnPos p_212719_1_, ColumnPos p_212719_2_, boolean p_212719_3_) throws CommandSyntaxException {
      int i = Math.min(p_212719_1_.x, p_212719_2_.x);
      int j = Math.min(p_212719_1_.z, p_212719_2_.z);
      int k = Math.max(p_212719_1_.x, p_212719_2_.x);
      int l = Math.max(p_212719_1_.z, p_212719_2_.z);
      if (i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000) {
         int i1 = i >> 4;
         int j1 = j >> 4;
         int k1 = k >> 4;
         int l1 = l >> 4;
         long i2 = ((long)(k1 - i1) + 1L) * ((long)(l1 - j1) + 1L);
         if (i2 > 256L) {
            throw ERROR_TOO_MANY_CHUNKS.create(256, i2);
         } else {
            ServerWorld serverworld = p_212719_0_.getLevel();
            RegistryKey<World> registrykey = serverworld.dimension();
            ChunkPos chunkpos = null;
            int j2 = 0;

            for(int k2 = i1; k2 <= k1; ++k2) {
               for(int l2 = j1; l2 <= l1; ++l2) {
                  boolean flag = serverworld.setChunkForced(k2, l2, p_212719_3_);
                  if (flag) {
                     ++j2;
                     if (chunkpos == null) {
                        chunkpos = new ChunkPos(k2, l2);
                     }
                  }
               }
            }

            if (j2 == 0) {
               throw (p_212719_3_ ? ERROR_ALL_ADDED : ERROR_NONE_REMOVED).create();
            } else {
               if (j2 == 1) {
                  p_212719_0_.sendSuccess(new TranslationTextComponent("commands.forceload." + (p_212719_3_ ? "added" : "removed") + ".single", chunkpos, registrykey.location()), true);
               } else {
                  ChunkPos chunkpos1 = new ChunkPos(i1, j1);
                  ChunkPos chunkpos2 = new ChunkPos(k1, l1);
                  p_212719_0_.sendSuccess(new TranslationTextComponent("commands.forceload." + (p_212719_3_ ? "added" : "removed") + ".multiple", j2, registrykey.location(), chunkpos1, chunkpos2), true);
               }

               return j2;
            }
         }
      } else {
         throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();
      }
   }
}
