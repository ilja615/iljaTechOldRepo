package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.LocationInput;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;

public class TeleportCommand {
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.teleport.invalidPosition"));

   public static void register(CommandDispatcher<CommandSource> p_198809_0_) {
      LiteralCommandNode<CommandSource> literalcommandnode = p_198809_0_.register(Commands.literal("teleport").requires((p_198816_0_) -> {
         return p_198816_0_.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("location", Vec3Argument.vec3()).executes((p_198807_0_) -> {
         return teleportToPos(p_198807_0_.getSource(), EntityArgument.getEntities(p_198807_0_, "targets"), p_198807_0_.getSource().getLevel(), Vec3Argument.getCoordinates(p_198807_0_, "location"), (ILocationArgument)null, (TeleportCommand.Facing)null);
      }).then(Commands.argument("rotation", RotationArgument.rotation()).executes((p_198811_0_) -> {
         return teleportToPos(p_198811_0_.getSource(), EntityArgument.getEntities(p_198811_0_, "targets"), p_198811_0_.getSource().getLevel(), Vec3Argument.getCoordinates(p_198811_0_, "location"), RotationArgument.getRotation(p_198811_0_, "rotation"), (TeleportCommand.Facing)null);
      })).then(Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("facingEntity", EntityArgument.entity()).executes((p_198806_0_) -> {
         return teleportToPos(p_198806_0_.getSource(), EntityArgument.getEntities(p_198806_0_, "targets"), p_198806_0_.getSource().getLevel(), Vec3Argument.getCoordinates(p_198806_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(EntityArgument.getEntity(p_198806_0_, "facingEntity"), EntityAnchorArgument.Type.FEET));
      }).then(Commands.argument("facingAnchor", EntityAnchorArgument.anchor()).executes((p_198812_0_) -> {
         return teleportToPos(p_198812_0_.getSource(), EntityArgument.getEntities(p_198812_0_, "targets"), p_198812_0_.getSource().getLevel(), Vec3Argument.getCoordinates(p_198812_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(EntityArgument.getEntity(p_198812_0_, "facingEntity"), EntityAnchorArgument.getAnchor(p_198812_0_, "facingAnchor")));
      })))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes((p_198805_0_) -> {
         return teleportToPos(p_198805_0_.getSource(), EntityArgument.getEntities(p_198805_0_, "targets"), p_198805_0_.getSource().getLevel(), Vec3Argument.getCoordinates(p_198805_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(Vec3Argument.getVec3(p_198805_0_, "facingLocation")));
      })))).then(Commands.argument("destination", EntityArgument.entity()).executes((p_198814_0_) -> {
         return teleportToEntity(p_198814_0_.getSource(), EntityArgument.getEntities(p_198814_0_, "targets"), EntityArgument.getEntity(p_198814_0_, "destination"));
      }))).then(Commands.argument("location", Vec3Argument.vec3()).executes((p_200560_0_) -> {
         return teleportToPos(p_200560_0_.getSource(), Collections.singleton(p_200560_0_.getSource().getEntityOrException()), p_200560_0_.getSource().getLevel(), Vec3Argument.getCoordinates(p_200560_0_, "location"), LocationInput.current(), (TeleportCommand.Facing)null);
      })).then(Commands.argument("destination", EntityArgument.entity()).executes((p_200562_0_) -> {
         return teleportToEntity(p_200562_0_.getSource(), Collections.singleton(p_200562_0_.getSource().getEntityOrException()), EntityArgument.getEntity(p_200562_0_, "destination"));
      })));
      p_198809_0_.register(Commands.literal("tp").requires((p_200556_0_) -> {
         return p_200556_0_.hasPermission(2);
      }).redirect(literalcommandnode));
   }

   private static int teleportToEntity(CommandSource p_201126_0_, Collection<? extends Entity> p_201126_1_, Entity p_201126_2_) throws CommandSyntaxException {
      for(Entity entity : p_201126_1_) {
         performTeleport(p_201126_0_, entity, (ServerWorld)p_201126_2_.level, p_201126_2_.getX(), p_201126_2_.getY(), p_201126_2_.getZ(), EnumSet.noneOf(SPlayerPositionLookPacket.Flags.class), p_201126_2_.yRot, p_201126_2_.xRot, (TeleportCommand.Facing)null);
      }

      if (p_201126_1_.size() == 1) {
         p_201126_0_.sendSuccess(new TranslationTextComponent("commands.teleport.success.entity.single", p_201126_1_.iterator().next().getDisplayName(), p_201126_2_.getDisplayName()), true);
      } else {
         p_201126_0_.sendSuccess(new TranslationTextComponent("commands.teleport.success.entity.multiple", p_201126_1_.size(), p_201126_2_.getDisplayName()), true);
      }

      return p_201126_1_.size();
   }

   private static int teleportToPos(CommandSource p_200559_0_, Collection<? extends Entity> p_200559_1_, ServerWorld p_200559_2_, ILocationArgument p_200559_3_, @Nullable ILocationArgument p_200559_4_, @Nullable TeleportCommand.Facing p_200559_5_) throws CommandSyntaxException {
      Vector3d vector3d = p_200559_3_.getPosition(p_200559_0_);
      Vector2f vector2f = p_200559_4_ == null ? null : p_200559_4_.getRotation(p_200559_0_);
      Set<SPlayerPositionLookPacket.Flags> set = EnumSet.noneOf(SPlayerPositionLookPacket.Flags.class);
      if (p_200559_3_.isXRelative()) {
         set.add(SPlayerPositionLookPacket.Flags.X);
      }

      if (p_200559_3_.isYRelative()) {
         set.add(SPlayerPositionLookPacket.Flags.Y);
      }

      if (p_200559_3_.isZRelative()) {
         set.add(SPlayerPositionLookPacket.Flags.Z);
      }

      if (p_200559_4_ == null) {
         set.add(SPlayerPositionLookPacket.Flags.X_ROT);
         set.add(SPlayerPositionLookPacket.Flags.Y_ROT);
      } else {
         if (p_200559_4_.isXRelative()) {
            set.add(SPlayerPositionLookPacket.Flags.X_ROT);
         }

         if (p_200559_4_.isYRelative()) {
            set.add(SPlayerPositionLookPacket.Flags.Y_ROT);
         }
      }

      for(Entity entity : p_200559_1_) {
         if (p_200559_4_ == null) {
            performTeleport(p_200559_0_, entity, p_200559_2_, vector3d.x, vector3d.y, vector3d.z, set, entity.yRot, entity.xRot, p_200559_5_);
         } else {
            performTeleport(p_200559_0_, entity, p_200559_2_, vector3d.x, vector3d.y, vector3d.z, set, vector2f.y, vector2f.x, p_200559_5_);
         }
      }

      if (p_200559_1_.size() == 1) {
         p_200559_0_.sendSuccess(new TranslationTextComponent("commands.teleport.success.location.single", p_200559_1_.iterator().next().getDisplayName(), vector3d.x, vector3d.y, vector3d.z), true);
      } else {
         p_200559_0_.sendSuccess(new TranslationTextComponent("commands.teleport.success.location.multiple", p_200559_1_.size(), vector3d.x, vector3d.y, vector3d.z), true);
      }

      return p_200559_1_.size();
   }

   private static void performTeleport(CommandSource p_201127_0_, Entity p_201127_1_, ServerWorld p_201127_2_, double p_201127_3_, double p_201127_5_, double p_201127_7_, Set<SPlayerPositionLookPacket.Flags> p_201127_9_, float p_201127_10_, float p_201127_11_, @Nullable TeleportCommand.Facing p_201127_12_) throws CommandSyntaxException {
      BlockPos blockpos = new BlockPos(p_201127_3_, p_201127_5_, p_201127_7_);
      if (!World.isInSpawnableBounds(blockpos)) {
         throw INVALID_POSITION.create();
      } else {
         if (p_201127_1_ instanceof ServerPlayerEntity) {
            ChunkPos chunkpos = new ChunkPos(new BlockPos(p_201127_3_, p_201127_5_, p_201127_7_));
            p_201127_2_.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, p_201127_1_.getId());
            p_201127_1_.stopRiding();
            if (((ServerPlayerEntity)p_201127_1_).isSleeping()) {
               ((ServerPlayerEntity)p_201127_1_).stopSleepInBed(true, true);
            }

            if (p_201127_2_ == p_201127_1_.level) {
               ((ServerPlayerEntity)p_201127_1_).connection.teleport(p_201127_3_, p_201127_5_, p_201127_7_, p_201127_10_, p_201127_11_, p_201127_9_);
            } else {
               ((ServerPlayerEntity)p_201127_1_).teleportTo(p_201127_2_, p_201127_3_, p_201127_5_, p_201127_7_, p_201127_10_, p_201127_11_);
            }

            p_201127_1_.setYHeadRot(p_201127_10_);
         } else {
            float f1 = MathHelper.wrapDegrees(p_201127_10_);
            float f = MathHelper.wrapDegrees(p_201127_11_);
            f = MathHelper.clamp(f, -90.0F, 90.0F);
            if (p_201127_2_ == p_201127_1_.level) {
               p_201127_1_.moveTo(p_201127_3_, p_201127_5_, p_201127_7_, f1, f);
               p_201127_1_.setYHeadRot(f1);
            } else {
               p_201127_1_.unRide();
               Entity entity = p_201127_1_;
               p_201127_1_ = p_201127_1_.getType().create(p_201127_2_);
               if (p_201127_1_ == null) {
                  return;
               }

               p_201127_1_.restoreFrom(entity);
               p_201127_1_.moveTo(p_201127_3_, p_201127_5_, p_201127_7_, f1, f);
               p_201127_1_.setYHeadRot(f1);
               p_201127_2_.addFromAnotherDimension(p_201127_1_);
            }
         }

         if (p_201127_12_ != null) {
            p_201127_12_.perform(p_201127_0_, p_201127_1_);
         }

         if (!(p_201127_1_ instanceof LivingEntity) || !((LivingEntity)p_201127_1_).isFallFlying()) {
            p_201127_1_.setDeltaMovement(p_201127_1_.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            p_201127_1_.setOnGround(true);
         }

         if (p_201127_1_ instanceof CreatureEntity) {
            ((CreatureEntity)p_201127_1_).getNavigation().stop();
         }

      }
   }

   static class Facing {
      private final Vector3d position;
      private final Entity entity;
      private final EntityAnchorArgument.Type anchor;

      public Facing(Entity p_i48274_1_, EntityAnchorArgument.Type p_i48274_2_) {
         this.entity = p_i48274_1_;
         this.anchor = p_i48274_2_;
         this.position = p_i48274_2_.apply(p_i48274_1_);
      }

      public Facing(Vector3d p_i48246_1_) {
         this.entity = null;
         this.position = p_i48246_1_;
         this.anchor = null;
      }

      public void perform(CommandSource p_201124_1_, Entity p_201124_2_) {
         if (this.entity != null) {
            if (p_201124_2_ instanceof ServerPlayerEntity) {
               ((ServerPlayerEntity)p_201124_2_).lookAt(p_201124_1_.getAnchor(), this.entity, this.anchor);
            } else {
               p_201124_2_.lookAt(p_201124_1_.getAnchor(), this.position);
            }
         } else {
            p_201124_2_.lookAt(p_201124_1_.getAnchor(), this.position);
         }

      }
   }
}
