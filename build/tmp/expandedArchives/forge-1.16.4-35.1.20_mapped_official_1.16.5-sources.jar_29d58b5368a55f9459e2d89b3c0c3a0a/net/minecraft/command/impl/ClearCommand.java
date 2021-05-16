package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemPredicateArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class ClearCommand {
   private static final DynamicCommandExceptionType ERROR_SINGLE = new DynamicCommandExceptionType((p_208785_0_) -> {
      return new TranslationTextComponent("clear.failed.single", p_208785_0_);
   });
   private static final DynamicCommandExceptionType ERROR_MULTIPLE = new DynamicCommandExceptionType((p_208787_0_) -> {
      return new TranslationTextComponent("clear.failed.multiple", p_208787_0_);
   });

   public static void register(CommandDispatcher<CommandSource> p_198243_0_) {
      p_198243_0_.register(Commands.literal("clear").requires((p_198247_0_) -> {
         return p_198247_0_.hasPermission(2);
      }).executes((p_198241_0_) -> {
         return clearInventory(p_198241_0_.getSource(), Collections.singleton(p_198241_0_.getSource().getPlayerOrException()), (p_198248_0_) -> {
            return true;
         }, -1);
      }).then(Commands.argument("targets", EntityArgument.players()).executes((p_198245_0_) -> {
         return clearInventory(p_198245_0_.getSource(), EntityArgument.getPlayers(p_198245_0_, "targets"), (p_198242_0_) -> {
            return true;
         }, -1);
      }).then(Commands.argument("item", ItemPredicateArgument.itemPredicate()).executes((p_198240_0_) -> {
         return clearInventory(p_198240_0_.getSource(), EntityArgument.getPlayers(p_198240_0_, "targets"), ItemPredicateArgument.getItemPredicate(p_198240_0_, "item"), -1);
      }).then(Commands.argument("maxCount", IntegerArgumentType.integer(0)).executes((p_198246_0_) -> {
         return clearInventory(p_198246_0_.getSource(), EntityArgument.getPlayers(p_198246_0_, "targets"), ItemPredicateArgument.getItemPredicate(p_198246_0_, "item"), IntegerArgumentType.getInteger(p_198246_0_, "maxCount"));
      })))));
   }

   private static int clearInventory(CommandSource p_198244_0_, Collection<ServerPlayerEntity> p_198244_1_, Predicate<ItemStack> p_198244_2_, int p_198244_3_) throws CommandSyntaxException {
      int i = 0;

      for(ServerPlayerEntity serverplayerentity : p_198244_1_) {
         i += serverplayerentity.inventory.clearOrCountMatchingItems(p_198244_2_, p_198244_3_, serverplayerentity.inventoryMenu.getCraftSlots());
         serverplayerentity.containerMenu.broadcastChanges();
         serverplayerentity.inventoryMenu.slotsChanged(serverplayerentity.inventory);
         serverplayerentity.broadcastCarriedItem();
      }

      if (i == 0) {
         if (p_198244_1_.size() == 1) {
            throw ERROR_SINGLE.create(p_198244_1_.iterator().next().getName());
         } else {
            throw ERROR_MULTIPLE.create(p_198244_1_.size());
         }
      } else {
         if (p_198244_3_ == 0) {
            if (p_198244_1_.size() == 1) {
               p_198244_0_.sendSuccess(new TranslationTextComponent("commands.clear.test.single", i, p_198244_1_.iterator().next().getDisplayName()), true);
            } else {
               p_198244_0_.sendSuccess(new TranslationTextComponent("commands.clear.test.multiple", i, p_198244_1_.size()), true);
            }
         } else if (p_198244_1_.size() == 1) {
            p_198244_0_.sendSuccess(new TranslationTextComponent("commands.clear.success.single", i, p_198244_1_.iterator().next().getDisplayName()), true);
         } else {
            p_198244_0_.sendSuccess(new TranslationTextComponent("commands.clear.success.multiple", i, p_198244_1_.size()), true);
         }

         return i;
      }
   }
}
