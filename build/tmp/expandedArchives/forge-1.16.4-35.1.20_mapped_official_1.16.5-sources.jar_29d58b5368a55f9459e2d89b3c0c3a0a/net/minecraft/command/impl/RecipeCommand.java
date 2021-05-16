package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.text.TranslationTextComponent;

public class RecipeCommand {
   private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.recipe.give.failed"));
   private static final SimpleCommandExceptionType ERROR_TAKE_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.recipe.take.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198589_0_) {
      p_198589_0_.register(Commands.literal("recipe").requires((p_198593_0_) -> {
         return p_198593_0_.hasPermission(2);
      }).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(SuggestionProviders.ALL_RECIPES).executes((p_198588_0_) -> {
         return giveRecipes(p_198588_0_.getSource(), EntityArgument.getPlayers(p_198588_0_, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(p_198588_0_, "recipe")));
      })).then(Commands.literal("*").executes((p_198591_0_) -> {
         return giveRecipes(p_198591_0_.getSource(), EntityArgument.getPlayers(p_198591_0_, "targets"), p_198591_0_.getSource().getServer().getRecipeManager().getRecipes());
      })))).then(Commands.literal("take").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(SuggestionProviders.ALL_RECIPES).executes((p_198587_0_) -> {
         return takeRecipes(p_198587_0_.getSource(), EntityArgument.getPlayers(p_198587_0_, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(p_198587_0_, "recipe")));
      })).then(Commands.literal("*").executes((p_198592_0_) -> {
         return takeRecipes(p_198592_0_.getSource(), EntityArgument.getPlayers(p_198592_0_, "targets"), p_198592_0_.getSource().getServer().getRecipeManager().getRecipes());
      })))));
   }

   private static int giveRecipes(CommandSource p_198594_0_, Collection<ServerPlayerEntity> p_198594_1_, Collection<IRecipe<?>> p_198594_2_) throws CommandSyntaxException {
      int i = 0;

      for(ServerPlayerEntity serverplayerentity : p_198594_1_) {
         i += serverplayerentity.awardRecipes(p_198594_2_);
      }

      if (i == 0) {
         throw ERROR_GIVE_FAILED.create();
      } else {
         if (p_198594_1_.size() == 1) {
            p_198594_0_.sendSuccess(new TranslationTextComponent("commands.recipe.give.success.single", p_198594_2_.size(), p_198594_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198594_0_.sendSuccess(new TranslationTextComponent("commands.recipe.give.success.multiple", p_198594_2_.size(), p_198594_1_.size()), true);
         }

         return i;
      }
   }

   private static int takeRecipes(CommandSource p_198590_0_, Collection<ServerPlayerEntity> p_198590_1_, Collection<IRecipe<?>> p_198590_2_) throws CommandSyntaxException {
      int i = 0;

      for(ServerPlayerEntity serverplayerentity : p_198590_1_) {
         i += serverplayerentity.resetRecipes(p_198590_2_);
      }

      if (i == 0) {
         throw ERROR_TAKE_FAILED.create();
      } else {
         if (p_198590_1_.size() == 1) {
            p_198590_0_.sendSuccess(new TranslationTextComponent("commands.recipe.take.success.single", p_198590_2_.size(), p_198590_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198590_0_.sendSuccess(new TranslationTextComponent("commands.recipe.take.success.multiple", p_198590_2_.size(), p_198590_1_.size()), true);
         }

         return i;
      }
   }
}
