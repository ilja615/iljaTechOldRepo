package net.minecraft.command.impl;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.PotionArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.TranslationTextComponent;

public class EffectCommand {
   private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.effect.give.failed"));
   private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.effect.clear.everything.failed"));
   private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.effect.clear.specific.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198353_0_) {
      p_198353_0_.register(Commands.literal("effect").requires((p_198359_0_) -> {
         return p_198359_0_.hasPermission(2);
      }).then(Commands.literal("clear").executes((p_198352_0_) -> {
         return clearEffects(p_198352_0_.getSource(), ImmutableList.of(p_198352_0_.getSource().getEntityOrException()));
      }).then(Commands.argument("targets", EntityArgument.entities()).executes((p_198356_0_) -> {
         return clearEffects(p_198356_0_.getSource(), EntityArgument.getEntities(p_198356_0_, "targets"));
      }).then(Commands.argument("effect", PotionArgument.effect()).executes((p_198351_0_) -> {
         return clearEffect(p_198351_0_.getSource(), EntityArgument.getEntities(p_198351_0_, "targets"), PotionArgument.getEffect(p_198351_0_, "effect"));
      })))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("effect", PotionArgument.effect()).executes((p_198357_0_) -> {
         return giveEffect(p_198357_0_.getSource(), EntityArgument.getEntities(p_198357_0_, "targets"), PotionArgument.getEffect(p_198357_0_, "effect"), (Integer)null, 0, true);
      }).then(Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes((p_198350_0_) -> {
         return giveEffect(p_198350_0_.getSource(), EntityArgument.getEntities(p_198350_0_, "targets"), PotionArgument.getEffect(p_198350_0_, "effect"), IntegerArgumentType.getInteger(p_198350_0_, "seconds"), 0, true);
      }).then(Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((p_198358_0_) -> {
         return giveEffect(p_198358_0_.getSource(), EntityArgument.getEntities(p_198358_0_, "targets"), PotionArgument.getEffect(p_198358_0_, "effect"), IntegerArgumentType.getInteger(p_198358_0_, "seconds"), IntegerArgumentType.getInteger(p_198358_0_, "amplifier"), true);
      }).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((p_229759_0_) -> {
         return giveEffect(p_229759_0_.getSource(), EntityArgument.getEntities(p_229759_0_, "targets"), PotionArgument.getEffect(p_229759_0_, "effect"), IntegerArgumentType.getInteger(p_229759_0_, "seconds"), IntegerArgumentType.getInteger(p_229759_0_, "amplifier"), !BoolArgumentType.getBool(p_229759_0_, "hideParticles"));
      }))))))));
   }

   private static int giveEffect(CommandSource p_198360_0_, Collection<? extends Entity> p_198360_1_, Effect p_198360_2_, @Nullable Integer p_198360_3_, int p_198360_4_, boolean p_198360_5_) throws CommandSyntaxException {
      int i = 0;
      int j;
      if (p_198360_3_ != null) {
         if (p_198360_2_.isInstantenous()) {
            j = p_198360_3_;
         } else {
            j = p_198360_3_ * 20;
         }
      } else if (p_198360_2_.isInstantenous()) {
         j = 1;
      } else {
         j = 600;
      }

      for(Entity entity : p_198360_1_) {
         if (entity instanceof LivingEntity) {
            EffectInstance effectinstance = new EffectInstance(p_198360_2_, j, p_198360_4_, false, p_198360_5_);
            if (((LivingEntity)entity).addEffect(effectinstance)) {
               ++i;
            }
         }
      }

      if (i == 0) {
         throw ERROR_GIVE_FAILED.create();
      } else {
         if (p_198360_1_.size() == 1) {
            p_198360_0_.sendSuccess(new TranslationTextComponent("commands.effect.give.success.single", p_198360_2_.getDisplayName(), p_198360_1_.iterator().next().getDisplayName(), j / 20), true);
         } else {
            p_198360_0_.sendSuccess(new TranslationTextComponent("commands.effect.give.success.multiple", p_198360_2_.getDisplayName(), p_198360_1_.size(), j / 20), true);
         }

         return i;
      }
   }

   private static int clearEffects(CommandSource p_198354_0_, Collection<? extends Entity> p_198354_1_) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : p_198354_1_) {
         if (entity instanceof LivingEntity && ((LivingEntity)entity).removeAllEffects()) {
            ++i;
         }
      }

      if (i == 0) {
         throw ERROR_CLEAR_EVERYTHING_FAILED.create();
      } else {
         if (p_198354_1_.size() == 1) {
            p_198354_0_.sendSuccess(new TranslationTextComponent("commands.effect.clear.everything.success.single", p_198354_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198354_0_.sendSuccess(new TranslationTextComponent("commands.effect.clear.everything.success.multiple", p_198354_1_.size()), true);
         }

         return i;
      }
   }

   private static int clearEffect(CommandSource p_198355_0_, Collection<? extends Entity> p_198355_1_, Effect p_198355_2_) throws CommandSyntaxException {
      int i = 0;

      for(Entity entity : p_198355_1_) {
         if (entity instanceof LivingEntity && ((LivingEntity)entity).removeEffect(p_198355_2_)) {
            ++i;
         }
      }

      if (i == 0) {
         throw ERROR_CLEAR_SPECIFIC_FAILED.create();
      } else {
         if (p_198355_1_.size() == 1) {
            p_198355_0_.sendSuccess(new TranslationTextComponent("commands.effect.clear.specific.success.single", p_198355_2_.getDisplayName(), p_198355_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198355_0_.sendSuccess(new TranslationTextComponent("commands.effect.clear.specific.success.multiple", p_198355_2_.getDisplayName(), p_198355_1_.size()), true);
         }

         return i;
      }
   }
}
