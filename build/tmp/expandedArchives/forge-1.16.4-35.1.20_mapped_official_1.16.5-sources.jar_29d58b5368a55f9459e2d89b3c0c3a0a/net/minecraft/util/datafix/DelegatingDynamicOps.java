package net.minecraft.util.datafix;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class DelegatingDynamicOps<T> implements DynamicOps<T> {
   protected final DynamicOps<T> delegate;

   protected DelegatingDynamicOps(DynamicOps<T> p_i232586_1_) {
      this.delegate = p_i232586_1_;
   }

   public T empty() {
      return this.delegate.empty();
   }

   public <U> U convertTo(DynamicOps<U> p_convertTo_1_, T p_convertTo_2_) {
      return this.delegate.convertTo(p_convertTo_1_, p_convertTo_2_);
   }

   public DataResult<Number> getNumberValue(T p_getNumberValue_1_) {
      return this.delegate.getNumberValue(p_getNumberValue_1_);
   }

   public T createNumeric(Number p_createNumeric_1_) {
      return this.delegate.createNumeric(p_createNumeric_1_);
   }

   public T createByte(byte p_createByte_1_) {
      return this.delegate.createByte(p_createByte_1_);
   }

   public T createShort(short p_createShort_1_) {
      return this.delegate.createShort(p_createShort_1_);
   }

   public T createInt(int p_createInt_1_) {
      return this.delegate.createInt(p_createInt_1_);
   }

   public T createLong(long p_createLong_1_) {
      return this.delegate.createLong(p_createLong_1_);
   }

   public T createFloat(float p_createFloat_1_) {
      return this.delegate.createFloat(p_createFloat_1_);
   }

   public T createDouble(double p_createDouble_1_) {
      return this.delegate.createDouble(p_createDouble_1_);
   }

   public DataResult<Boolean> getBooleanValue(T p_getBooleanValue_1_) {
      return this.delegate.getBooleanValue(p_getBooleanValue_1_);
   }

   public T createBoolean(boolean p_createBoolean_1_) {
      return this.delegate.createBoolean(p_createBoolean_1_);
   }

   public DataResult<String> getStringValue(T p_getStringValue_1_) {
      return this.delegate.getStringValue(p_getStringValue_1_);
   }

   public T createString(String p_createString_1_) {
      return this.delegate.createString(p_createString_1_);
   }

   public DataResult<T> mergeToList(T p_mergeToList_1_, T p_mergeToList_2_) {
      return this.delegate.mergeToList(p_mergeToList_1_, p_mergeToList_2_);
   }

   public DataResult<T> mergeToList(T p_mergeToList_1_, List<T> p_mergeToList_2_) {
      return this.delegate.mergeToList(p_mergeToList_1_, p_mergeToList_2_);
   }

   public DataResult<T> mergeToMap(T p_mergeToMap_1_, T p_mergeToMap_2_, T p_mergeToMap_3_) {
      return this.delegate.mergeToMap(p_mergeToMap_1_, p_mergeToMap_2_, p_mergeToMap_3_);
   }

   public DataResult<T> mergeToMap(T p_mergeToMap_1_, MapLike<T> p_mergeToMap_2_) {
      return this.delegate.mergeToMap(p_mergeToMap_1_, p_mergeToMap_2_);
   }

   public DataResult<Stream<Pair<T, T>>> getMapValues(T p_getMapValues_1_) {
      return this.delegate.getMapValues(p_getMapValues_1_);
   }

   public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T p_getMapEntries_1_) {
      return this.delegate.getMapEntries(p_getMapEntries_1_);
   }

   public T createMap(Stream<Pair<T, T>> p_createMap_1_) {
      return this.delegate.createMap(p_createMap_1_);
   }

   public DataResult<MapLike<T>> getMap(T p_getMap_1_) {
      return this.delegate.getMap(p_getMap_1_);
   }

   public DataResult<Stream<T>> getStream(T p_getStream_1_) {
      return this.delegate.getStream(p_getStream_1_);
   }

   public DataResult<Consumer<Consumer<T>>> getList(T p_getList_1_) {
      return this.delegate.getList(p_getList_1_);
   }

   public T createList(Stream<T> p_createList_1_) {
      return this.delegate.createList(p_createList_1_);
   }

   public DataResult<ByteBuffer> getByteBuffer(T p_getByteBuffer_1_) {
      return this.delegate.getByteBuffer(p_getByteBuffer_1_);
   }

   public T createByteList(ByteBuffer p_createByteList_1_) {
      return this.delegate.createByteList(p_createByteList_1_);
   }

   public DataResult<IntStream> getIntStream(T p_getIntStream_1_) {
      return this.delegate.getIntStream(p_getIntStream_1_);
   }

   public T createIntList(IntStream p_createIntList_1_) {
      return this.delegate.createIntList(p_createIntList_1_);
   }

   public DataResult<LongStream> getLongStream(T p_getLongStream_1_) {
      return this.delegate.getLongStream(p_getLongStream_1_);
   }

   public T createLongList(LongStream p_createLongList_1_) {
      return this.delegate.createLongList(p_createLongList_1_);
   }

   public T remove(T p_remove_1_, String p_remove_2_) {
      return this.delegate.remove(p_remove_1_, p_remove_2_);
   }

   public boolean compressMaps() {
      return this.delegate.compressMaps();
   }

   public ListBuilder<T> listBuilder() {
      return this.delegate.listBuilder();
   }

   public RecordBuilder<T> mapBuilder() {
      return this.delegate.mapBuilder();
   }
}
