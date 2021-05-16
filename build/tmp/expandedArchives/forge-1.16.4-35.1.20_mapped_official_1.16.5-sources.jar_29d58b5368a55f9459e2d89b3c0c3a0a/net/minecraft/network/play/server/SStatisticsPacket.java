package net.minecraft.network.play.server;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.util.Map;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SStatisticsPacket implements IPacket<IClientPlayNetHandler> {
   private Object2IntMap<Stat<?>> stats;

   public SStatisticsPacket() {
   }

   public SStatisticsPacket(Object2IntMap<Stat<?>> p_i47942_1_) {
      this.stats = p_i47942_1_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAwardStats(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      int i = p_148837_1_.readVarInt();
      this.stats = new Object2IntOpenHashMap<>(i);

      for(int j = 0; j < i; ++j) {
         this.readStat(Registry.STAT_TYPE.byId(p_148837_1_.readVarInt()), p_148837_1_);
      }

   }

   private <T> void readStat(StatType<T> p_197684_1_, PacketBuffer p_197684_2_) {
      int i = p_197684_2_.readVarInt();
      int j = p_197684_2_.readVarInt();
      this.stats.put(p_197684_1_.get(p_197684_1_.getRegistry().byId(i)), j);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.stats.size());

      for(Entry<Stat<?>> entry : this.stats.object2IntEntrySet()) {
         Stat<?> stat = entry.getKey();
         p_148840_1_.writeVarInt(Registry.STAT_TYPE.getId(stat.getType()));
         p_148840_1_.writeVarInt(this.getId(stat));
         p_148840_1_.writeVarInt(entry.getIntValue());
      }

   }

   private <T> int getId(Stat<T> p_197683_1_) {
      return p_197683_1_.getType().getRegistry().getId(p_197683_1_.getValue());
   }

   @OnlyIn(Dist.CLIENT)
   public Map<Stat<?>, Integer> getStats() {
      return this.stats;
   }
}
