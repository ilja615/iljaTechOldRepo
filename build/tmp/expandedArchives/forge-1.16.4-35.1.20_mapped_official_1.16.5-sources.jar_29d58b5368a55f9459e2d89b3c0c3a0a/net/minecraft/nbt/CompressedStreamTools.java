package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;

public class CompressedStreamTools {
   public static CompoundNBT readCompressed(File p_244263_0_) throws IOException {
      CompoundNBT compoundnbt;
      try (InputStream inputstream = new FileInputStream(p_244263_0_)) {
         compoundnbt = readCompressed(inputstream);
      }

      return compoundnbt;
   }

   public static CompoundNBT readCompressed(InputStream p_74796_0_) throws IOException {
      CompoundNBT compoundnbt;
      try (DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(p_74796_0_)))) {
         compoundnbt = read(datainputstream, NBTSizeTracker.UNLIMITED);
      }

      return compoundnbt;
   }

   public static void writeCompressed(CompoundNBT p_244264_0_, File p_244264_1_) throws IOException {
      try (OutputStream outputstream = new FileOutputStream(p_244264_1_)) {
         writeCompressed(p_244264_0_, outputstream);
      }

   }

   public static void writeCompressed(CompoundNBT p_74799_0_, OutputStream p_74799_1_) throws IOException {
      try (DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(p_74799_1_)))) {
         write(p_74799_0_, dataoutputstream);
      }

   }

   public static void write(CompoundNBT p_74795_0_, File p_74795_1_) throws IOException {
      try (
         FileOutputStream fileoutputstream = new FileOutputStream(p_74795_1_);
         DataOutputStream dataoutputstream = new DataOutputStream(fileoutputstream);
      ) {
         write(p_74795_0_, dataoutputstream);
      }

   }

   @Nullable
   public static CompoundNBT read(File p_74797_0_) throws IOException {
      if (!p_74797_0_.exists()) {
         return null;
      } else {
         CompoundNBT compoundnbt;
         try (
            FileInputStream fileinputstream = new FileInputStream(p_74797_0_);
            DataInputStream datainputstream = new DataInputStream(fileinputstream);
         ) {
            compoundnbt = read(datainputstream, NBTSizeTracker.UNLIMITED);
         }

         return compoundnbt;
      }
   }

   public static CompoundNBT read(DataInput p_74794_0_) throws IOException {
      return read(p_74794_0_, NBTSizeTracker.UNLIMITED);
   }

   public static CompoundNBT read(DataInput p_152456_0_, NBTSizeTracker p_152456_1_) throws IOException {
      INBT inbt = readUnnamedTag(p_152456_0_, 0, p_152456_1_);
      if (inbt instanceof CompoundNBT) {
         return (CompoundNBT)inbt;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void write(CompoundNBT p_74800_0_, DataOutput p_74800_1_) throws IOException {
      writeUnnamedTag(p_74800_0_, p_74800_1_);
   }

   private static void writeUnnamedTag(INBT p_150663_0_, DataOutput p_150663_1_) throws IOException {
      p_150663_1_.writeByte(p_150663_0_.getId());
      if (p_150663_0_.getId() != 0) {
         p_150663_1_.writeUTF("");
         p_150663_0_.write(p_150663_1_);
      }
   }

   private static INBT readUnnamedTag(DataInput p_152455_0_, int p_152455_1_, NBTSizeTracker p_152455_2_) throws IOException {
      byte b0 = p_152455_0_.readByte();
      p_152455_2_.accountBits(8); // Forge: Count everything!
      if (b0 == 0) {
         return EndNBT.INSTANCE;
      } else {
         p_152455_2_.readUTF(p_152455_0_.readUTF()); //Forge: Count this string.
         p_152455_2_.accountBits(32); //Forge: 4 extra bytes for the object allocation.

         try {
            return NBTTypes.getType(b0).load(p_152455_0_, p_152455_1_, p_152455_2_);
         } catch (IOException ioexception) {
            CrashReport crashreport = CrashReport.forThrowable(ioexception, "Loading NBT data");
            CrashReportCategory crashreportcategory = crashreport.addCategory("NBT Tag");
            crashreportcategory.setDetail("Tag type", b0);
            throw new ReportedException(crashreport);
         }
      }
   }
}
