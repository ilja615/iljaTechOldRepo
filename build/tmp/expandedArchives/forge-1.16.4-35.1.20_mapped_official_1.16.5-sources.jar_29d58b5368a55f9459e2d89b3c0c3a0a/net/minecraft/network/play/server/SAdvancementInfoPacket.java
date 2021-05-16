package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SAdvancementInfoPacket implements IPacket<IClientPlayNetHandler> {
   private boolean reset;
   private Map<ResourceLocation, Advancement.Builder> added;
   private Set<ResourceLocation> removed;
   private Map<ResourceLocation, AdvancementProgress> progress;

   public SAdvancementInfoPacket() {
   }

   public SAdvancementInfoPacket(boolean p_i47519_1_, Collection<Advancement> p_i47519_2_, Set<ResourceLocation> p_i47519_3_, Map<ResourceLocation, AdvancementProgress> p_i47519_4_) {
      this.reset = p_i47519_1_;
      this.added = Maps.newHashMap();

      for(Advancement advancement : p_i47519_2_) {
         this.added.put(advancement.getId(), advancement.deconstruct());
      }

      this.removed = p_i47519_3_;
      this.progress = Maps.newHashMap(p_i47519_4_);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUpdateAdvancementsPacket(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.reset = p_148837_1_.readBoolean();
      this.added = Maps.newHashMap();
      this.removed = Sets.newLinkedHashSet();
      this.progress = Maps.newHashMap();
      int i = p_148837_1_.readVarInt();

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = p_148837_1_.readResourceLocation();
         Advancement.Builder advancement$builder = Advancement.Builder.fromNetwork(p_148837_1_);
         this.added.put(resourcelocation, advancement$builder);
      }

      i = p_148837_1_.readVarInt();

      for(int k = 0; k < i; ++k) {
         ResourceLocation resourcelocation1 = p_148837_1_.readResourceLocation();
         this.removed.add(resourcelocation1);
      }

      i = p_148837_1_.readVarInt();

      for(int l = 0; l < i; ++l) {
         ResourceLocation resourcelocation2 = p_148837_1_.readResourceLocation();
         this.progress.put(resourcelocation2, AdvancementProgress.fromNetwork(p_148837_1_));
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBoolean(this.reset);
      p_148840_1_.writeVarInt(this.added.size());

      for(Entry<ResourceLocation, Advancement.Builder> entry : this.added.entrySet()) {
         ResourceLocation resourcelocation = entry.getKey();
         Advancement.Builder advancement$builder = entry.getValue();
         p_148840_1_.writeResourceLocation(resourcelocation);
         advancement$builder.serializeToNetwork(p_148840_1_);
      }

      p_148840_1_.writeVarInt(this.removed.size());

      for(ResourceLocation resourcelocation1 : this.removed) {
         p_148840_1_.writeResourceLocation(resourcelocation1);
      }

      p_148840_1_.writeVarInt(this.progress.size());

      for(Entry<ResourceLocation, AdvancementProgress> entry1 : this.progress.entrySet()) {
         p_148840_1_.writeResourceLocation(entry1.getKey());
         entry1.getValue().serializeToNetwork(p_148840_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Map<ResourceLocation, Advancement.Builder> getAdded() {
      return this.added;
   }

   @OnlyIn(Dist.CLIENT)
   public Set<ResourceLocation> getRemoved() {
      return this.removed;
   }

   @OnlyIn(Dist.CLIENT)
   public Map<ResourceLocation, AdvancementProgress> getProgress() {
      return this.progress;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldReset() {
      return this.reset;
   }
}
