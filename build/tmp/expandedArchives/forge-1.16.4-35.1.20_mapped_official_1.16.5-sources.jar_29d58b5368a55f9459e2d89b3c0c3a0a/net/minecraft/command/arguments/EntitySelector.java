package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.server.ServerWorld;

public class EntitySelector {
   private final int maxResults;
   private final boolean includesEntities;
   private final boolean worldLimited;
   private final Predicate<Entity> predicate;
   private final MinMaxBounds.FloatBound range;
   private final Function<Vector3d, Vector3d> position;
   @Nullable
   private final AxisAlignedBB aabb;
   private final BiConsumer<Vector3d, List<? extends Entity>> order;
   private final boolean currentEntity;
   @Nullable
   private final String playerName;
   @Nullable
   private final UUID entityUUID;
   @Nullable
   private final EntityType<?> type;
   private final boolean usesSelector;

   public EntitySelector(int p_i50800_1_, boolean p_i50800_2_, boolean p_i50800_3_, Predicate<Entity> p_i50800_4_, MinMaxBounds.FloatBound p_i50800_5_, Function<Vector3d, Vector3d> p_i50800_6_, @Nullable AxisAlignedBB p_i50800_7_, BiConsumer<Vector3d, List<? extends Entity>> p_i50800_8_, boolean p_i50800_9_, @Nullable String p_i50800_10_, @Nullable UUID p_i50800_11_, @Nullable EntityType<?> p_i50800_12_, boolean p_i50800_13_) {
      this.maxResults = p_i50800_1_;
      this.includesEntities = p_i50800_2_;
      this.worldLimited = p_i50800_3_;
      this.predicate = p_i50800_4_;
      this.range = p_i50800_5_;
      this.position = p_i50800_6_;
      this.aabb = p_i50800_7_;
      this.order = p_i50800_8_;
      this.currentEntity = p_i50800_9_;
      this.playerName = p_i50800_10_;
      this.entityUUID = p_i50800_11_;
      this.type = p_i50800_12_;
      this.usesSelector = p_i50800_13_;
   }

   public int getMaxResults() {
      return this.maxResults;
   }

   public boolean includesEntities() {
      return this.includesEntities;
   }

   public boolean isSelfSelector() {
      return this.currentEntity;
   }

   public boolean isWorldLimited() {
      return this.worldLimited;
   }

   private void checkPermissions(CommandSource p_210324_1_) throws CommandSyntaxException {
      if (this.usesSelector && !p_210324_1_.hasPermission(2)) {
         throw EntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create();
      }
   }

   public Entity findSingleEntity(CommandSource p_197340_1_) throws CommandSyntaxException {
      this.checkPermissions(p_197340_1_);
      List<? extends Entity> list = this.findEntities(p_197340_1_);
      if (list.isEmpty()) {
         throw EntityArgument.NO_ENTITIES_FOUND.create();
      } else if (list.size() > 1) {
         throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
      } else {
         return list.get(0);
      }
   }

   public List<? extends Entity> findEntities(CommandSource p_197341_1_) throws CommandSyntaxException {
      this.checkPermissions(p_197341_1_);
      if (!this.includesEntities) {
         return this.findPlayers(p_197341_1_);
      } else if (this.playerName != null) {
         ServerPlayerEntity serverplayerentity = p_197341_1_.getServer().getPlayerList().getPlayerByName(this.playerName);
         return (List<? extends Entity>)(serverplayerentity == null ? Collections.emptyList() : Lists.newArrayList(serverplayerentity));
      } else if (this.entityUUID != null) {
         for(ServerWorld serverworld1 : p_197341_1_.getServer().getAllLevels()) {
            Entity entity = serverworld1.getEntity(this.entityUUID);
            if (entity != null) {
               return Lists.newArrayList(entity);
            }
         }

         return Collections.emptyList();
      } else {
         Vector3d vector3d = this.position.apply(p_197341_1_.getPosition());
         Predicate<Entity> predicate = this.getPredicate(vector3d);
         if (this.currentEntity) {
            return (List<? extends Entity>)(p_197341_1_.getEntity() != null && predicate.test(p_197341_1_.getEntity()) ? Lists.newArrayList(p_197341_1_.getEntity()) : Collections.emptyList());
         } else {
            List<Entity> list = Lists.newArrayList();
            if (this.isWorldLimited()) {
               this.addEntities(list, p_197341_1_.getLevel(), vector3d, predicate);
            } else {
               for(ServerWorld serverworld : p_197341_1_.getServer().getAllLevels()) {
                  this.addEntities(list, serverworld, vector3d, predicate);
               }
            }

            return this.sortAndLimit(vector3d, list);
         }
      }
   }

   private void addEntities(List<Entity> p_197348_1_, ServerWorld p_197348_2_, Vector3d p_197348_3_, Predicate<Entity> p_197348_4_) {
      if (this.aabb != null) {
         p_197348_1_.addAll(p_197348_2_.getEntities(this.type, this.aabb.move(p_197348_3_), p_197348_4_));
      } else {
         p_197348_1_.addAll(p_197348_2_.getEntities(this.type, p_197348_4_));
      }

   }

   public ServerPlayerEntity findSinglePlayer(CommandSource p_197347_1_) throws CommandSyntaxException {
      this.checkPermissions(p_197347_1_);
      List<ServerPlayerEntity> list = this.findPlayers(p_197347_1_);
      if (list.size() != 1) {
         throw EntityArgument.NO_PLAYERS_FOUND.create();
      } else {
         return list.get(0);
      }
   }

   public List<ServerPlayerEntity> findPlayers(CommandSource p_197342_1_) throws CommandSyntaxException {
      this.checkPermissions(p_197342_1_);
      if (this.playerName != null) {
         ServerPlayerEntity serverplayerentity2 = p_197342_1_.getServer().getPlayerList().getPlayerByName(this.playerName);
         return (List<ServerPlayerEntity>)(serverplayerentity2 == null ? Collections.emptyList() : Lists.newArrayList(serverplayerentity2));
      } else if (this.entityUUID != null) {
         ServerPlayerEntity serverplayerentity1 = p_197342_1_.getServer().getPlayerList().getPlayer(this.entityUUID);
         return (List<ServerPlayerEntity>)(serverplayerentity1 == null ? Collections.emptyList() : Lists.newArrayList(serverplayerentity1));
      } else {
         Vector3d vector3d = this.position.apply(p_197342_1_.getPosition());
         Predicate<Entity> predicate = this.getPredicate(vector3d);
         if (this.currentEntity) {
            if (p_197342_1_.getEntity() instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverplayerentity3 = (ServerPlayerEntity)p_197342_1_.getEntity();
               if (predicate.test(serverplayerentity3)) {
                  return Lists.newArrayList(serverplayerentity3);
               }
            }

            return Collections.emptyList();
         } else {
            List<ServerPlayerEntity> list;
            if (this.isWorldLimited()) {
               list = p_197342_1_.getLevel().getPlayers(predicate::test);
            } else {
               list = Lists.newArrayList();

               for(ServerPlayerEntity serverplayerentity : p_197342_1_.getServer().getPlayerList().getPlayers()) {
                  if (predicate.test(serverplayerentity)) {
                     list.add(serverplayerentity);
                  }
               }
            }

            return this.sortAndLimit(vector3d, list);
         }
      }
   }

   private Predicate<Entity> getPredicate(Vector3d p_197349_1_) {
      Predicate<Entity> predicate = this.predicate;
      if (this.aabb != null) {
         AxisAlignedBB axisalignedbb = this.aabb.move(p_197349_1_);
         predicate = predicate.and((p_197344_1_) -> {
            return axisalignedbb.intersects(p_197344_1_.getBoundingBox());
         });
      }

      if (!this.range.isAny()) {
         predicate = predicate.and((p_211376_2_) -> {
            return this.range.matchesSqr(p_211376_2_.distanceToSqr(p_197349_1_));
         });
      }

      return predicate;
   }

   private <T extends Entity> List<T> sortAndLimit(Vector3d p_197345_1_, List<T> p_197345_2_) {
      if (p_197345_2_.size() > 1) {
         this.order.accept(p_197345_1_, p_197345_2_);
      }

      return p_197345_2_.subList(0, Math.min(this.maxResults, p_197345_2_.size()));
   }

   public static IFormattableTextComponent joinNames(List<? extends Entity> p_197350_0_) {
      return TextComponentUtils.formatList(p_197350_0_, Entity::getDisplayName);
   }
}
