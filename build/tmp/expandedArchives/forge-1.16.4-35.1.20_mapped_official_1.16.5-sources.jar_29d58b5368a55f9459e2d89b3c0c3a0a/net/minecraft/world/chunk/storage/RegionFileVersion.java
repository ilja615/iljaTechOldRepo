package net.minecraft.world.chunk.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;

public class RegionFileVersion {
   private static final Int2ObjectMap<RegionFileVersion> VERSIONS = new Int2ObjectOpenHashMap<>();
   public static final RegionFileVersion VERSION_GZIP = register(new RegionFileVersion(1, GZIPInputStream::new, GZIPOutputStream::new));
   public static final RegionFileVersion VERSION_DEFLATE = register(new RegionFileVersion(2, InflaterInputStream::new, DeflaterOutputStream::new));
   public static final RegionFileVersion VERSION_NONE = register(new RegionFileVersion(3, (p_227171_0_) -> {
      return p_227171_0_;
   }, (p_227172_0_) -> {
      return p_227172_0_;
   }));
   private final int id;
   private final RegionFileVersion.IWrapper<InputStream> inputWrapper;
   private final RegionFileVersion.IWrapper<OutputStream> outputWrapper;

   private RegionFileVersion(int p_i225787_1_, RegionFileVersion.IWrapper<InputStream> p_i225787_2_, RegionFileVersion.IWrapper<OutputStream> p_i225787_3_) {
      this.id = p_i225787_1_;
      this.inputWrapper = p_i225787_2_;
      this.outputWrapper = p_i225787_3_;
   }

   private static RegionFileVersion register(RegionFileVersion p_227167_0_) {
      VERSIONS.put(p_227167_0_.id, p_227167_0_);
      return p_227167_0_;
   }

   @Nullable
   public static RegionFileVersion fromId(int p_227166_0_) {
      return VERSIONS.get(p_227166_0_);
   }

   public static boolean isValidVersion(int p_227170_0_) {
      return VERSIONS.containsKey(p_227170_0_);
   }

   public int getId() {
      return this.id;
   }

   public OutputStream wrap(OutputStream p_227169_1_) throws IOException {
      return this.outputWrapper.wrap(p_227169_1_);
   }

   public InputStream wrap(InputStream p_227168_1_) throws IOException {
      return this.inputWrapper.wrap(p_227168_1_);
   }

   @FunctionalInterface
   interface IWrapper<O> {
      O wrap(O p_wrap_1_) throws IOException;
   }
}
