package net.minecraft.util.palette;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BitArray;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PalettedContainer<T> implements IResizeCallback<T> {
   private final IPalette<T> globalPalette;
   private final IResizeCallback<T> dummyPaletteResize = (p_205517_0_, p_205517_1_) -> {
      return 0;
   };
   private final ObjectIntIdentityMap<T> registry;
   private final Function<CompoundNBT, T> reader;
   private final Function<T, CompoundNBT> writer;
   private final T defaultValue;
   protected BitArray storage;
   private IPalette<T> palette;
   private int bits;
   private final ReentrantLock lock = new ReentrantLock();

   public void acquire() {
      if (this.lock.isLocked() && !this.lock.isHeldByCurrentThread()) {
         String s = Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map((p_210458_0_) -> {
            return p_210458_0_.getName() + ": \n\tat " + (String)Arrays.stream(p_210458_0_.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
         }).collect(Collectors.joining("\n"));
         CrashReport crashreport = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
         CrashReportCategory crashreportcategory = crashreport.addCategory("Thread dumps");
         crashreportcategory.setDetail("Thread dumps", s);
         throw new ReportedException(crashreport);
      } else {
         this.lock.lock();
      }
   }

   public void release() {
      this.lock.unlock();
   }

   public PalettedContainer(IPalette<T> p_i48961_1_, ObjectIntIdentityMap<T> p_i48961_2_, Function<CompoundNBT, T> p_i48961_3_, Function<T, CompoundNBT> p_i48961_4_, T p_i48961_5_) {
      this.globalPalette = p_i48961_1_;
      this.registry = p_i48961_2_;
      this.reader = p_i48961_3_;
      this.writer = p_i48961_4_;
      this.defaultValue = p_i48961_5_;
      this.setBits(4);
   }

   private static int getIndex(int p_186011_0_, int p_186011_1_, int p_186011_2_) {
      return p_186011_1_ << 8 | p_186011_2_ << 4 | p_186011_0_;
   }

   private void setBits(int p_186012_1_) {
      setBits(p_186012_1_, false);
   }
   private void setBits(int bitsIn, boolean forceBits) {
      if (bitsIn != this.bits) {
         this.bits = bitsIn;
         if (this.bits <= 4) {
            this.bits = 4;
            this.palette = new ArrayPalette<>(this.registry, this.bits, this, this.reader);
         } else if (this.bits < 9) {
            this.palette = new HashMapPalette<>(this.registry, this.bits, this, this.reader, this.writer);
         } else {
            this.palette = this.globalPalette;
            this.bits = MathHelper.ceillog2(this.registry.size());
            if (forceBits)
               this.bits = bitsIn;
         }

         this.palette.idFor(this.defaultValue);
         this.storage = new BitArray(this.bits, 4096);
      }
   }

   public int onResize(int p_onResize_1_, T p_onResize_2_) {
      this.acquire();
      BitArray bitarray = this.storage;
      IPalette<T> ipalette = this.palette;
      this.setBits(p_onResize_1_);

      for(int i = 0; i < bitarray.getSize(); ++i) {
         T t = ipalette.valueFor(bitarray.get(i));
         if (t != null) {
            this.set(i, t);
         }
      }

      int j = this.palette.idFor(p_onResize_2_);
      this.release();
      return j;
   }

   public T getAndSet(int p_222641_1_, int p_222641_2_, int p_222641_3_, T p_222641_4_) {
      this.acquire();
      T t = this.getAndSet(getIndex(p_222641_1_, p_222641_2_, p_222641_3_), p_222641_4_);
      this.release();
      return t;
   }

   public T getAndSetUnchecked(int p_222639_1_, int p_222639_2_, int p_222639_3_, T p_222639_4_) {
      return this.getAndSet(getIndex(p_222639_1_, p_222639_2_, p_222639_3_), p_222639_4_);
   }

   protected T getAndSet(int p_222643_1_, T p_222643_2_) {
      int i = this.palette.idFor(p_222643_2_);
      int j = this.storage.getAndSet(p_222643_1_, i);
      T t = this.palette.valueFor(j);
      return (T)(t == null ? this.defaultValue : t);
   }

   protected void set(int p_186014_1_, T p_186014_2_) {
      int i = this.palette.idFor(p_186014_2_);
      this.storage.set(p_186014_1_, i);
   }

   public T get(int p_186016_1_, int p_186016_2_, int p_186016_3_) {
      return this.get(getIndex(p_186016_1_, p_186016_2_, p_186016_3_));
   }

   protected T get(int p_186015_1_) {
      T t = this.palette.valueFor(this.storage.get(p_186015_1_));
      return (T)(t == null ? this.defaultValue : t);
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_186010_1_) {
      this.acquire();
      int i = p_186010_1_.readByte();
      if (this.bits != i) {
         this.setBits(i, true); //Forge, Force bit density to fix network issues, resize below if needed.
      }

      this.palette.read(p_186010_1_);
      p_186010_1_.readLongArray(this.storage.getRaw());
      this.release();

      int regSize = MathHelper.ceillog2(this.registry.size());
      if (this.palette == globalPalette && this.bits != regSize) // Resize bits to fit registry.
         this.onResize(regSize, defaultValue);
   }

   public void write(PacketBuffer p_186009_1_) {
      this.acquire();
      p_186009_1_.writeByte(this.bits);
      this.palette.write(p_186009_1_);
      p_186009_1_.writeLongArray(this.storage.getRaw());
      this.release();
   }

   public void read(ListNBT p_222642_1_, long[] p_222642_2_) {
      this.acquire();
      int i = Math.max(4, MathHelper.ceillog2(p_222642_1_.size()));
      if (i != this.bits) {
         this.setBits(i);
      }

      this.palette.read(p_222642_1_);
      int j = p_222642_2_.length * 64 / 4096;
      if (this.palette == this.globalPalette) {
         IPalette<T> ipalette = new HashMapPalette<>(this.registry, i, this.dummyPaletteResize, this.reader, this.writer);
         ipalette.read(p_222642_1_);
         BitArray bitarray = new BitArray(i, 4096, p_222642_2_);

         for(int k = 0; k < 4096; ++k) {
            this.storage.set(k, this.globalPalette.idFor(ipalette.valueFor(bitarray.get(k))));
         }
      } else if (j == this.bits) {
         System.arraycopy(p_222642_2_, 0, this.storage.getRaw(), 0, p_222642_2_.length);
      } else {
         BitArray bitarray1 = new BitArray(j, 4096, p_222642_2_);

         for(int l = 0; l < 4096; ++l) {
            this.storage.set(l, bitarray1.get(l));
         }
      }

      this.release();
   }

   public void write(CompoundNBT p_196963_1_, String p_196963_2_, String p_196963_3_) {
      this.acquire();
      HashMapPalette<T> hashmappalette = new HashMapPalette<>(this.registry, this.bits, this.dummyPaletteResize, this.reader, this.writer);
      T t = this.defaultValue;
      int i = hashmappalette.idFor(this.defaultValue);
      int[] aint = new int[4096];

      for(int j = 0; j < 4096; ++j) {
         T t1 = this.get(j);
         if (t1 != t) {
            t = t1;
            i = hashmappalette.idFor(t1);
         }

         aint[j] = i;
      }

      ListNBT listnbt = new ListNBT();
      hashmappalette.write(listnbt);
      p_196963_1_.put(p_196963_2_, listnbt);
      int l = Math.max(4, MathHelper.ceillog2(listnbt.size()));
      BitArray bitarray = new BitArray(l, 4096);

      for(int k = 0; k < aint.length; ++k) {
         bitarray.set(k, aint[k]);
      }

      p_196963_1_.putLongArray(p_196963_3_, bitarray.getRaw());
      this.release();
   }

   public int getSerializedSize() {
      return 1 + this.palette.getSerializedSize() + PacketBuffer.getVarIntSize(this.storage.getSize()) + this.storage.getRaw().length * 8;
   }

   public boolean maybeHas(Predicate<T> p_235963_1_) {
      return this.palette.maybeHas(p_235963_1_);
   }

   public void count(PalettedContainer.ICountConsumer<T> p_225497_1_) {
      Int2IntMap int2intmap = new Int2IntOpenHashMap();
      this.storage.getAll((p_225498_1_) -> {
         int2intmap.put(p_225498_1_, int2intmap.get(p_225498_1_) + 1);
      });
      int2intmap.int2IntEntrySet().forEach((p_225499_2_) -> {
         p_225497_1_.accept(this.palette.valueFor(p_225499_2_.getIntKey()), p_225499_2_.getIntValue());
      });
   }

   @FunctionalInterface
   public interface ICountConsumer<T> {
      void accept(T p_accept_1_, int p_accept_2_);
   }
}
