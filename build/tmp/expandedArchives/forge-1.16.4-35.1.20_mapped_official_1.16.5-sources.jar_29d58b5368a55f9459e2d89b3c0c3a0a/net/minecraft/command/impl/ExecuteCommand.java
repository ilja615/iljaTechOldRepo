package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.IRangeArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.SwizzleArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.command.impl.data.DataCommand;
import net.minecraft.command.impl.data.IDataAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.CustomServerBossInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class ExecuteCommand {
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((p_208885_0_, p_208885_1_) -> {
      return new TranslationTextComponent("commands.execute.blocks.toobig", p_208885_0_, p_208885_1_);
   });
   private static final SimpleCommandExceptionType ERROR_CONDITIONAL_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.execute.conditional.fail"));
   private static final DynamicCommandExceptionType ERROR_CONDITIONAL_FAILED_COUNT = new DynamicCommandExceptionType((p_210446_0_) -> {
      return new TranslationTextComponent("commands.execute.conditional.fail_count", p_210446_0_);
   });
   private static final BinaryOperator<ResultConsumer<CommandSource>> CALLBACK_CHAINER = (p_209937_0_, p_209937_1_) -> {
      return (p_209939_2_, p_209939_3_, p_209939_4_) -> {
         p_209937_0_.onCommandComplete(p_209939_2_, p_209939_3_, p_209939_4_);
         p_209937_1_.onCommandComplete(p_209939_2_, p_209939_3_, p_209939_4_);
      };
   };
   private static final SuggestionProvider<CommandSource> SUGGEST_PREDICATE = (p_229763_0_, p_229763_1_) -> {
      LootPredicateManager lootpredicatemanager = p_229763_0_.getSource().getServer().getPredicateManager();
      return ISuggestionProvider.suggestResource(lootpredicatemanager.getKeys(), p_229763_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_198378_0_) {
      LiteralCommandNode<CommandSource> literalcommandnode = p_198378_0_.register(Commands.literal("execute").requires((p_198387_0_) -> {
         return p_198387_0_.hasPermission(2);
      }));
      p_198378_0_.register(Commands.literal("execute").requires((p_229766_0_) -> {
         return p_229766_0_.hasPermission(2);
      }).then(Commands.literal("run").redirect(p_198378_0_.getRoot())).then(addConditionals(literalcommandnode, Commands.literal("if"), true)).then(addConditionals(literalcommandnode, Commands.literal("unless"), false)).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_198384_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getOptionalEntities(p_198384_0_, "targets")) {
            list.add(p_198384_0_.getSource().withEntity(entity));
         }

         return list;
      }))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_229809_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getOptionalEntities(p_229809_0_, "targets")) {
            list.add(p_229809_0_.getSource().withLevel((ServerWorld)entity.level).withPosition(entity.position()).withRotation(entity.getRotationVector()));
         }

         return list;
      }))).then(Commands.literal("store").then(wrapStores(literalcommandnode, Commands.literal("result"), true)).then(wrapStores(literalcommandnode, Commands.literal("success"), false))).then(Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (p_229808_0_) -> {
         return p_229808_0_.getSource().withPosition(Vec3Argument.getVec3(p_229808_0_, "pos")).withAnchor(EntityAnchorArgument.Type.FEET);
      })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_229807_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getOptionalEntities(p_229807_0_, "targets")) {
            list.add(p_229807_0_.getSource().withPosition(entity.position()));
         }

         return list;
      })))).then(Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect(literalcommandnode, (p_229806_0_) -> {
         return p_229806_0_.getSource().withRotation(RotationArgument.getRotation(p_229806_0_, "rot").getRotation(p_229806_0_.getSource()));
      })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_201083_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getOptionalEntities(p_201083_0_, "targets")) {
            list.add(p_201083_0_.getSource().withRotation(entity.getRotationVector()));
         }

         return list;
      })))).then(Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("anchor", EntityAnchorArgument.anchor()).fork(literalcommandnode, (p_229805_0_) -> {
         List<CommandSource> list = Lists.newArrayList();
         EntityAnchorArgument.Type entityanchorargument$type = EntityAnchorArgument.getAnchor(p_229805_0_, "anchor");

         for(Entity entity : EntityArgument.getOptionalEntities(p_229805_0_, "targets")) {
            list.add(p_229805_0_.getSource().facing(entity, entityanchorargument$type));
         }

         return list;
      })))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (p_198381_0_) -> {
         return p_198381_0_.getSource().facing(Vec3Argument.getVec3(p_198381_0_, "pos"));
      }))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect(literalcommandnode, (p_201091_0_) -> {
         return p_201091_0_.getSource().withPosition(p_201091_0_.getSource().getPosition().align(SwizzleArgument.getSwizzle(p_201091_0_, "axes")));
      }))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.anchor()).redirect(literalcommandnode, (p_201089_0_) -> {
         return p_201089_0_.getSource().withAnchor(EntityAnchorArgument.getAnchor(p_201089_0_, "anchor"));
      }))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionArgument.dimension()).redirect(literalcommandnode, (p_229804_0_) -> {
         return p_229804_0_.getSource().withLevel(DimensionArgument.getDimension(p_229804_0_, "dimension"));
      }))));
   }

   private static ArgumentBuilder<CommandSource, ?> wrapStores(LiteralCommandNode<CommandSource> p_198392_0_, LiteralArgumentBuilder<CommandSource> p_198392_1_, boolean p_198392_2_) {
      p_198392_1_.then(Commands.literal("score").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).redirect(p_198392_0_, (p_201468_1_) -> {
         return storeValue(p_201468_1_.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(p_201468_1_, "targets"), ObjectiveArgument.getObjective(p_201468_1_, "objective"), p_198392_2_);
      }))));
      p_198392_1_.then(Commands.literal("bossbar").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(BossBarCommand.SUGGEST_BOSS_BAR).then(Commands.literal("value").redirect(p_198392_0_, (p_201457_1_) -> {
         return storeValue(p_201457_1_.getSource(), BossBarCommand.getBossBar(p_201457_1_), true, p_198392_2_);
      })).then(Commands.literal("max").redirect(p_198392_0_, (p_229795_1_) -> {
         return storeValue(p_229795_1_.getSource(), BossBarCommand.getBossBar(p_229795_1_), false, p_198392_2_);
      }))));

      for(DataCommand.IDataProvider datacommand$idataprovider : DataCommand.TARGET_PROVIDERS) {
         datacommand$idataprovider.wrap(p_198392_1_, (p_229765_3_) -> {
            return p_229765_3_.then(Commands.argument("path", NBTPathArgument.nbtPath()).then(Commands.literal("int").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229801_2_) -> {
               return storeData(p_229801_2_.getSource(), datacommand$idataprovider.access(p_229801_2_), NBTPathArgument.getPath(p_229801_2_, "path"), (p_229800_1_) -> {
                  return IntNBT.valueOf((int)((double)p_229800_1_ * DoubleArgumentType.getDouble(p_229801_2_, "scale")));
               }, p_198392_2_);
            }))).then(Commands.literal("float").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229798_2_) -> {
               return storeData(p_229798_2_.getSource(), datacommand$idataprovider.access(p_229798_2_), NBTPathArgument.getPath(p_229798_2_, "path"), (p_229797_1_) -> {
                  return FloatNBT.valueOf((float)((double)p_229797_1_ * DoubleArgumentType.getDouble(p_229798_2_, "scale")));
               }, p_198392_2_);
            }))).then(Commands.literal("short").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229794_2_) -> {
               return storeData(p_229794_2_.getSource(), datacommand$idataprovider.access(p_229794_2_), NBTPathArgument.getPath(p_229794_2_, "path"), (p_229792_1_) -> {
                  return ShortNBT.valueOf((short)((int)((double)p_229792_1_ * DoubleArgumentType.getDouble(p_229794_2_, "scale"))));
               }, p_198392_2_);
            }))).then(Commands.literal("long").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229790_2_) -> {
               return storeData(p_229790_2_.getSource(), datacommand$idataprovider.access(p_229790_2_), NBTPathArgument.getPath(p_229790_2_, "path"), (p_229788_1_) -> {
                  return LongNBT.valueOf((long)((double)p_229788_1_ * DoubleArgumentType.getDouble(p_229790_2_, "scale")));
               }, p_198392_2_);
            }))).then(Commands.literal("double").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229784_2_) -> {
               return storeData(p_229784_2_.getSource(), datacommand$idataprovider.access(p_229784_2_), NBTPathArgument.getPath(p_229784_2_, "path"), (p_229781_1_) -> {
                  return DoubleNBT.valueOf((double)p_229781_1_ * DoubleArgumentType.getDouble(p_229784_2_, "scale"));
               }, p_198392_2_);
            }))).then(Commands.literal("byte").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229774_2_) -> {
               return storeData(p_229774_2_.getSource(), datacommand$idataprovider.access(p_229774_2_), NBTPathArgument.getPath(p_229774_2_, "path"), (p_229762_1_) -> {
                  return ByteNBT.valueOf((byte)((int)((double)p_229762_1_ * DoubleArgumentType.getDouble(p_229774_2_, "scale"))));
               }, p_198392_2_);
            }))));
         });
      }

      return p_198392_1_;
   }

   private static CommandSource storeValue(CommandSource p_209930_0_, Collection<String> p_209930_1_, ScoreObjective p_209930_2_, boolean p_209930_3_) {
      Scoreboard scoreboard = p_209930_0_.getServer().getScoreboard();
      return p_209930_0_.withCallback((p_229769_4_, p_229769_5_, p_229769_6_) -> {
         for(String s : p_209930_1_) {
            Score score = scoreboard.getOrCreatePlayerScore(s, p_209930_2_);
            int i = p_209930_3_ ? p_229769_6_ : (p_229769_5_ ? 1 : 0);
            score.setScore(i);
         }

      }, CALLBACK_CHAINER);
   }

   private static CommandSource storeValue(CommandSource p_209952_0_, CustomServerBossInfo p_209952_1_, boolean p_209952_2_, boolean p_209952_3_) {
      return p_209952_0_.withCallback((p_229779_3_, p_229779_4_, p_229779_5_) -> {
         int i = p_209952_3_ ? p_229779_5_ : (p_229779_4_ ? 1 : 0);
         if (p_209952_2_) {
            p_209952_1_.setValue(i);
         } else {
            p_209952_1_.setMax(i);
         }

      }, CALLBACK_CHAINER);
   }

   private static CommandSource storeData(CommandSource p_198397_0_, IDataAccessor p_198397_1_, NBTPathArgument.NBTPath p_198397_2_, IntFunction<INBT> p_198397_3_, boolean p_198397_4_) {
      return p_198397_0_.withCallback((p_229772_4_, p_229772_5_, p_229772_6_) -> {
         try {
            CompoundNBT compoundnbt = p_198397_1_.getData();
            int i = p_198397_4_ ? p_229772_6_ : (p_229772_5_ ? 1 : 0);
            p_198397_2_.set(compoundnbt, () -> {
               return p_198397_3_.apply(i);
            });
            p_198397_1_.setData(compoundnbt);
         } catch (CommandSyntaxException commandsyntaxexception) {
         }

      }, CALLBACK_CHAINER);
   }

   private static ArgumentBuilder<CommandSource, ?> addConditionals(CommandNode<CommandSource> p_198394_0_, LiteralArgumentBuilder<CommandSource> p_198394_1_, boolean p_198394_2_) {
      p_198394_1_.then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(addConditional(p_198394_0_, Commands.argument("block", BlockPredicateArgument.blockPredicate()), p_198394_2_, (p_210438_0_) -> {
         return BlockPredicateArgument.getBlockPredicate(p_210438_0_, "block").test(new CachedBlockInfo(p_210438_0_.getSource().getLevel(), BlockPosArgument.getLoadedBlockPos(p_210438_0_, "pos"), true));
      })))).then(Commands.literal("score").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.literal("=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_229803_0_) -> {
         return checkScore(p_229803_0_, Integer::equals);
      })))).then(Commands.literal("<").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_229802_0_) -> {
         return checkScore(p_229802_0_, (p_229793_0_, p_229793_1_) -> {
            return p_229793_0_ < p_229793_1_;
         });
      })))).then(Commands.literal("<=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_229799_0_) -> {
         return checkScore(p_229799_0_, (p_229789_0_, p_229789_1_) -> {
            return p_229789_0_ <= p_229789_1_;
         });
      })))).then(Commands.literal(">").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_229796_0_) -> {
         return checkScore(p_229796_0_, (p_229782_0_, p_229782_1_) -> {
            return p_229782_0_ > p_229782_1_;
         });
      })))).then(Commands.literal(">=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_201088_0_) -> {
         return checkScore(p_201088_0_, (p_229768_0_, p_229768_1_) -> {
            return p_229768_0_ >= p_229768_1_;
         });
      })))).then(Commands.literal("matches").then(addConditional(p_198394_0_, Commands.argument("range", IRangeArgument.intRange()), p_198394_2_, (p_229787_0_) -> {
         return checkScore(p_229787_0_, IRangeArgument.IntRange.getRange(p_229787_0_, "range"));
      })))))).then(Commands.literal("blocks").then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(Commands.argument("destination", BlockPosArgument.blockPos()).then(addIfBlocksConditional(p_198394_0_, Commands.literal("all"), p_198394_2_, false)).then(addIfBlocksConditional(p_198394_0_, Commands.literal("masked"), p_198394_2_, true)))))).then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).fork(p_198394_0_, (p_229791_1_) -> {
         return expect(p_229791_1_, p_198394_2_, !EntityArgument.getOptionalEntities(p_229791_1_, "entities").isEmpty());
      }).executes(createNumericConditionalHandler(p_198394_2_, (p_229780_0_) -> {
         return EntityArgument.getOptionalEntities(p_229780_0_, "entities").size();
      })))).then(Commands.literal("predicate").then(addConditional(p_198394_0_, Commands.argument("predicate", ResourceLocationArgument.id()).suggests(SUGGEST_PREDICATE), p_198394_2_, (p_229761_0_) -> {
         return checkCustomPredicate(p_229761_0_.getSource(), ResourceLocationArgument.getPredicate(p_229761_0_, "predicate"));
      })));

      for(DataCommand.IDataProvider datacommand$idataprovider : DataCommand.SOURCE_PROVIDERS) {
         p_198394_1_.then(datacommand$idataprovider.wrap(Commands.literal("data"), (p_229764_3_) -> {
            return p_229764_3_.then(Commands.argument("path", NBTPathArgument.nbtPath()).fork(p_198394_0_, (p_229777_2_) -> {
               return expect(p_229777_2_, p_198394_2_, checkMatchingData(datacommand$idataprovider.access(p_229777_2_), NBTPathArgument.getPath(p_229777_2_, "path")) > 0);
            }).executes(createNumericConditionalHandler(p_198394_2_, (p_229773_1_) -> {
               return checkMatchingData(datacommand$idataprovider.access(p_229773_1_), NBTPathArgument.getPath(p_229773_1_, "path"));
            })));
         }));
      }

      return p_198394_1_;
   }

   private static Command<CommandSource> createNumericConditionalHandler(boolean p_218834_0_, ExecuteCommand.INumericTest p_218834_1_) {
      return p_218834_0_ ? (p_229783_1_) -> {
         int i = p_218834_1_.test(p_229783_1_);
         if (i > 0) {
            p_229783_1_.getSource().sendSuccess(new TranslationTextComponent("commands.execute.conditional.pass_count", i), false);
            return i;
         } else {
            throw ERROR_CONDITIONAL_FAILED.create();
         }
      } : (p_229771_1_) -> {
         int i = p_218834_1_.test(p_229771_1_);
         if (i == 0) {
            p_229771_1_.getSource().sendSuccess(new TranslationTextComponent("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw ERROR_CONDITIONAL_FAILED_COUNT.create(i);
         }
      };
   }

   private static int checkMatchingData(IDataAccessor p_218831_0_, NBTPathArgument.NBTPath p_218831_1_) throws CommandSyntaxException {
      return p_218831_1_.countMatching(p_218831_0_.getData());
   }

   private static boolean checkScore(CommandContext<CommandSource> p_198371_0_, BiPredicate<Integer, Integer> p_198371_1_) throws CommandSyntaxException {
      String s = ScoreHolderArgument.getName(p_198371_0_, "target");
      ScoreObjective scoreobjective = ObjectiveArgument.getObjective(p_198371_0_, "targetObjective");
      String s1 = ScoreHolderArgument.getName(p_198371_0_, "source");
      ScoreObjective scoreobjective1 = ObjectiveArgument.getObjective(p_198371_0_, "sourceObjective");
      Scoreboard scoreboard = p_198371_0_.getSource().getServer().getScoreboard();
      if (scoreboard.hasPlayerScore(s, scoreobjective) && scoreboard.hasPlayerScore(s1, scoreobjective1)) {
         Score score = scoreboard.getOrCreatePlayerScore(s, scoreobjective);
         Score score1 = scoreboard.getOrCreatePlayerScore(s1, scoreobjective1);
         return p_198371_1_.test(score.getScore(), score1.getScore());
      } else {
         return false;
      }
   }

   private static boolean checkScore(CommandContext<CommandSource> p_201115_0_, MinMaxBounds.IntBound p_201115_1_) throws CommandSyntaxException {
      String s = ScoreHolderArgument.getName(p_201115_0_, "target");
      ScoreObjective scoreobjective = ObjectiveArgument.getObjective(p_201115_0_, "targetObjective");
      Scoreboard scoreboard = p_201115_0_.getSource().getServer().getScoreboard();
      return !scoreboard.hasPlayerScore(s, scoreobjective) ? false : p_201115_1_.matches(scoreboard.getOrCreatePlayerScore(s, scoreobjective).getScore());
   }

   private static boolean checkCustomPredicate(CommandSource p_229767_0_, ILootCondition p_229767_1_) {
      ServerWorld serverworld = p_229767_0_.getLevel();
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(serverworld)).withParameter(LootParameters.ORIGIN, p_229767_0_.getPosition()).withOptionalParameter(LootParameters.THIS_ENTITY, p_229767_0_.getEntity());
      return p_229767_1_.test(lootcontext$builder.create(LootParameterSets.COMMAND));
   }

   private static Collection<CommandSource> expect(CommandContext<CommandSource> p_198411_0_, boolean p_198411_1_, boolean p_198411_2_) {
      return (Collection<CommandSource>)(p_198411_2_ == p_198411_1_ ? Collections.singleton(p_198411_0_.getSource()) : Collections.emptyList());
   }

   private static ArgumentBuilder<CommandSource, ?> addConditional(CommandNode<CommandSource> p_210415_0_, ArgumentBuilder<CommandSource, ?> p_210415_1_, boolean p_210415_2_, ExecuteCommand.IBooleanTest p_210415_3_) {
      return p_210415_1_.fork(p_210415_0_, (p_229786_2_) -> {
         return expect(p_229786_2_, p_210415_2_, p_210415_3_.test(p_229786_2_));
      }).executes((p_229776_2_) -> {
         if (p_210415_2_ == p_210415_3_.test(p_229776_2_)) {
            p_229776_2_.getSource().sendSuccess(new TranslationTextComponent("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw ERROR_CONDITIONAL_FAILED.create();
         }
      });
   }

   private static ArgumentBuilder<CommandSource, ?> addIfBlocksConditional(CommandNode<CommandSource> p_212178_0_, ArgumentBuilder<CommandSource, ?> p_212178_1_, boolean p_212178_2_, boolean p_212178_3_) {
      return p_212178_1_.fork(p_212178_0_, (p_229778_2_) -> {
         return expect(p_229778_2_, p_212178_2_, checkRegions(p_229778_2_, p_212178_3_).isPresent());
      }).executes(p_212178_2_ ? (p_229785_1_) -> {
         return checkIfRegions(p_229785_1_, p_212178_3_);
      } : (p_229775_1_) -> {
         return checkUnlessRegions(p_229775_1_, p_212178_3_);
      });
   }

   private static int checkIfRegions(CommandContext<CommandSource> p_212175_0_, boolean p_212175_1_) throws CommandSyntaxException {
      OptionalInt optionalint = checkRegions(p_212175_0_, p_212175_1_);
      if (optionalint.isPresent()) {
         p_212175_0_.getSource().sendSuccess(new TranslationTextComponent("commands.execute.conditional.pass_count", optionalint.getAsInt()), false);
         return optionalint.getAsInt();
      } else {
         throw ERROR_CONDITIONAL_FAILED.create();
      }
   }

   private static int checkUnlessRegions(CommandContext<CommandSource> p_212173_0_, boolean p_212173_1_) throws CommandSyntaxException {
      OptionalInt optionalint = checkRegions(p_212173_0_, p_212173_1_);
      if (optionalint.isPresent()) {
         throw ERROR_CONDITIONAL_FAILED_COUNT.create(optionalint.getAsInt());
      } else {
         p_212173_0_.getSource().sendSuccess(new TranslationTextComponent("commands.execute.conditional.pass"), false);
         return 1;
      }
   }

   private static OptionalInt checkRegions(CommandContext<CommandSource> p_212169_0_, boolean p_212169_1_) throws CommandSyntaxException {
      return checkRegions(p_212169_0_.getSource().getLevel(), BlockPosArgument.getLoadedBlockPos(p_212169_0_, "start"), BlockPosArgument.getLoadedBlockPos(p_212169_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_212169_0_, "destination"), p_212169_1_);
   }

   private static OptionalInt checkRegions(ServerWorld p_198395_0_, BlockPos p_198395_1_, BlockPos p_198395_2_, BlockPos p_198395_3_, boolean p_198395_4_) throws CommandSyntaxException {
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(p_198395_1_, p_198395_2_);
      MutableBoundingBox mutableboundingbox1 = new MutableBoundingBox(p_198395_3_, p_198395_3_.offset(mutableboundingbox.getLength()));
      BlockPos blockpos = new BlockPos(mutableboundingbox1.x0 - mutableboundingbox.x0, mutableboundingbox1.y0 - mutableboundingbox.y0, mutableboundingbox1.z0 - mutableboundingbox.z0);
      int i = mutableboundingbox.getXSpan() * mutableboundingbox.getYSpan() * mutableboundingbox.getZSpan();
      if (i > 32768) {
         throw ERROR_AREA_TOO_LARGE.create(32768, i);
      } else {
         int j = 0;

         for(int k = mutableboundingbox.z0; k <= mutableboundingbox.z1; ++k) {
            for(int l = mutableboundingbox.y0; l <= mutableboundingbox.y1; ++l) {
               for(int i1 = mutableboundingbox.x0; i1 <= mutableboundingbox.x1; ++i1) {
                  BlockPos blockpos1 = new BlockPos(i1, l, k);
                  BlockPos blockpos2 = blockpos1.offset(blockpos);
                  BlockState blockstate = p_198395_0_.getBlockState(blockpos1);
                  if (!p_198395_4_ || !blockstate.is(Blocks.AIR)) {
                     if (blockstate != p_198395_0_.getBlockState(blockpos2)) {
                        return OptionalInt.empty();
                     }

                     TileEntity tileentity = p_198395_0_.getBlockEntity(blockpos1);
                     TileEntity tileentity1 = p_198395_0_.getBlockEntity(blockpos2);
                     if (tileentity != null) {
                        if (tileentity1 == null) {
                           return OptionalInt.empty();
                        }

                        CompoundNBT compoundnbt = tileentity.save(new CompoundNBT());
                        compoundnbt.remove("x");
                        compoundnbt.remove("y");
                        compoundnbt.remove("z");
                        CompoundNBT compoundnbt1 = tileentity1.save(new CompoundNBT());
                        compoundnbt1.remove("x");
                        compoundnbt1.remove("y");
                        compoundnbt1.remove("z");
                        if (!compoundnbt.equals(compoundnbt1)) {
                           return OptionalInt.empty();
                        }
                     }

                     ++j;
                  }
               }
            }
         }

         return OptionalInt.of(j);
      }
   }

   @FunctionalInterface
   interface IBooleanTest {
      boolean test(CommandContext<CommandSource> p_test_1_) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface INumericTest {
      int test(CommandContext<CommandSource> p_test_1_) throws CommandSyntaxException;
   }
}
