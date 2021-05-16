package net.minecraft.client.renderer.debug;

import com.google.common.collect.Iterables;
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
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.RandomObjectDescriptor;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PointOfInterestDebugRenderer implements DebugRenderer.IDebugRenderer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final Map<BlockPos, PointOfInterestDebugRenderer.POIInfo> pois = Maps.newHashMap();
   private final Map<UUID, PointOfInterestDebugRenderer.BrainInfo> brainDumpsPerEntity = Maps.newHashMap();
   @Nullable
   private UUID lastLookedAtUuid;

   public PointOfInterestDebugRenderer(Minecraft p_i50976_1_) {
      this.minecraft = p_i50976_1_;
   }

   public void clear() {
      this.pois.clear();
      this.brainDumpsPerEntity.clear();
      this.lastLookedAtUuid = null;
   }

   public void addPoi(PointOfInterestDebugRenderer.POIInfo p_217691_1_) {
      this.pois.put(p_217691_1_.pos, p_217691_1_);
   }

   public void removePoi(BlockPos p_217698_1_) {
      this.pois.remove(p_217698_1_);
   }

   public void setFreeTicketCount(BlockPos p_217706_1_, int p_217706_2_) {
      PointOfInterestDebugRenderer.POIInfo pointofinterestdebugrenderer$poiinfo = this.pois.get(p_217706_1_);
      if (pointofinterestdebugrenderer$poiinfo == null) {
         LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: " + p_217706_1_);
      } else {
         pointofinterestdebugrenderer$poiinfo.freeTicketCount = p_217706_2_;
      }
   }

   public void addOrUpdateBrainDump(PointOfInterestDebugRenderer.BrainInfo p_217692_1_) {
      this.brainDumpsPerEntity.put(p_217692_1_.uuid, p_217692_1_);
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      this.clearRemovedEntities();
      this.doRender(p_225619_3_, p_225619_5_, p_225619_7_);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      RenderSystem.popMatrix();
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void clearRemovedEntities() {
      this.brainDumpsPerEntity.entrySet().removeIf((p_239330_1_) -> {
         Entity entity = this.minecraft.level.getEntity((p_239330_1_.getValue()).id);
         return entity == null || entity.removed;
      });
   }

   private void doRender(double p_229035_1_, double p_229035_3_, double p_229035_5_) {
      BlockPos blockpos = new BlockPos(p_229035_1_, p_229035_3_, p_229035_5_);
      this.brainDumpsPerEntity.values().forEach((p_222924_7_) -> {
         if (this.isPlayerCloseEnoughToMob(p_222924_7_)) {
            this.renderBrainInfo(p_222924_7_, p_229035_1_, p_229035_3_, p_229035_5_);
         }

      });

      for(BlockPos blockpos1 : this.pois.keySet()) {
         if (blockpos.closerThan(blockpos1, 30.0D)) {
            highlightPoi(blockpos1);
         }
      }

      this.pois.values().forEach((p_239324_2_) -> {
         if (blockpos.closerThan(p_239324_2_.pos, 30.0D)) {
            this.renderPoiInfo(p_239324_2_);
         }

      });
      this.getGhostPois().forEach((p_239325_2_, p_239325_3_) -> {
         if (blockpos.closerThan(p_239325_2_, 30.0D)) {
            this.renderGhostPoi(p_239325_2_, p_239325_3_);
         }

      });
   }

   private static void highlightPoi(BlockPos p_217699_0_) {
      float f = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(p_217699_0_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostPoi(BlockPos p_222921_1_, List<String> p_222921_2_) {
      float f = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(p_222921_1_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos("" + p_222921_2_, p_222921_1_, 0, -256);
      renderTextOverPos("Ghost POI", p_222921_1_, 1, -65536);
   }

   private void renderPoiInfo(PointOfInterestDebugRenderer.POIInfo p_217705_1_) {
      int i = 0;
      Set<String> set = this.getTicketHolderNames(p_217705_1_);
      if (set.size() < 4) {
         renderTextOverPoi("Owners: " + set, p_217705_1_, i, -256);
      } else {
         renderTextOverPoi("" + set.size() + " ticket holders", p_217705_1_, i, -256);
      }

      ++i;
      Set<String> set1 = this.getPotentialTicketHolderNames(p_217705_1_);
      if (set1.size() < 4) {
         renderTextOverPoi("Candidates: " + set1, p_217705_1_, i, -23296);
      } else {
         renderTextOverPoi("" + set1.size() + " potential owners", p_217705_1_, i, -23296);
      }

      ++i;
      renderTextOverPoi("Free tickets: " + p_217705_1_.freeTicketCount, p_217705_1_, i, -256);
      ++i;
      renderTextOverPoi(p_217705_1_.type, p_217705_1_, i, -1);
   }

   private void renderPath(PointOfInterestDebugRenderer.BrainInfo p_229037_1_, double p_229037_2_, double p_229037_4_, double p_229037_6_) {
      if (p_229037_1_.path != null) {
         PathfindingDebugRenderer.renderPath(p_229037_1_.path, 0.5F, false, false, p_229037_2_, p_229037_4_, p_229037_6_);
      }

   }

   private void renderBrainInfo(PointOfInterestDebugRenderer.BrainInfo p_229038_1_, double p_229038_2_, double p_229038_4_, double p_229038_6_) {
      boolean flag = this.isMobSelected(p_229038_1_);
      int i = 0;
      renderTextOverMob(p_229038_1_.pos, i, p_229038_1_.name, -1, 0.03F);
      ++i;
      if (flag) {
         renderTextOverMob(p_229038_1_.pos, i, p_229038_1_.profession + " " + p_229038_1_.xp + " xp", -1, 0.02F);
         ++i;
      }

      if (flag) {
         int j = p_229038_1_.health < p_229038_1_.maxHealth ? -23296 : -1;
         renderTextOverMob(p_229038_1_.pos, i, "health: " + String.format("%.1f", p_229038_1_.health) + " / " + String.format("%.1f", p_229038_1_.maxHealth), j, 0.02F);
         ++i;
      }

      if (flag && !p_229038_1_.inventory.equals("")) {
         renderTextOverMob(p_229038_1_.pos, i, p_229038_1_.inventory, -98404, 0.02F);
         ++i;
      }

      if (flag) {
         for(String s : p_229038_1_.behaviors) {
            renderTextOverMob(p_229038_1_.pos, i, s, -16711681, 0.02F);
            ++i;
         }
      }

      if (flag) {
         for(String s1 : p_229038_1_.activities) {
            renderTextOverMob(p_229038_1_.pos, i, s1, -16711936, 0.02F);
            ++i;
         }
      }

      if (p_229038_1_.wantsGolem) {
         renderTextOverMob(p_229038_1_.pos, i, "Wants Golem", -23296, 0.02F);
         ++i;
      }

      if (flag) {
         for(String s2 : p_229038_1_.gossips) {
            if (s2.startsWith(p_229038_1_.name)) {
               renderTextOverMob(p_229038_1_.pos, i, s2, -1, 0.02F);
            } else {
               renderTextOverMob(p_229038_1_.pos, i, s2, -23296, 0.02F);
            }

            ++i;
         }
      }

      if (flag) {
         for(String s3 : Lists.reverse(p_229038_1_.memories)) {
            renderTextOverMob(p_229038_1_.pos, i, s3, -3355444, 0.02F);
            ++i;
         }
      }

      if (flag) {
         this.renderPath(p_229038_1_, p_229038_2_, p_229038_4_, p_229038_6_);
      }

   }

   private static void renderTextOverPoi(String p_217695_0_, PointOfInterestDebugRenderer.POIInfo p_217695_1_, int p_217695_2_, int p_217695_3_) {
      BlockPos blockpos = p_217695_1_.pos;
      renderTextOverPos(p_217695_0_, blockpos, p_217695_2_, p_217695_3_);
   }

   private static void renderTextOverPos(String p_222923_0_, BlockPos p_222923_1_, int p_222923_2_, int p_222923_3_) {
      double d0 = 1.3D;
      double d1 = 0.2D;
      double d2 = (double)p_222923_1_.getX() + 0.5D;
      double d3 = (double)p_222923_1_.getY() + 1.3D + (double)p_222923_2_ * 0.2D;
      double d4 = (double)p_222923_1_.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(p_222923_0_, d2, d3, d4, p_222923_3_, 0.02F, true, 0.0F, true);
   }

   private static void renderTextOverMob(IPosition p_217693_0_, int p_217693_1_, String p_217693_2_, int p_217693_3_, float p_217693_4_) {
      double d0 = 2.4D;
      double d1 = 0.25D;
      BlockPos blockpos = new BlockPos(p_217693_0_);
      double d2 = (double)blockpos.getX() + 0.5D;
      double d3 = p_217693_0_.y() + 2.4D + (double)p_217693_1_ * 0.25D;
      double d4 = (double)blockpos.getZ() + 0.5D;
      float f = 0.5F;
      DebugRenderer.renderFloatingText(p_217693_2_, d2, d3, d4, p_217693_3_, p_217693_4_, false, 0.5F, true);
   }

   private Set<String> getTicketHolderNames(PointOfInterestDebugRenderer.POIInfo p_217696_1_) {
      return this.getTicketHolders(p_217696_1_.pos).stream().map(RandomObjectDescriptor::getEntityName).collect(Collectors.toSet());
   }

   private Set<String> getPotentialTicketHolderNames(PointOfInterestDebugRenderer.POIInfo p_239342_1_) {
      return this.getPotentialTicketHolders(p_239342_1_.pos).stream().map(RandomObjectDescriptor::getEntityName).collect(Collectors.toSet());
   }

   private boolean isMobSelected(PointOfInterestDebugRenderer.BrainInfo p_217703_1_) {
      return Objects.equals(this.lastLookedAtUuid, p_217703_1_.uuid);
   }

   private boolean isPlayerCloseEnoughToMob(PointOfInterestDebugRenderer.BrainInfo p_217694_1_) {
      PlayerEntity playerentity = this.minecraft.player;
      BlockPos blockpos = new BlockPos(playerentity.getX(), p_217694_1_.pos.y(), playerentity.getZ());
      BlockPos blockpos1 = new BlockPos(p_217694_1_.pos);
      return blockpos.closerThan(blockpos1, 30.0D);
   }

   private Collection<UUID> getTicketHolders(BlockPos p_239340_1_) {
      return this.brainDumpsPerEntity.values().stream().filter((p_239336_1_) -> {
         return p_239336_1_.hasPoi(p_239340_1_);
      }).map(PointOfInterestDebugRenderer.BrainInfo::getUuid).collect(Collectors.toSet());
   }

   private Collection<UUID> getPotentialTicketHolders(BlockPos p_239343_1_) {
      return this.brainDumpsPerEntity.values().stream().filter((p_239323_1_) -> {
         return p_239323_1_.hasPotentialPoi(p_239343_1_);
      }).map(PointOfInterestDebugRenderer.BrainInfo::getUuid).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> getGhostPois() {
      Map<BlockPos, List<String>> map = Maps.newHashMap();

      for(PointOfInterestDebugRenderer.BrainInfo pointofinterestdebugrenderer$braininfo : this.brainDumpsPerEntity.values()) {
         for(BlockPos blockpos : Iterables.concat(pointofinterestdebugrenderer$braininfo.pois, pointofinterestdebugrenderer$braininfo.potentialPois)) {
            if (!this.pois.containsKey(blockpos)) {
               map.computeIfAbsent(blockpos, (p_241729_0_) -> {
                  return Lists.newArrayList();
               }).add(pointofinterestdebugrenderer$braininfo.name);
            }
         }
      }

      return map;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((p_239317_1_) -> {
         this.lastLookedAtUuid = p_239317_1_.getUUID();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static class BrainInfo {
      public final UUID uuid;
      public final int id;
      public final String name;
      public final String profession;
      public final int xp;
      public final float health;
      public final float maxHealth;
      public final IPosition pos;
      public final String inventory;
      public final Path path;
      public final boolean wantsGolem;
      public final List<String> activities = Lists.newArrayList();
      public final List<String> behaviors = Lists.newArrayList();
      public final List<String> memories = Lists.newArrayList();
      public final List<String> gossips = Lists.newArrayList();
      public final Set<BlockPos> pois = Sets.newHashSet();
      public final Set<BlockPos> potentialPois = Sets.newHashSet();

      public BrainInfo(UUID p_i241202_1_, int p_i241202_2_, String p_i241202_3_, String p_i241202_4_, int p_i241202_5_, float p_i241202_6_, float p_i241202_7_, IPosition p_i241202_8_, String p_i241202_9_, @Nullable Path p_i241202_10_, boolean p_i241202_11_) {
         this.uuid = p_i241202_1_;
         this.id = p_i241202_2_;
         this.name = p_i241202_3_;
         this.profession = p_i241202_4_;
         this.xp = p_i241202_5_;
         this.health = p_i241202_6_;
         this.maxHealth = p_i241202_7_;
         this.pos = p_i241202_8_;
         this.inventory = p_i241202_9_;
         this.path = p_i241202_10_;
         this.wantsGolem = p_i241202_11_;
      }

      private boolean hasPoi(BlockPos p_217744_1_) {
         return this.pois.stream().anyMatch(p_217744_1_::equals);
      }

      private boolean hasPotentialPoi(BlockPos p_239365_1_) {
         return this.potentialPois.contains(p_239365_1_);
      }

      public UUID getUuid() {
         return this.uuid;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class POIInfo {
      public final BlockPos pos;
      public String type;
      public int freeTicketCount;

      public POIInfo(BlockPos p_i50886_1_, String p_i50886_2_, int p_i50886_3_) {
         this.pos = p_i50886_1_;
         this.type = p_i50886_2_;
         this.freeTicketCount = p_i50886_3_;
      }
   }
}
