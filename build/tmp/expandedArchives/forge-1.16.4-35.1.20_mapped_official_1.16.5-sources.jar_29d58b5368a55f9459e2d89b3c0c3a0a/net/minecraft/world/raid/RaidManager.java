package net.minecraft.world.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class RaidManager extends WorldSavedData {
   private final Map<Integer, Raid> raidMap = Maps.newHashMap();
   private final ServerWorld level;
   private int nextAvailableID;
   private int tick;

   public RaidManager(ServerWorld p_i50142_1_) {
      super(getFileId(p_i50142_1_.dimensionType()));
      this.level = p_i50142_1_;
      this.nextAvailableID = 1;
      this.setDirty();
   }

   public Raid get(int p_215167_1_) {
      return this.raidMap.get(p_215167_1_);
   }

   public void tick() {
      ++this.tick;
      Iterator<Raid> iterator = this.raidMap.values().iterator();

      while(iterator.hasNext()) {
         Raid raid = iterator.next();
         if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            raid.stop();
         }

         if (raid.isStopped()) {
            iterator.remove();
            this.setDirty();
         } else {
            raid.tick();
         }
      }

      if (this.tick % 200 == 0) {
         this.setDirty();
      }

      DebugPacketSender.sendRaids(this.level, this.raidMap.values());
   }

   public static boolean canJoinRaid(AbstractRaiderEntity p_215165_0_, Raid p_215165_1_) {
      if (p_215165_0_ != null && p_215165_1_ != null && p_215165_1_.getLevel() != null) {
         return p_215165_0_.isAlive() && p_215165_0_.canJoinRaid() && p_215165_0_.getNoActionTime() <= 2400 && p_215165_0_.level.dimensionType() == p_215165_1_.getLevel().dimensionType();
      } else {
         return false;
      }
   }

   @Nullable
   public Raid createOrExtendRaid(ServerPlayerEntity p_215170_1_) {
      if (p_215170_1_.isSpectator()) {
         return null;
      } else if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
         return null;
      } else {
         DimensionType dimensiontype = p_215170_1_.level.dimensionType();
         if (!dimensiontype.hasRaids()) {
            return null;
         } else {
            BlockPos blockpos = p_215170_1_.blockPosition();
            List<PointOfInterest> list = this.level.getPoiManager().getInRange(PointOfInterestType.ALL, blockpos, 64, PointOfInterestManager.Status.IS_OCCUPIED).collect(Collectors.toList());
            int i = 0;
            Vector3d vector3d = Vector3d.ZERO;

            for(PointOfInterest pointofinterest : list) {
               BlockPos blockpos2 = pointofinterest.getPos();
               vector3d = vector3d.add((double)blockpos2.getX(), (double)blockpos2.getY(), (double)blockpos2.getZ());
               ++i;
            }

            BlockPos blockpos1;
            if (i > 0) {
               vector3d = vector3d.scale(1.0D / (double)i);
               blockpos1 = new BlockPos(vector3d);
            } else {
               blockpos1 = blockpos;
            }

            Raid raid = this.getOrCreateRaid(p_215170_1_.getLevel(), blockpos1);
            boolean flag = false;
            if (!raid.isStarted()) {
               if (!this.raidMap.containsKey(raid.getId())) {
                  this.raidMap.put(raid.getId(), raid);
               }

               flag = true;
            } else if (raid.getBadOmenLevel() < raid.getMaxBadOmenLevel()) {
               flag = true;
            } else {
               p_215170_1_.removeEffect(Effects.BAD_OMEN);
               p_215170_1_.connection.send(new SEntityStatusPacket(p_215170_1_, (byte)43));
            }

            if (flag) {
               raid.absorbBadOmen(p_215170_1_);
               p_215170_1_.connection.send(new SEntityStatusPacket(p_215170_1_, (byte)43));
               if (!raid.hasFirstWaveSpawned()) {
                  p_215170_1_.awardStat(Stats.RAID_TRIGGER);
                  CriteriaTriggers.BAD_OMEN.trigger(p_215170_1_);
               }
            }

            this.setDirty();
            return raid;
         }
      }
   }

   private Raid getOrCreateRaid(ServerWorld p_215168_1_, BlockPos p_215168_2_) {
      Raid raid = p_215168_1_.getRaidAt(p_215168_2_);
      return raid != null ? raid : new Raid(this.getUniqueId(), p_215168_1_, p_215168_2_);
   }

   public void load(CompoundNBT p_76184_1_) {
      this.nextAvailableID = p_76184_1_.getInt("NextAvailableID");
      this.tick = p_76184_1_.getInt("Tick");
      ListNBT listnbt = p_76184_1_.getList("Raids", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         Raid raid = new Raid(this.level, compoundnbt);
         this.raidMap.put(raid.getId(), raid);
      }

   }

   public CompoundNBT save(CompoundNBT p_189551_1_) {
      p_189551_1_.putInt("NextAvailableID", this.nextAvailableID);
      p_189551_1_.putInt("Tick", this.tick);
      ListNBT listnbt = new ListNBT();

      for(Raid raid : this.raidMap.values()) {
         CompoundNBT compoundnbt = new CompoundNBT();
         raid.save(compoundnbt);
         listnbt.add(compoundnbt);
      }

      p_189551_1_.put("Raids", listnbt);
      return p_189551_1_;
   }

   public static String getFileId(DimensionType p_234620_0_) {
      return "raids" + p_234620_0_.getFileSuffix();
   }

   private int getUniqueId() {
      return ++this.nextAvailableID;
   }

   @Nullable
   public Raid getNearbyRaid(BlockPos p_215174_1_, int p_215174_2_) {
      Raid raid = null;
      double d0 = (double)p_215174_2_;

      for(Raid raid1 : this.raidMap.values()) {
         double d1 = raid1.getCenter().distSqr(p_215174_1_);
         if (raid1.isActive() && d1 < d0) {
            raid = raid1;
            d0 = d1;
         }
      }

      return raid;
   }
}
