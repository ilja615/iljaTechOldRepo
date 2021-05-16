package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SMapDataPacket;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapData extends WorldSavedData {
   private static final Logger LOGGER = LogManager.getLogger();
   public int x;
   public int z;
   public RegistryKey<World> dimension;
   public boolean trackingPosition;
   public boolean unlimitedTracking;
   public byte scale;
   public byte[] colors = new byte[16384];
   public boolean locked;
   public final List<MapData.MapInfo> carriedBy = Lists.newArrayList();
   private final Map<PlayerEntity, MapData.MapInfo> carriedByPlayers = Maps.newHashMap();
   private final Map<String, MapBanner> bannerMarkers = Maps.newHashMap();
   public final Map<String, MapDecoration> decorations = Maps.newLinkedHashMap();
   private final Map<String, MapFrame> frameMarkers = Maps.newHashMap();

   public MapData(String p_i2140_1_) {
      super(p_i2140_1_);
   }

   public void setProperties(int p_237241_1_, int p_237241_2_, int p_237241_3_, boolean p_237241_4_, boolean p_237241_5_, RegistryKey<World> p_237241_6_) {
      this.scale = (byte)p_237241_3_;
      this.setOrigin((double)p_237241_1_, (double)p_237241_2_, this.scale);
      this.dimension = p_237241_6_;
      this.trackingPosition = p_237241_4_;
      this.unlimitedTracking = p_237241_5_;
      this.setDirty();
   }

   public void setOrigin(double p_176054_1_, double p_176054_3_, int p_176054_5_) {
      int i = 128 * (1 << p_176054_5_);
      int j = MathHelper.floor((p_176054_1_ + 64.0D) / (double)i);
      int k = MathHelper.floor((p_176054_3_ + 64.0D) / (double)i);
      this.x = j * i + i / 2 - 64;
      this.z = k * i + i / 2 - 64;
   }

   public void load(CompoundNBT p_76184_1_) {
      this.dimension = DimensionType.parseLegacy(new Dynamic<>(NBTDynamicOps.INSTANCE, p_76184_1_.get("dimension"))).resultOrPartial(LOGGER::error).orElseThrow(() -> {
         return new IllegalArgumentException("Invalid map dimension: " + p_76184_1_.get("dimension"));
      });
      this.x = p_76184_1_.getInt("xCenter");
      this.z = p_76184_1_.getInt("zCenter");
      this.scale = (byte)MathHelper.clamp(p_76184_1_.getByte("scale"), 0, 4);
      this.trackingPosition = !p_76184_1_.contains("trackingPosition", 1) || p_76184_1_.getBoolean("trackingPosition");
      this.unlimitedTracking = p_76184_1_.getBoolean("unlimitedTracking");
      this.locked = p_76184_1_.getBoolean("locked");
      this.colors = p_76184_1_.getByteArray("colors");
      if (this.colors.length != 16384) {
         this.colors = new byte[16384];
      }

      ListNBT listnbt = p_76184_1_.getList("banners", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         MapBanner mapbanner = MapBanner.load(listnbt.getCompound(i));
         this.bannerMarkers.put(mapbanner.getId(), mapbanner);
         this.addDecoration(mapbanner.getDecoration(), (IWorld)null, mapbanner.getId(), (double)mapbanner.getPos().getX(), (double)mapbanner.getPos().getZ(), 180.0D, mapbanner.getName());
      }

      ListNBT listnbt1 = p_76184_1_.getList("frames", 10);

      for(int j = 0; j < listnbt1.size(); ++j) {
         MapFrame mapframe = MapFrame.load(listnbt1.getCompound(j));
         this.frameMarkers.put(mapframe.getId(), mapframe);
         this.addDecoration(MapDecoration.Type.FRAME, (IWorld)null, "frame-" + mapframe.getEntityId(), (double)mapframe.getPos().getX(), (double)mapframe.getPos().getZ(), (double)mapframe.getRotation(), (ITextComponent)null);
      }

   }

   public CompoundNBT save(CompoundNBT p_189551_1_) {
      ResourceLocation.CODEC.encodeStart(NBTDynamicOps.INSTANCE, this.dimension.location()).resultOrPartial(LOGGER::error).ifPresent((p_237242_1_) -> {
         p_189551_1_.put("dimension", p_237242_1_);
      });
      p_189551_1_.putInt("xCenter", this.x);
      p_189551_1_.putInt("zCenter", this.z);
      p_189551_1_.putByte("scale", this.scale);
      p_189551_1_.putByteArray("colors", this.colors);
      p_189551_1_.putBoolean("trackingPosition", this.trackingPosition);
      p_189551_1_.putBoolean("unlimitedTracking", this.unlimitedTracking);
      p_189551_1_.putBoolean("locked", this.locked);
      ListNBT listnbt = new ListNBT();

      for(MapBanner mapbanner : this.bannerMarkers.values()) {
         listnbt.add(mapbanner.save());
      }

      p_189551_1_.put("banners", listnbt);
      ListNBT listnbt1 = new ListNBT();

      for(MapFrame mapframe : this.frameMarkers.values()) {
         listnbt1.add(mapframe.save());
      }

      p_189551_1_.put("frames", listnbt1);
      return p_189551_1_;
   }

   public void lockData(MapData p_215160_1_) {
      this.locked = true;
      this.x = p_215160_1_.x;
      this.z = p_215160_1_.z;
      this.bannerMarkers.putAll(p_215160_1_.bannerMarkers);
      this.decorations.putAll(p_215160_1_.decorations);
      System.arraycopy(p_215160_1_.colors, 0, this.colors, 0, p_215160_1_.colors.length);
      this.setDirty();
   }

   public void tickCarriedBy(PlayerEntity p_76191_1_, ItemStack p_76191_2_) {
      if (!this.carriedByPlayers.containsKey(p_76191_1_)) {
         MapData.MapInfo mapdata$mapinfo = new MapData.MapInfo(p_76191_1_);
         this.carriedByPlayers.put(p_76191_1_, mapdata$mapinfo);
         this.carriedBy.add(mapdata$mapinfo);
      }

      if (!p_76191_1_.inventory.contains(p_76191_2_)) {
         this.decorations.remove(p_76191_1_.getName().getString());
      }

      for(int i = 0; i < this.carriedBy.size(); ++i) {
         MapData.MapInfo mapdata$mapinfo1 = this.carriedBy.get(i);
         String s = mapdata$mapinfo1.player.getName().getString();
         if (!mapdata$mapinfo1.player.removed && (mapdata$mapinfo1.player.inventory.contains(p_76191_2_) || p_76191_2_.isFramed())) {
            if (!p_76191_2_.isFramed() && mapdata$mapinfo1.player.level.dimension() == this.dimension && this.trackingPosition) {
               this.addDecoration(MapDecoration.Type.PLAYER, mapdata$mapinfo1.player.level, s, mapdata$mapinfo1.player.getX(), mapdata$mapinfo1.player.getZ(), (double)mapdata$mapinfo1.player.yRot, (ITextComponent)null);
            }
         } else {
            this.carriedByPlayers.remove(mapdata$mapinfo1.player);
            this.carriedBy.remove(mapdata$mapinfo1);
            this.decorations.remove(s);
         }
      }

      if (p_76191_2_.isFramed() && this.trackingPosition) {
         ItemFrameEntity itemframeentity = p_76191_2_.getFrame();
         BlockPos blockpos = itemframeentity.getPos();
         MapFrame mapframe1 = this.frameMarkers.get(MapFrame.frameId(blockpos));
         if (mapframe1 != null && itemframeentity.getId() != mapframe1.getEntityId() && this.frameMarkers.containsKey(mapframe1.getId())) {
            this.decorations.remove("frame-" + mapframe1.getEntityId());
         }

         MapFrame mapframe = new MapFrame(blockpos, itemframeentity.getDirection().get2DDataValue() * 90, itemframeentity.getId());
         this.addDecoration(MapDecoration.Type.FRAME, p_76191_1_.level, "frame-" + itemframeentity.getId(), (double)blockpos.getX(), (double)blockpos.getZ(), (double)(itemframeentity.getDirection().get2DDataValue() * 90), (ITextComponent)null);
         this.frameMarkers.put(mapframe.getId(), mapframe);
      }

      CompoundNBT compoundnbt = p_76191_2_.getTag();
      if (compoundnbt != null && compoundnbt.contains("Decorations", 9)) {
         ListNBT listnbt = compoundnbt.getList("Decorations", 10);

         for(int j = 0; j < listnbt.size(); ++j) {
            CompoundNBT compoundnbt1 = listnbt.getCompound(j);
            if (!this.decorations.containsKey(compoundnbt1.getString("id"))) {
               this.addDecoration(MapDecoration.Type.byIcon(compoundnbt1.getByte("type")), p_76191_1_.level, compoundnbt1.getString("id"), compoundnbt1.getDouble("x"), compoundnbt1.getDouble("z"), compoundnbt1.getDouble("rot"), (ITextComponent)null);
            }
         }
      }

   }

   public static void addTargetDecoration(ItemStack p_191094_0_, BlockPos p_191094_1_, String p_191094_2_, MapDecoration.Type p_191094_3_) {
      ListNBT listnbt;
      if (p_191094_0_.hasTag() && p_191094_0_.getTag().contains("Decorations", 9)) {
         listnbt = p_191094_0_.getTag().getList("Decorations", 10);
      } else {
         listnbt = new ListNBT();
         p_191094_0_.addTagElement("Decorations", listnbt);
      }

      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putByte("type", p_191094_3_.getIcon());
      compoundnbt.putString("id", p_191094_2_);
      compoundnbt.putDouble("x", (double)p_191094_1_.getX());
      compoundnbt.putDouble("z", (double)p_191094_1_.getZ());
      compoundnbt.putDouble("rot", 180.0D);
      listnbt.add(compoundnbt);
      if (p_191094_3_.hasMapColor()) {
         CompoundNBT compoundnbt1 = p_191094_0_.getOrCreateTagElement("display");
         compoundnbt1.putInt("MapColor", p_191094_3_.getMapColor());
      }

   }

   private void addDecoration(MapDecoration.Type p_191095_1_, @Nullable IWorld p_191095_2_, String p_191095_3_, double p_191095_4_, double p_191095_6_, double p_191095_8_, @Nullable ITextComponent p_191095_10_) {
      int i = 1 << this.scale;
      float f = (float)(p_191095_4_ - (double)this.x) / (float)i;
      float f1 = (float)(p_191095_6_ - (double)this.z) / (float)i;
      byte b0 = (byte)((int)((double)(f * 2.0F) + 0.5D));
      byte b1 = (byte)((int)((double)(f1 * 2.0F) + 0.5D));
      int j = 63;
      byte b2;
      if (f >= -63.0F && f1 >= -63.0F && f <= 63.0F && f1 <= 63.0F) {
         p_191095_8_ = p_191095_8_ + (p_191095_8_ < 0.0D ? -8.0D : 8.0D);
         b2 = (byte)((int)(p_191095_8_ * 16.0D / 360.0D));
         if (this.dimension == World.NETHER && p_191095_2_ != null) {
            int l = (int)(p_191095_2_.getLevelData().getDayTime() / 10L);
            b2 = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
         }
      } else {
         if (p_191095_1_ != MapDecoration.Type.PLAYER) {
            this.decorations.remove(p_191095_3_);
            return;
         }

         int k = 320;
         if (Math.abs(f) < 320.0F && Math.abs(f1) < 320.0F) {
            p_191095_1_ = MapDecoration.Type.PLAYER_OFF_MAP;
         } else {
            if (!this.unlimitedTracking) {
               this.decorations.remove(p_191095_3_);
               return;
            }

            p_191095_1_ = MapDecoration.Type.PLAYER_OFF_LIMITS;
         }

         b2 = 0;
         if (f <= -63.0F) {
            b0 = -128;
         }

         if (f1 <= -63.0F) {
            b1 = -128;
         }

         if (f >= 63.0F) {
            b0 = 127;
         }

         if (f1 >= 63.0F) {
            b1 = 127;
         }
      }

      this.decorations.put(p_191095_3_, new MapDecoration(p_191095_1_, b0, b1, b2, p_191095_10_));
   }

   @Nullable
   public IPacket<?> getUpdatePacket(ItemStack p_176052_1_, IBlockReader p_176052_2_, PlayerEntity p_176052_3_) {
      MapData.MapInfo mapdata$mapinfo = this.carriedByPlayers.get(p_176052_3_);
      return mapdata$mapinfo == null ? null : mapdata$mapinfo.nextUpdatePacket(p_176052_1_);
   }

   public void setDirty(int p_176053_1_, int p_176053_2_) {
      this.setDirty();

      for(MapData.MapInfo mapdata$mapinfo : this.carriedBy) {
         mapdata$mapinfo.markDirty(p_176053_1_, p_176053_2_);
      }

   }

   public MapData.MapInfo getHoldingPlayer(PlayerEntity p_82568_1_) {
      MapData.MapInfo mapdata$mapinfo = this.carriedByPlayers.get(p_82568_1_);
      if (mapdata$mapinfo == null) {
         mapdata$mapinfo = new MapData.MapInfo(p_82568_1_);
         this.carriedByPlayers.put(p_82568_1_, mapdata$mapinfo);
         this.carriedBy.add(mapdata$mapinfo);
      }

      return mapdata$mapinfo;
   }

   public void toggleBanner(IWorld p_204269_1_, BlockPos p_204269_2_) {
      double d0 = (double)p_204269_2_.getX() + 0.5D;
      double d1 = (double)p_204269_2_.getZ() + 0.5D;
      int i = 1 << this.scale;
      double d2 = (d0 - (double)this.x) / (double)i;
      double d3 = (d1 - (double)this.z) / (double)i;
      int j = 63;
      boolean flag = false;
      if (d2 >= -63.0D && d3 >= -63.0D && d2 <= 63.0D && d3 <= 63.0D) {
         MapBanner mapbanner = MapBanner.fromWorld(p_204269_1_, p_204269_2_);
         if (mapbanner == null) {
            return;
         }

         boolean flag1 = true;
         if (this.bannerMarkers.containsKey(mapbanner.getId()) && this.bannerMarkers.get(mapbanner.getId()).equals(mapbanner)) {
            this.bannerMarkers.remove(mapbanner.getId());
            this.decorations.remove(mapbanner.getId());
            flag1 = false;
            flag = true;
         }

         if (flag1) {
            this.bannerMarkers.put(mapbanner.getId(), mapbanner);
            this.addDecoration(mapbanner.getDecoration(), p_204269_1_, mapbanner.getId(), d0, d1, 180.0D, mapbanner.getName());
            flag = true;
         }

         if (flag) {
            this.setDirty();
         }
      }

   }

   public void checkBanners(IBlockReader p_204268_1_, int p_204268_2_, int p_204268_3_) {
      Iterator<MapBanner> iterator = this.bannerMarkers.values().iterator();

      while(iterator.hasNext()) {
         MapBanner mapbanner = iterator.next();
         if (mapbanner.getPos().getX() == p_204268_2_ && mapbanner.getPos().getZ() == p_204268_3_) {
            MapBanner mapbanner1 = MapBanner.fromWorld(p_204268_1_, mapbanner.getPos());
            if (!mapbanner.equals(mapbanner1)) {
               iterator.remove();
               this.decorations.remove(mapbanner.getId());
            }
         }
      }

   }

   public void removedFromFrame(BlockPos p_212441_1_, int p_212441_2_) {
      this.decorations.remove("frame-" + p_212441_2_);
      this.frameMarkers.remove(MapFrame.frameId(p_212441_1_));
   }

   public class MapInfo {
      public final PlayerEntity player;
      private boolean dirtyData = true;
      private int minDirtyX;
      private int minDirtyY;
      private int maxDirtyX = 127;
      private int maxDirtyY = 127;
      private int tick;
      public int step;

      public MapInfo(PlayerEntity p_i2138_2_) {
         this.player = p_i2138_2_;
      }

      @Nullable
      public IPacket<?> nextUpdatePacket(ItemStack p_176101_1_) {
         if (this.dirtyData) {
            this.dirtyData = false;
            return new SMapDataPacket(FilledMapItem.getMapId(p_176101_1_), MapData.this.scale, MapData.this.trackingPosition, MapData.this.locked, MapData.this.decorations.values(), MapData.this.colors, this.minDirtyX, this.minDirtyY, this.maxDirtyX + 1 - this.minDirtyX, this.maxDirtyY + 1 - this.minDirtyY);
         } else {
            return this.tick++ % 5 == 0 ? new SMapDataPacket(FilledMapItem.getMapId(p_176101_1_), MapData.this.scale, MapData.this.trackingPosition, MapData.this.locked, MapData.this.decorations.values(), MapData.this.colors, 0, 0, 0, 0) : null;
         }
      }

      public void markDirty(int p_176102_1_, int p_176102_2_) {
         if (this.dirtyData) {
            this.minDirtyX = Math.min(this.minDirtyX, p_176102_1_);
            this.minDirtyY = Math.min(this.minDirtyY, p_176102_2_);
            this.maxDirtyX = Math.max(this.maxDirtyX, p_176102_1_);
            this.maxDirtyY = Math.max(this.maxDirtyY, p_176102_2_);
         } else {
            this.dirtyData = true;
            this.minDirtyX = p_176102_1_;
            this.minDirtyY = p_176102_2_;
            this.maxDirtyX = p_176102_1_;
            this.maxDirtyY = p_176102_2_;
         }

      }
   }
}
