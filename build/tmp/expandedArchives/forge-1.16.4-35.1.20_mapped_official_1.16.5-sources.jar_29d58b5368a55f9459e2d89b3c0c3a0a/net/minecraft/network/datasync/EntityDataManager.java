package net.minecraft.network.datasync;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityDataManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<Class<? extends Entity>, Integer> ENTITY_ID_POOL = Maps.newHashMap();
   private final Entity entity;
   private final Map<Integer, EntityDataManager.DataEntry<?>> itemsById = Maps.newHashMap();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private boolean isEmpty = true;
   private boolean isDirty;

   public EntityDataManager(Entity p_i46840_1_) {
      this.entity = p_i46840_1_;
   }

   public static <T> DataParameter<T> defineId(Class<? extends Entity> p_187226_0_, IDataSerializer<T> p_187226_1_) {
      if (true || LOGGER.isDebugEnabled()) { // Forge: This is very useful for mods that register keys on classes that are not their own
         try {
            Class<?> oclass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (!oclass.equals(p_187226_0_)) {
               // Forge: log at warn, mods should not add to classes that they don't own, and only add stacktrace when in debug is enabled as it is mostly not needed and consumes time
               if (LOGGER.isDebugEnabled()) LOGGER.warn("defineId called for: {} from {}", p_187226_0_, oclass, new RuntimeException());
               else LOGGER.warn("defineId called for: {} from {}", p_187226_0_, oclass);
            }
         } catch (ClassNotFoundException classnotfoundexception) {
         }
      }

      int j;
      if (ENTITY_ID_POOL.containsKey(p_187226_0_)) {
         j = ENTITY_ID_POOL.get(p_187226_0_) + 1;
      } else {
         int i = 0;
         Class<?> oclass1 = p_187226_0_;

         while(oclass1 != Entity.class) {
            oclass1 = oclass1.getSuperclass();
            if (ENTITY_ID_POOL.containsKey(oclass1)) {
               i = ENTITY_ID_POOL.get(oclass1) + 1;
               break;
            }
         }

         j = i;
      }

      if (j > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + j + "! (Max is " + 254 + ")");
      } else {
         ENTITY_ID_POOL.put(p_187226_0_, j);
         return p_187226_1_.createAccessor(j);
      }
   }

   public <T> void define(DataParameter<T> p_187214_1_, T p_187214_2_) {
      int i = p_187214_1_.getId();
      if (i > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
      } else if (this.itemsById.containsKey(i)) {
         throw new IllegalArgumentException("Duplicate id value for " + i + "!");
      } else if (DataSerializers.getSerializedId(p_187214_1_.getSerializer()) < 0) {
         throw new IllegalArgumentException("Unregistered serializer " + p_187214_1_.getSerializer() + " for " + i + "!");
      } else {
         this.createDataItem(p_187214_1_, p_187214_2_);
      }
   }

   private <T> void createDataItem(DataParameter<T> p_187222_1_, T p_187222_2_) {
      EntityDataManager.DataEntry<T> dataentry = new EntityDataManager.DataEntry<>(p_187222_1_, p_187222_2_);
      this.lock.writeLock().lock();
      this.itemsById.put(p_187222_1_.getId(), dataentry);
      this.isEmpty = false;
      this.lock.writeLock().unlock();
   }

   private <T> EntityDataManager.DataEntry<T> getItem(DataParameter<T> p_187219_1_) {
      this.lock.readLock().lock();

      EntityDataManager.DataEntry<T> dataentry;
      try {
         dataentry = (DataEntry<T>) this.itemsById.get(p_187219_1_.getId());
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting synched entity data");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Synched entity data");
         crashreportcategory.setDetail("Data ID", p_187219_1_);
         throw new ReportedException(crashreport);
      } finally {
         this.lock.readLock().unlock();
      }

      return dataentry;
   }

   public <T> T get(DataParameter<T> p_187225_1_) {
      return this.getItem(p_187225_1_).getValue();
   }

   public <T> void set(DataParameter<T> p_187227_1_, T p_187227_2_) {
      EntityDataManager.DataEntry<T> dataentry = this.getItem(p_187227_1_);
      if (ObjectUtils.notEqual(p_187227_2_, dataentry.getValue())) {
         dataentry.setValue(p_187227_2_);
         this.entity.onSyncedDataUpdated(p_187227_1_);
         dataentry.setDirty(true);
         this.isDirty = true;
      }

   }

   public boolean isDirty() {
      return this.isDirty;
   }

   public static void pack(List<EntityDataManager.DataEntry<?>> p_187229_0_, PacketBuffer p_187229_1_) throws IOException {
      if (p_187229_0_ != null) {
         int i = 0;

         for(int j = p_187229_0_.size(); i < j; ++i) {
            writeDataItem(p_187229_1_, p_187229_0_.get(i));
         }
      }

      p_187229_1_.writeByte(255);
   }

   @Nullable
   public List<EntityDataManager.DataEntry<?>> packDirty() {
      List<EntityDataManager.DataEntry<?>> list = null;
      if (this.isDirty) {
         this.lock.readLock().lock();

         for(EntityDataManager.DataEntry<?> dataentry : this.itemsById.values()) {
            if (dataentry.isDirty()) {
               dataentry.setDirty(false);
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(dataentry.copy());
            }
         }

         this.lock.readLock().unlock();
      }

      this.isDirty = false;
      return list;
   }

   @Nullable
   public List<EntityDataManager.DataEntry<?>> getAll() {
      List<EntityDataManager.DataEntry<?>> list = null;
      this.lock.readLock().lock();

      for(EntityDataManager.DataEntry<?> dataentry : this.itemsById.values()) {
         if (list == null) {
            list = Lists.newArrayList();
         }

         list.add(dataentry.copy());
      }

      this.lock.readLock().unlock();
      return list;
   }

   private static <T> void writeDataItem(PacketBuffer p_187220_0_, EntityDataManager.DataEntry<T> p_187220_1_) throws IOException {
      DataParameter<T> dataparameter = p_187220_1_.getAccessor();
      int i = DataSerializers.getSerializedId(dataparameter.getSerializer());
      if (i < 0) {
         throw new EncoderException("Unknown serializer type " + dataparameter.getSerializer());
      } else {
         p_187220_0_.writeByte(dataparameter.getId());
         p_187220_0_.writeVarInt(i);
         dataparameter.getSerializer().write(p_187220_0_, p_187220_1_.getValue());
      }
   }

   @Nullable
   public static List<EntityDataManager.DataEntry<?>> unpack(PacketBuffer p_187215_0_) throws IOException {
      List<EntityDataManager.DataEntry<?>> list = null;

      int i;
      while((i = p_187215_0_.readUnsignedByte()) != 255) {
         if (list == null) {
            list = Lists.newArrayList();
         }

         int j = p_187215_0_.readVarInt();
         IDataSerializer<?> idataserializer = DataSerializers.getSerializer(j);
         if (idataserializer == null) {
            throw new DecoderException("Unknown serializer type " + j);
         }

         list.add(genericHelper(p_187215_0_, i, idataserializer));
      }

      return list;
   }

   private static <T> EntityDataManager.DataEntry<T> genericHelper(PacketBuffer p_198167_0_, int p_198167_1_, IDataSerializer<T> p_198167_2_) {
      return new EntityDataManager.DataEntry<>(p_198167_2_.createAccessor(p_198167_1_), p_198167_2_.read(p_198167_0_));
   }

   @OnlyIn(Dist.CLIENT)
   public void assignValues(List<EntityDataManager.DataEntry<?>> p_187218_1_) {
      this.lock.writeLock().lock();

      for(EntityDataManager.DataEntry<?> dataentry : p_187218_1_) {
         EntityDataManager.DataEntry<?> dataentry1 = this.itemsById.get(dataentry.getAccessor().getId());
         if (dataentry1 != null) {
            this.assignValue(dataentry1, dataentry);
            this.entity.onSyncedDataUpdated(dataentry.getAccessor());
         }
      }

      this.lock.writeLock().unlock();
      this.isDirty = true;
   }

   @OnlyIn(Dist.CLIENT)
   private <T> void assignValue(EntityDataManager.DataEntry<T> p_187224_1_, EntityDataManager.DataEntry<?> p_187224_2_) {
      if (!Objects.equals(p_187224_2_.accessor.getSerializer(), p_187224_1_.accessor.getSerializer())) {
         throw new IllegalStateException(String.format("Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", p_187224_1_.accessor.getId(), this.entity, p_187224_1_.value, p_187224_1_.value.getClass(), p_187224_2_.value, p_187224_2_.value.getClass()));
      } else {
         p_187224_1_.setValue((T)p_187224_2_.getValue());
      }
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   public void clearDirty() {
      this.isDirty = false;
      this.lock.readLock().lock();

      for(EntityDataManager.DataEntry<?> dataentry : this.itemsById.values()) {
         dataentry.setDirty(false);
      }

      this.lock.readLock().unlock();
   }

   public static class DataEntry<T> {
      private final DataParameter<T> accessor;
      private T value;
      private boolean dirty;

      public DataEntry(DataParameter<T> p_i47010_1_, T p_i47010_2_) {
         this.accessor = p_i47010_1_;
         this.value = p_i47010_2_;
         this.dirty = true;
      }

      public DataParameter<T> getAccessor() {
         return this.accessor;
      }

      public void setValue(T p_187210_1_) {
         this.value = p_187210_1_;
      }

      public T getValue() {
         return this.value;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public void setDirty(boolean p_187208_1_) {
         this.dirty = p_187208_1_;
      }

      public EntityDataManager.DataEntry<T> copy() {
         return new EntityDataManager.DataEntry<>(this.accessor, this.accessor.getSerializer().copy(this.value));
      }
   }
}
