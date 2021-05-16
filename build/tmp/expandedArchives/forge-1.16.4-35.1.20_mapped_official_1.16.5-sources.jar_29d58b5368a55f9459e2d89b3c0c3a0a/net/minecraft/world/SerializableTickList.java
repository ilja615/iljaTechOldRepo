package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class SerializableTickList<T> implements ITickList<T> {
   private final List<SerializableTickList.TickHolder<T>> ticks;
   private final Function<T, ResourceLocation> toId;

   public SerializableTickList(Function<T, ResourceLocation> p_i231603_1_, List<NextTickListEntry<T>> p_i231603_2_, long p_i231603_3_) {
      this(p_i231603_1_, p_i231603_2_.stream().map((p_234854_2_) -> {
         return new SerializableTickList.TickHolder<>(p_234854_2_.getType(), p_234854_2_.pos, (int)(p_234854_2_.triggerTick - p_i231603_3_), p_234854_2_.priority);
      }).collect(Collectors.toList()));
   }

   private SerializableTickList(Function<T, ResourceLocation> p_i50010_1_, List<SerializableTickList.TickHolder<T>> p_i50010_2_) {
      this.ticks = p_i50010_2_;
      this.toId = p_i50010_1_;
   }

   public boolean hasScheduledTick(BlockPos p_205359_1_, T p_205359_2_) {
      return false;
   }

   public void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_) {
      this.ticks.add(new SerializableTickList.TickHolder<>(p_205362_2_, p_205362_1_, p_205362_3_, p_205362_4_));
   }

   public boolean willTickThisTick(BlockPos p_205361_1_, T p_205361_2_) {
      return false;
   }

   public ListNBT save() {
      ListNBT listnbt = new ListNBT();

      for(SerializableTickList.TickHolder<T> tickholder : this.ticks) {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putString("i", this.toId.apply(tickholder.type).toString());
         compoundnbt.putInt("x", tickholder.pos.getX());
         compoundnbt.putInt("y", tickholder.pos.getY());
         compoundnbt.putInt("z", tickholder.pos.getZ());
         compoundnbt.putInt("t", tickholder.delay);
         compoundnbt.putInt("p", tickholder.priority.getValue());
         listnbt.add(compoundnbt);
      }

      return listnbt;
   }

   public static <T> SerializableTickList<T> create(ListNBT p_222984_0_, Function<T, ResourceLocation> p_222984_1_, Function<ResourceLocation, T> p_222984_2_) {
      List<SerializableTickList.TickHolder<T>> list = Lists.newArrayList();

      for(int i = 0; i < p_222984_0_.size(); ++i) {
         CompoundNBT compoundnbt = p_222984_0_.getCompound(i);
         T t = p_222984_2_.apply(new ResourceLocation(compoundnbt.getString("i")));
         if (t != null) {
            BlockPos blockpos = new BlockPos(compoundnbt.getInt("x"), compoundnbt.getInt("y"), compoundnbt.getInt("z"));
            list.add(new SerializableTickList.TickHolder<>(t, blockpos, compoundnbt.getInt("t"), TickPriority.byValue(compoundnbt.getInt("p"))));
         }
      }

      return new SerializableTickList<>(p_222984_1_, list);
   }

   public void copyOut(ITickList<T> p_234855_1_) {
      this.ticks.forEach((p_234856_1_) -> {
         p_234855_1_.scheduleTick(p_234856_1_.pos, p_234856_1_.type, p_234856_1_.delay, p_234856_1_.priority);
      });
   }

   static class TickHolder<T> {
      private final T type;
      public final BlockPos pos;
      public final int delay;
      public final TickPriority priority;

      private TickHolder(T p_i231604_1_, BlockPos p_i231604_2_, int p_i231604_3_, TickPriority p_i231604_4_) {
         this.type = p_i231604_1_;
         this.pos = p_i231604_2_;
         this.delay = p_i231604_3_;
         this.priority = p_i231604_4_;
      }

      public String toString() {
         return this.type + ": " + this.pos + ", " + this.delay + ", " + this.priority;
      }
   }
}
