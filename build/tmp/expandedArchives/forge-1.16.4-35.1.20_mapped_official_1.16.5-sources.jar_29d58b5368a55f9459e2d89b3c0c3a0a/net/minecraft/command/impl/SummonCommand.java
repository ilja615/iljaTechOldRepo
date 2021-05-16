package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SummonCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.failed"));
   private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.failed.uuid"));
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.invalidPosition"));

   public static void register(CommandDispatcher<CommandSource> p_198736_0_) {
      p_198736_0_.register(Commands.literal("summon").requires((p_198740_0_) -> {
         return p_198740_0_.hasPermission(2);
      }).then(Commands.argument("entity", EntitySummonArgument.id()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) -> {
         return spawnEntity(p_198738_0_.getSource(), EntitySummonArgument.getSummonableEntity(p_198738_0_, "entity"), p_198738_0_.getSource().getPosition(), new CompoundNBT(), true);
      }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) -> {
         return spawnEntity(p_198735_0_.getSource(), EntitySummonArgument.getSummonableEntity(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new CompoundNBT(), true);
      }).then(Commands.argument("nbt", NBTCompoundTagArgument.compoundTag()).executes((p_198739_0_) -> {
         return spawnEntity(p_198739_0_.getSource(), EntitySummonArgument.getSummonableEntity(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), NBTCompoundTagArgument.getCompoundTag(p_198739_0_, "nbt"), false);
      })))));
   }

   private static int spawnEntity(CommandSource p_198737_0_, ResourceLocation p_198737_1_, Vector3d p_198737_2_, CompoundNBT p_198737_3_, boolean p_198737_4_) throws CommandSyntaxException {
      BlockPos blockpos = new BlockPos(p_198737_2_);
      if (!World.isInSpawnableBounds(blockpos)) {
         throw INVALID_POSITION.create();
      } else {
         CompoundNBT compoundnbt = p_198737_3_.copy();
         compoundnbt.putString("id", p_198737_1_.toString());
         ServerWorld serverworld = p_198737_0_.getLevel();
         Entity entity = EntityType.loadEntityRecursive(compoundnbt, serverworld, (p_218914_1_) -> {
            p_218914_1_.moveTo(p_198737_2_.x, p_198737_2_.y, p_198737_2_.z, p_218914_1_.yRot, p_218914_1_.xRot);
            return p_218914_1_;
         });
         if (entity == null) {
            throw ERROR_FAILED.create();
         } else {
            if (p_198737_4_ && entity instanceof MobEntity) {
               ((MobEntity)entity).finalizeSpawn(p_198737_0_.getLevel(), p_198737_0_.getLevel().getCurrentDifficultyAt(entity.blockPosition()), SpawnReason.COMMAND, (ILivingEntityData)null, (CompoundNBT)null);
            }

            if (!serverworld.tryAddFreshEntityWithPassengers(entity)) {
               throw ERROR_DUPLICATE_UUID.create();
            } else {
               p_198737_0_.sendSuccess(new TranslationTextComponent("commands.summon.success", entity.getDisplayName()), true);
               return 1;
            }
         }
      }
   }
}
