package net.minecraft.network.rcon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RConOutputStream {
   private final ByteArrayOutputStream outputStream;
   private final DataOutputStream dataOutputStream;

   public RConOutputStream(int p_i1533_1_) {
      this.outputStream = new ByteArrayOutputStream(p_i1533_1_);
      this.dataOutputStream = new DataOutputStream(this.outputStream);
   }

   public void writeBytes(byte[] p_72670_1_) throws IOException {
      this.dataOutputStream.write(p_72670_1_, 0, p_72670_1_.length);
   }

   public void writeString(String p_72671_1_) throws IOException {
      this.dataOutputStream.writeBytes(p_72671_1_);
      this.dataOutputStream.write(0);
   }

   public void write(int p_72667_1_) throws IOException {
      this.dataOutputStream.write(p_72667_1_);
   }

   public void writeShort(short p_72668_1_) throws IOException {
      this.dataOutputStream.writeShort(Short.reverseBytes(p_72668_1_));
   }

   public byte[] toByteArray() {
      return this.outputStream.toByteArray();
   }

   public void reset() {
      this.outputStream.reset();
   }
}
