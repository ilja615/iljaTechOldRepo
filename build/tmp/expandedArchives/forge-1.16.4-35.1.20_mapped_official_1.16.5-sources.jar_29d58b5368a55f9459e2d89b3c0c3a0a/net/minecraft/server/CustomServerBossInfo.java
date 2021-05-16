package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.server.ServerBossInfo;

public class CustomServerBossInfo extends ServerBossInfo {
   private final ResourceLocation id;
   private final Set<UUID> players = Sets.newHashSet();
   private int value;
   private int max = 100;

   public CustomServerBossInfo(ResourceLocation p_i48620_1_, ITextComponent p_i48620_2_) {
      super(p_i48620_2_, BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);
      this.id = p_i48620_1_;
      this.setPercent(0.0F);
   }

   public ResourceLocation getTextId() {
      return this.id;
   }

   public void addPlayer(ServerPlayerEntity p_186760_1_) {
      super.addPlayer(p_186760_1_);
      this.players.add(p_186760_1_.getUUID());
   }

   public void addOfflinePlayer(UUID p_201372_1_) {
      this.players.add(p_201372_1_);
   }

   public void removePlayer(ServerPlayerEntity p_186761_1_) {
      super.removePlayer(p_186761_1_);
      this.players.remove(p_186761_1_.getUUID());
   }

   public void removeAllPlayers() {
      super.removeAllPlayers();
      this.players.clear();
   }

   public int getValue() {
      return this.value;
   }

   public int getMax() {
      return this.max;
   }

   public void setValue(int p_201362_1_) {
      this.value = p_201362_1_;
      this.setPercent(MathHelper.clamp((float)p_201362_1_ / (float)this.max, 0.0F, 1.0F));
   }

   public void setMax(int p_201366_1_) {
      this.max = p_201366_1_;
      this.setPercent(MathHelper.clamp((float)this.value / (float)p_201366_1_, 0.0F, 1.0F));
   }

   public final ITextComponent getDisplayName() {
      return TextComponentUtils.wrapInSquareBrackets(this.getName()).withStyle((p_211569_1_) -> {
         return p_211569_1_.withColor(this.getColor().getFormatting()).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.getTextId().toString()))).withInsertion(this.getTextId().toString());
      });
   }

   public boolean setPlayers(Collection<ServerPlayerEntity> p_201368_1_) {
      Set<UUID> set = Sets.newHashSet();
      Set<ServerPlayerEntity> set1 = Sets.newHashSet();

      for(UUID uuid : this.players) {
         boolean flag = false;

         for(ServerPlayerEntity serverplayerentity : p_201368_1_) {
            if (serverplayerentity.getUUID().equals(uuid)) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            set.add(uuid);
         }
      }

      for(ServerPlayerEntity serverplayerentity1 : p_201368_1_) {
         boolean flag1 = false;

         for(UUID uuid2 : this.players) {
            if (serverplayerentity1.getUUID().equals(uuid2)) {
               flag1 = true;
               break;
            }
         }

         if (!flag1) {
            set1.add(serverplayerentity1);
         }
      }

      for(UUID uuid1 : set) {
         for(ServerPlayerEntity serverplayerentity3 : this.getPlayers()) {
            if (serverplayerentity3.getUUID().equals(uuid1)) {
               this.removePlayer(serverplayerentity3);
               break;
            }
         }

         this.players.remove(uuid1);
      }

      for(ServerPlayerEntity serverplayerentity2 : set1) {
         this.addPlayer(serverplayerentity2);
      }

      return !set.isEmpty() || !set1.isEmpty();
   }

   public CompoundNBT save() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", ITextComponent.Serializer.toJson(this.name));
      compoundnbt.putBoolean("Visible", this.isVisible());
      compoundnbt.putInt("Value", this.value);
      compoundnbt.putInt("Max", this.max);
      compoundnbt.putString("Color", this.getColor().getName());
      compoundnbt.putString("Overlay", this.getOverlay().getName());
      compoundnbt.putBoolean("DarkenScreen", this.shouldDarkenScreen());
      compoundnbt.putBoolean("PlayBossMusic", this.shouldPlayBossMusic());
      compoundnbt.putBoolean("CreateWorldFog", this.shouldCreateWorldFog());
      ListNBT listnbt = new ListNBT();

      for(UUID uuid : this.players) {
         listnbt.add(NBTUtil.createUUID(uuid));
      }

      compoundnbt.put("Players", listnbt);
      return compoundnbt;
   }

   public static CustomServerBossInfo load(CompoundNBT p_201371_0_, ResourceLocation p_201371_1_) {
      CustomServerBossInfo customserverbossinfo = new CustomServerBossInfo(p_201371_1_, ITextComponent.Serializer.fromJson(p_201371_0_.getString("Name")));
      customserverbossinfo.setVisible(p_201371_0_.getBoolean("Visible"));
      customserverbossinfo.setValue(p_201371_0_.getInt("Value"));
      customserverbossinfo.setMax(p_201371_0_.getInt("Max"));
      customserverbossinfo.setColor(BossInfo.Color.byName(p_201371_0_.getString("Color")));
      customserverbossinfo.setOverlay(BossInfo.Overlay.byName(p_201371_0_.getString("Overlay")));
      customserverbossinfo.setDarkenScreen(p_201371_0_.getBoolean("DarkenScreen"));
      customserverbossinfo.setPlayBossMusic(p_201371_0_.getBoolean("PlayBossMusic"));
      customserverbossinfo.setCreateWorldFog(p_201371_0_.getBoolean("CreateWorldFog"));
      ListNBT listnbt = p_201371_0_.getList("Players", 11);

      for(int i = 0; i < listnbt.size(); ++i) {
         customserverbossinfo.addOfflinePlayer(NBTUtil.loadUUID(listnbt.get(i)));
      }

      return customserverbossinfo;
   }

   public void onPlayerConnect(ServerPlayerEntity p_201361_1_) {
      if (this.players.contains(p_201361_1_.getUUID())) {
         this.addPlayer(p_201361_1_);
      }

   }

   public void onPlayerDisconnect(ServerPlayerEntity p_201363_1_) {
      super.removePlayer(p_201363_1_);
   }
}
