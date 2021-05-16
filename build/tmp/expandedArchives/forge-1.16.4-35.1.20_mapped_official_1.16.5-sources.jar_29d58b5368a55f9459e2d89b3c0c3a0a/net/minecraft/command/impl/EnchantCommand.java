package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EnchantmentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class EnchantCommand {
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((p_208839_0_) -> {
      return new TranslationTextComponent("commands.enchant.failed.entity", p_208839_0_);
   });
   private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((p_208835_0_) -> {
      return new TranslationTextComponent("commands.enchant.failed.itemless", p_208835_0_);
   });
   private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType((p_208837_0_) -> {
      return new TranslationTextComponent("commands.enchant.failed.incompatible", p_208837_0_);
   });
   private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType((p_208840_0_, p_208840_1_) -> {
      return new TranslationTextComponent("commands.enchant.failed.level", p_208840_0_, p_208840_1_);
   });
   private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.enchant.failed"));

   public static void register(CommandDispatcher<CommandSource> p_202649_0_) {
      p_202649_0_.register(Commands.literal("enchant").requires((p_203630_0_) -> {
         return p_203630_0_.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("enchantment", EnchantmentArgument.enchantment()).executes((p_202648_0_) -> {
         return enchant(p_202648_0_.getSource(), EntityArgument.getEntities(p_202648_0_, "targets"), EnchantmentArgument.getEnchantment(p_202648_0_, "enchantment"), 1);
      }).then(Commands.argument("level", IntegerArgumentType.integer(0)).executes((p_202650_0_) -> {
         return enchant(p_202650_0_.getSource(), EntityArgument.getEntities(p_202650_0_, "targets"), EnchantmentArgument.getEnchantment(p_202650_0_, "enchantment"), IntegerArgumentType.getInteger(p_202650_0_, "level"));
      })))));
   }

   private static int enchant(CommandSource p_202651_0_, Collection<? extends Entity> p_202651_1_, Enchantment p_202651_2_, int p_202651_3_) throws CommandSyntaxException {
      if (p_202651_3_ > p_202651_2_.getMaxLevel()) {
         throw ERROR_LEVEL_TOO_HIGH.create(p_202651_3_, p_202651_2_.getMaxLevel());
      } else {
         int i = 0;

         for(Entity entity : p_202651_1_) {
            if (entity instanceof LivingEntity) {
               LivingEntity livingentity = (LivingEntity)entity;
               ItemStack itemstack = livingentity.getMainHandItem();
               if (!itemstack.isEmpty()) {
                  if (p_202651_2_.canEnchant(itemstack) && EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantments(itemstack).keySet(), p_202651_2_)) {
                     itemstack.enchant(p_202651_2_, p_202651_3_);
                     ++i;
                  } else if (p_202651_1_.size() == 1) {
                     throw ERROR_INCOMPATIBLE.create(itemstack.getItem().getName(itemstack).getString());
                  }
               } else if (p_202651_1_.size() == 1) {
                  throw ERROR_NO_ITEM.create(livingentity.getName().getString());
               }
            } else if (p_202651_1_.size() == 1) {
               throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
            }
         }

         if (i == 0) {
            throw ERROR_NOTHING_HAPPENED.create();
         } else {
            if (p_202651_1_.size() == 1) {
               p_202651_0_.sendSuccess(new TranslationTextComponent("commands.enchant.success.single", p_202651_2_.getFullname(p_202651_3_), p_202651_1_.iterator().next().getDisplayName()), true);
            } else {
               p_202651_0_.sendSuccess(new TranslationTextComponent("commands.enchant.success.multiple", p_202651_2_.getFullname(p_202651_3_), p_202651_1_.size()), true);
            }

            return i;
         }
      }
   }
}
