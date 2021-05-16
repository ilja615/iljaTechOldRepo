package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.RandomObjectDescriptor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<BlockPos, BeeDebugRenderer.Hive> hives = Maps.newHashMap();
   private final Map<UUID, BeeDebugRenderer.Bee> beeInfosPerEntity = Maps.newHashMap();
   private UUID lastLookedAtUuid;

   public BeeDebugRenderer(Minecraft p_i226027_1_) {
      this.minecraft = p_i226027_1_;
   }

   public void clear() {
      this.hives.clear();
      this.beeInfosPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addOrUpdateHiveInfo(BeeDebugRenderer.Hive p_228966_1_) {
      this.hives.put(p_228966_1_.pos, p_228966_1_);
   }

   public void addOrUpdateBeeInfo(BeeDebugRenderer.Bee p_228964_1_) {
      this.beeInfosPerEntity.put(p_228964_1_.uuid, p_228964_1_);
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      this.clearRemovedHives();
      this.clearRemovedBees();
      this.doRender();
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      RenderSystem.popMatrix();
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void clearRemovedBees() {
      this.beeInfosPerEntity.entrySet().removeIf((p_228984_1_) -> {
         return this.minecraft.level.getEntity((p_228984_1_.getValue()).id) == null;
      });
   }

   private void clearRemovedHives() {
      long i = this.minecraft.level.getGameTime() - 20L;
      this.hives.entrySet().removeIf((p_228962_2_) -> {
         return (p_228962_2_.getValue()).lastSeen < i;
      });
   }

   private void doRender() {
      BlockPos blockpos = this.getCamera().getBlockPosition();
      this.beeInfosPerEntity.values().forEach((p_228994_1_) -> {
         if (this.isPlayerCloseEnoughToMob(p_228994_1_)) {
            this.renderBeeInfo(p_228994_1_);
         }

      });
      this.renderFlowerInfos();

      for(BlockPos blockpos1 : this.hives.keySet()) {
         if (blockpos.closerThan(blockpos1, 30.0D)) {
            highlightHive(blockpos1);
         }
      }

      Map<BlockPos, Set<UUID>> map = this.createHiveBlacklistMap();
      this.hives.values().forEach((p_228973_3_) -> {
         if (blockpos.closerThan(p_228973_3_.pos, 30.0D)) {
            Set<UUID> set = map.get(p_228973_3_.pos);
            this.renderHiveInfo(p_228973_3_, (Collection<UUID>)(set == null ? Sets.newHashSet() : set));
         }

      });
      this.getGhostHives().forEach((p_228971_2_, p_228971_3_) -> {
         if (blockpos.closerThan(p_228971_2_, 30.0D)) {
            this.renderGhostHive(p_228971_2_, p_228971_3_);
         }

      });
   }

   private Map<BlockPos, Set<UUID>> createHiveBlacklistMap() {
      Map<BlockPos, Set<UUID>> map = Maps.newHashMap();
      this.beeInfosPerEntity.values().forEach((p_228985_1_) -> {
         p_228985_1_.blacklistedHives.forEach((p_228986_2_) -> {
            map.computeIfAbsent(p_228986_2_, (p_241727_0_) -> {
               return Sets.newHashSet();
            }).add(p_228985_1_.getUuid());
         });
      });
      return map;
   }

   private void renderFlowerInfos() {
      Map<BlockPos, Set<UUID>> map = Maps.newHashMap();
      this.beeInfosPerEntity.values().stream().filter(BeeDebugRenderer.Bee::hasFlower).forEach((p_241722_1_) -> {
         map.computeIfAbsent(p_241722_1_.flowerPos, (p_241726_0_) -> {
            return Sets.newHashSet();
         }).add(p_241722_1_.getUuid());
      });
      map.entrySet().forEach((p_228978_0_) -> {
         BlockPos blockpos = p_228978_0_.getKey();
         Set<UUID> set = p_228978_0_.getValue();
         Set<String> set1 = set.stream().map(RandomObjectDescriptor::getEntityName).collect(Collectors.toSet());
         int i = 1;
         renderTextOverPos(set1.toString(), blockpos, i++, -256);
         renderTextOverPos("Flower", blockpos, i++, -1);
         float f = 0.05F;
         renderTransparentFilledBox(blockpos, 0.05F, 0.8F, 0.8F, 0.0F, 0.3F);
      });
   }

   private static String getBeeUuidsAsString(Collection<UUID> p_228977_0_) {
      if (p_228977_0_.isEmpty()) {
         return "-";
      } else {
         return p_228977_0_.size() > 3 ? "" + p_228977_0_.size() + " bees" : p_228977_0_.stream().map(RandomObjectDescriptor::getEntityName).collect(Collectors.toSet()).toString();
      }
   }

   private static void highlightHive(BlockPos p_228968_0_) {
      float f = 0.05F;
      renderTransparentFilledBox(p_228968_0_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostHive(BlockPos p_228972_1_, List<String> p_228972_2_) {
      float f = 0.05F;
      renderTransparentFilledBox(p_228972_1_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos("" + p_228972_2_, p_228972_1_, 0, -256);
      renderTextOverPos("Ghost Hive", p_228972_1_, 1, -65536);
   }

   private static void renderTransparentFilledBox(BlockPos p_228969_0_, float p_228969_1_, float p_228969_2_, float p_228969_3_, float p_228969_4_, float p_228969_5_) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(p_228969_0_, p_228969_1_, p_228969_2_, p_228969_3_, p_228969_4_, p_228969_5_);
   }

   private void renderHiveInfo(BeeDebugRenderer.Hive p_228967_1_, Collection<UUID> p_228967_2_) {
      int i = 0;
      if (!p_228967_2_.isEmpty()) {
         renderTextOverHive("Blacklisted by " + getBeeUuidsAsString(p_228967_2_), p_228967_1_, i++, -65536);
      }

      renderTextOverHive("Out: " + getBeeUuidsAsString(this.getHiveMembers(p_228967_1_.pos)), p_228967_1_, i++, -3355444);
      if (p_228967_1_.occupantCount == 0) {
         renderTextOverHive("In: -", p_228967_1_, i++, -256);
      } else if (p_228967_1_.occupantCount == 1) {
         renderTextOverHive("In: 1 bee", p_228967_1_, i++, -256);
      } else {
         renderTextOverHive("In: " + p_228967_1_.occupantCount + " bees", p_228967_1_, i++, -256);
      }

      renderTextOverHive("Honey: " + p_228967_1_.honeyLevel, p_228967_1_, i++, -23296);
      renderTextOverHive(p_228967_1_.hiveType + (p_228967_1_.sedated ? " (sedated)" : ""), p_228967_1_, i++, -1);
   }

   private void renderPath(BeeDebugRenderer.Bee p_228982_1_) {
      if (p_228982_1_.path != null) {
         PathfindingDebugRenderer.renderPath(p_228982_1_.path, 0.5F, false, false, this.getCamera().getPosition().x(), this.getCamera().getPosition().y(), this.getCamera().getPosition().z());
      }

   }

   private void renderBeeInfo(BeeDebugRenderer.Bee p_228988_1_) {
      boolean flag = this.isBeeSelected(p_228988_1_);
      int i = 0;
      renderTextOverMob(p_228988_1_.pos, i++, p_228988_1_.toString(), -1, 0.03F);
      if (p_228988_1_.hivePos == null) {
         renderTextOverMob(p_228988_1_.pos, i++, "No hive", -98404, 0.02F);
      } else {
         renderTextOverMob(p_228988_1_.pos, i++, "Hive: " + this.getPosDescription(p_228988_1_, p_228988_1_.hivePos), -256, 0.02F);
      }

      if (p_228988_1_.flowerPos == null) {
         renderTextOverMob(p_228988_1_.pos, i++, "No flower", -98404, 0.02F);
      } else {
         renderTextOverMob(p_228988_1_.pos, i++, "Flower: " + this.getPosDescription(p_228988_1_, p_228988_1_.flowerPos), -256, 0.02F);
      }

      for(String s : p_228988_1_.goals) {
         renderTextOverMob(p_228988_1_.pos, i++, s, -16711936, 0.02F);
      }

      if (flag) {
         this.renderPath(p_228988_1_);
      }

      if (p_228988_1_.travelTicks > 0) {
         int j = p_228988_1_.travelTicks < 600 ? -3355444 : -23296;
         renderTextOverMob(p_228988_1_.pos, i++, "Travelling: " + p_228988_1_.travelTicks + " ticks", j, 0.02F);
      }

   }

   private static void renderTextOverHive(String p_228975_0_, BeeDebugRenderer.Hive p_228975_1_, int p_228975_2_, int p_228975_3_) {
      BlockPos blockpos = p_228975_1_.pos;
      renderTextOverPos(p_228975_0_, blockpos, p_228975_2_, p_228975_3_);
   }

   private static void renderTextOverPos(String p_228976_0_, BlockPos p_228976_1_, int p_228976_2_, int p_228976_3_) {
      double d0 = 1.3D;
      double d1 = 0.2D;
      double d2 = (double)p_228976_1_.getX() + 0.5D;
      double d3 = (double)p_228976_1_.getY() + 1.3D + (double)p_228976_2_ * 0.2D;
      double d4 = (double)p_228976_1_.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(p_228976_0_, d2, d3, d4, p_228976_3_, 0.02F, true, 0.0F, true);
   }

   private static void renderTextOverMob(IPosition p_228974_0_, int p_228974_1_, String p_228974_2_, int p_228974_3_, float p_228974_4_) {
      double d0 = 2.4D;
      double d1 = 0.25D;
      BlockPos blockpos = new BlockPos(p_228974_0_);
      double d2 = (double)blockpos.getX() + 0.5D;
      double d3 = p_228974_0_.y() + 2.4D + (double)p_228974_1_ * 0.25D;
      double d4 = (double)blockpos.getZ() + 0.5D;
      float f = 0.5F;
      DebugRenderer.renderFloatingText(p_228974_2_, d2, d3, d4, p_228974_3_, p_228974_4_, false, 0.5F, true);
   }

   private ActiveRenderInfo getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }

   private String getPosDescription(BeeDebugRenderer.Bee p_228965_1_, BlockPos p_228965_2_) {
      float f = MathHelper.sqrt(p_228965_2_.distSqr(p_228965_1_.pos.x(), p_228965_1_.pos.y(), p_228965_1_.pos.z(), true));
      double d0 = (double)Math.round(f * 10.0F) / 10.0D;
      return p_228965_2_.toShortString() + " (dist " + d0 + ")";
   }

   private boolean isBeeSelected(BeeDebugRenderer.Bee p_228990_1_) {
      return Objects.equals(this.lastLookedAtUuid, p_228990_1_.uuid);
   }

   private boolean isPlayerCloseEnoughToMob(BeeDebugRenderer.Bee p_228992_1_) {
      PlayerEntity playerentity = this.minecraft.player;
      BlockPos blockpos = new BlockPos(playerentity.getX(), p_228992_1_.pos.y(), playerentity.getZ());
      BlockPos blockpos1 = new BlockPos(p_228992_1_.pos);
      return blockpos.closerThan(blockpos1, 30.0D);
   }

   private Collection<UUID> getHiveMembers(BlockPos p_228983_1_) {
      return this.beeInfosPerEntity.values().stream().filter((p_228970_1_) -> {
         return p_228970_1_.hasHive(p_228983_1_);
      }).map(BeeDebugRenderer.Bee::getUuid).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> getGhostHives() {
      Map<BlockPos, List<String>> map = Maps.newHashMap();

      for(BeeDebugRenderer.Bee beedebugrenderer$bee : this.beeInfosPerEntity.values()) {
         if (beedebugrenderer$bee.hivePos != null && !this.hives.containsKey(beedebugrenderer$bee.hivePos)) {
            map.computeIfAbsent(beedebugrenderer$bee.hivePos, (p_241725_0_) -> {
               return Lists.newArrayList();
            }).add(beedebugrenderer$bee.getName());
         }
      }

      return map;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((p_228963_1_) -> {
         this.lastLookedAtUuid = p_228963_1_.getUUID();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static class Bee {
      public final UUID uuid;
      public final int id;
      public final IPosition pos;
      @Nullable
      public final Path path;
      @Nullable
      public final BlockPos hivePos;
      @Nullable
      public final BlockPos flowerPos;
      public final int travelTicks;
      public final List<String> goals = Lists.newArrayList();
      public final Set<BlockPos> blacklistedHives = Sets.newHashSet();

      public Bee(UUID p_i226028_1_, int p_i226028_2_, IPosition p_i226028_3_, Path p_i226028_4_, BlockPos p_i226028_5_, BlockPos p_i226028_6_, int p_i226028_7_) {
         this.uuid = p_i226028_1_;
         this.id = p_i226028_2_;
         this.pos = p_i226028_3_;
         this.path = p_i226028_4_;
         this.hivePos = p_i226028_5_;
         this.flowerPos = p_i226028_6_;
         this.travelTicks = p_i226028_7_;
      }

      public boolean hasHive(BlockPos p_229008_1_) {
         return this.hivePos != null && this.hivePos.equals(p_229008_1_);
      }

      public UUID getUuid() {
         return this.uuid;
      }

      public String getName() {
         return RandomObjectDescriptor.getEntityName(this.uuid);
      }

      public String toString() {
         return this.getName();
      }

      public boolean hasFlower() {
         return this.flowerPos != null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Hive {
      public final BlockPos pos;
      public final String hiveType;
      public final int occupantCount;
      public final int honeyLevel;
      public final boolean sedated;
      public final long lastSeen;

      public Hive(BlockPos p_i226029_1_, String p_i226029_2_, int p_i226029_3_, int p_i226029_4_, boolean p_i226029_5_, long p_i226029_6_) {
         this.pos = p_i226029_1_;
         this.hiveType = p_i226029_2_;
         this.occupantCount = p_i226029_3_;
         this.honeyLevel = p_i226029_4_;
         this.sedated = p_i226029_5_;
         this.lastSeen = p_i226029_6_;
      }
   }
}
