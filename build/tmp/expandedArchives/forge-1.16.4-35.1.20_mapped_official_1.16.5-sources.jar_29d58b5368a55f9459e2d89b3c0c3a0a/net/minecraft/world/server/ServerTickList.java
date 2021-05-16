package net.minecraft.world.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ITickList;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.TickPriority;

public class ServerTickList<T> implements ITickList<T> {
   protected final Predicate<T> ignore;
   private final Function<T, ResourceLocation> toId;
   private final Set<NextTickListEntry<T>> tickNextTickSet = Sets.newHashSet();
   private final TreeSet<NextTickListEntry<T>> tickNextTickList = Sets.newTreeSet(NextTickListEntry.createTimeComparator());
   private final ServerWorld level;
   private final Queue<NextTickListEntry<T>> currentlyTicking = Queues.newArrayDeque();
   private final List<NextTickListEntry<T>> alreadyTicked = Lists.newArrayList();
   private final Consumer<NextTickListEntry<T>> ticker;

   public ServerTickList(ServerWorld p_i231625_1_, Predicate<T> p_i231625_2_, Function<T, ResourceLocation> p_i231625_3_, Consumer<NextTickListEntry<T>> p_i231625_4_) {
      this.ignore = p_i231625_2_;
      this.toId = p_i231625_3_;
      this.level = p_i231625_1_;
      this.ticker = p_i231625_4_;
   }

   public void tick() {
      int i = this.tickNextTickList.size();
      if (i != this.tickNextTickSet.size()) {
         throw new IllegalStateException("TickNextTick list out of synch");
      } else {
         if (i > 65536) {
            i = 65536;
         }

         ServerChunkProvider serverchunkprovider = this.level.getChunkSource();
         Iterator<NextTickListEntry<T>> iterator = this.tickNextTickList.iterator();
         this.level.getProfiler().push("cleaning");

         while(i > 0 && iterator.hasNext()) {
            NextTickListEntry<T> nextticklistentry = iterator.next();
            if (nextticklistentry.triggerTick > this.level.getGameTime()) {
               break;
            }

            if (serverchunkprovider.isTickingChunk(nextticklistentry.pos)) {
               iterator.remove();
               this.tickNextTickSet.remove(nextticklistentry);
               this.currentlyTicking.add(nextticklistentry);
               --i;
            }
         }

         this.level.getProfiler().popPush("ticking");

         NextTickListEntry<T> nextticklistentry1;
         while((nextticklistentry1 = this.currentlyTicking.poll()) != null) {
            if (serverchunkprovider.isTickingChunk(nextticklistentry1.pos)) {
               try {
                  this.alreadyTicked.add(nextticklistentry1);
                  this.ticker.accept(nextticklistentry1);
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception while ticking");
                  CrashReportCategory crashreportcategory = crashreport.addCategory("Block being ticked");
                  CrashReportCategory.populateBlockDetails(crashreportcategory, nextticklistentry1.pos, (BlockState)null);
                  throw new ReportedException(crashreport);
               }
            } else {
               this.scheduleTick(nextticklistentry1.pos, nextticklistentry1.getType(), 0);
            }
         }

         this.level.getProfiler().pop();
         this.alreadyTicked.clear();
         this.currentlyTicking.clear();
      }
   }

   public boolean willTickThisTick(BlockPos p_205361_1_, T p_205361_2_) {
      return this.currentlyTicking.contains(new NextTickListEntry(p_205361_1_, p_205361_2_));
   }

   public List<NextTickListEntry<T>> fetchTicksInChunk(ChunkPos p_223188_1_, boolean p_223188_2_, boolean p_223188_3_) {
      int i = (p_223188_1_.x << 4) - 2;
      int j = i + 16 + 2;
      int k = (p_223188_1_.z << 4) - 2;
      int l = k + 16 + 2;
      return this.fetchTicksInArea(new MutableBoundingBox(i, 0, k, j, 256, l), p_223188_2_, p_223188_3_);
   }

   public List<NextTickListEntry<T>> fetchTicksInArea(MutableBoundingBox p_205366_1_, boolean p_205366_2_, boolean p_205366_3_) {
      List<NextTickListEntry<T>> list = this.fetchTicksInArea((List<NextTickListEntry<T>>)null, this.tickNextTickList, p_205366_1_, p_205366_2_);
      if (p_205366_2_ && list != null) {
         this.tickNextTickSet.removeAll(list);
      }

      list = this.fetchTicksInArea(list, this.currentlyTicking, p_205366_1_, p_205366_2_);
      if (!p_205366_3_) {
         list = this.fetchTicksInArea(list, this.alreadyTicked, p_205366_1_, p_205366_2_);
      }

      return list == null ? Collections.emptyList() : list;
   }

   @Nullable
   private List<NextTickListEntry<T>> fetchTicksInArea(@Nullable List<NextTickListEntry<T>> p_223187_1_, Collection<NextTickListEntry<T>> p_223187_2_, MutableBoundingBox p_223187_3_, boolean p_223187_4_) {
      Iterator<NextTickListEntry<T>> iterator = p_223187_2_.iterator();

      while(iterator.hasNext()) {
         NextTickListEntry<T> nextticklistentry = iterator.next();
         BlockPos blockpos = nextticklistentry.pos;
         if (blockpos.getX() >= p_223187_3_.x0 && blockpos.getX() < p_223187_3_.x1 && blockpos.getZ() >= p_223187_3_.z0 && blockpos.getZ() < p_223187_3_.z1) {
            if (p_223187_4_) {
               iterator.remove();
            }

            if (p_223187_1_ == null) {
               p_223187_1_ = Lists.newArrayList();
            }

            p_223187_1_.add(nextticklistentry);
         }
      }

      return p_223187_1_;
   }

   public void copy(MutableBoundingBox p_205368_1_, BlockPos p_205368_2_) {
      for(NextTickListEntry<T> nextticklistentry : this.fetchTicksInArea(p_205368_1_, false, false)) {
         if (p_205368_1_.isInside(nextticklistentry.pos)) {
            BlockPos blockpos = nextticklistentry.pos.offset(p_205368_2_);
            T t = nextticklistentry.getType();
            this.addTickData(new NextTickListEntry<>(blockpos, t, nextticklistentry.triggerTick, nextticklistentry.priority));
         }
      }

   }

   public ListNBT save(ChunkPos p_219503_1_) {
      List<NextTickListEntry<T>> list = this.fetchTicksInChunk(p_219503_1_, false, true);
      return saveTickList(this.toId, list, this.level.getGameTime());
   }

   private static <T> ListNBT saveTickList(Function<T, ResourceLocation> p_219502_0_, Iterable<NextTickListEntry<T>> p_219502_1_, long p_219502_2_) {
      ListNBT listnbt = new ListNBT();

      for(NextTickListEntry<T> nextticklistentry : p_219502_1_) {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putString("i", p_219502_0_.apply(nextticklistentry.getType()).toString());
         compoundnbt.putInt("x", nextticklistentry.pos.getX());
         compoundnbt.putInt("y", nextticklistentry.pos.getY());
         compoundnbt.putInt("z", nextticklistentry.pos.getZ());
         compoundnbt.putInt("t", (int)(nextticklistentry.triggerTick - p_219502_2_));
         compoundnbt.putInt("p", nextticklistentry.priority.getValue());
         listnbt.add(compoundnbt);
      }

      return listnbt;
   }

   public boolean hasScheduledTick(BlockPos p_205359_1_, T p_205359_2_) {
      return this.tickNextTickSet.contains(new NextTickListEntry(p_205359_1_, p_205359_2_));
   }

   public void scheduleTick(BlockPos p_205362_1_, T p_205362_2_, int p_205362_3_, TickPriority p_205362_4_) {
      if (!this.ignore.test(p_205362_2_)) {
         this.addTickData(new NextTickListEntry<>(p_205362_1_, p_205362_2_, (long)p_205362_3_ + this.level.getGameTime(), p_205362_4_));
      }

   }

   private void addTickData(NextTickListEntry<T> p_219504_1_) {
      if (!this.tickNextTickSet.contains(p_219504_1_)) {
         this.tickNextTickSet.add(p_219504_1_);
         this.tickNextTickList.add(p_219504_1_);
      }

   }

   public int size() {
      return this.tickNextTickSet.size();
   }
}
