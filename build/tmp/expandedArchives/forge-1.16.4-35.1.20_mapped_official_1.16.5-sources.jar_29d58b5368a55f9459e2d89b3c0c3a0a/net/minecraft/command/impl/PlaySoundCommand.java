package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;

public class PlaySoundCommand {
   private static final SimpleCommandExceptionType ERROR_TOO_FAR = new SimpleCommandExceptionType(new TranslationTextComponent("commands.playsound.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198572_0_) {
      RequiredArgumentBuilder<CommandSource, ResourceLocation> requiredargumentbuilder = Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);

      for(SoundCategory soundcategory : SoundCategory.values()) {
         requiredargumentbuilder.then(source(soundcategory));
      }

      p_198572_0_.register(Commands.literal("playsound").requires((p_198576_0_) -> {
         return p_198576_0_.hasPermission(2);
      }).then(requiredargumentbuilder));
   }

   private static LiteralArgumentBuilder<CommandSource> source(SoundCategory p_198577_0_) {
      return Commands.literal(p_198577_0_.getName()).then(Commands.argument("targets", EntityArgument.players()).executes((p_198575_1_) -> {
         return playSound(p_198575_1_.getSource(), EntityArgument.getPlayers(p_198575_1_, "targets"), ResourceLocationArgument.getId(p_198575_1_, "sound"), p_198577_0_, p_198575_1_.getSource().getPosition(), 1.0F, 1.0F, 0.0F);
      }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198578_1_) -> {
         return playSound(p_198578_1_.getSource(), EntityArgument.getPlayers(p_198578_1_, "targets"), ResourceLocationArgument.getId(p_198578_1_, "sound"), p_198577_0_, Vec3Argument.getVec3(p_198578_1_, "pos"), 1.0F, 1.0F, 0.0F);
      }).then(Commands.argument("volume", FloatArgumentType.floatArg(0.0F)).executes((p_198571_1_) -> {
         return playSound(p_198571_1_.getSource(), EntityArgument.getPlayers(p_198571_1_, "targets"), ResourceLocationArgument.getId(p_198571_1_, "sound"), p_198577_0_, Vec3Argument.getVec3(p_198571_1_, "pos"), p_198571_1_.getArgument("volume", Float.class), 1.0F, 0.0F);
      }).then(Commands.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F)).executes((p_198574_1_) -> {
         return playSound(p_198574_1_.getSource(), EntityArgument.getPlayers(p_198574_1_, "targets"), ResourceLocationArgument.getId(p_198574_1_, "sound"), p_198577_0_, Vec3Argument.getVec3(p_198574_1_, "pos"), p_198574_1_.getArgument("volume", Float.class), p_198574_1_.getArgument("pitch", Float.class), 0.0F);
      }).then(Commands.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((p_198570_1_) -> {
         return playSound(p_198570_1_.getSource(), EntityArgument.getPlayers(p_198570_1_, "targets"), ResourceLocationArgument.getId(p_198570_1_, "sound"), p_198577_0_, Vec3Argument.getVec3(p_198570_1_, "pos"), p_198570_1_.getArgument("volume", Float.class), p_198570_1_.getArgument("pitch", Float.class), p_198570_1_.getArgument("minVolume", Float.class));
      }))))));
   }

   private static int playSound(CommandSource p_198573_0_, Collection<ServerPlayerEntity> p_198573_1_, ResourceLocation p_198573_2_, SoundCategory p_198573_3_, Vector3d p_198573_4_, float p_198573_5_, float p_198573_6_, float p_198573_7_) throws CommandSyntaxException {
      double d0 = Math.pow(p_198573_5_ > 1.0F ? (double)(p_198573_5_ * 16.0F) : 16.0D, 2.0D);
      int i = 0;
      Iterator iterator = p_198573_1_.iterator();

      while(true) {
         ServerPlayerEntity serverplayerentity;
         Vector3d vector3d;
         float f;
         while(true) {
            if (!iterator.hasNext()) {
               if (i == 0) {
                  throw ERROR_TOO_FAR.create();
               }

               if (p_198573_1_.size() == 1) {
                  p_198573_0_.sendSuccess(new TranslationTextComponent("commands.playsound.success.single", p_198573_2_, p_198573_1_.iterator().next().getDisplayName()), true);
               } else {
                  p_198573_0_.sendSuccess(new TranslationTextComponent("commands.playsound.success.multiple", p_198573_2_, p_198573_1_.size()), true);
               }

               return i;
            }

            serverplayerentity = (ServerPlayerEntity)iterator.next();
            double d1 = p_198573_4_.x - serverplayerentity.getX();
            double d2 = p_198573_4_.y - serverplayerentity.getY();
            double d3 = p_198573_4_.z - serverplayerentity.getZ();
            double d4 = d1 * d1 + d2 * d2 + d3 * d3;
            vector3d = p_198573_4_;
            f = p_198573_5_;
            if (!(d4 > d0)) {
               break;
            }

            if (!(p_198573_7_ <= 0.0F)) {
               double d5 = (double)MathHelper.sqrt(d4);
               vector3d = new Vector3d(serverplayerentity.getX() + d1 / d5 * 2.0D, serverplayerentity.getY() + d2 / d5 * 2.0D, serverplayerentity.getZ() + d3 / d5 * 2.0D);
               f = p_198573_7_;
               break;
            }
         }

         serverplayerentity.connection.send(new SPlaySoundPacket(p_198573_2_, p_198573_3_, vector3d, f, p_198573_6_));
         ++i;
      }
   }
}
