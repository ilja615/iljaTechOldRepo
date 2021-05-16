package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class AdvancementCommand {
   private static final SuggestionProvider<CommandSource> SUGGEST_ADVANCEMENTS = (p_198206_0_, p_198206_1_) -> {
      Collection<Advancement> collection = p_198206_0_.getSource().getServer().getAdvancements().getAllAdvancements();
      return ISuggestionProvider.suggestResource(collection.stream().map(Advancement::getId), p_198206_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_198199_0_) {
      p_198199_0_.register(Commands.literal("advancement").requires((p_198205_0_) -> {
         return p_198205_0_.hasPermission(2);
      }).then(Commands.literal("grant").then(Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198202_0_) -> {
         return perform(p_198202_0_.getSource(), EntityArgument.getPlayers(p_198202_0_, "targets"), AdvancementCommand.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_198202_0_, "advancement"), AdvancementCommand.Mode.ONLY));
      }).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_198209_0_, p_198209_1_) -> {
         return ISuggestionProvider.suggest(ResourceLocationArgument.getAdvancement(p_198209_0_, "advancement").getCriteria().keySet(), p_198209_1_);
      }).executes((p_198212_0_) -> {
         return performCriterion(p_198212_0_.getSource(), EntityArgument.getPlayers(p_198212_0_, "targets"), AdvancementCommand.Action.GRANT, ResourceLocationArgument.getAdvancement(p_198212_0_, "advancement"), StringArgumentType.getString(p_198212_0_, "criterion"));
      })))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198215_0_) -> {
         return perform(p_198215_0_.getSource(), EntityArgument.getPlayers(p_198215_0_, "targets"), AdvancementCommand.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_198215_0_, "advancement"), AdvancementCommand.Mode.FROM));
      }))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198204_0_) -> {
         return perform(p_198204_0_.getSource(), EntityArgument.getPlayers(p_198204_0_, "targets"), AdvancementCommand.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_198204_0_, "advancement"), AdvancementCommand.Mode.UNTIL));
      }))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198211_0_) -> {
         return perform(p_198211_0_.getSource(), EntityArgument.getPlayers(p_198211_0_, "targets"), AdvancementCommand.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(p_198211_0_, "advancement"), AdvancementCommand.Mode.THROUGH));
      }))).then(Commands.literal("everything").executes((p_198217_0_) -> {
         return perform(p_198217_0_.getSource(), EntityArgument.getPlayers(p_198217_0_, "targets"), AdvancementCommand.Action.GRANT, p_198217_0_.getSource().getServer().getAdvancements().getAllAdvancements());
      })))).then(Commands.literal("revoke").then(Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198198_0_) -> {
         return perform(p_198198_0_.getSource(), EntityArgument.getPlayers(p_198198_0_, "targets"), AdvancementCommand.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_198198_0_, "advancement"), AdvancementCommand.Mode.ONLY));
      }).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_198210_0_, p_198210_1_) -> {
         return ISuggestionProvider.suggest(ResourceLocationArgument.getAdvancement(p_198210_0_, "advancement").getCriteria().keySet(), p_198210_1_);
      }).executes((p_198200_0_) -> {
         return performCriterion(p_198200_0_.getSource(), EntityArgument.getPlayers(p_198200_0_, "targets"), AdvancementCommand.Action.REVOKE, ResourceLocationArgument.getAdvancement(p_198200_0_, "advancement"), StringArgumentType.getString(p_198200_0_, "criterion"));
      })))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198208_0_) -> {
         return perform(p_198208_0_.getSource(), EntityArgument.getPlayers(p_198208_0_, "targets"), AdvancementCommand.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_198208_0_, "advancement"), AdvancementCommand.Mode.FROM));
      }))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198201_0_) -> {
         return perform(p_198201_0_.getSource(), EntityArgument.getPlayers(p_198201_0_, "targets"), AdvancementCommand.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_198201_0_, "advancement"), AdvancementCommand.Mode.UNTIL));
      }))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((p_198197_0_) -> {
         return perform(p_198197_0_.getSource(), EntityArgument.getPlayers(p_198197_0_, "targets"), AdvancementCommand.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(p_198197_0_, "advancement"), AdvancementCommand.Mode.THROUGH));
      }))).then(Commands.literal("everything").executes((p_198213_0_) -> {
         return perform(p_198213_0_.getSource(), EntityArgument.getPlayers(p_198213_0_, "targets"), AdvancementCommand.Action.REVOKE, p_198213_0_.getSource().getServer().getAdvancements().getAllAdvancements());
      })))));
   }

   private static int perform(CommandSource p_198214_0_, Collection<ServerPlayerEntity> p_198214_1_, AdvancementCommand.Action p_198214_2_, Collection<Advancement> p_198214_3_) {
      int i = 0;

      for(ServerPlayerEntity serverplayerentity : p_198214_1_) {
         i += p_198214_2_.perform(serverplayerentity, p_198214_3_);
      }

      if (i == 0) {
         if (p_198214_3_.size() == 1) {
            if (p_198214_1_.size() == 1) {
               throw new CommandException(new TranslationTextComponent(p_198214_2_.getKey() + ".one.to.one.failure", p_198214_3_.iterator().next().getChatComponent(), p_198214_1_.iterator().next().getDisplayName()));
            } else {
               throw new CommandException(new TranslationTextComponent(p_198214_2_.getKey() + ".one.to.many.failure", p_198214_3_.iterator().next().getChatComponent(), p_198214_1_.size()));
            }
         } else if (p_198214_1_.size() == 1) {
            throw new CommandException(new TranslationTextComponent(p_198214_2_.getKey() + ".many.to.one.failure", p_198214_3_.size(), p_198214_1_.iterator().next().getDisplayName()));
         } else {
            throw new CommandException(new TranslationTextComponent(p_198214_2_.getKey() + ".many.to.many.failure", p_198214_3_.size(), p_198214_1_.size()));
         }
      } else {
         if (p_198214_3_.size() == 1) {
            if (p_198214_1_.size() == 1) {
               p_198214_0_.sendSuccess(new TranslationTextComponent(p_198214_2_.getKey() + ".one.to.one.success", p_198214_3_.iterator().next().getChatComponent(), p_198214_1_.iterator().next().getDisplayName()), true);
            } else {
               p_198214_0_.sendSuccess(new TranslationTextComponent(p_198214_2_.getKey() + ".one.to.many.success", p_198214_3_.iterator().next().getChatComponent(), p_198214_1_.size()), true);
            }
         } else if (p_198214_1_.size() == 1) {
            p_198214_0_.sendSuccess(new TranslationTextComponent(p_198214_2_.getKey() + ".many.to.one.success", p_198214_3_.size(), p_198214_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198214_0_.sendSuccess(new TranslationTextComponent(p_198214_2_.getKey() + ".many.to.many.success", p_198214_3_.size(), p_198214_1_.size()), true);
         }

         return i;
      }
   }

   private static int performCriterion(CommandSource p_198203_0_, Collection<ServerPlayerEntity> p_198203_1_, AdvancementCommand.Action p_198203_2_, Advancement p_198203_3_, String p_198203_4_) {
      int i = 0;
      if (!p_198203_3_.getCriteria().containsKey(p_198203_4_)) {
         throw new CommandException(new TranslationTextComponent("commands.advancement.criterionNotFound", p_198203_3_.getChatComponent(), p_198203_4_));
      } else {
         for(ServerPlayerEntity serverplayerentity : p_198203_1_) {
            if (p_198203_2_.performCriterion(serverplayerentity, p_198203_3_, p_198203_4_)) {
               ++i;
            }
         }

         if (i == 0) {
            if (p_198203_1_.size() == 1) {
               throw new CommandException(new TranslationTextComponent(p_198203_2_.getKey() + ".criterion.to.one.failure", p_198203_4_, p_198203_3_.getChatComponent(), p_198203_1_.iterator().next().getDisplayName()));
            } else {
               throw new CommandException(new TranslationTextComponent(p_198203_2_.getKey() + ".criterion.to.many.failure", p_198203_4_, p_198203_3_.getChatComponent(), p_198203_1_.size()));
            }
         } else {
            if (p_198203_1_.size() == 1) {
               p_198203_0_.sendSuccess(new TranslationTextComponent(p_198203_2_.getKey() + ".criterion.to.one.success", p_198203_4_, p_198203_3_.getChatComponent(), p_198203_1_.iterator().next().getDisplayName()), true);
            } else {
               p_198203_0_.sendSuccess(new TranslationTextComponent(p_198203_2_.getKey() + ".criterion.to.many.success", p_198203_4_, p_198203_3_.getChatComponent(), p_198203_1_.size()), true);
            }

            return i;
         }
      }
   }

   private static List<Advancement> getAdvancements(Advancement p_198216_0_, AdvancementCommand.Mode p_198216_1_) {
      List<Advancement> list = Lists.newArrayList();
      if (p_198216_1_.parents) {
         for(Advancement advancement = p_198216_0_.getParent(); advancement != null; advancement = advancement.getParent()) {
            list.add(advancement);
         }
      }

      list.add(p_198216_0_);
      if (p_198216_1_.children) {
         addChildren(p_198216_0_, list);
      }

      return list;
   }

   private static void addChildren(Advancement p_198207_0_, List<Advancement> p_198207_1_) {
      for(Advancement advancement : p_198207_0_.getChildren()) {
         p_198207_1_.add(advancement);
         addChildren(advancement, p_198207_1_);
      }

   }

   static enum Action {
      GRANT("grant") {
         protected boolean perform(ServerPlayerEntity p_198179_1_, Advancement p_198179_2_) {
            AdvancementProgress advancementprogress = p_198179_1_.getAdvancements().getOrStartProgress(p_198179_2_);
            if (advancementprogress.isDone()) {
               return false;
            } else {
               for(String s : advancementprogress.getRemainingCriteria()) {
                  p_198179_1_.getAdvancements().award(p_198179_2_, s);
               }

               return true;
            }
         }

         protected boolean performCriterion(ServerPlayerEntity p_198182_1_, Advancement p_198182_2_, String p_198182_3_) {
            return p_198182_1_.getAdvancements().award(p_198182_2_, p_198182_3_);
         }
      },
      REVOKE("revoke") {
         protected boolean perform(ServerPlayerEntity p_198179_1_, Advancement p_198179_2_) {
            AdvancementProgress advancementprogress = p_198179_1_.getAdvancements().getOrStartProgress(p_198179_2_);
            if (!advancementprogress.hasProgress()) {
               return false;
            } else {
               for(String s : advancementprogress.getCompletedCriteria()) {
                  p_198179_1_.getAdvancements().revoke(p_198179_2_, s);
               }

               return true;
            }
         }

         protected boolean performCriterion(ServerPlayerEntity p_198182_1_, Advancement p_198182_2_, String p_198182_3_) {
            return p_198182_1_.getAdvancements().revoke(p_198182_2_, p_198182_3_);
         }
      };

      private final String key;

      private Action(String p_i48092_3_) {
         this.key = "commands.advancement." + p_i48092_3_;
      }

      public int perform(ServerPlayerEntity p_198180_1_, Iterable<Advancement> p_198180_2_) {
         int i = 0;

         for(Advancement advancement : p_198180_2_) {
            if (this.perform(p_198180_1_, advancement)) {
               ++i;
            }
         }

         return i;
      }

      protected abstract boolean perform(ServerPlayerEntity p_198179_1_, Advancement p_198179_2_);

      protected abstract boolean performCriterion(ServerPlayerEntity p_198182_1_, Advancement p_198182_2_, String p_198182_3_);

      protected String getKey() {
         return this.key;
      }
   }

   static enum Mode {
      ONLY(false, false),
      THROUGH(true, true),
      FROM(false, true),
      UNTIL(true, false),
      EVERYTHING(true, true);

      private final boolean parents;
      private final boolean children;

      private Mode(boolean p_i48091_3_, boolean p_i48091_4_) {
         this.parents = p_i48091_3_;
         this.children = p_i48091_4_;
      }
   }
}
