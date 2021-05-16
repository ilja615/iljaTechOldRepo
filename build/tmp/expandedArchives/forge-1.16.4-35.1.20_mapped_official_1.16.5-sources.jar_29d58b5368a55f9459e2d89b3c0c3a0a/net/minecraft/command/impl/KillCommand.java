package net.minecraft.command.impl;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TranslationTextComponent;

public class KillCommand {
   public static void register(CommandDispatcher<CommandSource> p_198518_0_) {
      p_198518_0_.register(Commands.literal("kill").requires((p_198521_0_) -> {
         return p_198521_0_.hasPermission(2);
      }).executes((p_198520_0_) -> {
         return kill(p_198520_0_.getSource(), ImmutableList.of(p_198520_0_.getSource().getEntityOrException()));
      }).then(Commands.argument("targets", EntityArgument.entities()).executes((p_229810_0_) -> {
         return kill(p_229810_0_.getSource(), EntityArgument.getEntities(p_229810_0_, "targets"));
      })));
   }

   private static int kill(CommandSource p_198519_0_, Collection<? extends Entity> p_198519_1_) {
      for(Entity entity : p_198519_1_) {
         entity.kill();
      }

      if (p_198519_1_.size() == 1) {
         p_198519_0_.sendSuccess(new TranslationTextComponent("commands.kill.success.single", p_198519_1_.iterator().next().getDisplayName()), true);
      } else {
         p_198519_0_.sendSuccess(new TranslationTextComponent("commands.kill.success.multiple", p_198519_1_.size()), true);
      }

      return p_198519_1_.size();
   }
}
